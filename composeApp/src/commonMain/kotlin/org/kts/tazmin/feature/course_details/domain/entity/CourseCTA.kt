package org.kts.tazmin.feature.course_details.domain.entity


data class CourseCTA(
    val isEnrolled: Boolean,
    val isPaid: Boolean,
    val price: String?,
    val discountPrice: String?,
    val discountUntil: String?,
    val progress: Progress?,
    val rating: Double,
    val reviewsCount: Int
)

