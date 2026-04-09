package org.kts.tazmin.feature.profile.data.mapper

import org.kts.tazmin.feature.profile.data.local.ProfileEntity
import org.kts.tazmin.feature.profile.domain.model.Name
import org.kts.tazmin.feature.profile.domain.model.Profile
import org.kts.tazmin.feature.profile.domain.model.UserStats

class ProfileDbMapper {

    fun toEntity(profile: Profile): ProfileEntity {
        return ProfileEntity(
            id = profile.id,
            firstName = profile.name.first,
            lastName = profile.name.last,
            avatarUrl = profile.avatarUrl,
            bio = profile.bio,
            knowledge = profile.stats.knowledge,
            knowledgeRank = profile.stats.knowledgeRank,
            reputation = profile.stats.reputation,
            reputationRank = profile.stats.reputationRank,
            followers = profile.stats.followers,
            solvedSteps = profile.stats.solvedSteps,
            joinedAt = profile.joinedAt,
            isPrivate = profile.isPrivate
        )
    }

    fun toDomain(entity: ProfileEntity): Profile {
        return Profile(
            id = entity.id,
            name = Name(entity.firstName, entity.lastName),
            avatarUrl = entity.avatarUrl,
            bio = entity.bio,
            stats = UserStats(
                knowledge = entity.knowledge,
                knowledgeRank = entity.knowledgeRank,
                reputation = entity.reputation,
                reputationRank = entity.reputationRank,
                followers = entity.followers,
                solvedSteps = entity.solvedSteps
            ),
            joinedAt = entity.joinedAt,
            isPrivate = entity.isPrivate,
        )
    }
}
