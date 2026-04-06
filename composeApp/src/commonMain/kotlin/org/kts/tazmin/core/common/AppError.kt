package org.kts.tazmin.core.common

import io.ktor.client.plugins.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException

sealed class AppError {
    object NoInternet : AppError()
    object Timeout : AppError()
    object Unauthorized : AppError()
    object Forbidden : AppError()
    object NotFound : AppError()
    object Server : AppError()
    object Parsing : AppError()
    data class RateLimit(val seconds: Int?) : AppError()
    object Unknown : AppError()
    object BadRequest : AppError()
}

fun Throwable.toAppError(): AppError =
    when (this) {

        is IOException ->
            AppError.NoInternet

        is TimeoutCancellationException ->
            AppError.Timeout

        is ClientRequestException -> {
            when (response.status.value) {
                400 -> AppError.BadRequest
                401 -> AppError.Unauthorized
                403 -> AppError.Forbidden
                404 -> AppError.NotFound
                429 -> AppError.RateLimit(null)
                else -> AppError.Unknown
            }
        }

        is ServerResponseException ->
            AppError.Server

        else ->
            AppError.Unknown
    }

sealed class UiMessage {
    data class Error(val error: AppError) : UiMessage()
    data class Info(val message: String) : UiMessage()
}
