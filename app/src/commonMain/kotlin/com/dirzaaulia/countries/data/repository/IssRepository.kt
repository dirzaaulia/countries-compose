package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.iss.ISSTelemetryApiClient
import com.dirzaaulia.countries.domain.country.ISSTelemetry

class IssRepository(
    private val issTelemetryApiClient: ISSTelemetryApiClient,
) {
    suspend fun fetchISSTelemetry(): ISSTelemetry? = issTelemetryApiClient.fetchISSTelemetry()
}
