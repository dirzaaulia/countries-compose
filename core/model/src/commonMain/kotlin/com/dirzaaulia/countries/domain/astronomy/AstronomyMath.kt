package com.dirzaaulia.countries.domain.astronomy

import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.toDegrees
import com.dirzaaulia.countries.domain.globe.toRadians
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class SunPosition(
    val lat: Double,
    val lng: Double,
    val vector: Point3D,
)

data class MoonInfo(
    val lat: Double,
    val lng: Double,
    val phaseAngle: Double,
    val subsolarLatitude: Double,
    val librationLatitude: Double,
    val librationLongitude: Double,
    val illuminatedFraction: Double,
    val phaseName: String,
    val phaseEmoji: String,
    val distanceKm: Double,
    val moonAgeDays: Double,
    val lightTravelSec: Double,
    val isSupermoon: Boolean,
    val distancePercent: Float,
)

data class LunarOrientation(
    val subsolarLatitude: Double,
    val librationLatitude: Double,
    val librationLongitude: Double,
)

fun calculateLunarOrientation(
    d: Double,
    eclipticLng: Double,
    eclipticLat: Double,
    argLatitude: Double,
    sunEclipticLng: Double,
): LunarOrientation {
    val meanNode = (125.044 - 0.0529537 * d) % 360.0
    val iDeg = 1.543
    val iRad = iDeg.toRadians

    val nodeRad = meanNode.toRadians
    val eclLngRad = eclipticLng.toRadians
    val eclLatRad = eclipticLat.toRadians

    val sinSubLat = sin(eclLatRad) * cos(iRad) - cos(eclLatRad) * sin(iRad) * sin(eclLngRad - nodeRad)
    val subsolarLat = asin(sinSubLat.coerceIn(-1.0, 1.0)).toDegrees

    val libLat = (eclipticLat - subsolarLat) * 0.5
    val libLng = 5.7 * sin(argLatitude.toRadians)

    return LunarOrientation(
        subsolarLatitude = subsolarLat,
        librationLatitude = libLat,
        librationLongitude = libLng,
    )
}

object AstronomyMath {
    fun calculateSunPosition(epochMillis: Long = 1700000000000L): SunPosition {
        val d = (epochMillis - 946728000000L) / 86400000.0
        val meanAnomaly = (357.529 + 0.98560028 * d) % 360.0
        val gRad = meanAnomaly.toRadians
        val meanLng = (280.459 + 0.98564736 * d) % 360.0

        val eclipticLng = (meanLng + 1.915 * sin(gRad) + 0.020 * sin(2.0 * gRad)) % 360.0
        val lambdaRad = eclipticLng.toRadians

        val obliquity = 23.439 - 0.00000036 * d
        val epsRad = obliquity.toRadians

        val sinDec = sin(epsRad) * sin(lambdaRad)
        val dec = asin(sinDec.coerceIn(-1.0, 1.0)).toDegrees

        val y = cos(epsRad) * sin(lambdaRad)
        val x = cos(lambdaRad)
        val ra = (atan2(y, x).toDegrees + 360.0) % 360.0

        var eotDeg = (meanLng - ra) % 360.0
        if (eotDeg > 180.0) eotDeg -= 360.0
        if (eotDeg < -180.0) eotDeg += 360.0

        val utcMillis = ((epochMillis % 86400000L) + 86400000L) % 86400000L
        val utcHours = utcMillis / 3600000.0

        var subsolarLng = (12.0 - utcHours) * 15.0 + eotDeg
        subsolarLng = (subsolarLng + 180.0) % 360.0
        if (subsolarLng < 0.0) subsolarLng += 360.0
        subsolarLng -= 180.0

        val unitVector = latLngToCartesian(dec, subsolarLng, 1.0)

        return SunPosition(
            lat = dec,
            lng = subsolarLng,
            vector = unitVector,
        )
    }

    fun calculatePlanetRotationAngle(
        planetId: PlanetId,
        epochMillis: Long,
    ): Double {
        val d = (epochMillis - 946728000000L) / 86400000.0
        val rotRateDegPerDay =
            when (planetId) {
                PlanetId.MERCURY -> 6.1385025
                PlanetId.VENUS -> -1.481368
                PlanetId.EARTH -> 360.98564736
                PlanetId.MOON -> 13.1763964
                PlanetId.MARS -> 350.89198226
                PlanetId.JUPITER -> 870.5360000
                PlanetId.SATURN -> 818.1822000
                PlanetId.URANUS -> -501.1600928
                PlanetId.NEPTUNE -> 536.3128492
                PlanetId.SUN -> 14.1844000
            }
        return (rotRateDegPerDay * d) % 360.0
    }

