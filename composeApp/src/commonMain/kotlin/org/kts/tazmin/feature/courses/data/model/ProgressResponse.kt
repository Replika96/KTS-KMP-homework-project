package org.kts.tazmin.feature.courses.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProgressResponse(
    @SerialName("progresses")
    val progresses: List<Progress>
)

@Serializable
data class Progress(
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
