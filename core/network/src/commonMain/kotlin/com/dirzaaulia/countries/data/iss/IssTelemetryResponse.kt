package com.dirzaaulia.countries.data.iss

import kotlinx.serialization.Serializable

@Serializable
data class IssTelemetryResponse(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val velocity: Double? = null,
    val visibility: String? = null,
    val timestamp: Long? = null,
)
