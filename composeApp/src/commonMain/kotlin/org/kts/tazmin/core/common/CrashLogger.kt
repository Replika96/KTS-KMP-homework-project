package org.kts.tazmin.core.common

interface CrashLogger {
    fun log(message: String)
    fun logError(throwable: Throwable)
}
