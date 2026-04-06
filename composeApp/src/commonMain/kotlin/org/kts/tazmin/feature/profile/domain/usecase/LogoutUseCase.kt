package org.kts.tazmin.feature.profile.domain.usecase

import io.github.aakira.napier.Napier
import org.kts.tazmin.core.token.TokenStorage
import org.kts.tazmin.feature.auth.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        tokenStorage.clear()

        try {
            authRepository.logout()
        } catch (e: Exception) {
            Napier.w("Ошибка при logout на сервере: ${e.message}")
        }
    }
}