package com.dirzaaulia.countries.data.repository

import com.dirzaaulia.countries.data.eclipse.EclipseFeedApiClient
import com.dirzaaulia.countries.domain.astronomy.EclipseFeed

class EclipseRepository(
    private val eclipseFeedApiClient: EclipseFeedApiClient,
) {
    suspend fun fetchEclipseFeed(): EclipseFeed? = eclipseFeedApiClient.fetchEclipseFeed()
}
