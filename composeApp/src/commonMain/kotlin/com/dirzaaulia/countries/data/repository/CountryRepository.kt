package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.domain.country.BoundingBox
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.GeoJson
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.globe.toDegrees
import com.dirzaaulia.countries.domain.globe.toRadians
import com.dirzaaulia.countries.domain.repository.CountryRepository
import com.dirzaaulia.countries.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private val jsonFormatter =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

class CountryRepositoryImpl : CountryRepository {
    private var cachedCountries: List<Country>? = null

    override suspend fun loadCountries(): List<Country> =
        withContext(Dispatchers.Default) {
            cachedCountries?.let { return@withContext it }

            val geoJsonBytes = Res.readBytes("files/countries.geojson")
            val geoJson = jsonFormatter.decodeFromString<GeoJson>(geoJsonBytes.decodeToString())

            val countries =
                geoJson.features.map { feature ->
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
                    val center =
                        if (mainlandPoly != null && mainlandPoly.isNotEmpty()) {
                            var mX = 0.0
                            var mY = 0.0
                            var mZ = 0.0
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

                    val effectiveArea =
                        run {
                            val minLat = mainlandPoly?.minOfOrNull { it.lat } ?: 0.0
                            val maxLat = mainlandPoly?.maxOfOrNull { it.lat } ?: 0.0
                            val minLng = mainlandPoly?.minOfOrNull { it.lng } ?: 0.0
                            val maxLng = mainlandPoly?.maxOfOrNull { it.lng } ?: 0.0
                            val dLatKm = (maxLat - minLat).absoluteValue * 111.0
                            val dLngKm = (maxLng - minLng).absoluteValue * 111.0 * cos(center.lat.toRadians).absoluteValue
                            dLatKm * dLngKm
                        }

                    // Calibrated zoom levels ensuring entire country fits in viewport with comfortable margin
                    val zoomLevel =
                        when {
                            effectiveArea >= 8_000_000 -> 1.05f // Russia, Canada, China, USA
                            effectiveArea >= 5_000_000 -> 1.15f // Brazil, Australia
                            effectiveArea >= 2_000_000 -> 1.35f // India, Argentina, Kazakhstan, Algeria, Saudi Arabia, Greenland
                            effectiveArea >= 1_000_000 -> 1.50f // Mexico, Indonesia, Sudan, Libya, Iran, Mongolia, Peru
                            effectiveArea >= 500_000 -> 1.75f // France, Germany, Spain, Ukraine, Turkey, Colombia, Egypt
                            effectiveArea >= 200_000 -> 2.00f // UK, Italy, Japan, New Zealand, Poland, Vietnam, Philippines
                            effectiveArea >= 80_000 -> 2.30f // Greece, Portugal, Austria, Iceland, Cuba, Jordan, Ireland
                            effectiveArea >= 25_000 -> 2.60f // Switzerland, Netherlands, Belgium, Taiwan, Denmark, Albania
                            effectiveArea >= 5_000 -> 2.90f // Luxembourg, Cyprus, Lebanon, Jamaica, Qatar, Brunei
                            else -> 3.20f // Singapore, Bahrain, Malta, Andorra, Monaco, Vatican
                        }

                    Country(
                        id = id,
                        name = name,
                        formalName = formalName,
                        continent = continent,
                        subregion = subregion,
                        iso2 = iso2,
                        population = popEst,
                        areaSqKm = effectiveArea,
                        gdpMillions = gdpEst,
                        incomeGroup = incomeGroup,
                        economy = economy,
                        polygons = polygons,
                        center = center,
                        zoomLevel = zoomLevel,
                        boundingBox =
                            if (polygons.isNotEmpty()) {
                                val allPoints = polygons.flatten()
                                BoundingBox(
                                    minLat = allPoints.minOf { it.lat },
                                    maxLat = allPoints.maxOf { it.lat },
                                    minLng = allPoints.minOf { it.lng },
                                    maxLng = allPoints.maxOf { it.lng },
                                )
                            } else {
                                BoundingBox(-90.0, 90.0, -180.0, 180.0)
                            },
                    )
                }
            cachedCountries = countries
            countries
        }

    override suspend fun loadCloudBytes(): ByteArray = Res.readBytes("files/earth_clouds.jpg")

    private fun parsePolygon(jsonCoords: JsonArray): List<LatLng> =
        jsonCoords.map { point ->
            val p = point.jsonArray
            LatLng(p[1].jsonPrimitive.double, p[0].jsonPrimitive.double)
        }
}
