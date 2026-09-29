package com.dirzaaulia.countries.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class AuroralCountry(
    val name: String,
    val code: String,
    val lat: Double,
    val lng: Double,
)

data class SpaceWeatherInfo(
    val kpIndex: Double,
    val solarWindSpeed: Double,
    val bzGsm: Double,
    val isAuroraActive: Boolean,
    val affectedCountries: List<AuroralCountry>,
)

class SpaceWeatherRepository(
    private val httpClient: HttpClient,
) {
    private var cachedInfo: SpaceWeatherInfo? = null
    private val mutex = Mutex()

    suspend fun fetchSpaceWeather(): SpaceWeatherInfo {
        mutex.withLock {
            cachedInfo?.let { return it }
        }

        val fallback = getFallbackSpaceWeather()
        try {
            val response: HttpResponse = httpClient.get("https://services.swpc.noaa.gov/products/noaa-planetary-k-index.json")
            if (response.status.isSuccess()) {
                val rawData: List<List<String>> = response.body()
                val latestRow = rawData.lastOrNull { it.size >= 2 && it[1].toDoubleOrNull() != null }
                val kp = latestRow?.get(1)?.toDoubleOrNull() ?: 3.6
                val info = createSpaceWeatherInfo(kpIndex = kp, windSpeed = 420.0, bz = -2.5)
                mutex.withLock { cachedInfo = info }
                return info
            }
        } catch (e: Exception) {
            // Graceful offline fallback
        }

        mutex.withLock { cachedInfo = fallback }
        return fallback
    }

    private fun createSpaceWeatherInfo(
        kpIndex: Double,
        windSpeed: Double,
        bz: Double,
    ): SpaceWeatherInfo {
        val affected = calculateAffectedCountries(kpIndex)
        return SpaceWeatherInfo(
            kpIndex = kpIndex,
            solarWindSpeed = windSpeed,
            bzGsm = bz,
            isAuroraActive = kpIndex >= 2.5,
            affectedCountries = affected,
        )
    }

    private fun getFallbackSpaceWeather(): SpaceWeatherInfo =
        createSpaceWeatherInfo(
            kpIndex = 3.6,
            windSpeed = 420.0,
            bz = -2.5,
        )

    private fun calculateAffectedCountries(kpIndex: Double): List<AuroralCountry> {
        val list = mutableListOf<AuroralCountry>()
        list.add(AuroralCountry("Greenland", "GL", 71.7, -42.6))
        list.add(AuroralCountry("Svalbard", "SJ", 77.87, 20.97))
        list.add(AuroralCountry("Alaska (USA)", "US", 64.2, -149.49))
        list.add(AuroralCountry("Northern Canada", "CA", 62.2, -106.3))

        if (kpIndex >= 3.0) {
            list.add(AuroralCountry("Iceland", "IS", 64.96, -19.02))
            list.add(AuroralCountry("Norway", "NO", 60.47, 8.46))
            list.add(AuroralCountry("Sweden", "SE", 60.12, 18.64))
            list.add(AuroralCountry("Finland", "FI", 61.92, 25.74))
            list.add(AuroralCountry("Russia (North)", "RU", 64.0, 100.0))
        }

        if (kpIndex >= 6.0) {
            list.add(AuroralCountry("United Kingdom", "GB", 55.37, -3.43))
            list.add(AuroralCountry("Denmark", "DK", 56.26, 9.5))
            list.add(AuroralCountry("New Zealand", "NZ", -40.9, 174.88))
            list.add(AuroralCountry("Tasmania (Australia)", "AU", -41.45, 145.97))
        }
        return list
    }
}
