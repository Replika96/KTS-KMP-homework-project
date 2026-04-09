package org.kts.tazmin.core.common

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

class CrashlyticsAntilog(
    private val crashLogger: CrashLogger
) : Antilog() {

    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        throwable?.let { crashLogger.logError(it) }
        message?.let { crashLogger.log(it) }
    }
}
