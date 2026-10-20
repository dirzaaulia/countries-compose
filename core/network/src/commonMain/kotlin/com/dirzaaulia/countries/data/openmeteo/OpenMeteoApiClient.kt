package com.dirzaaulia.countries.data.openmeteo

import com.dirzaaulia.countries.data.openmeteo.responses.OpenMeteoResponse
import com.dirzaaulia.countries.domain.country.DailyForecastItem
import com.dirzaaulia.countries.domain.country.HourlyForecastItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

data class WeatherResults(
    val tempC: Double,
    val humidity: Int,
    val windSpeed: Double,
    val code: Int,
    val description: String,
    val icon: String,
    val uvIndex: Double?,
    val sunrise: String?,
    val sunset: String?,
    val surfacePressureHpa: Double?,
    val windDirectionDeg: Double?,
    val dailyForecast: List<DailyForecastItem>,
    val hourlyForecast: List<HourlyForecastItem>,
)

class OpenMeteoApiClient(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun fetchWeatherData(
        lat: Double,
        lng: Double,
    ): WeatherResults? {
        return try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,surface_pressure,wind_direction_10m&hourly=temperature_2m,precipitation_probability,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset,uv_index_max&timezone=auto"
            val response = client.get(url).bodyAsText()
            val forecast = json.decodeFromString<OpenMeteoResponse>(response)
            val current = forecast.current ?: return null
            val daily = forecast.daily
            val hourly = forecast.hourly

            val temp = current.temperature ?: 0.0
            val humidity = current.humidity ?: 0
            val windSpeed = current.windSpeed ?: 0.0
            val code = current.weatherCode ?: 0
            val pressure = current.surfacePressure
            val windDir = current.windDirection

            val (icon, desc) = mapWeatherCode(code)

            val uvIndex = daily?.uvIndexMax?.firstOrNull()
            val sunriseFull = daily?.sunrise?.firstOrNull()
            val sunsetFull = daily?.sunset?.firstOrNull()
            val sunrise = sunriseFull?.substringAfter("T")
            val sunset = sunsetFull?.substringAfter("T")

            // Parse 7-day forecast
            val dailyForecast = mutableListOf<DailyForecastItem>()
            val dailyDates = daily?.time

            if (dailyDates != null) {
                for (i in 0 until minOf(7, dailyDates.size)) {
                    val dateStr = dailyDates[i]
                    val dCode = daily.weatherCodes.getOrNull(i) ?: 0
                    val dMax = daily.temperaturesMax.getOrNull(i) ?: 20.0
                    val dMin = daily.temperaturesMin.getOrNull(i) ?: 12.0
                    val dPrecip = daily.precipitationMax.getOrNull(i) ?: 0
                    val (dIcon, _) = mapWeatherCode(dCode)
                    val dayName =
                        when (i) {
                            0 -> "Today"
                            1 -> "Tomorrow"
                            else -> {
                                val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                val dayNum = dateStr.takeLast(2).toIntOrNull() ?: i
                                daysOfWeek[(dayNum + i) % 7]
                            }
                        }
                    dailyForecast.add(
                        DailyForecastItem(
                            date = dateStr,
                            dayName = dayName,
                            tempMax = dMax,
                            tempMin = dMin,
                            weatherCode = dCode,
                            weatherIcon = dIcon,
                            precipitationProb = dPrecip,
                        ),
                    )
                }
            }

            // Parse 24-hour forecast
            val hourlyForecast = mutableListOf<HourlyForecastItem>()
            val hourlyTimes = hourly?.time

            if (hourlyTimes != null) {
                for (i in 0 until minOf(24, hourlyTimes.size)) {
                    val timeStr = hourlyTimes[i]
                    val hTemp = hourly.temperatures.getOrNull(i) ?: 18.0
                    val hPrecip = hourly.precipitation.getOrNull(i) ?: 0
                    val hCode = hourly.weatherCodes.getOrNull(i) ?: 0
                    val hour = timeStr.substringAfter("T").take(2).toIntOrNull() ?: i
                    hourlyForecast.add(
                        HourlyForecastItem(
                            time = timeStr.substringAfter("T").take(5),
                            hour = hour,
                            tempC = hTemp,
                            precipitationProb = hPrecip,
                            weatherCode = hCode,
                        ),
                    )
                }
            }

            WeatherResults(
                tempC = temp,
                humidity = humidity,
                windSpeed = windSpeed,
                code = code,
                description = desc,
                icon = icon,
                uvIndex = uvIndex,
                sunrise = sunrise,
                sunset = sunset,
                surfacePressureHpa = pressure,
                windDirectionDeg = windDir,
                dailyForecast = dailyForecast,
                hourlyForecast = hourlyForecast,
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun mapWeatherCode(code: Int): Pair<String, String> =
        when (code) {
            0 -> "☀️" to "Clear Sky"
            1 -> "🌤️" to "Mainly Clear"
            2 -> "⛅" to "Partly Cloudy"
            3 -> "☁️" to "Overcast"
            45, 48 -> "🌫️" to "Fog / Mist"
            51, 53, 55 -> "🌦️" to "Drizzle"
            61, 63, 65 -> "🌧️" to "Rain"
            66, 67 -> "🌧️" to "Freezing Rain"
            71, 73, 75 -> "❄️" to "Snowfall"
            77 -> "❄️" to "Snow Grains"
            80, 81, 82 -> "🌧️" to "Rain Showers"
            85, 86 -> "🌨️" to "Snow Showers"
            95 -> "⛈️" to "Thunderstorm"
            96, 99 -> "⛈️" to "Severe Thunderstorm"
            else -> "🌡️" to "Fair"
        }
}
