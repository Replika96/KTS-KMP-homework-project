package org.kts.tazmin.feature.course_details.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnitsResponse(
    val units: List<UnitDto>
)

@Serializable
data class UnitDto(
    val id: Int,
    @SerialName("lesson") val lessonId: Int? = null,
    val position: Int? = null
)
