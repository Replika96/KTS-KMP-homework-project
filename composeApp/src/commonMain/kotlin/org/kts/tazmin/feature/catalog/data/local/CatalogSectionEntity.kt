package org.kts.tazmin.feature.catalog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_sections")
data class CatalogSectionEntity(

    @PrimaryKey(autoGenerate = false)
    val id: Int = 0, // API ID

    val title: String,

    val type: SectionType, // full_course_lists, simple_course_lists, banner

    val position: Int,

    val cover: String? = null,
    val url: String? = null,
    val courseListId: Int? = null,
    val totalCount: Int = 0
)

enum class SectionType {
    FULL_COURSE_LISTS,
    SIMPLE_COURSE_LISTS,
    BANNER,
    UNKNOWN
    //UNKNOWN
}
