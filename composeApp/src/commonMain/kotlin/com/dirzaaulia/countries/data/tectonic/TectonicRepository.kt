package com.dirzaaulia.countries.data.tectonic

import com.dirzaaulia.countries.domain.repository.TectonicRepository
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlinx.coroutines.CancellationException

class TectonicRepositoryImpl(
    private val usgsApiClient: UsgsApiClient,
) : TectonicRepository {
    private var cachedEarthquakes: List<Earthquake>? = null
    private var lastCacheTimestamp: Long = 0L

    override suspend fun loadTectonicPlates(): List<TectonicPlate> {
        return ALL_TECTONIC_PLATES
    }

    override suspend fun fetchLiveEarthquakes(forceRefresh: Boolean): List<Earthquake> {
        val now = currentEpochMillis()
        if (!forceRefresh && cachedEarthquakes != null && (now - lastCacheTimestamp < 15 * 60 * 1000L)) {
            return cachedEarthquakes!!
        }

        return try {
            val response = usgsApiClient.fetchEarthquakes()
            val parsed =
                response?.features?.mapNotNull { feature ->
                    val props = feature.properties ?: return@mapNotNull null
                    val coords = feature.geometry?.coordinates ?: return@mapNotNull null
                    if (coords.size < 2) return@mapNotNull null

                    val lng = coords[0]
                    val lat = coords[1]
                    val depthKm = if (coords.size > 2) coords[2] else 10.0
                    val mag = props.mag ?: 4.5

                    Earthquake(
                        id = feature.id ?: "eq_${props.time ?: now}",
                        title = props.title ?: "M $mag - ${props.place.orEmpty()}",
                        magnitude = mag,
                        place = props.place ?: "Unknown Location",
                        timeEpochMillis = props.time ?: now,
                        lat = lat,
                        lng = lng,
                        depthKm = depthKm,
                        tsunamiAlert = (props.tsunami ?: 0) > 0,
                        usgsUrl = props.url ?: "https://earthquake.usgs.gov",
                    )
                }.orEmpty()

            val result = parsed.ifEmpty { cachedEarthquakes ?: FALLBACK_EARTHQUAKES }
            cachedEarthquakes = result
            lastCacheTimestamp = now
            result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            cachedEarthquakes ?: FALLBACK_EARTHQUAKES
        }
    }
}
