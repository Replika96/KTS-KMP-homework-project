package org.kts.tazmin.feature.catalog.data.local

import androidx.room.Entity

@Entity(tableName = "course_list_page_cache", primaryKeys = ["courseListId"])
data class CourseListPageEntity(
    val courseListId: Int,
    val nextPage: Int?,      // null = больше страниц нет
    val totalCount: Int = 0
)
