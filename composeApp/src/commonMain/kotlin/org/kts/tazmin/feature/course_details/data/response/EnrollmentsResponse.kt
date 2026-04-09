package org.kts.tazmin.feature.course_details.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnrollmentsResponse(
    val enrollments: List<EnrollmentDto>
)

@Serializable
data class EnrollmentDto(
    val id: Int,
    @SerialName("course") val courseId: Int,
    @SerialName("is_active") val isActive: Boolean
)
