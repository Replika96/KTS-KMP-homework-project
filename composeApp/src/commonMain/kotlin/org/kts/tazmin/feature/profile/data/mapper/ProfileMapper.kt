package org.kts.tazmin.feature.profile.data.mapper

import org.kts.tazmin.feature.profile.data.model.ProfileDto
import org.kts.tazmin.feature.profile.domain.model.Name
import org.kts.tazmin.feature.profile.domain.model.Profile
import org.kts.tazmin.feature.profile.domain.model.UserStats

class ProfileMapper {

    fun mapToDomain(dto: ProfileDto): Profile {
        return Profile(
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
}
