package org.kts.tazmin.feature.course_details.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import org.kts.tazmin.feature.course_details.data.local.LessonEntity
import org.kts.tazmin.feature.course_details.data.local.UnitEntity

data class UnitWithLesson(
    @Embedded val unit: UnitEntity,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "id"
    )
    val lesson: LessonEntity?
)
