package org.kts.tazmin.feature.profile.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.profile.domain.model.Profile
import org.kts.tazmin.feature.profile.domain.repository.ProfileRepository

class GetUserUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<Resource<Profile>> {
        return profileRepository.getCurrentUser()
    }
}
