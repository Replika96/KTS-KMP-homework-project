package org.kts.tazmin.feature.catalog.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "catalog_section_items",
    foreignKeys = [
        ForeignKey(
            entity = CatalogSectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sectionId")]
)
data class CatalogSectionItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sectionId: Int,
    val sectionPosition: Int,
    val itemId: Int,       // courseId
    val itemType: String,  // always course
    val position: Int
)
