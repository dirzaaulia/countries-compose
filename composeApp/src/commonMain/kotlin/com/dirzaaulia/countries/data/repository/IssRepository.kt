package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.iss.ISSTelemetryApiClient
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.domain.repository.IssRepository

class IssRepositoryImpl(
    private val issTelemetryApiClient: ISSTelemetryApiClient,
) : IssRepository {
    override suspend fun fetchISSTelemetry(): ISSTelemetry? = issTelemetryApiClient.fetchISSTelemetry()
}
