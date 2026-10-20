package com.dirzaaulia.countries.data.iss

import com.dirzaaulia.countries.data.iss.IssTelemetryResponse
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.platform.currentEpochMillis
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class ISSTelemetryApiClient(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun fetchISSTelemetry(): ISSTelemetry? =
        try {
            val response = client.get("https://api.wheretheiss.at/v1/satellites/25544")
            val body = response.bodyAsText()
            val telemetry = json.decodeFromString<IssTelemetryResponse>(body)
            val lat = telemetry.latitude ?: 0.0
            val lng = telemetry.longitude ?: 0.0
            val alt = telemetry.altitude ?: 420.0
            val vel = telemetry.velocity ?: 27600.0
            val vis = telemetry.visibility ?: "daylight"
            val ts = telemetry.timestamp ?: (currentEpochMillis() / 1000)
            ISSTelemetry(lat, lng, alt, vel, vis, ts)
        } catch (e: Exception) {
            null
        }
}
