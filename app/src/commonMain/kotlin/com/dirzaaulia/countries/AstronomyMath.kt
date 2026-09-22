package com.dirzaaulia.countries

import kotlin.math.*

data class SunPosition(
    val lat: Double,
    val lng: Double,
    val vector: Point3D
)

data class MoonInfo(
    val lat: Double,
    val lng: Double,
    val phaseAngle: Double, // 0 to 360 deg
    val illuminatedFraction: Double, // 0.0 to 1.0
    val phaseName: String,
    val phaseEmoji: String,
    val distanceKm: Double
)

object AstronomyMath {

    /**
     * Calculates the real-time subsolar point (lat, lng) on Earth for a given epoch millis.
     * At this point, the Sun is directly overhead (zenith).
     */
    fun calculateSunPosition(epochMillis: Long = currentEpochMillis()): SunPosition {
        // Days since J2000.0 (January 1, 2000, 12:00 TT)
        val d = (epochMillis / 86400000.0) - 10957.5

        // Mean longitude of the Sun (degrees)
        val meanLng = (280.460 + 0.9856474 * d) % 360.0

        // Mean anomaly of the Sun (degrees)
        val meanAnomaly = (357.528 + 0.9856003 * d) % 360.0
        val gRad = meanAnomaly.toRadians

        // Ecliptic longitude of the Sun (degrees)
        val eclipticLng = meanLng + 1.915 * sin(gRad) + 0.020 * sin(2 * gRad)
        val lambdaRad = eclipticLng.toRadians

        // Obliquity of the ecliptic (Earth's axial tilt)
        val obliquity = 23.439 - 0.0000004 * d
        val epsRad = obliquity.toRadians

        // Solar Declination = Subsolar Latitude
        val sinDec = sin(epsRad) * sin(lambdaRad)
        val dec = asin(sinDec.coerceIn(-1.0, 1.0)).toDegrees

        // Right ascension of the Sun
        val y = cos(epsRad) * sin(lambdaRad)
        val x = cos(lambdaRad)
        val ra = atan2(y, x).toDegrees

        // Greenwich Mean Sidereal Time (GMST) in degrees
        val utHours = ((epochMillis % 86400000L) + 86400000L) % 86400000L / 3600000.0
        val gmst = (280.46061837 + 360.98564736629 * d + utHours * 15.0) % 360.0

        // Subsolar Longitude: where local solar time is solar noon
        var sunLng = (ra - gmst) % 360.0
        if (sunLng > 180.0) sunLng -= 360.0
        if (sunLng < -180.0) sunLng += 360.0

        // Unit vector in Earth coordinate system (lon 0 at +Z, +Y is North, +X is Lon 90E)
        val unitVector = latLngToCartesian(dec, sunLng, 1.0)

        return SunPosition(
            lat = dec,
            lng = sunLng,
            vector = unitVector
        )
    }

    /**
     * Calculates real-time position and lunar phase of the Moon.
     */
    fun calculateMoonInfo(epochMillis: Long = currentEpochMillis()): MoonInfo {
        val d = (epochMillis / 86400000.0) - 10957.5

        // Approximate lunar orbital parameters
        val meanLng = (218.316 + 13.176396 * d) % 360.0
        val meanAnomaly = (134.963 + 13.064993 * d) % 360.0
        val argLatitude = (93.272 + 13.229350 * d) % 360.0

        val eclipticLng = (meanLng + 6.289 * sin(meanAnomaly.toRadians)) % 360.0
        val eclipticLat = 5.128 * sin(argLatitude.toRadians)

        // Sun's ecliptic longitude
        val sunMeanAnomaly = (357.528 + 0.9856003 * d).toRadians
        val sunEclipticLng = ((280.460 + 0.9856474 * d) + 1.915 * sin(sunMeanAnomaly)) % 360.0

        // Phase angle (elongation)
        var elongation = (eclipticLng - sunEclipticLng) % 360.0
        if (elongation < 0) elongation += 360.0

        val phaseAngleRad = (180.0 - elongation).toRadians
        val illuminatedFraction = (1.0 + cos(phaseAngleRad)) / 2.0

        val (name, emoji) = when {
            elongation < 22.5 || elongation >= 337.5 -> "New Moon" to "🌑"
            elongation < 67.5 -> "Waxing Crescent" to "🌒"
            elongation < 112.5 -> "First Quarter" to "🌓"
            elongation < 157.5 -> "Waxing Gibbous" to "🌔"
            elongation < 202.5 -> "Full Moon" to "🌕"
            elongation < 247.5 -> "Waning Gibbous" to "🌖"
            elongation < 292.5 -> "Last Quarter" to "🌗"
            else -> "Waning Crescent" to "🌘"
        }

        // Greenwich Sidereal Time for Moon longitude
        val utHours = ((epochMillis % 86400000L) + 86400000L) % 86400000L / 3600000.0
        val gmst = (280.46061837 + 360.98564736629 * d + utHours * 15.0) % 360.0

        var moonLng = (eclipticLng - gmst) % 360.0
        if (moonLng > 180.0) moonLng -= 360.0
        if (moonLng < -180.0) moonLng += 360.0

        val distanceKm = 384400.0 - 20000.0 * cos(meanAnomaly.toRadians)

        return MoonInfo(
            lat = eclipticLat,
            lng = moonLng,
            phaseAngle = elongation,
            illuminatedFraction = illuminatedFraction,
            phaseName = name,
            phaseEmoji = emoji,
            distanceKm = distanceKm
        )
    }

    /**
     * Computes the Great Circle distance (km) between two geographic points using Haversine formula.
     */
    fun calculateGreatCircleDistance(p1: LatLng, p2: LatLng): Double {
        val r = 6371.0 // Earth mean radius in km
        val dLat = (p2.lat - p1.lat).toRadians
        val dLng = (p2.lng - p1.lng).toRadians
        val a = sin(dLat / 2).pow(2) + cos(p1.lat.toRadians) * cos(p2.lat.toRadians) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Interpolates geodesic Great Circle arc points using Spherical Linear Interpolation (Slerp).
     */
    fun calculateGreatCircleArc(from: LatLng, to: LatLng, steps: Int = 40): List<LatLng> {
        val v1 = latLngToCartesian(from.lat, from.lng, 1.0)
        val v2 = latLngToCartesian(to.lat, to.lng, 1.0)

        val dot = (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z).coerceIn(-1.0, 1.0)
        val omega = acos(dot)

        if (omega < 1e-4) return listOf(from, to)

        val sinOmega = sin(omega)
        val arc = ArrayList<LatLng>(steps + 1)

        for (i in 0..steps) {
            val t = i.toDouble() / steps
            val w1 = sin((1.0 - t) * omega) / sinOmega
            val w2 = sin(t * omega) / sinOmega

            val x = w1 * v1.x + w2 * v2.x
            val y = w1 * v1.y + w2 * v2.y
            val z = w1 * v1.z + w2 * v2.z

            val norm = sqrt(x * x + y * y + z * z)
            val unitX = x / norm
            val unitY = y / norm
            val unitZ = z / norm

            // Convert back to LatLng
            val lat = asin(unitY.coerceIn(-1.0, 1.0)).toDegrees
            val lng = atan2(unitX, unitZ).toDegrees

            arc.add(LatLng(lat, lng))
        }

        return arc
    }
}
