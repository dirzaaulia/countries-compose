package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.repository.CountryDetailRepository
import com.dirzaaulia.countries.domain.repository.CountryRepository
import com.dirzaaulia.countries.domain.repository.EclipseRepository
import com.dirzaaulia.countries.domain.repository.HazardRepository
import com.dirzaaulia.countries.domain.repository.IssRepository

/**
 * Composite repository delegating to specialized domain repositories.
 * Retained for backward compatibility. New features should inject domain repositories directly.
 */
class GlobeRepository(
    val countryRepository: CountryRepository,
    val countryDetailRepository: CountryDetailRepository,
    val hazardRepository: HazardRepository,
    val issRepository: IssRepository,
    val eclipseRepository: EclipseRepository,
) {
    suspend fun loadCountries(): List<Country> = countryRepository.loadCountries()

    suspend fun loadCloudBytes(): ByteArray = countryRepository.loadCloudBytes()

    suspend fun fetchLiveDetails(country: Country): LiveCountryDetails = countryDetailRepository.fetchLiveDetails(country)

    suspend fun fetchISSTelemetry(): ISSTelemetry? = issRepository.fetchISSTelemetry()

    suspend fun fetchGlobalNasaEvents(forceRefresh: Boolean = false): List<NasaNaturalEvent> = hazardRepository.fetchGlobalNasaEvents(forceRefresh)

    suspend fun fetchEclipseFeed(): EclipseFeed? = eclipseRepository.fetchEclipseFeed()
}
