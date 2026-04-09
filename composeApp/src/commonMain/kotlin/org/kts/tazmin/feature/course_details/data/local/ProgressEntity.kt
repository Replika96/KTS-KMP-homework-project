package org.kts.tazmin.feature.course_details.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val id: String,

    val score: Double,
    val cost: Int,

    val steps: Int,
    val stepsPassed: Int,
    val isPassed: Boolean
)
