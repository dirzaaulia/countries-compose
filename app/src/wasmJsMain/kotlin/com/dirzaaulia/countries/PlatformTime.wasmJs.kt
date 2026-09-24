package com.dirzaaulia.countries

@JsFun("() => Date.now()")
private external fun jsDateNow(): Double

@JsFun("(ms) => { const d = new Date(ms); return d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0'); }")
private external fun jsFormatLocalTime(ms: Double): String

actual fun currentEpochMillis(): Long = jsDateNow().toLong()

actual fun formatLocalTime(epochMillis: Long): String {
    return try {
        jsFormatLocalTime(epochMillis.toDouble())
    } catch (e: Throwable) {
        val utcMillis = ((epochMillis % 86400000L) + 86400000L) % 86400000L
        val hours = (utcMillis / 3600000L).toInt()
        val minutes = ((utcMillis % 3600000L) / 60000L).toInt()
        "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
    }
}
