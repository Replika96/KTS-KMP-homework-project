package org.kts.tazmin.feature.course_details.data.local.crossRef

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["lessonId", "stepId"],
    tableName = "lesson_step_crossref",
    indices = [Index("stepId")]
)
data class LessonStepCrossRef(
    val lessonId: Int,
    val stepId: Int
)

