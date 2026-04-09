package org.kts.tazmin.feature.course_details.data.local.fullEntity

import androidx.room.Embedded
import androidx.room.Relation
import org.kts.tazmin.feature.course_details.data.local.SectionEntity
import org.kts.tazmin.feature.course_details.data.local.UnitEntity
import org.kts.tazmin.feature.course_details.data.local.relation.UnitWithLesson

data class SectionFull(
    @Embedded val section: SectionEntity,
    @Relation(
        entity = UnitEntity::class,
        parentColumn = "id",
        entityColumn = "sectionId"
    )
    val units: List<UnitWithLesson>
)
