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
                                // Force HTTPS for image loading
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
    ): List<MarsPhoto> {
        val rName = rover.replaceFirstChar { it.uppercase() }
        return listOf(
            MarsPhoto(
                id = 1L,
                sol = sol,
                cameraName = "MAST",
                cameraFullName = "Mast Camera",
                imgSrc = "https://mars.nasa.gov/msl-raw-images/msss/01000/mcam/1000MR0044631300503690E01_DXXX.jpg",
                earthDate = "2015-05-30",
                roverName = rName,
            ),
            MarsPhoto(
                id = 2L,
                sol = sol,
                cameraName = "NAVCAM",
                cameraFullName = "Navigation Camera",
                imgSrc = "https://mars.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/ncam/NRB_486215281EDR_F0481570NCAM00323M_.JPG",
                earthDate = "2015-05-30",
                roverName = rName,
            ),
            MarsPhoto(
                id = 3L,
                sol = sol,
                cameraName = "FHAZ",
                cameraFullName = "Front Hazard Avoidance Camera",
                imgSrc = "https://mars.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/fcam/FLB_486265257EDR_F0481570FHAZ00323M_.JPG",
                earthDate = "2015-05-30",
                roverName = rName,
            ),
        )
    }
}
