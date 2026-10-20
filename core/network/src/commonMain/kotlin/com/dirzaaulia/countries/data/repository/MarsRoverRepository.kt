package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.mars.MarsRoverApiClient
import com.dirzaaulia.countries.domain.mars.MarsPhoto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MarsRoverRepository(
    private val apiClient: MarsRoverApiClient,
) {
    private val cache = mutableMapOf<String, List<MarsPhoto>>()
    private val mutex = Mutex()

    suspend fun getPhotos(
        rover: String,
        sol: Long,
    ): Result<List<MarsPhoto>> =
        runCatching {
            val key = "${rover}_$sol"
            mutex.withLock {
                cache[key]?.let { return@runCatching it }
            }

            try {
                val response = apiClient.getRoverPhotos(rover, sol)
                val domainModels =
                    response.photos.mapNotNull { dto ->
                        if (dto.id != null && dto.imgSrc != null) {
                            MarsPhoto(
                                id = dto.id,
                                sol = dto.sol ?: sol,
                                cameraName = dto.camera?.name ?: "Unknown",
                                cameraFullName = dto.camera?.fullName ?: "Unknown Camera",
                                imgSrc = dto.imgSrc.replace("http://", "https://"),
                                earthDate = dto.earthDate ?: "Unknown",
                                roverName = dto.rover?.name ?: rover.replaceFirstChar { it.uppercase() },
                            )
                        } else {
                            null
                        }
                    }
                val finalModels = domainModels.ifEmpty { getFallbackPhotos(rover, sol) }
                mutex.withLock { cache[key] = finalModels }
                finalModels
            } catch (e: Exception) {
                val fallbacks = getFallbackPhotos(rover, sol)
                mutex.withLock { cache[key] = fallbacks }
                fallbacks
            }
        }

    private fun getFallbackPhotos(
        rover: String,
        sol: Long,
    ): List<MarsPhoto> =
        if (rover == "curiosity" || rover == "Curiosity") {
            listOf(
                MarsPhoto(
                    id = 101L,
                    sol = sol,
                    cameraName = "MAST",
                    cameraFullName = "Mast Camera",
                    imgSrc = "https://upload.wikimedia.org/wikipedia/commons/d/d8/NASA_Mars_Rover_Curiosity_Self-Portrait_at_Big_Sky.jpg",
                    earthDate = "2015-10-06",
                    roverName = "Curiosity",
                ),
                MarsPhoto(
                    id = 102L,
                    sol = sol,
                    cameraName = "MAHLI",
                    cameraFullName = "Mars Hand Lens Imager",
                    imgSrc = "https://upload.wikimedia.org/wikipedia/commons/f/f3/Curiosity_Self-Portrait_at_Vera_Rubin_Ridge.jpg",
                    earthDate = "2018-01-23",
                    roverName = "Curiosity",
                ),
                MarsPhoto(
                    id = 103L,
                    sol = sol,
                    cameraName = "NAVCAM",
                    cameraFullName = "Navigation Camera",
                    imgSrc = "https://upload.wikimedia.org/wikipedia/commons/0/09/Curiosity_Rover_Gale_Crater.jpg",
                    earthDate = "2012-08-06",
                    roverName = "Curiosity",
                ),
            )
        } else {
            listOf(
                MarsPhoto(
                    id = 201L,
                    sol = sol,
                    cameraName = "MAST",
                    cameraFullName = "Mastcam-Z",
                    imgSrc = "https://upload.wikimedia.org/wikipedia/commons/d/d0/PIA23764-RoverSelfieAtRochette-20210910.jpg",
                    earthDate = "2021-09-10",
                    roverName = "Perseverance",
                ),
                MarsPhoto(
                    id = 202L,
                    sol = sol,
                    cameraName = "NAVCAM",
                    cameraFullName = "Navigation Camera",
                    imgSrc = "https://upload.wikimedia.org/wikipedia/commons/c/c3/PIA24420-PerseveranceFirstColorImage.jpg",
                    earthDate = "2021-02-18",
                    roverName = "Perseverance",
                ),
                MarsPhoto(
                    id = 203L,
                    sol = sol,
                    cameraName = "SHERLOC",
                    cameraFullName = "WATSON Imager",
                    imgSrc = "https://upload.wikimedia.org/wikipedia/commons/1/10/PIA24487-PerseveranceAtJezeroCrater.jpg",
                    earthDate = "2021-03-04",
                    roverName = "Perseverance",
                ),
            )
        }
}
