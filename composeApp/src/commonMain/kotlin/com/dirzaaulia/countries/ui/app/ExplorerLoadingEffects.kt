package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dirzaaulia.countries.platform.markStartupReady
import com.dirzaaulia.countries.ui.globe.GlobeViewModel
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun ExplorerLoadingEffects(
    globeVm: GlobeViewModel,
    hazardVm: HazardViewModel,
    issVm: IssViewModel,
    showSatellites: Boolean,
) {
    LaunchedEffect(Unit) {
        try {
            globeVm.loadInitialData()
        } finally {
            markStartupReady()
        }
    }
    LaunchedEffect(Unit) {
        globeVm.loadEclipseFeed()
        hazardVm.load()
    }
    LaunchedEffect(showSatellites) {
        if (showSatellites) {
            while (isActive) {
                issVm.refreshISSTelemetry()
                delay(6000.milliseconds)
            }
        }
    }
}
