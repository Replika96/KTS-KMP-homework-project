package org.kts.tazmin.feature.auth.domain.repository

import org.kts.tazmin.feature.auth.data.model.TokenResponse
import org.kts.tazmin.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun login(code: String): Result<User>
    suspend fun refreshToken(): Result<TokenResponse>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}