package com.dirzaaulia.countries.domain.solarsystem

enum class PlanetId {
    SUN,
    MERCURY,
    VENUS,
    EARTH,
    MOON,
    MARS,
    JUPITER,
    SATURN,
    URANUS,
    NEPTUNE,
}

data class PlanetOrbitalData(
    val id: PlanetId,
    val name: String,
    val symbolTag: String,
    val colorHex: Long,
    val diameterKm: Double,
    val meanDistanceAu: Double,
    val orbitalPeriodDays: Double,
    val orbitalSpeedKms: Double,
    val axialTiltDeg: Double,
    val description: String,
)

data class LivePlanetPosition(
    val planet: PlanetOrbitalData,
    val xAu: Double,
    val yAu: Double,
    val zAu: Double,
    val distanceAu: Double,
    val distanceKm: Double,
    val trueAnomalyDeg: Double,
    val eclipticLongitudeDeg: Double,
    val screenX: Float = 0f,
    val screenY: Float = 0f,
)

data class KeplerianOrbitElements(
    val semiMajorAxisAu: Double,
    val eccentricity: Double,
    val inclinationDeg: Double,
    val meanLongitudeDeg: Double,
    val longitudeOfPerihelionDeg: Double,
    val longitudeOfAscendingNodeDeg: Double,
    val rateSemiMajorAxisAu: Double = 0.0,
    val rateEccentricity: Double = 0.0,
    val rateInclinationDeg: Double = 0.0,
    val rateMeanLongitudeDeg: Double = 0.0,
    val rateLongitudeOfPerihelionDeg: Double = 0.0,
    val rateLongitudeOfAscendingNodeDeg: Double = 0.0,
)
