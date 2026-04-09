package org.kts.tazmin.feature.catalog.data.network.api


import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.kts.tazmin.core.common.Config.baseUrl
import org.kts.tazmin.feature.catalog.data.model.CourseDto
import org.kts.tazmin.feature.catalog.data.model.CoursesResponse
import org.kts.tazmin.feature.catalog.data.model.ProgressDto
import org.kts.tazmin.feature.catalog.data.model.ProgressResponse
import org.kts.tazmin.feature.catalog.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.catalog.data.model.ReviewSummaryResponse
import org.kts.tazmin.feature.catalog.domain.network.CoursesApi

class CoursesApiImpl(
    private val client: HttpClient,
): CoursesApi {
    override suspend fun getCourses(
        page: Int,
        pageSize: Int
    ): CoursesResponse {
        Napier.d(tag = "CourseApi", message = "Запрос курсов: page=$page, pageSize=$pageSize")

        return try {
            val response: CoursesResponse = client.get("$baseUrl/api/courses") {
                parameter("page", page)
                parameter("page_size", pageSize)
            }.body()

            Napier.d(
                tag = "CourseApi",
                message = "Ответ сервера: всего курсов=${response.courses.size}," +
                        " page=${response.meta.page}, hasNext=${response.meta.hasNext}"
            )

            Napier.d("Первые 3 курса: ${response.courses.take(3).map { it.title }}")

            response
        } catch (e: Exception) {
            Napier.e(tag = "CourseApi", message = "Ошибка запроса курсов: $e")
            throw e
        }
    }

    override suspend fun searchCourses(
        query: String,
        page: Int,
        pageSize: Int
    ): CoursesResponse {
        return client.get("$baseUrl/api/courses") {
            parameter("search", query)
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
    }

    override suspend fun getReviewSummary(id: Int): ReviewSummaryDto {
        val response: ReviewSummaryResponse =
            client.get("$baseUrl/api/course-review-summaries/$id").body()

        return response.reviewSummaries.first()
    }

    override suspend fun getUserCourses(): List<Pair<CourseDto, ProgressDto?>> {

        val coursesResponse: CoursesResponse = client.get("$baseUrl/api/courses") {
            parameter("enrolled", true)
        }.body()

        val courseDtos = coursesResponse.courses

        Napier.d("Courses size = ${courseDtos.size}")
        Napier.d("Progress IDs = ${courseDtos.map { it.progress }}")

        val progressIds = courseDtos.mapNotNull { it.progress }


        val progressesMap: Map<String, ProgressDto> =
            if (progressIds.isNotEmpty()) {
                val response: ProgressResponse = client.get("$baseUrl/api/progresses") {
                    progressIds.forEach { id ->
                        parameter("ids[]", id)
                    }
                }.body()

                Napier.d("Progress response size = ${response.progressDtos.size}")

                response.progressDtos.associateBy { it.id }
            } else emptyMap()
        return courseDtos.map { dto ->
            val progress = dto.progress?.let { progressesMap[it] }
            dto to progress
        }
    }
}
