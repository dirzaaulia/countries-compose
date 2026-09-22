package com.dirzaaulia.countries.util

import com.dirzaaulia.countries.LatLng
import kotlin.math.abs

fun formatPopulation(pop: Long): String {
    return when {
        pop >= 1_000_000_000 -> "${(pop / 100_000_000) / 10.0} Billion"
        pop >= 1_000_000 -> "${(pop / 100_000) / 10.0} Million"
        pop >= 1_000 -> "${pop / 1_000} Thousand"
        pop > 0 -> pop.toString()
        else -> "N/A"
    }
}

fun formatArea(sqKm: Double): String {
    if (sqKm <= 0.0) return "N/A"
    return "${formatNumber(sqKm)} km²"
}

fun formatNumber(value: Double): String {
    val longVal = value.toLong()
    return longVal.toString().reversed().chunked(3).joinToString(",").reversed()
}

fun formatDecimal(value: Double): String {
    return ((value * 10).toLong() / 10.0).toString()
}

fun formatCoordinates(coord: LatLng): String {
    val latDir = if (coord.lat >= 0) "N" else "S"
    val lngDir = if (coord.lng >= 0) "E" else "W"
    val lat = (abs(coord.lat) * 10).toLong() / 10.0
    val lng = (abs(coord.lng) * 10).toLong() / 10.0
    return "$lat°$latDir, $lng°$lngDir"
}
