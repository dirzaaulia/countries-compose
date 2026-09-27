package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.nasa.NasaEonetApiClient
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.globe.toRadians
import com.dirzaaulia.countries.domain.repository.HazardRepository
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class HazardRepositoryImpl(
    private val nasaEonetApiClient: NasaEonetApiClient,
) : HazardRepository {
    private data class CachedNasaEvents(
        val events: List<NasaNaturalEvent>,
        val timestamp: Long,
    )

    private var cachedNasaEvents: CachedNasaEvents? = null

    override suspend fun fetchGlobalNasaEvents(forceRefresh: Boolean): List<NasaNaturalEvent> {
        val now = currentEpochMillis()
        val cached = cachedNasaEvents
        if (!forceRefresh && cached != null && (now - cached.timestamp < 30 * 60 * 1000L)) {
            return cached.events
        }

        val events = nasaEonetApiClient.fetchGlobalNasaEvents()
        if (events.isNotEmpty()) {
            cachedNasaEvents = CachedNasaEvents(events, now)
        }
        return events
    }

    override suspend fun fetchNearbyNasaEvents(
        center: LatLng,
        radiusKm: Double,
        limit: Int,
    ): List<NasaNaturalEvent> {
        val globalEvents = fetchGlobalNasaEvents()
        return globalEvents
            .filter { hazard ->
                haversineDistance(center.lat, center.lng, hazard.lat, hazard.lng) <= radiusKm
            }.take(limit)
    }

    private fun haversineDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = (lat2 - lat1).toRadians
        val dLon = (lon2 - lon1).toRadians
        val a = sin(dLat / 2).pow(2) + cos(lat1.toRadians) * cos(lat2.toRadians) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
