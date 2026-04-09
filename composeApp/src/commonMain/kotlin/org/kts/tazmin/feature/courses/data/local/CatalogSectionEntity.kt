package org.kts.tazmin.feature.courses.data.local

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
    val url: String? = null
)

enum class SectionType {
    FULL_COURSE_LISTS,
    SIMPLE_COURSE_LISTS,
    BANNER
}
