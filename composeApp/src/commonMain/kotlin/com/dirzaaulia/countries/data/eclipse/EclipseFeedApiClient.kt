package com.dirzaaulia.countries.data.eclipse

import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class EclipseFeedApiClient(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun fetchEclipseFeed(): EclipseFeed? =
        try {
            val response = client.get("https://countries-eclipse-feed.dirzaaulia11.workers.dev/v1/eclipses")
            json.decodeFromString<EclipseFeed>(response.bodyAsText())
        } catch (_: Exception) {
            null
        }
}
