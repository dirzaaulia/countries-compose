package com.dirzaaulia.countries.domain.mars

data class MarsLandmark(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: String, // e.g. "ROVER", "LANDER", "MOUNTAIN", "CANYON", "BASIN", "POLAR_CAP", "MISSION"
    val lat: Double,
    val lng: Double,
    val year: Int?,
    val agency: String?,
    val elevationKm: Double?,
    val significance: String
)
