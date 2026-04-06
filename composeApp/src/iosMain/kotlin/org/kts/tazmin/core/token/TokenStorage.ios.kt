package org.kts.tazmin.core.token

import androidx.datastore.preferences.core.Preferences

//надо разобраться
actual class TokenStorage {

    private var accessToken: String? = null
    private var refreshToken: String? = null

    actual suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    actual suspend fun clear() {
        accessToken = null
        refreshToken = null
    }

    actual suspend fun <T> getToken(key: Preferences.Key<T>): T? {

    }

    actual suspend fun isTokenExpired(): Boolean {

    }

    actual suspend fun isLoggedIn(): Boolean {

    }
}
