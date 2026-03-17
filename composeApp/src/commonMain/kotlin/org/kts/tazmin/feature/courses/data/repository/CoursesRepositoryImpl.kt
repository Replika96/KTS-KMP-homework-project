package org.kts.tazmin.feature.courses.data.repository

import coil3.network.HttpException
import io.github.aakira.napier.Napier
import kotlinx.io.IOException
import org.kts.tazmin.feature.courses.data.local.CourseDao
import org.kts.tazmin.feature.courses.data.mapper.CourseDbMapper.toDomain
import org.kts.tazmin.feature.courses.data.mapper.CourseDbMapper.toEntity
import org.kts.tazmin.feature.courses.data.mapper.CourseMapper
import org.kts.tazmin.feature.courses.data.model.CourseDto
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.domain.entity.Course
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import kotlin.coroutines.cancellation.CancellationException

class CoursesRepositoryImpl(
    private val api: CoursesApi,
    private val courseDao: CourseDao,
    private val courseMapper: CourseMapper
) : CoursesRepository {

    override suspend fun getCourses(
        page: Int,
        pageSize: Int
    ): Result<CoursesPage> = try {
        runCatching {
            val response = api.getCourses(page, pageSize)

            val reviewMap = loadReviewMap(response.courses)

            val courses = courseMapper.mapToDomainList(response.courses, reviewMap)

            saveCoursesToCache(courses, page, null)

            CoursesPage(
                courses = courses,
                page = response.meta.page,
                hasNext = response.meta.hasNext
            )
        }.recoverCatching { e ->
            if (e is CancellationException) throw e

            if (e is IOException || e is HttpException) {
                val cached = courseDao.getCoursesByPage(page)
                if (cached.isNotEmpty()) {
                    val courses = cached.toDomain()
                    val nextPageExists = courseDao.getCoursesByPage(page + 1).isNotEmpty()

                    return@recoverCatching CoursesPage(
                        courses = courses,
                        page = page,
                        hasNext = nextPageExists
                    )
                }
            }
            throw e
        }
    } catch (e: CancellationException) {
        throw e
    }

    override suspend fun searchCourses(
        query: String,
        page: Int
    ): Result<CoursesPage> = runCatching {
        val response = api.searchCourses(query, page)
        val courses = courseMapper.mapToDomainList(response.courses)

        saveCoursesToCache(courses, page, query)

        CoursesPage(
            courses = courses,
            page = response.meta.page,
            hasNext = response.meta.hasNext
        )
    }.recoverCatching { e ->
        if (e is CancellationException) throw e

        if (e is IOException || e is HttpException) {
            val cached = courseDao.getSearchResults(query, page)
            if (cached.isNotEmpty()) {
                val courses = cached.toDomain()
                val nextPageExists = courseDao.getSearchResults(query, page + 1).isNotEmpty()

                return@recoverCatching CoursesPage(
                    courses = courses,
                    page = page,
                    hasNext = nextPageExists
                )
            }
        }
        throw e
    }

    private suspend fun loadReviewMap(courseDtos: List<CourseDto>): Map<Int, ReviewSummaryDto> {
        return try {
            courseDtos
                .mapNotNull { dto ->
                    dto.reviewSummary?.let { reviewId ->
                        runCatching {
                            api.getReviewSummary(reviewId)
                        }.getOrNull()?.let { review ->
                            dto.id to review
                        }
                    }
                }.toMap()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Napier.w("Не удалось загрузить некоторые отзывы: ${e.message}")
            emptyMap()
        }
    }

    private suspend fun saveCoursesToCache(
        courses: List<Course>,
        page: Int,
        query: String?
    ) {
        if (query == null) {
            courseDao.clearPage(page)
        } else {
            courseDao.clearSearch(query)
        }

        val entities = courses.map { course ->
            course.toEntity(page, query)
        }

        courseDao.insertCourses(entities)
    }
}
