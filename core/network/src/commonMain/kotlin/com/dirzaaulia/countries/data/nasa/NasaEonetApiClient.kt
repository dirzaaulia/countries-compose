package com.dirzaaulia.countries.data.nasa

import com.dirzaaulia.countries.data.nasa.responses.NasaEonetResponse
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.util.sanitizeHtmlEntities
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class NasaEonetApiClient(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun fetchGlobalNasaEvents(): List<NasaNaturalEvent> =
        try {
            val response = client.get("https://eonet.gsfc.nasa.gov/api/v3/events?status=open&limit=30")
            val body = response.bodyAsText()
            val events = json.decodeFromString<NasaEonetResponse>(body).events

            val list = mutableListOf<NasaNaturalEvent>()
            for (event in events) {
                val id = event.id ?: continue
                val title = event.title ?: "Natural Event"
                val catTitle = event.categories.firstOrNull()?.title ?: "Hazard"
                val geometry = event.geometry.lastOrNull() ?: continue
                val date = geometry.date.orEmpty()
                val coords = geometry.coordinates
                if (coords.size >= 2) {
                    val lng = coords[0]
                    val lat = coords[1]
                    val icon =
                        when {
                            catTitle.contains("Volcano", ignoreCase = true) -> "[VOLCANO]"
                            catTitle.contains("Wildfire", ignoreCase = true) || catTitle.contains("Fire", ignoreCase = true) -> "[FIRE]"
                            catTitle.contains("Storm", ignoreCase = true) || catTitle.contains("Cyclone", ignoreCase = true) -> "[STORM]"
                            catTitle.contains("Ice", ignoreCase = true) -> "[ICE]"
                            catTitle.contains("Flood", ignoreCase = true) || catTitle.contains("Water", ignoreCase = true) -> "[FLOOD]"
                            else -> "[ALERT]"
                        }
                    val magVal = geometry.magnitudeValue
                    val magUnit = geometry.magnitudeUnit
                    val magnitude = if (magVal != null) "$magVal ${magUnit ?: ""}".trim() else null

                    list.add(
                        NasaNaturalEvent(
                            id = id,
                            title = title.sanitizeHtmlEntities(),
                            category = catTitle.sanitizeHtmlEntities(),
                            categoryIcon = icon,
                            date = date,
                            lat = lat,
                            lng = lng,
                            magnitude = magnitude,
                        ),
                    )
                }
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
}
