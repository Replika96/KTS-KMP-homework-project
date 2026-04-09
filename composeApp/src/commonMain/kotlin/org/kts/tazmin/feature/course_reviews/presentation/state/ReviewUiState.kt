package org.kts.tazmin.feature.course_reviews.presentation.state

import org.kts.tazmin.feature.course_reviews.domain.model.Review

data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
    val selectedScore: Int? = null
)
