package org.kts.tazmin.feature.courses.data.model

import org.kts.tazmin.feature.courses.domain.entity.Course

data class CoursesResponse(
    val meta: Meta,
    val courses: List<Course>
)
data class Meta(
    val page: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
