package com.dirzaaulia.countries.ui.dossier.components

import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlin.math.round

fun dossierTimeInfo(
    country: Country,
    liveTimezones: List<String>?,
): Pair<String, String> {
    val now = currentEpochMillis()
    val timezones = liveTimezones?.ifEmpty { null } ?: country.timezones
    val timezone = timezones.firstOrNull().orEmpty()
    val offsetHours =
        if (timezone.startsWith("UTC")) {
            val sign = if (timezone.contains("-")) -1 else 1
            val parts =
                timezone
                    .removePrefix("UTC")
                    .removePrefix("+")
                    .removePrefix("-")
                    .split(":")
            val hours = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            val minutes = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
            sign * (hours + minutes / 60.0)
        } else {
            round(country.center.lng / 15.0)
        }
    val utcMillis = (now % MILLIS_PER_DAY + MILLIS_PER_DAY) % MILLIS_PER_DAY
    val localMillis =
        (utcMillis + (offsetHours * MILLIS_PER_HOUR).toLong())
            .let { (it % MILLIS_PER_DAY + MILLIS_PER_DAY) % MILLIS_PER_DAY }
    val hours = (localMillis / MILLIS_PER_HOUR).toInt()
    val minutes = (localMillis % MILLIS_PER_HOUR / MILLIS_PER_MINUTE).toInt()
    val time = "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
    val timezoneLabel =
        timezone.ifEmpty {
            "UTC${if (offsetHours >= 0) "+${offsetHours.toInt()}" else offsetHours.toInt().toString()}"
        }
    return time to timezoneLabel
}

fun localSolarTime(longitude: Double): String {
    val utcMillis = (currentEpochMillis() % MILLIS_PER_DAY + MILLIS_PER_DAY) % MILLIS_PER_DAY
    val solarHour = ((utcMillis / MILLIS_PER_HOUR.toDouble() + longitude / 15.0) % 24.0 + 24.0) % 24.0
    val hours = solarHour.toInt()
    val minutes = ((solarHour - hours) * 60.0).toInt()
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}

private const val MILLIS_PER_MINUTE = 60_000L
private const val MILLIS_PER_HOUR = 3_600_000L
private const val MILLIS_PER_DAY = 86_400_000L
