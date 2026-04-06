package org.kts.tazmin.core.utils

import kotlinx.datetime.Instant

object DateFormatter {

    fun formatJoinedDate(
        isoDateString: String,
        yearAgo: (Int) -> String,
        monthAgo: (Int) -> String,
        dayAgo: (Int) -> String,
        yesterday: String,
        justNow: String
    ): String {
        return try {
            val instant = Instant.parse(isoDateString)
            val now = kotlin.time.Clock.System.now()
            val duration = now - instant

            when {
                duration.inWholeDays >= 365 -> {
                    val years = (duration.inWholeDays / 365).toInt()
                    yearAgo(years)
                }

                duration.inWholeDays >= 30 -> {
                    val months = (duration.inWholeDays / 30).toInt()
                    monthAgo(months)
                }

                duration.inWholeDays > 0 -> {
                    val days = duration.inWholeDays.toInt()
                    dayAgo(days)
                }

                else -> justNow
            }
        } catch (e: Exception) {
            isoDateString
        }
    }
}
