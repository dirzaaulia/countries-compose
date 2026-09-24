package com.dirzaaulia.countries

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class GeoJson(
    val type: String,
    val features: List<Feature>
)

@Serializable
data class Feature(
    val type: String,
    val properties: JsonObject,
    val geometry: Geometry
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: JsonElement
)

data class LatLng(val lat: Double, val lng: Double)

data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double
) {
    fun contains(latLng: LatLng): Boolean {
        return latLng.lat in minLat..maxLat && latLng.lng in minLng..maxLng
    }
}

data class Country(
    val id: String,
    val name: String,
    val formalName: String = "",
    val officialName: String = "",
    val nativeName: String = "",
    val continent: String = "",
    val subregion: String = "",
    val iso2: String = "",
    val capital: String = "N/A",
    val capitalLatLng: LatLng? = null,
    val languages: List<String> = emptyList(),
    val currencies: List<String> = emptyList(),
    val population: Long = 0L,
    val areaSqKm: Double = 0.0,
    val landlocked: Boolean = false,
    val demonym: String = "",
    val callingCode: String = "",
    val drivingSide: String = "",
    val topLevelDomain: String = "",
    val timezones: List<String> = emptyList(),
    val borders: List<String> = emptyList(),
    val unMember: Boolean = true,
    val gdpMillions: Double = 0.0,
    val economy: String = "",
    val incomeGroup: String = "",
    val polygons: List<List<LatLng>> = emptyList(),
    val center: LatLng = LatLng(0.0, 0.0),
    val zoomLevel: Float = 1.0f,
    val boundingBox: BoundingBox = BoundingBox(-90.0, 90.0, -180.0, 180.0)
) {
    val flagEmoji: String
        get() {
            val code = iso2.uppercase()
            if (code.length != 2 || code == "-99" || code[0] !in 'A'..'Z' || code[1] !in 'A'..'Z') {
                return "🌐"
            }
            val first = 0x1F1E6 + (code[0].code - 'A'.code) - 0x10000
            val second = 0x1F1E6 + (code[1].code - 'A'.code) - 0x10000
            return buildString {
                append(Char((first shr 10) + 0xD800))
                append(Char((first and 0x3FF) + 0xDC00))
                append(Char((second shr 10) + 0xD800))
                append(Char((second and 0x3FF) + 0xDC00))
            }
        }
}

data class DailyForecastItem(
    val date: String,
    val dayName: String,
    val tempMax: Double,
    val tempMin: Double,
    val weatherCode: Int,
    val weatherIcon: String,
    val precipitationProb: Int
)

data class HourlyForecastItem(
    val time: String,
    val hour: Int,
    val tempC: Double,
    val precipitationProb: Int,
    val weatherCode: Int
)

data class LiveCountryDetails(
    val isLiveWorldBankLoaded: Boolean = false,
    val isLiveWeatherLoaded: Boolean = false,
    val isLiveNasaLoaded: Boolean = false,
    val isLoading: Boolean = false,
    // World Bank Open Data
    val gdpPerCapita: Double? = null,
    val inflationRate: Double? = null,
    val lifeExpectancy: Double? = null,
    val unemploymentRate: Double? = null,
    val renewableEnergyShare: Double? = null,
    val co2Emissions: Double? = null,
    val gdpHistory: List<Pair<String, Double>> = emptyList(),
    val inflationHistory: List<Pair<String, Double>> = emptyList(),
    // Open-Meteo Weather (for capital or center)
    val weatherTempC: Double? = null,
    val weatherHumidity: Int? = null,
    val weatherWindSpeed: Double? = null,
    val weatherCode: Int? = null,
    val weatherDescription: String? = null,
    val weatherIcon: String? = null,
    val uvIndex: Double? = null,
    val sunrise: String? = null,
    val sunset: String? = null,
    val surfacePressureHpa: Double? = null,
    val windDirectionDeg: Double? = null,
    val dailyForecast: List<DailyForecastItem> = emptyList(),
    val hourlyForecast: List<HourlyForecastItem> = emptyList(),
    // NASA Natural Events (EONET)
    val nasaEvents: List<NasaNaturalEvent> = emptyList()
)

data class NasaNaturalEvent(
    val id: String,
    val title: String,
    val category: String,
    val categoryIcon: String,
    val date: String,
    val lat: Double,
    val lng: Double,
    val magnitude: String? = null
)

data class ISSTelemetry(
    val latitude: Double,
    val longitude: Double,
    val altitudeKm: Double,
    val velocityKmh: Double,
    val visibility: String,
    val timestamp: Long
)

