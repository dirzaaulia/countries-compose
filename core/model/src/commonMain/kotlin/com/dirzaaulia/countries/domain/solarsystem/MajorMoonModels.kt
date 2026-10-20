package com.dirzaaulia.countries.domain.solarsystem

data class MajorMoon(
    val id: String,
    val planetId: PlanetId,
    val name: String,
    val description: String,
    val colorHex: Long,
    val radiusKm: Double,
    val massKg: String,
    val orbitalDistanceKm: Double,
    val orbitalPeriodDays: Double,
    val surfaceComposition: String,
    val tidalHeatingIndex: String,
    val habitabilityScore: Int, // 1 to 5
    val astrobiologyNotes: String,
)
