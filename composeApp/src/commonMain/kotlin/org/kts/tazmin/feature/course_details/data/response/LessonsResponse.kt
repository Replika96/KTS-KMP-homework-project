package org.kts.tazmin.feature.course_details.data.response

import kotlinx.serialization.Serializable

@Serializable
data class LessonsResponse(
    val lessons: List<LessonDto>
)

@Serializable
data class LessonDto(
    val id: Int,
    val title: String? = null,
    val steps: List<Int> = emptyList()
)
