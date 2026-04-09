package org.kts.tazmin.feature.course_details.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "units",
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sectionId"),
        Index("lessonId"),
        Index("position")
    ]
)
data class UnitEntity(
    @PrimaryKey val id: Int,
    val sectionId: Int,
    val lessonId: Int?,
    val position: Int? = null
)

