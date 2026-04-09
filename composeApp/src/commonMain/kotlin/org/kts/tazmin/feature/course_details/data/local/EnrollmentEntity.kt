package org.kts.tazmin.feature.course_details.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enrollment")
data class EnrollmentEntity(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val isActive: Boolean
)
