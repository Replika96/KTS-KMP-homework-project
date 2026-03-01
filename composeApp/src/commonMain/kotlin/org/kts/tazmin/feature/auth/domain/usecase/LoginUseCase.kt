package org.kts.tazmin.feature.auth.domain.usecase

import org.kts.tazmin.feature.auth.domain.repository.LoginRepository

class LoginUseCase(private val loginRepository: LoginRepository) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> {
        // пока простой usecase
        return loginRepository.login(username, password)
    }
}
