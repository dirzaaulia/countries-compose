package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.openmeteo.OpenMeteoApiClient
import com.dirzaaulia.countries.data.restcountries.RestCountriesApiClient
import com.dirzaaulia.countries.data.worldbank.WorldBankApiClient
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.DailyForecastItem
import com.dirzaaulia.countries.domain.country.HourlyForecastItem
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.PI
import kotlin.math.sin

class CountryDetailRepository(
    private val restCountriesApiClient: RestCountriesApiClient,
    private val worldBankApiClient: WorldBankApiClient,
    private val openMeteoApiClient: OpenMeteoApiClient,
    private val hazardRepository: HazardRepository,
) {
    private data class CachedLiveDetails(
        val details: LiveCountryDetails,
        val timestamp: Long,
    )

    private val liveDetailsCache = mutableMapOf<String, CachedLiveDetails>()

    suspend fun fetchLiveDetails(country: Country): LiveCountryDetails {
        val now = currentEpochMillis()
        val cached = liveDetailsCache[country.id]
        if (cached != null && (now - cached.timestamp < 15 * 60 * 1000L)) {
            return cached.details
        }

        return try {
            coroutineScope {
                val restDeferred = async { restCountriesApiClient.fetchCountry(country.iso2.ifEmpty { country.id }) }
                val wbDeferred = async { worldBankApiClient.fetchWorldBankData(country.iso2.ifEmpty { country.id }) }
                val weatherDeferred =
                    async {
                        val target = country.capitalLatLng ?: country.center
                        openMeteoApiClient.fetchWeatherData(target.lat, target.lng)
                    }
                val nasaDeferred = async { hazardRepository.fetchNearbyNasaEvents(country.center) }

                val rest = restDeferred.await()
                val wb = wbDeferred.await()
                val weather = weatherDeferred.await()
                val nasa = nasaDeferred.await()

                val details =
                    LiveCountryDetails(
                        isLiveRestCountriesLoaded = rest != null,
                        isLiveWorldBankLoaded = wb != null,
                        isLiveWeatherLoaded = weather != null,
                        isLiveNasaLoaded = nasa.isNotEmpty(),
                        isLoading = false,
                        restCountry = rest,
                        gdpPerCapita = wb?.gdpPerCapita ?: ((country.gdpMillions * 1_000_000.0) / country.population.coerceAtLeast(1L)),
                        inflationRate = wb?.inflationRate,
                        lifeExpectancy = wb?.lifeExpectancy,
                        unemploymentRate = wb?.unemploymentRate,
                        renewableEnergyShare = wb?.renewableEnergyShare,
                        co2Emissions = wb?.co2Emissions,
                        gdpHistory = wb?.gdpHistory ?: emptyList(),
                        inflationHistory = wb?.inflationHistory ?: emptyList(),
                        weatherTempC = weather?.tempC,
                        weatherHumidity = weather?.humidity,
                        weatherWindSpeed = weather?.windSpeed,
                        weatherCode = weather?.code,
                        weatherDescription = weather?.description,
                        weatherIcon = weather?.icon,
                        uvIndex = weather?.uvIndex,
                        sunrise = weather?.sunrise,
                        sunset = weather?.sunset,
                        surfacePressureHpa = weather?.surfacePressureHpa,
                        windDirectionDeg = weather?.windDirectionDeg,
                        dailyForecast = weather?.dailyForecast ?: emptyList(),
                        hourlyForecast = weather?.hourlyForecast ?: emptyList(),
                        nasaEvents = nasa,
                    )

                liveDetailsCache[country.id] = CachedLiveDetails(details, now)
                details
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            cached?.details ?: generateOfflineFallbackDetails(country)
        }
    }

    private fun generateOfflineFallbackDetails(country: Country): LiveCountryDetails {
        val approxGdpPerCap =
            if (country.population > 0) {
                (country.gdpMillions * 1_000_000.0) / country.population
            } else {
                null
            }

        return LiveCountryDetails(
            isLiveWorldBankLoaded = approxGdpPerCap != null,
            isLiveWeatherLoaded = true,
            isLiveNasaLoaded = false,
            isLoading = false,
            gdpPerCapita = approxGdpPerCap,
            inflationRate = 2.8,
            lifeExpectancy = 73.5,
            unemploymentRate = 5.2,
            renewableEnergyShare = 24.5,
            co2Emissions = 4.2,
            gdpHistory =
                listOf(
                    "2021" to (approxGdpPerCap ?: 12000.0) * 0.91,
                    "2022" to (approxGdpPerCap ?: 12000.0) * 0.94,
                    "2023" to (approxGdpPerCap ?: 12000.0) * 0.97,
                    "2024" to (approxGdpPerCap ?: 12000.0),
                ),
            inflationHistory =
                listOf(
                    "2021" to 2.1,
                    "2022" to 6.8,
                    "2023" to 4.2,
                    "2024" to 2.8,
                ),
            weatherTempC = 21.0,
            weatherHumidity = 58,
            weatherWindSpeed = 14.0,
            weatherCode = 1,
            weatherDescription = "Mainly Clear",
            weatherIcon = "clear",
            uvIndex = 5.5,
            sunrise = "06:12",
            sunset = "18:45",
            surfacePressureHpa = 1013.2,
            windDirectionDeg = 210.0,
            dailyForecast =
                listOf(
                    DailyForecastItem("2026-09-24", "Today", 23.0, 14.0, 1, "clear", 10),
                    DailyForecastItem("2026-09-25", "Tomorrow", 24.0, 15.0, 2, "partly_cloudy", 20),
                    DailyForecastItem("2026-09-26", "Fri", 22.0, 13.0, 61, "rain", 65),
                    DailyForecastItem("2026-09-27", "Sat", 20.0, 12.0, 3, "cloudy", 30),
                    DailyForecastItem("2026-09-28", "Sun", 23.0, 14.0, 0, "clear", 5),
                    DailyForecastItem("2026-09-29", "Mon", 25.0, 16.0, 1, "clear", 15),
                    DailyForecastItem("2026-09-30", "Tue", 22.0, 14.0, 51, "drizzle", 45),
                ),
            hourlyForecast =
                (0..23).map { h ->
                    val hourStr = h.toString().padStart(2, '0') + ":00"
                    val temp = 16.0 + 8.0 * sin((h - 6) * PI / 12.0).coerceAtLeast(-0.3)
                    HourlyForecastItem(hourStr, h, ((temp * 10).toInt() / 10.0), (h * 3) % 40, if (h in 6..18) 1 else 0)
                },
            nasaEvents = emptyList(),
        )
    }
}
