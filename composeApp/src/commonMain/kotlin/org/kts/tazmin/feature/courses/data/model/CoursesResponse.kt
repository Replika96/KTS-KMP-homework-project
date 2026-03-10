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
    val summary: String? = null,
    val cover: String? = null,
    val authors: List<Int>? = null,
    @SerialName("learners_count")
    val learnersCount: Int? = null,
    @SerialName("average_rating")
    val averageRating: Double? = null,
    val language: String? = null
)

data class CoursesPage(
    val courses: List<Course>,
    val page: Int,
    val hasNext: Boolean
)
