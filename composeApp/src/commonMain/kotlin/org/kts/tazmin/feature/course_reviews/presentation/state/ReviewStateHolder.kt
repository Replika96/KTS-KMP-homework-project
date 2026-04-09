package org.kts.tazmin.feature.course_reviews.presentation.state

import org.kts.tazmin.feature.course_reviews.domain.model.RatingSummary
import org.kts.tazmin.feature.course_reviews.presentation.viewmodel.ReviewViewModel

data class ReviewStateHolder(
    val state: ReviewUiState,
    val summary: RatingSummary,
    val viewModel: ReviewViewModel
)
