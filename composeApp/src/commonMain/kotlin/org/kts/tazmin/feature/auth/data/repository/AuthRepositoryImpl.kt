package org.kts.tazmin.feature.auth.data.repository

import org.kts.tazmin.core.token.TokenStorage
import org.kts.tazmin.feature.auth.data.model.TokenResponse
import org.kts.tazmin.feature.auth.data.remote.AuthApi
import org.kts.tazmin.feature.auth.domain.model.User
import org.kts.tazmin.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
) : AuthRepository {

    override suspend fun login(code: String): Result<User> {
        return try {
            val response = authApi.getAccessToken(code)

            TokenStorage.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn
            )

            Result.success(User(id = 1, name = "User")) //TODO

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(): Result<TokenResponse> {
        val refreshToken = TokenStorage.getRefreshToken()
            ?: return Result.failure(Exception("No refresh token"))

        return try {
            val response = authApi.refreshAccessToken(refreshToken)
            TokenStorage.saveTokens(
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
        TokenStorage.clear()
    }

    override suspend fun isLoggedIn(): Boolean {
        return TokenStorage.isTokenValid()
    }
}