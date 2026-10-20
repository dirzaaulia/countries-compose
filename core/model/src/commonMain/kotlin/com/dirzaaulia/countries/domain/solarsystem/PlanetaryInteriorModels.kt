package com.dirzaaulia.countries.domain.solarsystem

data class InteriorLayer(
    val id: String,
    val name: String,
    val outerRadiusPercentage: Float, // 0.0 to 1.0 (relative to planet radius)
    val colorHex: Long,
    val depthRangeKm: String,
    val pressureRange: String,
    val temperatureKelvin: String,
    val stateOfMatter: String,
    val description: String,
)

data class PlanetaryInterior(
    val planetId: PlanetId,
    val totalRadiusKm: Double,
    val layers: List<InteriorLayer>, // Ordered from center to surface
)
