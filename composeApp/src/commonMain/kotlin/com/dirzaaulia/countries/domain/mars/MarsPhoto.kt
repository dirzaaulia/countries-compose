package com.dirzaaulia.countries.domain.mars

data class MarsPhoto(
    val id: Long,
    val sol: Long,
    val cameraName: String,
    val cameraFullName: String,
    val imgSrc: String,
    val earthDate: String,
    val roverName: String,
)
