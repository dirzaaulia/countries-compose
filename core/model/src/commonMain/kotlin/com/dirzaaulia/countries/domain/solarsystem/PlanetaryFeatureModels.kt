package com.dirzaaulia.countries.domain.solarsystem

enum class PlanetaryFeatureType {
    BASIN,
    CRATER,
    MOUNTAIN,
    TERRA,
    STORM,
    ATMOSPHERE,
    LANDER,
    ROVER,
    PROBE,
    ORBITER,
}

data class PlanetaryFeature(
    val id: String,
    val planetId: PlanetId,
    val name: String,
    val type: PlanetaryFeatureType,
    val lat: Double,
    val lng: Double,
    val missionAgency: String? = null,
    val missionYear: Int? = null,
    val significance: String,
    val discoveries: List<String>,
)
