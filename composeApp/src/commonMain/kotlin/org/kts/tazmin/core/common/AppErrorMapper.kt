package org.kts.tazmin.core.common

import androidx.compose.runtime.Composable
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.*
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.error_forbidden
import ktskotlinproject.composeapp.generated.resources.error_http
import ktskotlinproject.composeapp.generated.resources.error_no_internet
import ktskotlinproject.composeapp.generated.resources.error_not_found
import ktskotlinproject.composeapp.generated.resources.error_parsing
import ktskotlinproject.composeapp.generated.resources.error_server
import ktskotlinproject.composeapp.generated.resources.error_timeout
import ktskotlinproject.composeapp.generated.resources.error_unauthorized
import ktskotlinproject.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.stringResource
import kotlin.coroutines.cancellation.CancellationException

// доменные ошибки понятные для UI
sealed class AppError : Exception() {
    class NoInternet : AppError()
    class Timeout : AppError()
    class Unauthorized : AppError()
    class Forbidden : AppError()
    class NotFound() : AppError()
    class ServerError : AppError()
    class HttpError(val code: Int) : AppError()
    class ParseError : AppError()
    class Unknown(override val cause: Throwable) : AppError()
}

// маппер исключений
fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is CancellationException -> throw this
    is HttpRequestTimeoutException -> AppError.Timeout()
    is ConnectTimeoutException -> AppError.Timeout()
    is SocketTimeoutException -> AppError.Timeout()
    is UnresolvedAddressException -> AppError.NoInternet()
    is IOException -> AppError.NoInternet()
    is ResponseException -> when (response.status.value) {
        401 -> AppError.Unauthorized()
        403 -> AppError.Forbidden()
        404 -> AppError.NotFound()
        in 500..599 -> AppError.ServerError()
        else -> AppError.HttpError(response.status.value)
    }
    is SerializationException -> AppError.ParseError()
    else -> AppError.Unknown(this)
}

@Composable
fun AppError.toUiMessage(): String = when (this) {
    is AppError.NoInternet -> stringResource(Res.string.error_no_internet)
    is AppError.Timeout -> stringResource(Res.string.error_timeout)
    is AppError.Unauthorized -> stringResource(Res.string.error_unauthorized)
    is AppError.Forbidden -> stringResource(Res.string.error_forbidden)
    is AppError.NotFound -> stringResource(Res.string.error_not_found)
    is AppError.ServerError -> stringResource(Res.string.error_server)
    is AppError.ParseError -> stringResource(Res.string.error_parsing)
    is AppError.HttpError -> stringResource(Res.string.error_http, this.code)
    is AppError.Unknown -> stringResource(Res.string.error_unknown)
}
