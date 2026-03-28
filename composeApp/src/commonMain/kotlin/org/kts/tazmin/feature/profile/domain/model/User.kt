package org.kts.tazmin.feature.profile.domain.model

import kotlin.text.first

data class  User(
    val id: Int,
    val name: Name,
    val avatarUrl: String?,
    val bio: String?,
    val stats: UserStats,
    val joinedAt: String,
    val isPrivate: Boolean
)

data class Name(
    val first: String,
    val last: String
) {
    val full: String
        get() = "$first $last"
    val initials: String
        get() = buildString {
            if (first.isNotBlank()) append(first.first())
            if (last.isNotBlank()) append(last.first())
        }.ifEmpty { "U" }
}

data class UserStats(
    val knowledge: Int,
    val knowledgeRank: Int,
    val reputation: Int,
    val reputationRank: Int,
    val followers: Int,
    val solvedSteps: Int
)
