package com.dirzaaulia.countries.data.geoboundaries

import com.dirzaaulia.countries.data.geoboundaries.responses.GeoBoundariesFeatureCollection
import com.dirzaaulia.countries.data.geoboundaries.responses.GeoBoundariesMetadata
import com.dirzaaulia.countries.domain.country.AdminLevel
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.country.BoundingBox
import com.dirzaaulia.countries.domain.country.LatLng
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class GeoBoundariesApiClient(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun fetchAdministrativeDivisions(
        iso: String,
        level: AdminLevel,
    ): List<AdministrativeDivision> {
        val cleanIso = iso.trim().uppercase()
        if (cleanIso.isBlank() || cleanIso == "-99") return emptyList()

        val iso3 = if (cleanIso.length == 2) iso2ToIso3(cleanIso) else cleanIso
        val levelStr = level.name

        // Lightweight REST API metadata endpoint (returns pure JSON attributes without geometry overhead)
        val apiUrl = "https://www.geoboundaries.org/api/current/gbOpen/$iso3/$levelStr/"

        return try {
            val responseText = client.get(apiUrl).bodyAsText()
            val divisions = json.decodeFromString(ListSerializer(GeoBoundariesMetadata.serializer()), responseText)

            divisions.mapNotNull { division ->
                val shapeName =
                    division.shapeName
                        ?: division.name
                        ?: return@mapNotNull null
                val shapeISO =
                    division.shapeISO
                        ?: division.shapeID
                        ?: shapeName
                val shapeType = division.shapeType.orEmpty()

                AdministrativeDivision(
                    id = "$iso3-$levelStr-$shapeISO-${shapeName.hashCode()}",
                    code = shapeISO,
                    name = shapeName,
                    level = level,
                    center = LatLng(0.0, 0.0),
                    boundaryPolygons = emptyList(),
                    attribution = "geoBoundaries Open Data REST API",
                    shapeType = shapeType,
                    boundingBox = BoundingBox(-90.0, 90.0, -180.0, 180.0),
                )
            }
        } catch (e: Exception) {
            // Fallback lightweight GeoJSON parsing if API endpoint is unreachable
            fetchFallbackGeoJson(iso3, levelStr, level)
        }
    }

    private suspend fun fetchFallbackGeoJson(
        iso3: String,
        levelStr: String,
        level: AdminLevel,
    ): List<AdministrativeDivision> {
        return try {
            val url = "https://raw.githubusercontent.com/wmgeolab/geoBoundaries/main/releaseStatic/gbOpen/$iso3/$levelStr/geoBoundaries-$iso3-$levelStr-simplified.geojson"
            val responseText = client.get(url).bodyAsText()
            val features = json.decodeFromString<GeoBoundariesFeatureCollection>(responseText).features

            features.mapNotNull { feature ->
                val props = feature.properties ?: return@mapNotNull null
                val shapeName =
                    props.shapeName
                        ?: props.shapeGroup
                        ?: props.name
                        ?: return@mapNotNull null
                val shapeISO =
                    props.shapeISO
                        ?: props.shapeID
                        ?: shapeName
                val shapeType = props.shapeType.orEmpty()

                AdministrativeDivision(
                    id = "$iso3-$levelStr-$shapeISO-${shapeName.hashCode()}",
                    code = shapeISO,
                    name = shapeName,
                    level = level,
                    center = LatLng(0.0, 0.0),
                    boundaryPolygons = emptyList(),
                    attribution = "geoBoundaries Open Data",
                    shapeType = shapeType,
                    boundingBox = BoundingBox(-90.0, 90.0, -180.0, 180.0),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun iso2ToIso3(iso2: String): String =
        when (iso2) {
            "ID" -> "IDN"
            "US" -> "USA"
            "GB" -> "GBR"
            "CA" -> "CAN"
            "AU" -> "AUS"
            "DE" -> "DEU"
            "FR" -> "FRA"
            "IT" -> "ITA"
            "ES" -> "ESP"
            "JP" -> "JPN"
            "CN" -> "CHN"
            "IN" -> "IND"
            "BR" -> "BRA"
            "RU" -> "RUS"
            "MX" -> "MEX"
            "KR" -> "KOR"
            "ZA" -> "ZAF"
            "AR" -> "ARG"
            "CL" -> "CHL"
            "CO" -> "COL"
            "PH" -> "PHL"
            "MY" -> "MYS"
            "SG" -> "SGP"
            "TH" -> "THA"
            "VN" -> "VNM"
            "NZ" -> "NZL"
            "NL" -> "NLD"
            "BE" -> "BEL"
            "CH" -> "CHE"
            "SE" -> "SWE"
            "NO" -> "NOR"
            "FI" -> "FIN"
            "DK" -> "DNK"
            "PL" -> "POL"
            "UA" -> "UKR"
            "TR" -> "TUR"
            "SA" -> "SAU"
            "AE" -> "ARE"
            "EG" -> "EGY"
            "NG" -> "NGA"
            "KE" -> "KEN"
            else -> iso2
        }
}
