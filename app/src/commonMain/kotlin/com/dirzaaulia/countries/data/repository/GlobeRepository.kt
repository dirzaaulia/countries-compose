package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import com.dirzaaulia.countries.domain.country.AdminLevel
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent

/**
 * Composite repository delegating to specialized domain repositories.
 * Retained for backward compatibility. New features should inject domain repositories directly.
 */
class GlobeRepository(
    val countryRepository: CountryRepository,
    val countryDetailRepository: CountryDetailRepository,
    val hazardRepository: HazardRepository,
    val issRepository: IssRepository,
    val administrativeRepository: AdministrativeRepository,
    val eclipseRepository: EclipseRepository,
) {
    suspend fun loadCountries(): List<Country> = countryRepository.loadCountries()

    suspend fun loadCloudBytes(): ByteArray = countryRepository.loadCloudBytes()

    suspend fun fetchLiveDetails(country: Country): LiveCountryDetails = countryDetailRepository.fetchLiveDetails(country)

    suspend fun fetchAdministrativeDivisions(
        iso3: String,
        level: AdminLevel = AdminLevel.ADM1,
    ): List<AdministrativeDivision> = administrativeRepository.fetchAdministrativeDivisions(iso3, level)

    suspend fun fetchISSTelemetry(): ISSTelemetry? = issRepository.fetchISSTelemetry()

    suspend fun fetchGlobalNasaEvents(forceRefresh: Boolean = false): List<NasaNaturalEvent> = hazardRepository.fetchGlobalNasaEvents(forceRefresh)

    suspend fun fetchEclipseFeed(): EclipseFeed? = eclipseRepository.fetchEclipseFeed()
}
