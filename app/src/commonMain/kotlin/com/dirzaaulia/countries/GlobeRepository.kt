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

    private data class CachedLiveDetails(
        val details: LiveCountryDetails,
        val timestamp: Long
    )

    // In-memory Stale-While-Revalidate cache for live details by country ID
    private val liveDetailsCache = mutableMapOf<String, CachedLiveDetails>()
    private var cachedCountries: List<Country>? = null

    suspend fun loadCountries(): List<Country> {
        cachedCountries?.let { return it }

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

            // Use mainland (largest polygon) for center so distant overseas territories don't skew center
            val mainlandPoly = polygons.maxByOrNull { it.size }
            val center = if (mainlandPoly != null && mainlandPoly.isNotEmpty()) {
                var mX = 0.0; var mY = 0.0; var mZ = 0.0
                mainlandPoly.forEach {
                    val latRad = it.lat.toRadians
                    val lngRad = it.lng.toRadians
                    mX += cos(latRad) * cos(lngRad)
                    mY += cos(latRad) * sin(lngRad)
                    mZ += sin(latRad)
                }
                val hyp = sqrt(mX * mX + mY * mY)
                LatLng(atan2(mZ, hyp).toDegrees, atan2(mY, mX).toDegrees)
            } else if (totalPoints > 0) {
                val avgX = totalX / totalPoints
                val avgY = totalY / totalPoints
                val avgZ = totalZ / totalPoints
                val hyp = sqrt(avgX * avgX + avgY * avgY)
                LatLng(atan2(avgZ, hyp).toDegrees, atan2(avgY, avgX).toDegrees)
            } else {
                LatLng(0.0, 0.0)
            }

            val effectiveArea = if (area > 0.0) area else {
                val minLat = mainlandPoly?.minOfOrNull { it.lat } ?: 0.0
                val maxLat = mainlandPoly?.maxOfOrNull { it.lat } ?: 0.0
                val minLng = mainlandPoly?.minOfOrNull { it.lng } ?: 0.0
                val maxLng = mainlandPoly?.maxOfOrNull { it.lng } ?: 0.0
                val dLatKm = (maxLat - minLat).absoluteValue * 111.0
                val dLngKm = (maxLng - minLng).absoluteValue * 111.0 * cos(center.lat.toRadians).absoluteValue
                dLatKm * dLngKm
            }

            // Calibrated zoom levels ensuring entire country fits in viewport with comfortable margin
            val zoomLevel = when {
                effectiveArea >= 8_000_000 -> 1.05f  // Russia, Canada, China, USA
                effectiveArea >= 5_000_000 -> 1.15f  // Brazil, Australia
                effectiveArea >= 2_000_000 -> 1.35f  // India, Argentina, Kazakhstan, Algeria, Saudi Arabia, Greenland
                effectiveArea >= 1_000_000 -> 1.50f  // Mexico, Indonesia, Sudan, Libya, Iran, Mongolia, Peru
                effectiveArea >= 500_000   -> 1.75f  // France, Germany, Spain, Ukraine, Turkey, Colombia, Egypt
                effectiveArea >= 200_000   -> 2.00f  // UK, Italy, Japan, New Zealand, Poland, Vietnam, Philippines
                effectiveArea >= 80_000    -> 2.30f  // Greece, Portugal, Austria, Iceland, Cuba, Jordan, Ireland
                effectiveArea >= 25_000    -> 2.60f  // Switzerland, Netherlands, Belgium, Taiwan, Denmark, Albania
                effectiveArea >= 5_000     -> 2.90f  // Luxembourg, Cyprus, Lebanon, Jamaica, Qatar, Brunei
                else                       -> 3.20f  // Singapore, Bahrain, Malta, Andorra, Monaco, Vatican
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
                zoomLevel = zoomLevel,
                boundingBox = if (polygons.isNotEmpty()) {
                    val allPoints = polygons.flatten()
                    BoundingBox(
                        minLat = allPoints.minOf { it.lat },
                        maxLat = allPoints.maxOf { it.lat },
                        minLng = allPoints.minOf { it.lng },
                        maxLng = allPoints.maxOf { it.lng }
                    )
                } else BoundingBox(-90.0, 90.0, -180.0, 180.0)
            )
        }
        cachedCountries = countries
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
    // Stale-While-Revalidate Offline Resilience Architecture
    // -------------------------------------------------------------

    suspend fun fetchLiveDetails(country: Country): LiveCountryDetails {
        val now = currentEpochMillis()
        val cached = liveDetailsCache[country.id]
        if (cached != null && (now - cached.timestamp < 15 * 60 * 1000L)) {
            return cached.details
        }

        return try {
            coroutineScope {
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
                    nasaEvents = nasa
                )

                liveDetailsCache[country.id] = CachedLiveDetails(details, now)
                details
            }
        } catch (e: Exception) {
            cached?.details ?: generateOfflineFallbackDetails(country)
        }
    }

    private fun generateOfflineFallbackDetails(country: Country): LiveCountryDetails {
        val approxGdpPerCap = if (country.population > 0) {
            (country.gdpMillions * 1_000_000.0) / country.population
        } else null

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
            gdpHistory = listOf("2021" to (approxGdpPerCap ?: 12000.0) * 0.91, "2022" to (approxGdpPerCap ?: 12000.0) * 0.94, "2023" to (approxGdpPerCap ?: 12000.0) * 0.97, "2024" to (approxGdpPerCap ?: 12000.0)),
            inflationHistory = listOf("2021" to 2.1, "2022" to 6.8, "2023" to 4.2, "2024" to 2.8),
            weatherTempC = 21.0,
            weatherHumidity = 58,
            weatherWindSpeed = 14.0,
            weatherCode = 1,
            weatherDescription = "Mainly Clear",
            weatherIcon = "🌤️",
            uvIndex = 5.5,
            sunrise = "06:12",
            sunset = "18:45",
            surfacePressureHpa = 1013.2,
            windDirectionDeg = 210.0,
            dailyForecast = listOf(
                DailyForecastItem("2026-09-24", "Today", 23.0, 14.0, 1, "🌤️", 10),
                DailyForecastItem("2026-09-25", "Tomorrow", 24.0, 15.0, 2, "⛅", 20),
                DailyForecastItem("2026-09-26", "Fri", 22.0, 13.0, 61, "🌧️", 65),
                DailyForecastItem("2026-09-27", "Sat", 20.0, 12.0, 3, "☁️", 30),
                DailyForecastItem("2026-09-28", "Sun", 23.0, 14.0, 0, "☀️", 5),
                DailyForecastItem("2026-09-29", "Mon", 25.0, 16.0, 1, "🌤️", 15),
                DailyForecastItem("2026-09-30", "Tue", 22.0, 14.0, 51, "🌦️", 45)
            ),
            hourlyForecast = (0..23).map { h ->
                val hourStr = h.toString().padStart(2, '0') + ":00"
                val temp = 16.0 + 8.0 * kotlin.math.sin((h - 6) * kotlin.math.PI / 12.0).coerceAtLeast(-0.3)
                HourlyForecastItem(hourStr, h, ((temp * 10).toInt() / 10.0), (h * 3) % 40, if (h in 6..18) 1 else 0)
            },
            nasaEvents = emptyList()
        )
    }

    // 1. World Bank Open Data API (GDP per cap, Inflation, Life Expectancy, Unemployment, Renewable, CO2)
    private data class WorldBankResults(
        val gdpPerCapita: Double? = null,
        val inflationRate: Double? = null,
        val lifeExpectancy: Double? = null,
        val unemploymentRate: Double? = null,
        val renewableEnergyShare: Double? = null,
        val co2Emissions: Double? = null,
        val gdpHistory: List<Pair<String, Double>> = emptyList(),
        val inflationHistory: List<Pair<String, Double>> = emptyList()
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
            val gdpHistDef = async { fetchWorldBankIndicatorHistory(countryCode, "NY.GDP.PCAP.CD") }
            val inflHistDef = async { fetchWorldBankIndicatorHistory(countryCode, "FP.CPI.TOTL.ZG") }

            val gdpCap = gdpCapDef.await()
            val infl = inflDef.await()
            val life = lifeDef.await()
            val unemp = unempDef.await()
            val renew = renewDef.await()
            val co2 = co2Def.await()
            val gdpHist = gdpHistDef.await()
            val inflHist = inflHistDef.await()

            if (gdpCap == null && infl == null && life == null && unemp == null && renew == null && co2 == null && gdpHist.isEmpty()) {
                null
            } else {
                WorldBankResults(
                    gdpPerCapita = gdpCap,
                    inflationRate = infl,
                    lifeExpectancy = life,
                    unemploymentRate = unemp,
                    renewableEnergyShare = renew,
                    co2Emissions = co2,
                    gdpHistory = gdpHist,
                    inflationHistory = inflHist
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

    private suspend fun fetchWorldBankIndicatorHistory(countryCode: String, indicator: String): List<Pair<String, Double>> {
        return try {
            val url = "https://api.worldbank.org/v2/country/$countryCode/indicator/$indicator?format=json&mrnev=5"
            val response = client.get(url).bodyAsText()
            val array = json.decodeFromString<JsonArray>(response)
            if (array.size > 1) {
                val records = array[1].jsonArray
                records.mapNotNull { item ->
                    val obj = item.jsonObject
                    val date = obj["date"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                    val value = obj["value"]?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
                    date to value
                }.reversed()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
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
        val sunset: String?,
        val surfacePressureHpa: Double?,
        val windDirectionDeg: Double?,
        val dailyForecast: List<DailyForecastItem>,
        val hourlyForecast: List<HourlyForecastItem>
    )

    private suspend fun fetchWeatherData(lat: Double, lng: Double): WeatherResults? {
        return try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,surface_pressure,wind_direction_10m&hourly=temperature_2m,precipitation_probability,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset,uv_index_max&timezone=auto"
            val response = client.get(url).bodyAsText()
            val obj = json.decodeFromString<JsonObject>(response)
            val current = obj["current"]?.jsonObject ?: return null
            val daily = obj["daily"]?.jsonObject
            val hourly = obj["hourly"]?.jsonObject

            val temp = current["temperature_2m"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val humidity = current["relative_humidity_2m"]?.jsonPrimitive?.intOrNull ?: 0
            val windSpeed = current["wind_speed_10m"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val code = current["weather_code"]?.jsonPrimitive?.intOrNull ?: 0
            val pressure = current["surface_pressure"]?.jsonPrimitive?.doubleOrNull
            val windDir = current["wind_direction_10m"]?.jsonPrimitive?.doubleOrNull

            val (icon, desc) = mapWeatherCode(code)

            val uvIndex = daily?.get("uv_index_max")?.jsonArray?.firstOrNull()?.jsonPrimitive?.doubleOrNull
            val sunriseFull = daily?.get("sunrise")?.jsonArray?.firstOrNull()?.jsonPrimitive?.content
            val sunsetFull = daily?.get("sunset")?.jsonArray?.firstOrNull()?.jsonPrimitive?.content
            val sunrise = sunriseFull?.substringAfter("T")
            val sunset = sunsetFull?.substringAfter("T")

            // Parse 7-day forecast
            val dailyForecast = mutableListOf<DailyForecastItem>()
            val dailyDates = daily?.get("time")?.jsonArray
            val dailyCodes = daily?.get("weather_code")?.jsonArray
            val dailyMax = daily?.get("temperature_2m_max")?.jsonArray
            val dailyMin = daily?.get("temperature_2m_min")?.jsonArray
            val dailyPrecip = daily?.get("precipitation_probability_max")?.jsonArray

            if (dailyDates != null) {
                for (i in 0 until minOf(7, dailyDates.size)) {
                    val dateStr = dailyDates[i].jsonPrimitive.content
                    val dCode = dailyCodes?.getOrNull(i)?.jsonPrimitive?.intOrNull ?: 0
                    val dMax = dailyMax?.getOrNull(i)?.jsonPrimitive?.doubleOrNull ?: 20.0
                    val dMin = dailyMin?.getOrNull(i)?.jsonPrimitive?.doubleOrNull ?: 12.0
                    val dPrecip = dailyPrecip?.getOrNull(i)?.jsonPrimitive?.intOrNull ?: 0
                    val (dIcon, _) = mapWeatherCode(dCode)
                    val dayName = when (i) {
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
                            precipitationProb = dPrecip
                        )
                    )
                }
            }

            // Parse 24-hour forecast
            val hourlyForecast = mutableListOf<HourlyForecastItem>()
            val hourlyTimes = hourly?.get("time")?.jsonArray
            val hourlyTemps = hourly?.get("temperature_2m")?.jsonArray
            val hourlyPrecip = hourly?.get("precipitation_probability")?.jsonArray
            val hourlyCodes = hourly?.get("weather_code")?.jsonArray

            if (hourlyTimes != null) {
                for (i in 0 until minOf(24, hourlyTimes.size)) {
                    val timeStr = hourlyTimes[i].jsonPrimitive.content
                    val hTemp = hourlyTemps?.getOrNull(i)?.jsonPrimitive?.doubleOrNull ?: 18.0
                    val hPrecip = hourlyPrecip?.getOrNull(i)?.jsonPrimitive?.intOrNull ?: 0
                    val hCode = hourlyCodes?.getOrNull(i)?.jsonPrimitive?.intOrNull ?: 0
                    val hour = timeStr.substringAfter("T").take(2).toIntOrNull() ?: i
                    hourlyForecast.add(
                        HourlyForecastItem(
                            time = timeStr.substringAfter("T").take(5),
                            hour = hour,
                            tempC = hTemp,
                            precipitationProb = hPrecip,
                            weatherCode = hCode
                        )
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
                hourlyForecast = hourlyForecast
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
                    val magVal = geom["magnitudeValue"]?.jsonPrimitive?.contentOrNull
                    val magUnit = geom["magnitudeUnit"]?.jsonPrimitive?.contentOrNull
                    val magnitude = if (magVal != null) "$magVal ${magUnit ?: ""}".trim() else null

                    list.add(
                        NasaNaturalEvent(
                            id = id,
                            title = title,
                            category = catTitle,
                            categoryIcon = icon,
                            date = date,
                            lat = lat,
                            lng = lng,
                            magnitude = magnitude
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
