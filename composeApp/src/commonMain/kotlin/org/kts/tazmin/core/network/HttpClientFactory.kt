package org.kts.tazmin.core.network


import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kts.tazmin.core.token.TokenStorage
import org.kts.tazmin.feature.auth.domain.repository.AuthRepository

expect fun httpClientEngine(): HttpClientEngine

object HttpClientFactory {

    // общая конфигурация
    private fun HttpClientConfig<*>.baseConfig() {

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d(tag = "HTTP", message = message)
                }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
        }
    }

    // клиент без авторизации
    fun create(): HttpClient {
        return HttpClient(httpClientEngine()) {
            baseConfig()
        }
    }

    // клиент с авторизацией
    fun createAuthenticated(
        authRepository: AuthRepository
    ): HttpClient {

        return HttpClient(httpClientEngine()) {

            baseConfig()

            install(Auth) {
                bearer {

                    // загружаем токены
                    loadTokens {
                        val accessToken = TokenStorage.getAccessToken()
                        val refreshToken = TokenStorage.getRefreshToken()

                        if (accessToken != null && refreshToken != null) {
                            Napier.d("Loaded tokens")
                            BearerTokens(accessToken, refreshToken)
                        } else {
                            null
                        }
                    }

                    refreshTokens {

                        Napier.d("Refreshing token...")

                        val result = authRepository.refreshToken()

                        result.fold(
                            onSuccess = { tokenResponse ->

                                TokenStorage.saveTokens(
                                    accessToken = tokenResponse.accessToken,
                                    refreshToken = tokenResponse.refreshToken,
                                    expiresIn = tokenResponse.expiresIn
                                )

                                BearerTokens(
                                    tokenResponse.accessToken,
                                    tokenResponse.refreshToken
                                )
                            },
                            onFailure = { error ->
                                Napier.e("Token refresh failed", error)
                                null
                            }
                        )
                    }


                    sendWithoutRequest { request ->
                        request.url.host.contains("stepik.org")
                    }
                }
            }
        }
    }
}
