package org.kts.tazmin.feature.course_details.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import org.kts.tazmin.feature.course_details.data.local.SectionEntity
import org.kts.tazmin.feature.course_details.data.local.UnitEntity
import org.kts.tazmin.feature.course_details.data.local.crossRef.SectionUnitCrossRef

data class SectionWithUnits(
    @Embedded val section: SectionEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SectionUnitCrossRef::class,
            parentColumn = "sectionId",
            entityColumn = "unitId"
        )
    )
    val units: List<UnitEntity>
)

