package org.kts.tazmin.feature.profile.domain.usecase

import org.kts.tazmin.feature.profile.domain.model.User
import org.kts.tazmin.feature.profile.domain.repository.ProfileRepository

class GetUserUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Result<User>{
        return profileRepository.getCurrentUser()
    }
}