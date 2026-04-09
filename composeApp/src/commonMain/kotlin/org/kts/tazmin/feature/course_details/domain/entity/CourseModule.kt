package org.kts.tazmin.feature.course_details.domain.entity

data class CourseModule(
    val id: Int,
    val title: String,
    val lessons: List<CourseLesson>,
    val progress: Float
)
