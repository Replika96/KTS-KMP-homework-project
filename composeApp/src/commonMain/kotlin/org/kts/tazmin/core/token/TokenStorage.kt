package org.kts.tazmin.core.token

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect class TokenStorage(
    dataStore: DataStore<Preferences>
) {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    )

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun getExpiresAt(): Long?

    suspend fun isTokenExpired(): Boolean
    suspend fun clear()

    suspend fun isLoggedIn(): Boolean
}