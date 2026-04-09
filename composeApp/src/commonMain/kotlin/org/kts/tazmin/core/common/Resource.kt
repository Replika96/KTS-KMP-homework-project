package org.kts.tazmin.core.common

sealed class Resource<out T> {
    data class Success<T>(
        val data: T,
        val source: Source
    ) : Resource<T>()

    data class Error<T>(
        val message: AppError,
        val data: T? = null
    ) : Resource<T>()

    object Loading : Resource<Nothing>()
}

enum class Source {
    REMOTE,
    CACHE
}
