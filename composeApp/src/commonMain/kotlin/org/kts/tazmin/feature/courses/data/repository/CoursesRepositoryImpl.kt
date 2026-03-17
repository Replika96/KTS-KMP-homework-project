package org.kts.tazmin.feature.courses.data.repository

import coil3.network.HttpException
import io.github.aakira.napier.Napier
import kotlinx.io.IOException
import org.kts.tazmin.core.common.Source
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
import org.kts.tazmin.feature.courses.presentation.state.CoursesResult
import kotlin.coroutines.cancellation.CancellationException

class CoursesRepositoryImpl(
    private val api: CoursesApi,
    private val courseDao: CourseDao,
    private val courseMapper: CourseMapper
) : CoursesRepository {

    override suspend fun getCourses(
        page: Int,
        pageSize: Int
    ): CoursesResult {

        // пробуем сеть
        try {
            val response = api.getCourses(page, pageSize)

            val reviewMap = loadReviewMap(response.courses)

            val courses = courseMapper.mapToDomainList(response.courses, reviewMap)

            saveCoursesToCache(courses, page, null)
            Napier.d(tag = "getCourses error", message = "Курсы сохранены в кеш")
            return CoursesResult.Success(
                data = CoursesPage(
                    courses = courses,
                    page = response.meta.page,
                    hasNext = response.meta.hasNext
                ),
                source = Source.REMOTE
            )

        } catch (e: Exception) {

            if (e is CancellationException) throw e

            Napier.e("getCourses error", e)

            // fallback на кэш
            val cached = courseDao.getCoursesByPage(page)

            if (cached.isNotEmpty()) {
                val courses = cached.toDomain()
                val nextPageExists = courseDao.getCoursesByPage(page + 1).isNotEmpty()

                return CoursesResult.Error(
                    message = "Ошибка сети. Показаны сохранённые данные",
                    cachedData = CoursesPage(
                        courses = courses,
                        page = page,
                        hasNext = nextPageExists
                    )
                )
            }

            return CoursesResult.Error(
                message = e.message ?: "Ошибка загрузки"
            )
        }
    }

    override suspend fun searchCourses(
        query: String,
        page: Int
    ): CoursesResult {
         try {

            val response = api.searchCourses(query, page)
            val courses = courseMapper.mapToDomainList(response.courses)

            saveCoursesToCache(courses, page, query)

            return CoursesResult.Success(
                data = CoursesPage(
                    courses = courses,
                    page = response.meta.page,
                    hasNext = response.meta.hasNext
                ),
                source = Source.REMOTE
            )
        }  catch (e: Exception) {

             if (e is CancellationException) throw e

             Napier.e("searchCourses error", e)

             if (e is IOException || e is HttpException) {
                 val cached = courseDao.getSearchResults(query, page)

                 if (cached.isNotEmpty()) {
                     val courses = cached.toDomain()
                     val nextPageExists =
                         courseDao.getSearchResults(query, page + 1).isNotEmpty()

                     return CoursesResult.Error(
                         cachedData = CoursesPage(
                             courses = courses,
                             page = page,
                             hasNext = nextPageExists
                         ),
                         message = "Ошибка сети. Показаны сохраненные результаты"
                     )
                 }
             }

             return CoursesResult.Error(
                 message = e.message ?: "Ошибка поиска"
             )
         }
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
