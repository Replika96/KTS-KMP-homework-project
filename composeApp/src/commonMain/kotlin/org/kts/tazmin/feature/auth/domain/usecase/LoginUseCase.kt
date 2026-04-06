package org.kts.tazmin.feature.auth.domain.usecase

import org.kts.tazmin.feature.auth.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(code: String): Result<Unit> {
        // пока простой usecase
        return authRepository.login(code = code)
    }
}
