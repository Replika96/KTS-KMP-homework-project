package org.kts.tazmin.core.token

import android.content.Context
import com.liftric.kvault.KVault
import kotlin.time.Clock

actual class TokenStorage(
    context: Context
) {

    private val vault = KVault(
        context = context.applicationContext,
        fileName = "auth_tokens"
    )

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val EXPIRES_AT_KEY = "expires_at"

        private const val EXPIRATION_BUFFER_MS = 10_000L
    }

    actual suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        val expiresAt =
            Clock.System.now().toEpochMilliseconds() + expiresIn * 1000

        vault.set(key = ACCESS_TOKEN_KEY, stringValue = accessToken)
        vault.set(key = REFRESH_TOKEN_KEY, stringValue = refreshToken)
        vault.set(key = EXPIRES_AT_KEY, longValue = expiresAt)
    }

    actual suspend fun getAccessToken(): String? {
        return vault.string(forKey = ACCESS_TOKEN_KEY)
    }

    actual suspend fun getRefreshToken(): String? {
        return vault.string(forKey = REFRESH_TOKEN_KEY)
    }

    actual suspend fun getExpiresAt(): Long? {
        return vault.long(forKey = EXPIRES_AT_KEY)
    }

    actual suspend fun isTokenExpired(): Boolean {
        val expiresAt = getExpiresAt() ?: return true
        val now = Clock.System.now().toEpochMilliseconds()
        return now >= expiresAt - EXPIRATION_BUFFER_MS
    }

    actual suspend fun clear() {
        vault.deleteObject(forKey = ACCESS_TOKEN_KEY)
        vault.deleteObject(forKey = REFRESH_TOKEN_KEY)
        vault.deleteObject(forKey = EXPIRES_AT_KEY)
    }

    actual suspend fun isLoggedIn(): Boolean {
        val access = getAccessToken() ?: return false
        return !isTokenExpired()
    }
}
