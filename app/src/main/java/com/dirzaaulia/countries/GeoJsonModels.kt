package com.dirzaaulia.countries

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GeoJson(
    val type: String,
    val features: List<Feature>
)

@Serializable
data class Feature(
    val type: String,
    val properties: Map<String, JsonElement>,
    val geometry: Geometry
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: JsonElement // Can be List<List<List<Double>>> or List<List<List<List<Double>>>>
)

data class Country(
    val id: String,
    val name: String,
    val polygons: List<List<LatLng>>,
    val center: LatLng,
    val zoomLevel: Float
)

data class LatLng(val lat: Double, val lng: Double)
