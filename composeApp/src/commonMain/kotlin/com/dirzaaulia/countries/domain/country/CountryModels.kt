package com.dirzaaulia.countries.domain.country

import com.dirzaaulia.countries.data.restcountries.areaSqKm
import com.dirzaaulia.countries.data.restcountries.capitalCoordinates
import com.dirzaaulia.countries.data.restcountries.capitalName
import com.dirzaaulia.countries.data.restcountries.officialName
import com.dirzaaulia.countries.data.restcountries.responses.RestCountryResponse
import com.dirzaaulia.countries.platform.platformCountryMarker
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class GeoJson(
    val type: String,
    val features: List<Feature>,
)

@Serializable
data class Feature(
    val type: String,
    val properties: JsonObject,
    val geometry: Geometry,
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: JsonElement,
)

data class LatLng(
    val lat: Double,
    val lng: Double,
)

data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double,
) {
    fun contains(latLng: LatLng): Boolean = latLng.lat in minLat..maxLat && latLng.lng in minLng..maxLng
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
    val boundingBox: BoundingBox = BoundingBox(-90.0, 90.0, -180.0, 180.0),
) {
    val flagEmoji: String
        get() = platformCountryMarker(iso2)
}

internal fun Country.enrich(rest: RestCountryResponse): Country =
    this.copy(
        officialName = rest.officialName.ifEmpty { this.officialName },
        formalName = rest.officialName.ifEmpty { this.formalName },
        nativeName =
            rest.names
                ?.native
                ?.values
                ?.asSequence()
                ?.mapNotNull { it.common }
                ?.firstOrNull()
                .orEmpty()
                .ifEmpty { this.nativeName },
        capital = rest.capitalName.ifEmpty { this.capital },
        capitalLatLng =
            rest.capitalCoordinates?.let { point ->
                if (point.lat != null && point.lng != null) LatLng(point.lat, point.lng) else null
            } ?: this.capitalLatLng,
        languages = rest.languages.mapNotNull { it.name }.ifEmpty { this.languages },
        currencies =
            rest.currencies
                .mapNotNull { currency ->
                    currency.name?.let { name ->
                        val symbol = currency.symbol.orEmpty()
                        val code = currency.code.orEmpty()
                        if (symbol.isNotEmpty()) "$name ($symbol • $code)" else "$name ($code)"
                    }
                }.ifEmpty { this.currencies },
        population = rest.population?.takeIf { it > 0 } ?: this.population,
        areaSqKm = rest.areaSqKm?.takeIf { it > 0 } ?: this.areaSqKm,
        landlocked = rest.landlocked ?: this.landlocked,
        demonym = rest.demonyms?.get("eng")?.m ?: this.demonym,
        callingCode = rest.callingCodes.firstOrNull() ?: this.callingCode,
        drivingSide = rest.cars?.drivingSide ?: this.drivingSide,
        topLevelDomain = rest.tlds.firstOrNull() ?: this.topLevelDomain,
        timezones = rest.timezones.ifEmpty { this.timezones },
        borders = rest.borders.ifEmpty { this.borders },
        unMember = rest.classification?.unMember ?: this.unMember,
    )

data class DailyForecastItem(
    val date: String,
    val dayName: String,
    val tempMax: Double,
    val tempMin: Double,
    val weatherCode: Int,
    val weatherIcon: String,
    val precipitationProb: Int,
)

data class HourlyForecastItem(
    val time: String,
    val hour: Int,
    val tempC: Double,
    val precipitationProb: Int,
    val weatherCode: Int,
)

data class LiveCountryDetails(
    val isLiveRestCountriesLoaded: Boolean = false,
    val isLiveWorldBankLoaded: Boolean = false,
    val isLiveWeatherLoaded: Boolean = false,
    val isLiveNasaLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val restCountry: RestCountryResponse? = null,
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
    val nasaEvents: List<NasaNaturalEvent> = emptyList(),
)

data class NasaNaturalEvent(
    val id: String,
    val title: String,
    val category: String,
    val categoryIcon: String,
    val date: String,
    val lat: Double,
    val lng: Double,
    val magnitude: String? = null,
)

data class ISSTelemetry(
    val latitude: Double,
    val longitude: Double,
    val altitudeKm: Double,
    val velocityKmh: Double,
    val visibility: String,
    val timestamp: Long,
)
