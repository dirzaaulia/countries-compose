package com.dirzaaulia.countries.domain.tectonic

import com.dirzaaulia.countries.domain.country.LatLng

enum class BoundaryType {
    CONVERGENT,
    DIVERGENT,
    TRANSFORM,
}

data class TectonicPlate(
    val id: String,
    val name: String,
    val plateType: String,
    val areaMillionSqKm: Double,
    val driftVelocityCmYear: Double,
    val driftDirection: String,
    val boundaries: List<List<LatLng>>,
)

data class Earthquake(
    val id: String,
    val title: String,
    val magnitude: Double,
    val place: String,
    val timeEpochMillis: Long,
    val lat: Double,
    val lng: Double,
    val depthKm: Double,
    val tsunamiAlert: Boolean,
    val usgsUrl: String,
)
