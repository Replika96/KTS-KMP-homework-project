package org.kts.tazmin.feature.courses.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kts.tazmin.feature.courses.domain.entity.Course

@Serializable
data class CoursesResponse(
    val meta: Meta,
    val courses: List<CourseDto>
)

@Serializable
data class Meta(
    val page: Int,
    @SerialName("has_next")
    val hasNext: Boolean,
    @SerialName("has_previous")
    val hasPrevious: Boolean
)

@Serializable
data class CourseDto(
    val id: Int,
    val title: String,
    val cover: String? = null,
    val authors: List<Int>? = null,
    val summary: String? = null,
    @SerialName("learners_count")
    val learnersCount: Int? = null,
    @SerialName("review_summary")
    val reviewSummary: Int? = null,
    @SerialName("display_price")
    val displayPrice: String? = null,
    @SerialName("is_paid")
    val isPaid: Boolean? = null,
    val language: String? = null
)
@Serializable
data class ReviewSummaryDto(
    val id: Int,
    val average: Double? = null,
    val count: Int? = null
)
@Serializable
data class ReviewSummaryResponse(
    @SerialName("course-review-summaries")
    val reviewSummaries: List<ReviewSummaryDto>
)
data class CoursesPage(
    val courses: List<Course>,
    val page: Int,
    val hasNext: Boolean
)
