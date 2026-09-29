package com.dirzaaulia.countries.domain.satellite

import com.dirzaaulia.countries.domain.country.LatLng

enum class SatelliteType {
    SPACE_STATION,
    SPACE_TELESCOPE,
    DEEP_SPACE_OBSERVATORY
}

data class SatelliteTelemetry(
    val id: String,
    val noradId: Int,
    val name: String,
    val acronym: String,
    val type: SatelliteType,
    val lat: Double,
    val lng: Double,
    val altitudeKm: Double,
    val velocityKmH: Double,
    val isDaylight: Boolean,
    val groundTrack: List<LatLng>,
    val orbitalPeriodMin: Double,
    val apogeeKm: Double,
    val perigeeKm: Double,
    val operator: String = "",
    val launchYear: Int = 1998
)

data class PassOverheadPrediction(
    val minutesUntilPass: Int,
    val passTimeFormatted: String,
    val maxElevationDeg: Int
)
