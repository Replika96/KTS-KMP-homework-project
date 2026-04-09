package org.kts.tazmin.feature.course_details.data.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.kts.tazmin.core.common.Config
import org.kts.tazmin.feature.course_details.data.response.CourseDetailsDto
import org.kts.tazmin.feature.course_details.data.response.CourseDetailsResponse
import org.kts.tazmin.feature.course_details.data.response.EnrollmentDto
import org.kts.tazmin.feature.course_details.data.response.EnrollmentsResponse
import org.kts.tazmin.feature.catalog.data.model.ProgressDto
import org.kts.tazmin.feature.catalog.data.model.ProgressResponse
import org.kts.tazmin.feature.catalog.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.catalog.data.model.ReviewSummaryResponse
import org.kts.tazmin.feature.profile.data.model.UserDto
import org.kts.tazmin.feature.profile.data.model.UserResponse

class CourseInfoApi(
    private val client: HttpClient
) {
    suspend fun getCourse(id: Int): CourseDetailsDto {
        val response: CourseDetailsResponse =
            client.get("${Config.baseUrl}/api/courses/$id").body()
        return response.courses.first()
    }

    suspend fun getReviewSummary(courseId: Int): ReviewSummaryDto? {
        val response: ReviewSummaryResponse =
            client.get("${Config.baseUrl}/api/course-review-summaries/$courseId")
                .body()

        return response.reviewSummaries.firstOrNull()
    }

    suspend fun getEnrollment(courseId: Int): EnrollmentDto? {
        val response: EnrollmentsResponse =
            client.get("${Config.baseUrl}/api/enrollments") {
                parameter("course", courseId)
            }.body()

        return response.enrollments.firstOrNull()
    }

    suspend fun getProgress(progressId: String): ProgressDto? {
        val response: ProgressResponse =
            client.get("${Config.baseUrl}/api/progresses") {
                parameter("ids[]", progressId)
            }.body()

        Napier.i("Progress response: ${response.progressDtos}", tag = "API")
        return response.progressDtos.firstOrNull()
    }

    suspend fun getUsers(ids: List<Int>): List<UserDto> {
        if (ids.isEmpty()) return emptyList()

        val response: UserResponse =
            client.get("${Config.baseUrl}/api/users") {
                ids.forEach { id -> parameter("ids[]", id) }
            }.body()

        return response.users
    }
}
