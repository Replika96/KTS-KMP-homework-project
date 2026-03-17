package org.kts.tazmin.feature.courses.data.network.api


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.kts.tazmin.feature.courses.data.model.CoursesResponse
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryResponse

class CoursesApi(
    private val client: HttpClient,
    private val baseUrl: String = "https://stepik.org"
) {
    suspend fun getCourses(
        page: Int = 1,
        pageSize: Int = 20
    ): CoursesResponse {
        return client.get("$baseUrl/api/courses") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
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
