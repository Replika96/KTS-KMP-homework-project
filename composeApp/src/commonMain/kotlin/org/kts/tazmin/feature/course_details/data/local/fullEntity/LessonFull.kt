package org.kts.tazmin.feature.course_details.data.local.fullEntity

import androidx.room.Embedded
import androidx.room.Relation
import org.kts.tazmin.feature.course_details.data.local.LessonEntity
import org.kts.tazmin.feature.course_details.data.local.StepEntity

data class LessonFull(
    @Embedded val lesson: LessonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    val steps: List<StepEntity>
)
