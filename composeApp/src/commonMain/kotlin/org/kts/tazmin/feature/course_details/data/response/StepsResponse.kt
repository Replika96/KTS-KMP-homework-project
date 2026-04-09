package org.kts.tazmin.feature.course_details.data.response

import kotlinx.serialization.Serializable

@Serializable
data class StepsResponse(
    val steps: List<StepDto>
)

@Serializable
data class StepDto(
    val id: Int,
    val block: StepBlockDto
)

@Serializable
data class StepBlockDto(
    val name: String = "unknown",
    val text: String? = null
)
