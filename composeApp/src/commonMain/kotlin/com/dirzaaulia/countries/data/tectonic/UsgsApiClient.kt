package com.dirzaaulia.countries.data.tectonic

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class UsgsApiClient(
    private val httpClient: HttpClient,
) {
    suspend fun fetchEarthquakes(): UsgsFeatureCollectionResponse? {
        return try {
            httpClient.get("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_day.geojson").body()
        } catch (e: Exception) {
            null
        }
    }
}
