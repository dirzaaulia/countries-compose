package com.dirzaaulia.countries

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun currentEpochMillis(): Long = System.currentTimeMillis()

actual fun formatLocalTime(epochMillis: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}
