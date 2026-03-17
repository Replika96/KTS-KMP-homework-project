package org.kts.tazmin.feature.courses.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val coverUrl: String?,
    val rating: Double,
    val studentsCount: Int,
    val isPaid: Boolean,
    val price: String?,

    val cacheTimestamp: Long,
    val cachePage: Int,
    val cacheQuery: String?
)
