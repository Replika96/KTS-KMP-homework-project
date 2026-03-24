package org.kts.tazmin.feature.courses.data.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.courses.data.local.CatalogDao
import org.kts.tazmin.feature.courses.data.local.CourseDao
import org.kts.tazmin.feature.courses.data.local.CourseEntity
import org.kts.tazmin.feature.courses.data.local.CourseSource
import org.kts.tazmin.feature.courses.data.local.SectionType
import org.kts.tazmin.feature.courses.data.local.SectionWithItems
import org.kts.tazmin.feature.courses.data.mapper.CatalogMapper
import org.kts.tazmin.feature.courses.data.mapper.CourseMapper
import org.kts.tazmin.feature.courses.data.network.api.CatalogApi
import org.kts.tazmin.feature.courses.domain.entity.CatalogSection
import org.kts.tazmin.feature.courses.domain.repository.CatalogRepository

class CatalogRepositoryImpl(
    private val catalogApi: CatalogApi,
    private val catalogDao: CatalogDao,
    private val courseDao: CourseDao,
    private val courseMapper: CourseMapper,
    private val catalogMapper: CatalogMapper
) : CatalogRepository {

    override fun loadCatalog(): Flow<Resource<List<CatalogSection>>> {
        return catalogDao.observeSectionsWithItems()
            .map<List<SectionWithItems>, Resource<List<CatalogSection>>> { sectionsWithItems ->

                // собираем ВСЕ courseId одним списком
                val allIds = sectionsWithItems
                    .flatMap { it.items }
                    .map { it.itemId }
                    .distinct()

                // загружаем курсы одним запросом
                val coursesMap = if (allIds.isNotEmpty()) {
                    courseDao.getCoursesByIds(allIds)
                        .associateBy { it.id }
                } else emptyMap()

                // мапим в domain
                val domain = sectionsWithItems.map { relation ->
                    mapToDomain(relation, coursesMap)
                }

                Resource.Success(domain, Source.CACHE)
            }
            .distinctUntilChanged()
    }

    // network
    override suspend fun refreshFromNetwork() {
        try {
            val blocks = catalogApi.getCatalogBlocks().blocks
                .distinctBy { it.title to it.kind }

            // секции
            val sectionEntities = blocks.mapIndexed { index, block ->
                catalogMapper.toSectionEntity(block, index)
            }

            // элементы секций
            val itemEntities = blocks.flatMapIndexed { sectionIndex, block ->
                catalogMapper.toSectionItems(block, sectionIndex)
            }

            // превью (по 2 курса на секцию)
            val previewIds = itemEntities
                .groupBy { it.sectionPosition }
                .values
                .flatMap { items ->
                    items.sortedBy { it.position }.take(2)
                }
                .map { it.itemId }
                .distinct()
                .take(60)

            Napier.d("Preview course IDs count: ${previewIds.size}")

            // загружаем курсы пачками
            val courseDtos = coroutineScope {
                previewIds
                    .chunked(30)
                    .map { chunk ->
                        async { catalogApi.getCourses(chunk).courses }
                    }
                    .awaitAll()
                    .flatten()
            }

            val courseEntities = courseDtos.map { dto ->
                val domain = courseMapper.fromDto(dto)
                courseMapper.toEntity(
                    course = domain,
                    source = CourseSource.CATALOG
                )
            }

            // сохраняем курсы
            if (courseEntities.isNotEmpty()) {
                courseDao.insertCourses(courseEntities)
            }

            // заменяем каталог (транзакция)
            catalogDao.replaceCatalog(sectionEntities, itemEntities)

        } catch (e: Throwable) {
            Napier.e("Catalog refresh failed: ${e.message}", e)
            throw e
        }
    }

    // mapping
    private fun mapToDomain(
        relation: SectionWithItems,
        coursesMap: Map<Int, CourseEntity>
    ): CatalogSection {

        val section = relation.section

        return when (section.type) {

            SectionType.FULL_COURSE_LISTS,
            SectionType.SIMPLE_COURSE_LISTS -> {

                val sortedItems = relation.items.sortedBy { it.position }

                val courses = sortedItems.mapNotNull { item ->
                    coursesMap[item.itemId]
                        ?.let { courseMapper.fromEntity(it) }
                }

                CatalogSection.CourseList(
                    id = section.id,
                    title = section.title,
                    courses = courses
                )
            }

            SectionType.BANNER -> {
                CatalogSection.Banner(
                    id = section.id,
                    title = section.title,
                    cover = section.cover,
                    url = section.url ?: ""
                )
            }
        }
    }
}
