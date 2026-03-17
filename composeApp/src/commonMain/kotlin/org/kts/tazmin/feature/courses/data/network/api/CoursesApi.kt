package org.kts.tazmin.feature.courses.data.network.api


import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.kts.tazmin.core.common.Config.baseUrl
import org.kts.tazmin.feature.courses.data.model.CoursesResponse
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryResponse

class CoursesApi(
    private val client: HttpClient,
) {
    suspend fun getCourses(
        page: Int = 1,
        pageSize: Int = 20
    ): CoursesResponse {
        Napier.d(tag = "CourseApi", message = "Запрос курсов: page=$page, pageSize=$pageSize")

        return try {
            val response: CoursesResponse = client.get("$baseUrl/api/courses") {
                parameter("page", page)
                parameter("page_size", pageSize)
            }.body()

            Napier.d(tag = "CourseApi", message = "Ответ сервера: всего курсов=${response.courses.size}," +
                    " page=${response.meta.page}, hasNext=${response.meta.hasNext}")
            Napier.d("Первые 3 курса: ${response.courses.take(3).map { it.title }}")

            response
        } catch (e: Exception) {
            Napier.e(tag = "CourseApi", message = "Ошибка запроса курсов: $e")
            throw e
        }
    }

    suspend fun searchCourses(
        query: String,
        page: Int = 1,
        pageSize: Int = 20
    ): CoursesResponse {
        return client.get("$baseUrl/api/courses") {
            parameter("search", query)
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
    }

    suspend fun getReviewSummary(id: Int): ReviewSummaryDto {
        val response: ReviewSummaryResponse =
            client.get("$baseUrl/api/course-review-summaries/$id").body()

        return response.reviewSummaries.first()
    }
}
