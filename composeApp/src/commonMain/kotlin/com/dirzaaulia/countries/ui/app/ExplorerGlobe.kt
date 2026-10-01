package com.dirzaaulia.countries.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.ui.globe.GlobeView

@Composable
internal fun ExplorerGlobe(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    astronomy: ExplorerAstronomy,
    camera: GlobeState,
    page: Int,
    actions: ExplorerActions,
) {
    GlobeView(
        countries = features.globe.countries,
        selectedCountryId = features.globe.selectedCountryId,
        onCountrySelected = actions::onCountryTap,
        state = camera,
        isPageActive = page == 1,
        isSheetOpen = features.isAnySheetOpen(controls, page),
        isSupersonic = controls.isSupersonicFlight,
        showBorders = controls.showBorders,
        showSatellites = controls.showSatellites,
        showHazards = controls.showHazards,
        showAurora = controls.showAurora,
        kpIndex = features.spaceWeather.kpIndex,
        satelliteFleet = features.satellite.fleet,
        selectedSatellite = features.satellite.selectedSatellite,
        onSatelliteSelected = actions::selectSatellite,
        issTelemetry = features.iss.issTelemetry,
        hazards = features.hazards.globalHazards,
        flightRoute = if (features.flight.isFlightMode) features.flight.flightRoute else null,
        quizTargetCountryId = if (features.quiz.isQuizMode) features.quiz.quizTargetCountry?.id else null,
        quizIsCorrect = features.quiz.quizIsCorrect,
        onHazardSelected = actions::selectHazard,
        onIssSelected = actions::selectIss,
        sunPos = astronomy.sun,
        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
        comparisonCountryA = features.comparison.countryA,
        comparisonCountryB = features.comparison.countryB,
        isComparing = features.comparison.isComparing,
        isTimezoneLayerActive = features.timezone.isTimezoneLayerActive,
        selectedMeridianOffset = features.timezone.selectedMeridianOffset,
        isTectonicLayerActive = features.tectonic.isTectonicLayerActive,
        tectonicPlates = features.tectonic.plates,
        earthquakes = features.tectonic.earthquakes,
        selectedPlateId = features.tectonic.selectedPlate?.id,
        selectedEarthquakeId = features.tectonic.selectedEarthquake?.id,
        minMagnitudeFilter = features.tectonic.minMagnitudeFilter,
        onEarthquakeSelected = actions::selectEarthquake,
        isMarketLayerActive = features.timezone.isMarketLayerActive,
        markets = features.timezone.markets,
        selectedMarketId = features.timezone.selectedMarket?.id,
        currentUtcTimeMillis = controls.currentTimeMillis,
        onMarketSelected = actions::selectMarket,
        modifier = Modifier.fillMaxSize(),
    )
}
