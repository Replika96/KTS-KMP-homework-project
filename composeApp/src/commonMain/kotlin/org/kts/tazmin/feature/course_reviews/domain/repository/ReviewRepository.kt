package org.kts.tazmin.feature.course_reviews.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_reviews.domain.model.Review

interface ReviewRepository {
    fun observeReviews(courseId: Long, score: Int?): Flow<List<Review>>
    suspend fun loadNextPage(courseId: Long, score: Int?): Result<Boolean> // Boolean = hasMore
    suspend fun refreshReviews(courseId: Long, score: Int?): Result<Boolean>
    suspend fun createReview(courseId: Long, score: Int, text: String): Result<Unit>
    suspend fun voteReview(reviewId: Long, vote: String): Result<Unit>
}
