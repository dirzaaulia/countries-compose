package com.dirzaaulia.countries.data.restcountries

import com.dirzaaulia.countries.data.restcountries.responses.RestCountriesEnvelope
import com.dirzaaulia.countries.data.restcountries.responses.RestCountryResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

class RestCountriesApiClient(
    private val client: HttpClient,
    private val json: Json,
    private val baseUrl: String,
) {
    suspend fun fetchCountry(alphaCode: String): RestCountryResponse? {
        val code = alphaCode.trim().uppercase()
        if (code.isBlank() || code == "-99" || baseUrl.isBlank()) return null

        return try {
            val response = client.get("$baseUrl/$code").bodyAsText()
            val envelope = json.decodeFromString<RestCountriesEnvelope>(response)
            if (envelope.errors.isNotEmpty()) return null
            val data = envelope.data ?: return null
            data.objects.firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }
}
