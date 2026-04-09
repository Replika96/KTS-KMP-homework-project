package org.kts.tazmin.feature.catalog.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.feature.catalog.data.local.dao.CatalogDao
import org.kts.tazmin.feature.catalog.data.local.dao.CourseDao
import org.kts.tazmin.feature.catalog.data.local.CourseEntity
import org.kts.tazmin.feature.catalog.data.local.CourseSource
import org.kts.tazmin.feature.catalog.data.local.SectionType
import org.kts.tazmin.feature.catalog.data.local.SectionWithItems
import org.kts.tazmin.feature.catalog.domain.entity.CatalogSection
import org.kts.tazmin.feature.catalog.domain.mapper.CatalogMapper
import org.kts.tazmin.feature.catalog.domain.mapper.CourseMapper
import org.kts.tazmin.feature.catalog.domain.network.CatalogApi
import org.kts.tazmin.feature.catalog.domain.repository.CatalogRepository

class CatalogRepositoryImpl(
    private val catalogApi: CatalogApi,
    private val catalogDao: CatalogDao,
    private val courseDao: CourseDao,
    private val courseMapper: CourseMapper,
    private val catalogMapper: CatalogMapper
) : CatalogRepository {

    private val previewLimit = 11

    private val allowedKinds = setOf(
        "full_course_lists",
        "simple_course_lists",
        "banner"
    )

    override fun observeCatalog(): Flow<List<CatalogSection>> {
        return catalogDao.observeSectionsWithItems()
            .map { relations ->

                val allIds = relations
                    .flatMap { it.items }
                    .map { it.itemId }
                    .distinct()

                val coursesMap = if (allIds.isNotEmpty()) {
                    courseDao.getCoursesByIds(allIds)
                        .associateBy { it.id }
                } else emptyMap()

                relations.mapNotNull { relation ->
                    mapToDomain(relation, coursesMap)
                }
            }
    }

    override suspend fun refresh(): Result<Unit> {
        return runCatchingCancellable {

            val blocks = catalogApi.getCatalogBlocks().blocks
                .filter { it.kind in allowedKinds }
                .distinctBy { it.title to it.kind }

            val sectionEntities = blocks.mapIndexed { index, block ->
                catalogMapper.toSectionEntity(block, index)
            }

            val itemEntities = blocks.flatMapIndexed { sectionIndex, block ->
                catalogMapper.toSectionItems(block, sectionIndex)
            }

            val previewIds = itemEntities
                .groupBy { it.sectionPosition }
                .values
                .flatMap { items ->
                    items.sortedBy { it.position }.take(previewLimit)
                }
                .map { it.itemId }
                .distinct()
                .take(300)

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
                val domain = courseMapper.fromDto(
                    dto,
                    reviewSummary = null
                )
                courseMapper.toEntity(
                    course = domain,
                    source = CourseSource.CATALOG,
                    page = null,
                    query = null
                )
            }

            if (courseEntities.isNotEmpty()) {
                courseDao.insertCourses(courseEntities)
            }

            catalogDao.replaceCatalog(sectionEntities, itemEntities)
        }
    }

    private fun mapToDomain(
        relation: SectionWithItems,
        coursesMap: Map<Int, CourseEntity>
    ): CatalogSection? {

        val section = relation.section

        return when (section.type) {

            SectionType.FULL_COURSE_LISTS,
            SectionType.SIMPLE_COURSE_LISTS -> {

                val previewItems = relation.items
                    .sortedBy { it.position }
                    .take(previewLimit)

                val courses = previewItems.mapNotNull { item ->
                    coursesMap[item.itemId]
                        ?.let { courseMapper.fromEntity(it) }
                }

                CatalogSection.CourseList(
                    id = section.id,
                    title = section.title,
                    courses = courses,
                    courseListId = section.courseListId,
                    totalCount = section.totalCount
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
            SectionType.UNKNOWN -> null
        }
    }
}
