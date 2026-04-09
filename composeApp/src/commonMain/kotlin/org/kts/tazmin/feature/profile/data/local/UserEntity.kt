package org.kts.tazmin.feature.profile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int,

    val firstName: String,
    val lastName: String,

    val avatarUrl: String?,
    val bio: String?,

    val knowledge: Int,
    val knowledgeRank: Int,
    val reputation: Int,
    val reputationRank: Int,
    val followers: Int,
    val solvedSteps: Int,

    val joinedAt: String,
    val isPrivate: Boolean
)
