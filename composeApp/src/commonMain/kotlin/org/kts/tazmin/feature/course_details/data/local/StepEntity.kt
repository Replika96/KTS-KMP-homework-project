package org.kts.tazmin.feature.course_details.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "steps",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("lessonId")
    ]
)
data class StepEntity(
    @PrimaryKey val id: Int,
    val lessonId: Int,
    val name: String,
    val text: String?
)



