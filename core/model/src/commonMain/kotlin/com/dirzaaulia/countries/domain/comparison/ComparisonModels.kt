package com.dirzaaulia.countries.domain.comparison

import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LatLng

data class CountryComparison(
    val primaryCountry: Country,
    val secondaryCountry: Country,
    val populationRatio: Double = 0.0,
    val areaRatio: Double = 0.0,
    val gdpRatio: Double = 0.0,
    val distanceKm: Double = 0.0,
)

fun buildComparisonData(
    primary: Country,
    secondary: Country,
): CountryComparison {
    val popRatio = if (secondary.population > 0) primary.population.toDouble() / secondary.population else 0.0
    val areaRatio = if (secondary.areaSqKm > 0) primary.areaSqKm / secondary.areaSqKm else 0.0
    val gdpRatio = if (secondary.gdpMillions > 0) primary.gdpMillions / secondary.gdpMillions else 0.0

    return CountryComparison(
        primaryCountry = primary,
        secondaryCountry = secondary,
        populationRatio = popRatio,
        areaRatio = areaRatio,
        gdpRatio = gdpRatio,
    )
}

fun projectTransposedPolygon(
    countryToTranspose: Country,
    newCenter: LatLng,
): List<List<LatLng>> {
    val dLat = newCenter.lat - countryToTranspose.center.lat
    val dLng = newCenter.lng - countryToTranspose.center.lng

    return countryToTranspose.polygons.map { poly ->
        poly.map { point ->
            val nLat = (point.lat + dLat).coerceIn(-89.9, 89.9)
            val nLng = ((point.lng + dLng + 180.0) % 360.0 + 360.0) % 360.0 - 180.0
            LatLng(nLat, nLng)
        }
    }
}
