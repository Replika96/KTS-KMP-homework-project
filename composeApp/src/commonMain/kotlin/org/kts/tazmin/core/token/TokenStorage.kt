package org.kts.tazmin.core.token

import kotlin.time.Clock

object TokenStorage {
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var expiresAt: Long = 0

    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.expiresAt = Clock.System.now().toEpochMilliseconds() + expiresIn * 1000
        println("Токены сохранены: access=$accessToken, refresh=$refreshToken")
    }

    fun getAccessToken(): String? = accessToken
    fun getRefreshToken(): String? = refreshToken

    fun isTokenValid(): Boolean {
        return accessToken != null && Clock.System.now().toEpochMilliseconds() < expiresAt
    }

    fun clear() {
        accessToken = null
        refreshToken = null
        expiresAt = 0
        println("Токены очищены")
    }
}
