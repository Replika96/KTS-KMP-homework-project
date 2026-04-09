package org.kts.tazmin.feature.course_details.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_summary")
data class ReviewSummaryEntity(
    @PrimaryKey val courseId: Int,
    val average: Double,
    val count: Int
)
