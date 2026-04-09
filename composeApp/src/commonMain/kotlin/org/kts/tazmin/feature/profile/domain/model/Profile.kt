package org.kts.tazmin.feature.profile.domain.model

data class Profile(
    val id: Int,
    val name: Name,
    val avatarUrl: String?,
    val bio: String?,
    val stats: UserStats,
    val joinedAt: String,
    val joinedAtFormatted: String = "–",
    val isPrivate: Boolean
)
