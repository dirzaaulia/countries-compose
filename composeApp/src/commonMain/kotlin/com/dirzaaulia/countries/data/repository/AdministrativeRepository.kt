package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.geoboundaries.GeoBoundariesApiClient
import com.dirzaaulia.countries.domain.country.AdminLevel
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.repository.AdministrativeRepository

class AdministrativeRepositoryImpl(
    private val geoBoundariesApiClient: GeoBoundariesApiClient,
) : AdministrativeRepository {
    private val cachedAdministrativeDivisions = mutableMapOf<String, List<AdministrativeDivision>>()

    override suspend fun fetchAdministrativeDivisions(
        iso3: String,
        level: AdminLevel,
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
