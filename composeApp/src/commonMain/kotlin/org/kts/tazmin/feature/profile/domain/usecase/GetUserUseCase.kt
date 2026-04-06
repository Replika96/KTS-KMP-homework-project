package org.kts.tazmin.feature.profile.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.profile.domain.model.User
import org.kts.tazmin.feature.profile.domain.repository.ProfileRepository

class GetUserUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<Resource<User>> {
        return profileRepository.getCurrentUser()
    }
}
