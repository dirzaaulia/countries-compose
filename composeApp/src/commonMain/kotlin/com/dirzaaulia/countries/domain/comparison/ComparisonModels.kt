package com.dirzaaulia.countries.domain.comparison

import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toDegrees
import com.dirzaaulia.countries.domain.globe.toRadians
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class CountryComparisonData(
    val countryA: Country,
    val countryB: Country,
    val detailsA: LiveCountryDetails?,
    val detailsB: LiveCountryDetails?,
    val areaRatio: Double,
    val populationRatio: Double,
    val timeDifferenceHours: Double,
)

fun projectTransposedPolygon(
    sourceCountry: Country,
    targetCenter: LatLng,
): List<List<LatLng>> {
    val sourceCenter = sourceCountry.center
    val latA = sourceCenter.lat.toRadians
    val lngA = sourceCenter.lng.toRadians
    val latB = targetCenter.lat.toRadians
    val lngB = targetCenter.lng.toRadians

    val cosLngA = cos(-lngA)
    val sinLngA = sin(-lngA)
    val cosLatA = cos(-latA)
    val sinLatA = sin(-latA)

    val cosLatB = cos(latB)
    val sinLatB = sin(latB)
    val cosLngB = cos(lngB)
    val sinLngB = sin(lngB)

    return sourceCountry.polygons.map { polygon ->
        polygon.map { point ->
            val p0 = latLngToCartesian(point.lat, point.lng, 1.0)
            val p1 = rotateY(p0, cosLngA, sinLngA)
            val p2 = rotateX(p1, cosLatA, sinLatA)
            val p3 = rotateX(p2, cosLatB, sinLatB)
            val p4 = rotateY(p3, cosLngB, sinLngB)

            val lat = asin(p4.y.coerceIn(-1.0, 1.0)).toDegrees
            val lng = atan2(p4.x, p4.z).toDegrees
            LatLng(lat, lng)
        }
    }
}

fun buildComparisonData(
    countryA: Country,
    countryB: Country,
    detailsA: LiveCountryDetails?,
    detailsB: LiveCountryDetails?,
): CountryComparisonData {
    val areaRatio = if (countryB.areaSqKm > 0.0) countryA.areaSqKm / countryB.areaSqKm else 0.0
    val popRatio =
        if (countryB.population > 0L) countryA.population.toDouble() / countryB.population.toDouble() else 0.0

    val offsetA = parseUtcOffsetHours(countryA.timezones.firstOrNull(), countryA.center.lng)
    val offsetB = parseUtcOffsetHours(countryB.timezones.firstOrNull(), countryB.center.lng)
    val diffHours = offsetB - offsetA

    return CountryComparisonData(
        countryA = countryA,
        countryB = countryB,
        detailsA = detailsA,
        detailsB = detailsB,
        areaRatio = areaRatio,
        populationRatio = popRatio,
        timeDifferenceHours = diffHours,
    )
}

fun parseUtcOffsetHours(
    timezone: String?,
    centerLng: Double,
): Double {
    if (timezone.orEmpty().startsWith("UTC")) {
        val clean = timezone.orEmpty().removePrefix("UTC")
        val sign = if (clean.contains("-")) -1.0 else 1.0
        val parts = clean.removePrefix("+").removePrefix("-").split(":")
        val hours = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
        val minutes = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
        return sign * (hours + minutes / 60.0)
    }
    return centerLng / 15.0
}
