package org.kts.tazmin.core.common

import io.github.aakira.napier.Napier
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> runCatchingCancellable(
    block: suspend () -> T
): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        val appError = e.toAppError()
        Napier.e("Error: $appError", e)
        Result.failure(appError)
    }
}

