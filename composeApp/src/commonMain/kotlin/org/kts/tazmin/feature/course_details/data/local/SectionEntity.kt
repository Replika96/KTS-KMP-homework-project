package org.kts.tazmin.feature.course_details.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sections",
    indices = [
        Index("courseId"),
        Index("position")
    ]
)
data class SectionEntity(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val title: String,
    val position: Int? = null
)

