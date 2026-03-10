package org.kts.tazmin.feature.courses.domain.entity

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val coverUrl: String?,
    val rating: Double,
    val studentsCount: Int
)
