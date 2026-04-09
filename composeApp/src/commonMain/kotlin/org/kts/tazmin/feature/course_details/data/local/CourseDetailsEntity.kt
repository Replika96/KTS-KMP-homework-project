package org.kts.tazmin.feature.course_details.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "course")
data class CourseDetailsEntity(
    @PrimaryKey val id: Int,

    val title: String,
    val summary: String?,
    val description: String?,
    val coverUrl: String?,
    val workload: String?,

    val isEnrolled: Boolean,
    val learnersCount: Int,

    val displayPrice: String?,
    val isPaid: Boolean,
    val discountPrice: String?,
    val discountDeadline: String?,

    val isVerified: Boolean,

    val certificateAvailable: Boolean,
    val certificateDescription: String?,

    val language: String?,

    val reviewSummaryId: Int?,

    val progressId: String?,

    // actions
    val isFavorite: Boolean,
    val shareUrl: String?,

    val lastUpdated: Long,

    val requirements: String?,
    val targetAudience: String?,

    val sectionIds: String = ""
)
