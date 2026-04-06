package org.kts.tazmin.feature.auth.data.repository

import org.kts.tazmin.core.token.TokenStorage
import org.kts.tazmin.feature.auth.data.model.TokenResponse
import org.kts.tazmin.feature.auth.data.network.api.AuthApi
import org.kts.tazmin.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(code: String): Result<Unit> {
        return try {
            val response = authApi.getAccessToken(code)

            tokenStorage.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn
            )

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(): Result<TokenResponse> {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: return Result.failure(Exception("No refresh token"))

        return try {
            val response = authApi.refreshAccessToken(refreshToken)
            tokenStorage.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn
            )
            Result.success(response)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenStorage.clear()
    }
}
