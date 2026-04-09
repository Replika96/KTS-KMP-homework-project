package org.kts.tazmin.core.utils


import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

fun formatRelativeTime(
    instant: Instant,
    now: Instant = Clock.System.now(),
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val diff = now - instant

    val seconds = diff.inWholeSeconds
    val minutes = diff.inWholeMinutes
    val hours = diff.inWholeHours
    val daysByDuration = diff.inWholeDays

    val localNow = now.toLocalDateTime(timeZone)
    val localInstant = instant.toLocalDateTime(timeZone)
    val period = localInstant.date.periodUntil(localNow.date)

    return when {
        seconds < 60 -> "только что"

        minutes < 60 ->
            plural(minutes, "минута", "минуты", "минут") + " назад"

        hours < 24 ->
            plural(hours, "час", "часа", "часов") + " назад"

        daysByDuration == 1L -> "вчера"
        daysByDuration == 2L -> "позавчера"

        daysByDuration < 7 ->
            plural(daysByDuration, "день", "дня", "дней") + " назад"

        daysByDuration < 30 ->
            plural(daysByDuration / 7, "неделю", "недели", "недель") + " назад"

        period.years == 0 && period.months > 0 ->
            plural(period.months.toLong(), "месяц", "месяца", "месяцев") + " назад"

        period.years > 0 ->
            plural(period.years.toLong(), "год", "года", "лет") + " назад"

        else -> "давно"
    }
}

private fun plural(value: Long, one: String, few: String, many: String): String {
    val v = value % 100
    return when {
        v in 11..19 -> "$value $many"
        v % 10 == 1L -> "$value $one"
        v % 10 in 2..4 -> "$value $few"
        else -> "$value $many"
    }
}
