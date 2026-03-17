package org.kts.tazmin.feature.profile.data.mapper

import org.kts.tazmin.feature.profile.data.local.UserEntity
import org.kts.tazmin.feature.profile.domain.model.Name
import org.kts.tazmin.feature.profile.domain.model.User
import org.kts.tazmin.feature.profile.domain.model.UserStats

object UserDbMapper {

    fun User.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            firstName = name.first,
            lastName = name.last,
            avatarUrl = avatarUrl,
            bio = bio,
            knowledge = stats.knowledge,
            knowledgeRank = stats.knowledgeRank,
            reputation = stats.reputation,
            reputationRank = stats.reputationRank,
            followers = stats.followers,
            solvedSteps = stats.solvedSteps,
            joinedAt = joinedAt,
            isPrivate = isPrivate
        )
    }

    fun UserEntity.toDomain(): User {
        return User(
            id = id,
            name = Name(firstName, lastName),
            avatarUrl = avatarUrl,
            bio = bio,
            stats = UserStats(
                knowledge = knowledge,
                knowledgeRank = knowledgeRank,
                reputation = reputation,
                reputationRank = reputationRank,
                followers = followers,
                solvedSteps = solvedSteps
            ),
            joinedAt = joinedAt,
            isPrivate = isPrivate
        )
    }
}