package com.dirzaaulia.countries.data.tectonic

import kotlinx.serialization.Serializable

@Serializable
data class UsgsFeatureCollectionResponse(
    val features: List<UsgsFeatureResponse> = emptyList(),
)

@Serializable
data class UsgsFeatureResponse(
    val id: String? = null,
    val properties: UsgsPropertiesResponse? = null,
    val geometry: UsgsGeometryResponse? = null,
)

@Serializable
data class UsgsPropertiesResponse(
    val mag: Double? = null,
    val place: String? = null,
    val time: Long? = null,
    val title: String? = null,
    val url: String? = null,
    val tsunami: Int? = null,
)

@Serializable
data class UsgsGeometryResponse(
    val type: String? = null,
    val coordinates: List<Double> = emptyList(),
)
