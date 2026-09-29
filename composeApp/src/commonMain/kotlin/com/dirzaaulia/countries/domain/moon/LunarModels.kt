package com.dirzaaulia.countries.domain.moon

enum class LunarLandmarkType {
    CREWED_APOLLO,
    HISTORIC_ROBOTIC,
    MODERN_INTERNATIONAL,
    COMMERCIAL_CLPS,
    LUNAR_MARE,
    IMPACT_CRATER,
    ARTEMIS_SOUTH_POLE,
}

enum class LunarGeologicPeriod {
    PRE_NECTARIAN,
    NECTARIAN,
    IMBRIAN,
    ERATOSTHENIAN,
    COPERNICAN,
}

data class LunarMissionMetadata(
    val agency: String,
    val countryIso2: String,
    val landingDate: String,
    val sampleMassKg: Double? = null,
    val roverName: String? = null,
)

data class LunarLandmark(
    val id: String,
    val name: String,
    val latinName: String? = null,
    val type: LunarLandmarkType,
    val lat: Double,
    val lng: Double,
    val diameterKm: Double? = null,
    val mission: LunarMissionMetadata? = null,
    val geologicPeriod: LunarGeologicPeriod? = null,
    val significance: String,
    val isFarSide: Boolean = false,
)
