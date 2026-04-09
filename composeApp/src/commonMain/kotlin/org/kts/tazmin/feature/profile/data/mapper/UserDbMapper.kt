package org.kts.tazmin.feature.profile.data.mapper

import org.kts.tazmin.feature.profile.data.local.UserEntity
import org.kts.tazmin.feature.profile.domain.model.Name
import org.kts.tazmin.feature.profile.domain.model.User
import org.kts.tazmin.feature.profile.domain.model.UserStats

class UserDbMapper {

    fun toEntity(user: User): UserEntity {

        return UserEntity(
            id = user.id,
            firstName = user.name.first,
            lastName = user.name.last,
            avatarUrl = user.avatarUrl,
            bio = user.bio,
            knowledge = user.stats.knowledge,
            knowledgeRank = user.stats.knowledgeRank,
            reputation = user.stats.reputation,
            reputationRank = user.stats.reputationRank,
            followers = user.stats.followers,
            solvedSteps = user.stats.solvedSteps,
            joinedAt = user.joinedAt,
            isPrivate = user.isPrivate
        )
    }

    fun toDomain(entity: UserEntity): User {
        return User(
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
            isPrivate = entity.isPrivate
        )
    }
}
