package org.kts.tazmin.feature.catalog.domain.entity

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val coverUrl: String?,
    val rating: Double,
    val studentsCount: Int,
    val isPaid: Boolean,
    val price: String?,
    val progress: Float? = null,
    val score: Int? = null,
    val cost: Int? = null
)
