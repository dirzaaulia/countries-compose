package com.dirzaaulia.countries.data.openmeteo.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenMeteoResponse(
    val current: OpenMeteoCurrent? = null,
    val daily: OpenMeteoDaily? = null,
    val hourly: OpenMeteoHourly? = null,
)

@Serializable
internal data class OpenMeteoCurrent(
    @SerialName("temperature_2m") val temperature: Double? = null,
    @SerialName("relative_humidity_2m") val humidity: Int? = null,
    @SerialName("weather_code") val weatherCode: Int? = null,
    @SerialName("wind_speed_10m") val windSpeed: Double? = null,
    @SerialName("surface_pressure") val surfacePressure: Double? = null,
    @SerialName("wind_direction_10m") val windDirection: Double? = null,
)

@Serializable
internal data class OpenMeteoDaily(
    val time: List<String> = emptyList(),
    @SerialName("weather_code") val weatherCodes: List<Int> = emptyList(),
    @SerialName("temperature_2m_max") val temperaturesMax: List<Double> = emptyList(),
    @SerialName("temperature_2m_min") val temperaturesMin: List<Double> = emptyList(),
    @SerialName("precipitation_probability_max") val precipitationMax: List<Int> = emptyList(),
    val sunrise: List<String> = emptyList(),
    val sunset: List<String> = emptyList(),
    @SerialName("uv_index_max") val uvIndexMax: List<Double> = emptyList(),
)

@Serializable
internal data class OpenMeteoHourly(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m") val temperatures: List<Double> = emptyList(),
    @SerialName("precipitation_probability") val precipitation: List<Int> = emptyList(),
    @SerialName("weather_code") val weatherCodes: List<Int> = emptyList(),
)
