package com.dirzaaulia.countries.data.mars

import com.dirzaaulia.countries.data.mars.responses.MarsPhotosResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

class MarsRoverApiClient(private val httpClient: HttpClient) {

    private companion object {
        const val PRIMARY_API_KEY = "zLs6Iv5XDNrgezxoIrJsJFqip1lTO6lI7Sb6ubki"
        const val DEMO_API_KEY = "DEMO_KEY"
    }

    suspend fun getRoverPhotos(rover: String, sol: Long): MarsPhotosResponse {
        // 1. Try Primary API Key
        tryFetch(rover, sol, PRIMARY_API_KEY)?.let { return it }

        // 2. Fallback to DEMO_KEY if Primary Key rate limits (429) or fails
        tryFetch(rover, sol, DEMO_API_KEY)?.let { return it }

        // 3. Fallback to empty response (Repository will serve bundled sample photos)
        return MarsPhotosResponse()
    }

    private suspend fun tryFetch(rover: String, sol: Long, apiKey: String): MarsPhotosResponse? {
        return runCatching {
            val response: HttpResponse = httpClient.get("https://api.nasa.gov/mars-photos/api/v1/rovers/$rover/photos") {
                parameter("sol", sol)
                parameter("page", 1)
                parameter("api_key", apiKey)
            }
            if (response.status.isSuccess()) {
                response.body<MarsPhotosResponse>()
            } else {
                null
            }
        }.getOrNull()
    }
}
