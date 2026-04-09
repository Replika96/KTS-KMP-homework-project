package org.kts.tazmin.feature.catalog.data.local.ref

import androidx.room.Entity

@Entity(tableName = "course_list_courses", primaryKeys = ["courseListId", "courseId"])
data class CourseListCourseRef(
    val courseListId: Int,
    val courseId: Int,
    val position: Int
)
