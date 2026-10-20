package com.dirzaaulia.countries.data.satellite

import com.dirzaaulia.countries.data.iss.IssTelemetryResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class SatelliteApiClient(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun fetchSatelliteRaw(noradId: Int): IssTelemetryResponse? =
        try {
            val response = client.get("https://api.wheretheiss.at/v1/satellites/$noradId")
            val body = response.bodyAsText()
            json.decodeFromString<IssTelemetryResponse>(body)
        } catch (e: Exception) {
            null
        }
}
