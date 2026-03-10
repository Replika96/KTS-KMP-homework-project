package org.kts.tazmin.feature.auth.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import org.kts.tazmin.core.common.Config
import org.kts.tazmin.feature.auth.data.model.TokenResponse

class AuthApi(
    private val client: HttpClient
) {
    suspend fun getAccessToken(code: String): TokenResponse {
        return client.submitForm(
            url = Config.STEPIK_TOKEN_URL,
            formParameters = Parameters.build {
                append("grant_type", "authorization_code")
                append("code", code)
                append("redirect_uri", Config.STEPIK_REDIRECT_URI)
                append("client_id", Config.STEPIK_CLIENT_ID)
            }
        ).body()
    }

    suspend fun refreshAccessToken(refreshToken: String): TokenResponse {
        return client.submitForm(
            url = Config.STEPIK_TOKEN_URL,
            formParameters = Parameters.build {
                append("grant_type", "refresh_token")
                append("refresh_token", refreshToken)
                append("client_id", Config.STEPIK_CLIENT_ID)
            }
        ).body()
    }
}