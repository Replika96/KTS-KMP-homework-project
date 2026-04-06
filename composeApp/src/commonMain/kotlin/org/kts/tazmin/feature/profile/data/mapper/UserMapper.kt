package org.kts.tazmin.feature.profile.data.mapper

import org.kts.tazmin.feature.profile.data.model.UserDto
import org.kts.tazmin.feature.profile.domain.model.Name
import org.kts.tazmin.feature.profile.domain.model.User
import org.kts.tazmin.feature.profile.domain.model.UserStats

object UserMapper {

    fun mapToDomain(dto: UserDto): User {
        return User(
            id = dto.id,

            name = Name(
                first = dto.firstName?: "",
                last = dto.lastName?: ""
            ),

            avatarUrl = dto.avatar?.takeIf { it.isNotBlank() },

            bio = dto.shortBio?.takeIf { it.isNotBlank() },

            stats = UserStats(
                knowledge = dto.knowledge,
                knowledgeRank = dto.knowledgeRank?: 0,
                reputation = dto.reputation,
                reputationRank = dto.reputationRank?: 0,
                followers = dto.followersCount,
                solvedSteps = dto.solvedStepsCount?: 0
            ),

            joinedAt = dto.joinDate?: "",

            isPrivate = dto.isPrivate
        )
    }

    fun mapToDomainList(dtos: List<UserDto>): List<User> {
        return dtos.map(::mapToDomain)
    }
}