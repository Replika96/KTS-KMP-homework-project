package org.kts.tazmin.feature.courses.data.repository

import io.github.aakira.napier.Napier
import org.kts.tazmin.feature.courses.data.mapper.CourseMapper
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository

class CoursesRepositoryImpl(
    private val api: CoursesApi
) : CoursesRepository {

    override suspend fun getCourses(
        page: Int,
        pageSize: Int
    ): Result<CoursesPage> {
        return try {

            val response = api.getCourses(page, pageSize)
            val reviewMap: Map<Int, ReviewSummaryDto> = response.courses.mapNotNull { dto ->
                val review = dto.reviewSummary?.let { api.getReviewSummary(it) }
                if (review != null) dto.id to review else null
            }.toMap()
            val courses = CourseMapper.mapToDomainList(response.courses, reviewMap)

            val result = CoursesPage(
                courses = courses,
                page = response.meta.page,
                hasNext = response.meta.hasNext
            )

            Result.success(result)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchCourses(
        query: String,
        page: Int
    ): Result<CoursesPage> {

        Napier.d(tag = "Repository", message = "Поиск: '$query', page=$page")

        return try {

            val response = api.searchCourses(query, page)

            val courses = CourseMapper.mapToDomainList(response.courses)

            val result = CoursesPage(
                courses = courses,
                page = response.meta.page,
                hasNext = response.meta.hasNext
            )

            Result.success(result)

        } catch (e: Exception) {

            Napier.e(tag = "Repository", message = "Ошибка поиска $e")

            Result.failure(e)

        }
    }
}
