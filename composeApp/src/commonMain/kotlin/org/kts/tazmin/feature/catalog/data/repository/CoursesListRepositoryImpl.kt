package org.kts.tazmin.feature.catalog.data.repository

import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.feature.catalog.data.local.CourseListPageEntity
import org.kts.tazmin.feature.catalog.data.local.CourseSource
import org.kts.tazmin.feature.catalog.data.local.dao.CourseDao
import org.kts.tazmin.feature.catalog.data.local.dao.CourseListDao
import org.kts.tazmin.feature.catalog.data.local.ref.CourseListCourseRef
import org.kts.tazmin.feature.catalog.domain.mapper.CourseMapper
import org.kts.tazmin.feature.catalog.domain.network.CatalogApi
import org.kts.tazmin.feature.catalog.domain.repository.CoursesListRepository

class CoursesListRepositoryImpl(
    private val courseListDao: CourseListDao,
    private val catalogApi: CatalogApi,
    private val courseMapper: CourseMapper,
    private val courseDao: CourseDao,
) : CoursesListRepository {
    override suspend fun loadNextPage(courseListId: Int): Result<Boolean> =
        runCatchingCancellable {
            val cached = courseListDao.getPage(courseListId)
            val page = cached?.nextPage ?: return@runCatchingCancellable false

            val response = catalogApi.getCoursesByList(courseListId, page)

            val courses = response.courses.map { dto ->
                val domain = courseMapper.fromDto(dto, reviewSummary = null)
                courseMapper.toEntity(
                    domain,
                    source = CourseSource.CATALOG,
                    page = page,
                    query = null
                )
            }

            val refs = response.courses.mapIndexed { index, dto ->
                CourseListCourseRef(
                    courseListId = courseListId,
                    courseId = dto.id,
                    position = (page - 1) * 20 + index
                )
            }

            courseDao.insertCourses(courses)
            courseListDao.insertCourseRefs(refs)
            courseListDao.insertPage(
                CourseListPageEntity(
                    courseListId = courseListId,
                    nextPage = if (response.meta.hasNext) page + 1 else null
                )
            )

            response.meta.hasNext
        }

    override suspend fun refresh(courseListId: Int): Result<Unit> =
        runCatchingCancellable {
            val response = catalogApi.getCoursesByList(courseListId, page = 1)

            val courses = response.courses.map { dto ->
                val domain = courseMapper.fromDto(dto, reviewSummary = null)
                courseMapper.toEntity(domain, source = CourseSource.CATALOG, page = 1, query = null)
            }

            val refs = response.courses.mapIndexed { index, dto ->
                CourseListCourseRef(
                    courseListId = courseListId,
                    courseId = dto.id,
                    position = index
                )
            }

            courseListDao.replaceCourses(courseListId, refs)
            courseDao.insertCourses(courses)
            courseListDao.insertPage(
                CourseListPageEntity(
                    courseListId = courseListId,
                    nextPage = if (response.meta.hasNext) 2 else null
                )
            )
        }
}
