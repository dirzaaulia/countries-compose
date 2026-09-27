package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.geoboundaries.GeoBoundariesApiClient
import com.dirzaaulia.countries.domain.country.AdminLevel
import com.dirzaaulia.countries.domain.country.AdministrativeDivision

class AdministrativeRepository(
    private val geoBoundariesApiClient: GeoBoundariesApiClient,
) {
    private val cachedAdministrativeDivisions = mutableMapOf<String, List<AdministrativeDivision>>()

    suspend fun fetchAdministrativeDivisions(
        iso3: String,
        level: AdminLevel = AdminLevel.ADM1,
    ): List<AdministrativeDivision> {
        val cacheKey = "$iso3-${level.name}"
        cachedAdministrativeDivisions[cacheKey]?.let { return it }
        val divisions = geoBoundariesApiClient.fetchAdministrativeDivisions(iso3, level)
        if (divisions.isNotEmpty()) {
            cachedAdministrativeDivisions[cacheKey] = divisions
        }
        return divisions
    }
}
