package org.kts.tazmin.feature.courses.domain.entity

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val coverUrl: String?,
    val studentsCount: Int,
    val rating: Double,
    val author: String
)
