package org.kts.tazmin.core.token

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

actual class TokenStorage actual constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object TokenKeys {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
    }

    actual suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[EXPIRES_AT_KEY] = Clock.System.now().toEpochMilliseconds() + expiresIn * 1000
        }
    }

    private var cachedToken: String? = null
    actual suspend fun getAccessToken(): String? {
        if (cachedToken != null) return cachedToken

        cachedToken = dataStore.data.first()[ACCESS_TOKEN_KEY]
        return cachedToken
    }

    actual suspend fun getRefreshToken(): String? {
        cachedToken = null
        return dataStore.data.first()[REFRESH_TOKEN_KEY]
    }

    actual suspend fun getExpiresAt(): Long? {
        return dataStore.data.map { prefs ->
            prefs[EXPIRES_AT_KEY]
        }.first()
    }

    actual suspend fun isTokenExpired(): Boolean {
        val expiresAt = getExpiresAt() ?: return true
        return Clock.System.now().toEpochMilliseconds() >= expiresAt
    }

    actual suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    actual suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}