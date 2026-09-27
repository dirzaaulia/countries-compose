package com.dirzaaulia.countries.data.geoboundaries.responses

import kotlinx.serialization.Serializable

@Serializable
internal data class GeoBoundariesMetadata(
    val shapeName: String? = null,
    val name: String? = null,
    val shapeISO: String? = null,
    val shapeID: String? = null,
    val shapeType: String? = null,
)

@Serializable
internal data class GeoBoundariesFeatureCollection(
    val features: List<GeoBoundariesFeature> = emptyList(),
)

@Serializable
internal data class GeoBoundariesFeature(
    val properties: GeoBoundariesFeatureProperties? = null,
)

@Serializable
internal data class GeoBoundariesFeatureProperties(
    val shapeName: String? = null,
    val shapeGroup: String? = null,
    val shapeISO: String? = null,
    val shapeID: String? = null,
    val name: String? = null,
    val shapeType: String? = null,
)
