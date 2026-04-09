package org.kts.tazmin.feature.course_details.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    indices = [
        Index("id")
    ]
)
data class LessonEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val slug: String? = null
)

