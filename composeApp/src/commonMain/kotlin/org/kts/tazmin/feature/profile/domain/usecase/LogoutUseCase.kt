package org.kts.tazmin.feature.profile.domain.usecase

import io.github.aakira.napier.Napier
import org.kts.tazmin.core.token.TokenStorage
import org.kts.tazmin.feature.auth.domain.repository.AuthRepository
import kotlin.coroutines.cancellation.CancellationException

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) {
    suspend operator fun invoke() {
        tokenStorage.clear()

        try {
            authRepository.logout()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Napier.w("Ошибка при logout на сервере: ${e.message}")
        }
    }
}
