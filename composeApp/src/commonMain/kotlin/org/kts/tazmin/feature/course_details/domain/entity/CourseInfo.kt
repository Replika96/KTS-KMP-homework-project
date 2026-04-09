package org.kts.tazmin.feature.course_details.domain.entity

import org.kts.tazmin.feature.profile.domain.model.User

data class CourseInfo(
    val description: String,
    val summary: String?,
    val requirements: List<String>,
    val targetAudience: List<String>,
    val duration: String?,
    val language: String?,
    val certificateAvailable: Boolean,
    val certificateDescription: String?,
    val learnersCount: Int,
    val authors: List<User>,
    val coverUrl: String?,
    val instructors: List<User>
)
