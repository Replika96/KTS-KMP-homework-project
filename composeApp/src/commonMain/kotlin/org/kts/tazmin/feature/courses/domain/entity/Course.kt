package org.kts.tazmin.feature.courses.domain.entity

import kotlinx.serialization.Serializable

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val coverUrl: String?,
    val rating: Double,
    val studentsCount: Int
)
