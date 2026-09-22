package com.dirzaaulia.countries

import com.dirzaaulia.countries.generated.resources.Res
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.*
import kotlin.math.*

class GlobeRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    // In-memory cache for live details by country ID
    private val liveDetailsCache = mutableMapOf<String, LiveCountryDetails>()

    suspend fun loadCountries(): List<Country> {
        val geoJsonBytes = Res.readBytes("files/countries.geojson")
        val geoJson = json.decodeFromString<GeoJson>(geoJsonBytes.decodeToString())

        // Load enriched metadata (capital, coordinates, area, languages, currencies, timezones, borders, etc.)
        val extraMap: Map<String, JsonObject> = try {
            val extraBytes = Res.readBytes("files/country_extra.json")
            val extraObj = json.decodeFromString<JsonObject>(extraBytes.decodeToString())
            extraObj.mapValues { it.value.jsonObject }
        } catch (e: Exception) {
            emptyMap()
        }

        val countries = geoJson.features.map { feature ->
            val props = feature.properties
            val name = props["name"]?.jsonPrimitive?.content ?: "Unknown"
            val formalName = props["formal_en"]?.jsonPrimitive?.content ?: name
            val continent = props["continent"]?.jsonPrimitive?.content ?: ""
            val subregion = props["subregion"]?.jsonPrimitive?.content ?: ""
            val iso2 = props["iso_a2"]?.jsonPrimitive?.content ?: ""
            val id = props["iso_a3"]?.jsonPrimitive?.content ?: name
            val popEst = props["pop_est"]?.jsonPrimitive?.doubleOrNull?.toLong() ?: 0L
            val gdpEst = props["gdp_md_est"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val incomeGroup = props["income_grp"]?.jsonPrimitive?.content ?: ""
            val economy = props["economy"]?.jsonPrimitive?.content ?: ""

            // Extra metadata lookup
            val extra = extraMap[id] ?: extraMap[props["adm0_a3"]?.jsonPrimitive?.content]
            val capital = extra?.get("capital")?.jsonPrimitive?.content ?: "N/A"
            val officialName = extra?.get("officialName")?.jsonPrimitive?.content ?: formalName
            val nativeName = extra?.get("nativeName")?.jsonPrimitive?.content ?: ""
            val area = extra?.get("area")?.jsonPrimitive?.doubleOrNull ?: 0.0
            val landlocked = extra?.get("landlocked")?.jsonPrimitive?.booleanOrNull ?: false
            val demonym = extra?.get("demonym")?.jsonPrimitive?.content ?: ""
            val callingCode = extra?.get("callingCode")?.jsonPrimitive?.content ?: ""
            val drivingSide = extra?.get("drivingSide")?.jsonPrimitive?.content ?: ""
            val tld = extra?.get("tld")?.jsonPrimitive?.content ?: ""
            val timezones = extra?.get("timezones")?.jsonArray?.mapNotNull { it.jsonPrimitive.content } ?: emptyList()
            val borders = extra?.get("borders")?.jsonArray?.mapNotNull { it.jsonPrimitive.content } ?: emptyList()
            val unMember = extra?.get("unMember")?.jsonPrimitive?.booleanOrNull ?: true
            val capLat = extra?.get("capitalLat")?.jsonPrimitive?.doubleOrNull
            val capLng = extra?.get("capitalLng")?.jsonPrimitive?.doubleOrNull
            val capitalLatLng = if (capLat != null && capLng != null) LatLng(capLat, capLng) else null
            val languages = extra?.get("languages")?.jsonArray?.mapNotNull { it.jsonPrimitive.content } ?: emptyList()
            val currencies = extra?.get("currencies")?.jsonArray?.mapNotNull { it.jsonPrimitive.content } ?: emptyList()

            val polygons = mutableListOf<List<LatLng>>()
            var totalX = 0.0
            var totalY = 0.0
            var totalZ = 0.0
            var totalPoints = 0

            val geometry = feature.geometry
            when (geometry.type) {
                "Polygon" -> {
                    val coords = geometry.coordinates.jsonArray[0].jsonArray
                    val poly = parsePolygon(coords)
                    polygons.add(poly)
                    poly.forEach {
                        val latRad = it.lat.toRadians
                        val lngRad = it.lng.toRadians
                        totalX += cos(latRad) * cos(lngRad)
                        totalY += cos(latRad) * sin(lngRad)
                        totalZ += sin(latRad)
                        totalPoints++
                    }
                }
                "MultiPolygon" -> {
                    geometry.coordinates.jsonArray.forEach { poly ->
                        val coords = poly.jsonArray[0].jsonArray
                        val p = parsePolygon(coords)
                        polygons.add(p)
                        p.forEach {
                            val latRad = it.lat.toRadians
                            val lngRad = it.lng.toRadians
                            totalX += cos(latRad) * cos(lngRad)
                            totalY += cos(latRad) * sin(lngRad)
                            totalZ += sin(latRad)
                            totalPoints++
                        }
                    }
                }
            }

            val center = if (totalPoints > 0) {
                val avgX = totalX / totalPoints
                val avgY = totalY / totalPoints
                val avgZ = totalZ / totalPoints
                val hyp = sqrt(avgX * avgX + avgY * avgY)
                LatLng(atan2(avgZ, hyp).toDegrees, atan2(avgY, avgX).toDegrees)
            } else {
                LatLng(0.0, 0.0)
            }

            val areaEst = feature.properties["name_len"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 10.0
            val zoomLevel = when {
                areaEst > 12 -> 1.0f
                areaEst > 8 -> 1.3f
                else -> 1.6f
            }

            Country(
                id = id,
                name = name,
                formalName = formalName,
                officialName = officialName,
                nativeName = nativeName,
                continent = continent,
                subregion = subregion,
                iso2 = iso2,
                capital = capital,
                capitalLatLng = capitalLatLng,
                languages = languages,
                currencies = currencies,
                population = popEst,
                areaSqKm = area,
                landlocked = landlocked,
                demonym = demonym,
                callingCode = callingCode,
                drivingSide = drivingSide,
                topLevelDomain = tld,
                timezones = timezones,
                borders = borders,
                unMember = unMember,
                gdpMillions = gdpEst,
                incomeGroup = incomeGroup,
                economy = economy,
                polygons = polygons,
                center = center,
                zoomLevel = zoomLevel
            )
        }
        return countries
    }

    private fun parsePolygon(jsonCoords: JsonArray): List<LatLng> {
        return jsonCoords.map { point ->
            val p = point.jsonArray
            LatLng(p[1].jsonPrimitive.double, p[0].jsonPrimitive.double)
        }
    }

    suspend fun loadCloudBytes(): ByteArray {
        return Res.readBytes("files/earth_clouds.jpg")
    }

    // -------------------------------------------------------------
    // LIVE APIS: World Bank, Open-Meteo Weather, and NASA EONET
    // -------------------------------------------------------------

    suspend fun fetchLiveDetails(country: Country): LiveCountryDetails {
        liveDetailsCache[country.id]?.let { return it }

        return coroutineScope {
            val wbDeferred = async { fetchWorldBankData(country.iso2.ifEmpty { country.id }) }
            val weatherDeferred = async {
                val target = country.capitalLatLng ?: country.center
                fetchWeatherData(target.lat, target.lng)
            }
            val nasaDeferred = async { fetchNasaEvents(country.center) }

            val wb = wbDeferred.await()
            val weather = weatherDeferred.await()
            val nasa = nasaDeferred.await()

            val details = LiveCountryDetails(
                isLiveWorldBankLoaded = wb != null,
                isLiveWeatherLoaded = weather != null,
                isLiveNasaLoaded = nasa.isNotEmpty(),
                isLoading = false,
                gdpPerCapita = wb?.gdpPerCapita,
                inflationRate = wb?.inflationRate,
                lifeExpectancy = wb?.lifeExpectancy,
                unemploymentRate = wb?.unemploymentRate,
                renewableEnergyShare = wb?.renewableEnergyShare,
                co2Emissions = wb?.co2Emissions,
                weatherTempC = weather?.tempC,
                weatherHumidity = weather?.humidity,
                weatherWindSpeed = weather?.windSpeed,
                weatherCode = weather?.code,
                weatherDescription = weather?.description,
                weatherIcon = weather?.icon,
                uvIndex = weather?.uvIndex,
                sunrise = weather?.sunrise,
                sunset = weather?.sunset,
                nasaEvents = nasa
            )

            liveDetailsCache[country.id] = details
            details
        }
    }

    // 1. World Bank Open Data API (GDP per cap, Inflation, Life Expectancy, Unemployment, Renewable, CO2)
    private data class WorldBankResults(
        val gdpPerCapita: Double? = null,
        val inflationRate: Double? = null,
        val lifeExpectancy: Double? = null,
        val unemploymentRate: Double? = null,
        val renewableEnergyShare: Double? = null,
        val co2Emissions: Double? = null
    )

    private suspend fun fetchWorldBankData(countryCode: String): WorldBankResults? {
        if (countryCode.isBlank() || countryCode == "-99") return null
        return coroutineScope {
            val gdpCapDef = async { fetchWorldBankIndicator(countryCode, "NY.GDP.PCAP.CD") }
            val inflDef = async { fetchWorldBankIndicator(countryCode, "FP.CPI.TOTL.ZG") }
            val lifeDef = async { fetchWorldBankIndicator(countryCode, "SP.DYN.LE00.IN") }
            val unempDef = async { fetchWorldBankIndicator(countryCode, "SL.UEM.TOTL.ZS") }
            val renewDef = async { fetchWorldBankIndicator(countryCode, "EG.FEC.RNEW.ZS") }
            val co2Def = async { fetchWorldBankIndicator(countryCode, "EN.ATM.CO2E.PC") }

            val gdpCap = gdpCapDef.await()
            val infl = inflDef.await()
            val life = lifeDef.await()
            val unemp = unempDef.await()
            val renew = renewDef.await()
            val co2 = co2Def.await()

            if (gdpCap == null && infl == null && life == null && unemp == null && renew == null && co2 == null) {
                null
            } else {
                WorldBankResults(
                    gdpPerCapita = gdpCap,
                    inflationRate = infl,
                    lifeExpectancy = life,
                    unemploymentRate = unemp,
                    renewableEnergyShare = renew,
                    co2Emissions = co2
                )
            }
        }
    }

    private suspend fun fetchWorldBankIndicator(countryCode: String, indicator: String): Double? {
        return try {
            val url = "https://api.worldbank.org/v2/country/$countryCode/indicator/$indicator?format=json&mrnev=1"
            val response = client.get(url).bodyAsText()
            val array = json.decodeFromString<JsonArray>(response)
            if (array.size > 1) {
                val records = array[1].jsonArray
                if (records.isNotEmpty()) {
                    records[0].jsonObject["value"]?.jsonPrimitive?.doubleOrNull
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // 2. Open-Meteo Weather API
    private data class WeatherResults(
        val tempC: Double,
        val humidity: Int,
        val windSpeed: Double,
        val code: Int,
        val description: String,
        val icon: String,
        val uvIndex: Double?,
        val sunrise: String?,
        val sunset: String?
    )

    private suspend fun fetchWeatherData(lat: Double, lng: Double): WeatherResults? {
        return try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&daily=sunrise,sunset,uv_index_max&timezone=auto"
            val response = client.get(url).bodyAsText()
            val obj = json.decodeFromString<JsonObject>(response)
            val current = obj["current"]?.jsonObject ?: return null
            val daily = obj["daily"]?.jsonObject

            val temp = current["temperature_2m"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val humidity = current["relative_humidity_2m"]?.jsonPrimitive?.intOrNull ?: 0
            val windSpeed = current["wind_speed_10m"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val code = current["weather_code"]?.jsonPrimitive?.intOrNull ?: 0

            val (icon, desc) = mapWeatherCode(code)

            val uvIndex = daily?.get("uv_index_max")?.jsonArray?.firstOrNull()?.jsonPrimitive?.doubleOrNull
            val sunriseFull = daily?.get("sunrise")?.jsonArray?.firstOrNull()?.jsonPrimitive?.content
            val sunsetFull = daily?.get("sunset")?.jsonArray?.firstOrNull()?.jsonPrimitive?.content
            val sunrise = sunriseFull?.substringAfter("T")
            val sunset = sunsetFull?.substringAfter("T")

            WeatherResults(
                tempC = temp,
                humidity = humidity,
                windSpeed = windSpeed,
                code = code,
                description = desc,
                icon = icon,
                uvIndex = uvIndex,
                sunrise = sunrise,
                sunset = sunset
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun mapWeatherCode(code: Int): Pair<String, String> {
        return when (code) {
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

    // 3. NASA EONET Natural Events API
    private suspend fun fetchNasaEvents(center: LatLng): List<NasaNaturalEvent> {
        return try {
            val url = "https://eonet.gsfc.nasa.gov/api/v3/events?status=open&limit=30"
            val response = client.get(url).bodyAsText()
            val obj = json.decodeFromString<JsonObject>(response)
            val eventsArray = obj["events"]?.jsonArray ?: return emptyList()

            val parsedList = mutableListOf<NasaNaturalEvent>()
            for (item in eventsArray) {
                val itemObj = item.jsonObject
                val id = itemObj["id"]?.jsonPrimitive?.content ?: ""
                val title = itemObj["title"]?.jsonPrimitive?.content ?: ""
                val catObj = itemObj["categories"]?.jsonArray?.firstOrNull()?.jsonObject
                val catTitle = catObj?.get("title")?.jsonPrimitive?.content ?: "Natural Hazard"
                val catId = catObj?.get("id")?.jsonPrimitive?.content ?: ""

                val (icon, niceCat) = when (catId.lowercase()) {
                    "wildfires" -> "🔥" to "Wildfire"
                    "severestorms" -> "🌀" to "Severe Storm"
                    "volcanoes" -> "🌋" to "Volcano"
                    "sealakeice" -> "🧊" to "Ice Activity"
                    else -> "⚠️" to catTitle
                }

                val geomArray = itemObj["geometry"]?.jsonArray
                val latestGeom = geomArray?.lastOrNull()?.jsonObject
                val coords = latestGeom?.get("coordinates")?.jsonArray
                val date = latestGeom?.get("date")?.jsonPrimitive?.content?.take(10) ?: ""
                val mag = latestGeom?.get("magnitudeValue")?.jsonPrimitive?.content

                if (coords != null && coords.size >= 2) {
                    val lng = coords[0].jsonPrimitive.double
                    val lat = coords[1].jsonPrimitive.double

                    // Calculate distance to country center
                    val distKm = haversineDistance(center.lat, center.lng, lat, lng)
                    // Include events within 3000km or globally significant
                    if (distKm < 3500.0) {
                        parsedList.add(
                            NasaNaturalEvent(
                                id = id,
                                title = title,
                                category = niceCat,
                                categoryIcon = icon,
                                date = date,
                                lat = lat,
                                lng = lng,
                                magnitude = mag
                            )
                        )
                    }
                }
            }
            parsedList.take(4)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchISSTelemetry(): ISSTelemetry? {
        return try {
            val response = client.get("https://api.wheretheiss.at/v1/satellites/25544")
            val body = response.bodyAsText()
            val obj = json.decodeFromString<JsonObject>(body)
            val lat = obj["latitude"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val lng = obj["longitude"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val alt = obj["altitude"]?.jsonPrimitive?.doubleOrNull ?: 420.0
            val vel = obj["velocity"]?.jsonPrimitive?.doubleOrNull ?: 27600.0
            val vis = obj["visibility"]?.jsonPrimitive?.content ?: "daylight"
            val ts = obj["timestamp"]?.jsonPrimitive?.longOrNull ?: (currentEpochMillis() / 1000)
            ISSTelemetry(lat, lng, alt, vel, vis, ts)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchGlobalNasaEvents(): List<NasaNaturalEvent> {
        return try {
            val response = client.get("https://eonet.gsfc.nasa.gov/api/v3/events?status=open&limit=30")
            val body = response.bodyAsText()
            val root = json.decodeFromString<JsonObject>(body)
            val events = root["events"]?.jsonArray ?: return emptyList()

            val list = mutableListOf<NasaNaturalEvent>()
            for (elem in events) {
                val ev = elem.jsonObject
                val id = ev["id"]?.jsonPrimitive?.content ?: continue
                val title = ev["title"]?.jsonPrimitive?.content ?: "Natural Event"
                val catObj = ev["categories"]?.jsonArray?.firstOrNull()?.jsonObject
                val catTitle = catObj?.get("title")?.jsonPrimitive?.content ?: "Hazard"
                val geom = ev["geometry"]?.jsonArray?.lastOrNull()?.jsonObject ?: continue
                val date = geom["date"]?.jsonPrimitive?.content ?: ""
                val coords = geom["coordinates"]?.jsonArray ?: continue
                if (coords.size >= 2) {
                    val lng = coords[0].jsonPrimitive.doubleOrNull ?: continue
                    val lat = coords[1].jsonPrimitive.doubleOrNull ?: continue
                    val icon = when {
                        catTitle.contains("Volcano", ignoreCase = true) -> "🌋"
                        catTitle.contains("Wildfire", ignoreCase = true) || catTitle.contains("Fire", ignoreCase = true) -> "🔥"
                        catTitle.contains("Storm", ignoreCase = true) || catTitle.contains("Cyclone", ignoreCase = true) -> "🌀"
                        catTitle.contains("Ice", ignoreCase = true) -> "🧊"
                        catTitle.contains("Flood", ignoreCase = true) || catTitle.contains("Water", ignoreCase = true) -> "🌊"
                        else -> "⚠️"
                    }
                    list.add(
                        NasaNaturalEvent(
                            id = id,
                            title = title,
                            category = catTitle,
                            categoryIcon = icon,
                            date = date,
                            lat = lat,
                            lng = lng
                        )
                    )
                }
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = (lat2 - lat1).toRadians
        val dLon = (lon2 - lon1).toRadians
        val a = sin(dLat / 2).pow(2) + cos(lat1.toRadians) * cos(lat2.toRadians) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
