package org.kts.tazmin.feature.profile.domain.repository

import org.kts.tazmin.feature.profile.domain.model.User

interface ProfileRepository {
    suspend fun getCurrentUser(): Result<User>
}