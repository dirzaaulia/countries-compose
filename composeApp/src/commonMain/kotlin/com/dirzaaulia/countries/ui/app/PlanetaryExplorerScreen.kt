package com.dirzaaulia.countries.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.ui.hud.MissionControlTopBar
import com.dirzaaulia.countries.ui.mars.MarsView
import com.dirzaaulia.countries.ui.moon.MoonView
import kotlinx.coroutines.launch

@Composable
fun PlanetaryExplorerScreen(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    astronomy: ExplorerAstronomy,
    camera: GlobeState,
    moonCamera: GlobeState,
    marsCamera: GlobeState,
    pager: PagerState,
    actions: ExplorerActions,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().background(Color(0xFF03060C))) {
        ExplorerPages(
            features = features,
            controls = controls,
            astronomy = astronomy,
            camera = camera,
            moonCamera = moonCamera,
            marsCamera = marsCamera,
            pager = pager,
            actions = actions,
        )
        ExplorerTopBar(
            features = features,
            controls = controls,
            astronomy = astronomy,
            pager = pager,
            actions = actions,
        )
        if (pager.currentPage == 0) {
            FeatureHudHost(features, controls, astronomy.sun, actions)
        }
        ExplorerSheets(features, controls, pager.currentPage, actions)
    }
}

@Composable
private fun ExplorerPages(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    astronomy: ExplorerAstronomy,
    camera: GlobeState,
    moonCamera: GlobeState,
    marsCamera: GlobeState,
    pager: PagerState,
    actions: ExplorerActions,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = pager,
        userScrollEnabled = false,
        modifier = modifier.fillMaxSize(),
    ) { page ->
        if (page == pager.currentPage) {
            when (page) {
                0 -> ExplorerGlobe(features, controls, astronomy, camera, page, actions)
                1 ->
                    MoonView(
                        moonInfo = astronomy.moon,
                        modifier = Modifier.fillMaxSize(),
                        eclipseFeed = features.globe.eclipseFeed,
                        isEclipseFeedLoading = features.globe.isEclipseFeedLoading,
                        isPageActive = true,
                        state = moonCamera,
                    )

                else ->
                    MarsView(
                        state = marsCamera,
                        modifier = Modifier.fillMaxSize(),
                        sunPosition = astronomy.sun,
                        isPageActive = true,
                    )
            }
        }
    }
}

@Composable
private fun ExplorerTopBar(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    astronomy: ExplorerAstronomy,
    pager: PagerState,
    actions: ExplorerActions,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    MissionControlTopBar(
        currentPage = pager.currentPage,
        onSelectPage = { page -> scope.launch { pager.scrollToPage(page) } },
        showBorders = controls.showBorders,
        onToggleBorders = actions::toggleBorders,
        showSatellites = controls.showSatellites,
        onToggleSatellites = actions::toggleSatellites,
        showHazards = controls.showHazards,
        onToggleHazards = actions::toggleHazards,
        showAurora = controls.showAurora,
        onToggleAurora = actions::toggleAurora,
        showTimezones = features.timezone.isTimezoneLayerActive,
        onToggleTimezones = actions::toggleTimezoneLayer,
        showMarkets = features.timezone.showMarketCard,
        onToggleMarkets = actions::toggleMarketCard,
        showTectonic = features.tectonic.isTectonicLayerActive,
        onToggleTectonic = actions::toggleTectonicLayer,
        moonDistanceKm = astronomy.moon.distanceKm,
        localTime = astronomy.localTime,
        utcTime = astronomy.utcTime,
        satelliteFleet = features.satellite.fleet,
        selectedSatellite = features.satellite.selectedSatellite,
        onSelectSatellite = { sat ->
            actions.selectSatellite(sat)
            actions.flyTo(sat.lat, sat.lng, 1.6f)
        },
        onOpenLegend = { actions.setOverlay(ExplorerOverlay.LEGEND) },
        onOpenSearch = { actions.setOverlay(ExplorerOverlay.SEARCH) },
        onOpenSpaceWeather = { actions.setOverlay(ExplorerOverlay.SPACE_WEATHER) },
        modifier = modifier,
    )
}
