package com.dirzaaulia.countries.data.mars.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarsPhotosResponse(
    @SerialName("photos") val photos: List<MarsPhotoDto> = emptyList()
)

@Serializable
data class MarsPhotoDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("sol") val sol: Long? = null,
    @SerialName("camera") val camera: MarsCameraDto? = null,
    @SerialName("img_src") val imgSrc: String? = null,
    @SerialName("earth_date") val earthDate: String? = null,
    @SerialName("rover") val rover: MarsRoverDto? = null
)

@Serializable
data class MarsCameraDto(
    @SerialName("name") val name: String? = null,
    @SerialName("full_name") val fullName: String? = null
)

@Serializable
data class MarsRoverDto(
    @SerialName("name") val name: String? = null
)
