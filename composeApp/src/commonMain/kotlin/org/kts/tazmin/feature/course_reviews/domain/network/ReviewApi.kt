package org.kts.tazmin.feature.course_reviews.domain.network

import org.kts.tazmin.feature.course_reviews.data.response.CourseReviewDto
import org.kts.tazmin.feature.course_reviews.data.response.CourseReviewsResponseDto
import org.kts.tazmin.feature.course_reviews.data.response.ReviewVoteRequest

interface ReviewApi {

    suspend fun getCourseReviews(
        courseId: Long,
        page: Int,
        pageSize: Int,
        score: Int? = null
    ): CourseReviewsResponseDto

    suspend fun createReview(
        courseId: Long,
        score: Int,
        text: String
    ): CourseReviewDto

    suspend fun voteReview(
        reviewId: Long,
        vote: ReviewVoteRequest
    ): CourseReviewDto
}
