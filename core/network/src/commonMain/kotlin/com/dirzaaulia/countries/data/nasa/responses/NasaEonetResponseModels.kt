package com.dirzaaulia.countries.data.nasa.responses

import kotlinx.serialization.Serializable

@Serializable
internal data class NasaEonetResponse(
    val events: List<NasaEonetEvent> = emptyList(),
)

@Serializable
internal data class NasaEonetEvent(
    val id: String? = null,
    val title: String? = null,
    val categories: List<NasaEonetCategory> = emptyList(),
    val geometry: List<NasaEonetGeometry> = emptyList(),
)

@Serializable
internal data class NasaEonetCategory(
    val title: String? = null,
)

@Serializable
internal data class NasaEonetGeometry(
    val date: String? = null,
    val coordinates: List<Double> = emptyList(),
    val magnitudeValue: String? = null,
    val magnitudeUnit: String? = null,
)
