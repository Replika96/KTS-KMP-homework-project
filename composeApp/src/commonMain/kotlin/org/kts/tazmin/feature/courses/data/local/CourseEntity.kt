package org.kts.tazmin.feature.courses.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val coverUrl: String?,
    val studentsCount: Int,
    val description: String,
    val author: String,
    val rating: Double?,
    val isPaid: Boolean,
    val price: String?,
    val progress: Float?,
    val score: Int?,
    val cost: Int?,

    // для пагинации
    val cachePage: Int? = null,
    val cacheQuery: String? = null,

    // для каталога
    val source: CourseSource? = null
)

enum class CourseSource {
    CATALOG, SEARCH, PAGINATION
}
