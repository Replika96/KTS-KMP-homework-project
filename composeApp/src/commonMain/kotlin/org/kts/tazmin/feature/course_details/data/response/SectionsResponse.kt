package org.kts.tazmin.feature.course_details.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SectionsResponse(
    val sections: List<SectionDto>
)

@Serializable
data class SectionDto(
    val id: Int,
    val title: String? = null,
    @SerialName("units") val unitIds: List<Int> = emptyList(),
    val position: Int
)
