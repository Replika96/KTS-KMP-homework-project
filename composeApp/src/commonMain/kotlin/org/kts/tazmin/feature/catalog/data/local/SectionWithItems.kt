package org.kts.tazmin.feature.catalog.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class SectionWithItems(
    @Embedded val section: CatalogSectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sectionId"
    )
    val items: List<CatalogSectionItemEntity>
)
