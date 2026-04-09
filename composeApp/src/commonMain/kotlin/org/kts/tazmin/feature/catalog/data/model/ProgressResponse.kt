package org.kts.tazmin.feature.catalog.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProgressResponse(
    @SerialName("progresses")
    val progressDtos: List<ProgressDto>
)

@Serializable
data class ProgressDto(
    val id: String,

    val score: Double,

    val cost: Int,

    @SerialName("n_steps")
    val steps: Int,

    @SerialName("n_steps_passed")
    val stepsPassed: Int,

    @SerialName("is_passed")
    val isPassed: Boolean
)
