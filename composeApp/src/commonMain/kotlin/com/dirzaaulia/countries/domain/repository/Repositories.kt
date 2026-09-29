package com.dirzaaulia.countries.domain.repository

import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent

interface CountryRepository {
    suspend fun loadCountries(): List<Country>

    suspend fun loadCloudBytes(): ByteArray
}

interface CountryDetailRepository {
    suspend fun fetchLiveDetails(country: Country): LiveCountryDetails
}

interface HazardRepository {
    suspend fun fetchGlobalNasaEvents(forceRefresh: Boolean = false): List<NasaNaturalEvent>

    suspend fun fetchNearbyNasaEvents(
        center: com.dirzaaulia.countries.domain.country.LatLng,
        radiusKm: Double = 3500.0,
        limit: Int = 4,
    ): List<NasaNaturalEvent>
}

interface IssRepository {
    suspend fun fetchISSTelemetry(): ISSTelemetry?
}

interface EclipseRepository {
    suspend fun fetchEclipseFeed(): EclipseFeed?
}

interface TectonicRepository {
    suspend fun loadTectonicPlates(): List<com.dirzaaulia.countries.domain.tectonic.TectonicPlate>

    suspend fun fetchLiveEarthquakes(forceRefresh: Boolean = false): List<com.dirzaaulia.countries.domain.tectonic.Earthquake>
}
