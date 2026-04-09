package org.kts.tazmin.feature.course_reviews.domain.model

data class RatingSummary(
    val average: Float,
    val total: Int,
    val counts: Map<Int, Int>
)
