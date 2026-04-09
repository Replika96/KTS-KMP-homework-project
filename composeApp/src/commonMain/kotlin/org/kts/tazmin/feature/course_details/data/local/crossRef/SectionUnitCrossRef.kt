package org.kts.tazmin.feature.course_details.data.local.crossRef

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["sectionId", "unitId"],
    tableName = "section_unit_crossref",
    indices = [Index("unitId")]
)
data class SectionUnitCrossRef(
    val sectionId: Int,
    val unitId: Int
)
