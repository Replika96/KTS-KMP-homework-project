package org.kts.tazmin.core.common

import com.google.firebase.crashlytics.FirebaseCrashlytics

class AndroidCrashLogger : CrashLogger {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun logError(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}
