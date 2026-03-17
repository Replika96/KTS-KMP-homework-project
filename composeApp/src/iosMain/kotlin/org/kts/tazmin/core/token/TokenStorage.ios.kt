package org.kts.tazmin.core.token
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

    actual suspend fun getAccessToken(): String? {
        return accessToken
    }

    actual suspend fun getRefreshToken(): String? {
        return refreshToken
    }

    actual suspend fun clear() {
        accessToken = null
        refreshToken = null
    }
}