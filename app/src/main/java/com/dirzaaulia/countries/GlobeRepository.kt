package com.dirzaaulia.countries

import android.content.Context
import kotlinx.serialization.json.*
import kotlin.math.*

class GlobeRepository(private val context: Context) {
    
    private val json = Json { ignoreUnknownKeys = true }

    fun loadCountries(): List<Country> {
        android.util.Log.d("GlobeRepo", "Loading countries...")
        val geoJsonString = context.assets.open("countries.geojson").bufferedReader().use { it.readText() }
        android.util.Log.d("GlobeRepo", "GeoJSON size: ${geoJsonString.length}")
        val geoJson = json.decodeFromString<GeoJson>(geoJsonString)
        android.util.Log.d("GlobeRepo", "Decoded features: ${geoJson.features.size}")
        
        val countries = geoJson.features.map { feature ->
            val name = feature.properties["name"]?.jsonPrimitive?.content ?: "Unknown"
            val id = feature.properties["iso_a3"]?.jsonPrimitive?.content ?: name
            
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
                        val latRad = Math.toRadians(it.lat)
                        val lngRad = Math.toRadians(it.lng)
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
                            val latRad = Math.toRadians(it.lat)
                            val lngRad = Math.toRadians(it.lng)
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
                val lng = atan2(avgY, avgX)
                val lat = atan2(avgZ, hyp)
                LatLng(Math.toDegrees(lat), Math.toDegrees(lng))
            } else {
                LatLng(0.0, 0.0)
            }
            
            // Calculate zoom level based on coordinate spread
            var minLat = 90.0; var maxLat = -90.0
            var minLng = 180.0; var maxLng = -180.0
            polygons.flatten().forEach {
                minLat = minOf(minLat, it.lat); maxLat = maxOf(maxLat, it.lat)
                minLng = minOf(minLng, it.lng); maxLng = maxOf(maxLng, it.lng)
            }
            val spread = maxOf(maxLat - minLat, maxLng - minLng)
            val zoomLevel = when {
                spread < 1.0 -> 6.0f  // Very small (Vatican, Singapore)
                spread < 5.0 -> 4.0f  // Small (Timor Leste)
                spread < 15.0 -> 2.5f // Medium
                else -> 1.2f          // Large (Russia, Brazil)
            }
            
            Country(id, name, polygons, center, zoomLevel)
        }
        android.util.Log.d("GlobeRepo", "Total countries parsed: ${countries.size}")
        return countries
    }
    
    private fun parsePolygon(coords: JsonArray): List<LatLng> {
        return coords.map {
            val point = it.jsonArray
            LatLng(point[1].jsonPrimitive.double, point[0].jsonPrimitive.double)
        }
    }
}