    fun calculatePlanetSunPosition(
        planetId: PlanetId,
        epochMillis: Long,
    ): SunPosition {
        if (planetId == PlanetId.EARTH) return calculateSunPosition(epochMillis)
        val d = (epochMillis - 946728000000L) / 86400000.0

        val axialTilt =
            when (planetId) {
                PlanetId.MERCURY -> 0.03
                PlanetId.VENUS -> 2.64
                PlanetId.MOON -> 1.54
                PlanetId.MARS -> 25.19
                PlanetId.JUPITER -> 3.13
                PlanetId.SATURN -> 26.73
                PlanetId.URANUS -> 82.23
                PlanetId.NEPTUNE -> 28.32
                else -> 0.0
            }

        val orbitalPeriodDays =
            when (planetId) {
                PlanetId.MERCURY -> 87.97
                PlanetId.VENUS -> 224.70
                PlanetId.MOON -> 27.32
                PlanetId.MARS -> 686.98
                PlanetId.JUPITER -> 4332.59
                PlanetId.SATURN -> 10759.22
                PlanetId.URANUS -> 30685.40
                PlanetId.NEPTUNE -> 60189.00
                else -> 365.25
            }

        val orbitProg = (d / orbitalPeriodDays) * 2.0 * kotlin.math.PI
        val subsolarLat = sin(orbitProg) * axialTilt

        val rotRateDeg =
            when (planetId) {
                PlanetId.MERCURY -> 6.1385025
                PlanetId.VENUS -> -1.481368
                PlanetId.MOON -> 13.1763964
                PlanetId.MARS -> 350.89198226
                PlanetId.JUPITER -> 870.5360000
                PlanetId.SATURN -> 818.1822000
                PlanetId.URANUS -> -501.1600928
                PlanetId.NEPTUNE -> 536.3128492
                else -> 360.9856
            }

        var subsolarLng = (180.0 - (rotRateDeg * d)) % 360.0
        if (subsolarLng > 180.0) subsolarLng -= 360.0
        if (subsolarLng < -180.0) subsolarLng += 360.0

        val unitVector = latLngToCartesian(subsolarLat, subsolarLng, 1.0)
        return SunPosition(lat = subsolarLat, lng = subsolarLng, vector = unitVector)
    }

    fun calculateMoonInfo(epochMillis: Long = 1700000000000L): MoonInfo {
        val d = (epochMillis - 946728000000L) / 86400000.0
        val meanLng = (218.316 + 13.176396 * d) % 360.0
        val meanAnomaly = (134.963 + 13.064993 * d) % 360.0
        val argLatitude = (93.272 + 13.229350 * d) % 360.0

        val eclipticLng = (meanLng + 6.289 * sin(meanAnomaly.toRadians)) % 360.0
        val eclipticLat = 5.128 * sin(argLatitude.toRadians)

        val sunMeanAnomaly = (357.529 + 0.98560028 * d).toRadians
        val sunEclipticLng = ((280.459 + 0.98564736 * d) + 1.915 * sin(sunMeanAnomaly)) % 360.0

        var elongation = (eclipticLng - sunEclipticLng) % 360.0
        if (elongation < 0.0) elongation += 360.0

        val orientation = calculateLunarOrientation(d, eclipticLng, eclipticLat, argLatitude, sunEclipticLng)
        val phaseAngleRad = (180.0 - elongation).toRadians
        val illuminatedFraction = (1.0 + cos(phaseAngleRad)) / 2.0

        val (name, emoji) =
            when {
                elongation < 22.5 || elongation >= 337.5 -> "New Moon" to "[NEW]"
                elongation < 67.5 -> "Waxing Crescent" to "[WAX-C]"
                elongation < 112.5 -> "First Quarter" to "[Q1]"
                elongation < 157.5 -> "Waxing Gibbous" to "[WAX-G]"
                elongation < 202.5 -> "Full Moon" to "[FULL]"
                elongation < 247.5 -> "Waning Gibbous" to "[WAN-G]"
                elongation < 292.5 -> "Last Quarter" to "[Q3]"
                else -> "Waning Crescent" to "[WAN-C]"
            }

        val utcMillis = ((epochMillis % 86400000L) + 86400000L) % 86400000L
        val utcHours = utcMillis / 3600000.0
        var moonLng = (eclipticLng - utcHours * 15.0) % 360.0
        if (moonLng > 180.0) moonLng -= 360.0
        if (moonLng < -180.0) moonLng += 360.0

        val distanceKm = 384400.0 - 20000.0 * cos(meanAnomaly.toRadians)

        return MoonInfo(
            lat = eclipticLat,
            lng = moonLng,
            phaseAngle = elongation,
            subsolarLatitude = orientation.subsolarLatitude,
            librationLatitude = orientation.librationLatitude,
            librationLongitude = orientation.librationLongitude,
            illuminatedFraction = illuminatedFraction,
            phaseName = name,
            phaseEmoji = emoji,
            distanceKm = distanceKm,
            moonAgeDays = (elongation / 360.0) * 29.53059,
            lightTravelSec = distanceKm / 299792.458,
            isSupermoon = illuminatedFraction > 0.95 && distanceKm < 365000.0,
            distancePercent = ((distanceKm - 363300.0) / (405500.0 - 363300.0)).coerceIn(0.0, 1.0).toFloat(),
        )
    }

    fun isDaylight(
        point: LatLng,
        sunVector: Point3D,
    ): Boolean {
        val norm = latLngToCartesian(point.lat, point.lng, 1.0)
        val dot = norm.x * sunVector.x + norm.y * sunVector.y + norm.z * sunVector.z
        return dot > -0.06
    }

    fun calculateGreatCircleDistance(
        p1: LatLng,
        p2: LatLng,
    ): Double {
        val r = 6371.0
        val dLat = (p2.lat - p1.lat).toRadians
        val dLng = (p2.lng - p1.lng).toRadians
        val a = sin(dLat / 2).pow(2) + cos(p1.lat.toRadians) * cos(p2.lat.toRadians) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun calculateGreatCircleArc(
        from: LatLng,
        to: LatLng,
        steps: Int = 40,
    ): List<LatLng> {
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

            val lat = asin(unitY.coerceIn(-1.0, 1.0)).toDegrees
            val lng = atan2(unitX, unitZ).toDegrees

            arc.add(LatLng(lat, lng))
        }

        return arc
    }
}
