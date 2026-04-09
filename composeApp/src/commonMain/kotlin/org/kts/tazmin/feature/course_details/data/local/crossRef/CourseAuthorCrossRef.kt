package org.kts.tazmin.feature.course_details.data.local.crossRef

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["courseId", "userId"],
    indices = [Index("userId"), Index("courseId")]
)
data class CourseAuthorCrossRef(
    val courseId: Int,
    val userId: Int
)
