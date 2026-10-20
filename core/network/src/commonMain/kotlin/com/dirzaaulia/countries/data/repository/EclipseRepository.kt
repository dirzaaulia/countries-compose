package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.eclipse.EclipseFeedApiClient
import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import com.dirzaaulia.countries.domain.repository.EclipseRepository

class EclipseRepositoryImpl(
    private val eclipseFeedApiClient: EclipseFeedApiClient,
) : EclipseRepository {
    override suspend fun fetchEclipseFeed(): EclipseFeed? = eclipseFeedApiClient.fetchEclipseFeed()
}
