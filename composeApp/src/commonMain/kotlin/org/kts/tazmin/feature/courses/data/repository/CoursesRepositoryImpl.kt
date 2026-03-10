package org.kts.tazmin.feature.courses.data.repository

import io.github.aakira.napier.Napier
import org.kts.tazmin.core.network.HttpClientFactory
import org.kts.tazmin.feature.courses.data.mapper.CourseMapper
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.data.model.CoursesResponse
import org.kts.tazmin.feature.courses.data.model.Meta
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.domain.entity.Course
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

            val courses = CourseMapper.mapToDomainList(response.courses)

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
