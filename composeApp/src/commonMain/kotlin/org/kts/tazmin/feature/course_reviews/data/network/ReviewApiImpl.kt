package org.kts.tazmin.feature.course_reviews.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.kts.tazmin.core.common.Config
import org.kts.tazmin.feature.course_reviews.data.response.CourseReviewDto
import org.kts.tazmin.feature.course_reviews.data.response.CourseReviewsResponseDto
import org.kts.tazmin.feature.course_reviews.data.response.ReviewVoteRequest
import org.kts.tazmin.feature.course_reviews.domain.network.ReviewApi

class ReviewApiImpl(
    private val client: HttpClient
) : ReviewApi {

    override suspend fun getCourseReviews(
        courseId: Long,
        page: Int,
        pageSize: Int,
        score: Int?
    ): CourseReviewsResponseDto {

        return client.get("${Config.baseUrl}/api/course-reviews") {
            parameter("course", courseId)
            parameter("page", page)
            parameter("page_size", pageSize)

            score?.let {
                parameter("score", it)
            }
        }.body()
    }

    override suspend fun createReview(
        courseId: Long,
        score: Int,
        text: String
    ): CourseReviewDto {

        return client.post("${Config.baseUrl}/api/course-reviews") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "course" to courseId,
                    "score" to score,
                    "text" to text
                )
            )
        }.body()
    }

    override suspend fun voteReview(
        reviewId: Long,
        vote: ReviewVoteRequest
    ): CourseReviewDto {

        return client.post("${Config.baseUrl}/api/course-reviews/$reviewId/vote") {
            contentType(ContentType.Application.Json)
            setBody(vote)
        }.body()
    }
}
