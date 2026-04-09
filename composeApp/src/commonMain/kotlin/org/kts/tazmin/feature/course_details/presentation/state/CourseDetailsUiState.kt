package org.kts.tazmin.feature.course_details.presentation.state

import org.kts.tazmin.feature.course_details.domain.entity.CourseCTA
import org.kts.tazmin.feature.course_details.domain.entity.CourseHeader
import org.kts.tazmin.feature.course_details.domain.entity.CourseInfo
import org.kts.tazmin.feature.course_details.domain.entity.CourseModule

data class CourseDetailsUiState(
    val header: CourseHeader? = null,
    val info: CourseInfo? = null,
    val cta: CourseCTA? = null,
    val modules: List<CourseModule> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)
