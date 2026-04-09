package org.kts.tazmin.feature.course_details.domain.entity

data class Progress(
    val steps: Int,
    val stepsPassed: Int,
    val isPassed: Boolean
) {
    val percent: Int get() =
        if (steps == 0) 0 else ((stepsPassed.toFloat() / steps) * 100).toInt()
}

