package com.dirzaaulia.countries.ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.data.restcountries.capitalName
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import com.dirzaaulia.countries.ui.dossier.CountryDossierSheet
import com.dirzaaulia.countries.ui.globe.CountryPeekBar
import com.dirzaaulia.countries.ui.globe.FloatingExplorerBar
import com.dirzaaulia.countries.ui.hud.FlightRouteHudCard
import com.dirzaaulia.countries.ui.hud.HazardDetailSheet
import com.dirzaaulia.countries.ui.hud.ISSTelemetryCard
import com.dirzaaulia.countries.ui.hud.MissionControlTopBar
import com.dirzaaulia.countries.ui.hud.QuizHudCard
import com.dirzaaulia.countries.ui.satellite.SatelliteTelemetrySheet

@Composable
fun BoxScope.FeatureHudHost(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    astronomy: ExplorerAstronomy,
    activePage: Int,
    actions: ExplorerActions,
) {
    if (activePage != 1) return

    MissionControlTopBar(
        currentPage = activePage,
        onSelectPage = actions::selectPage,
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
        showTectonic = features.tectonic.isTectonicLayerActive,
        onToggleTectonic = actions::toggleTectonicLayer,
        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
        onToggleTimeMachine = actions::toggleTimeMachine,
        showMissionControl = controls.overlay == ExplorerOverlay.MISSION_CONTROL,
        onOpenMissionControl = { actions.setOverlay(ExplorerOverlay.MISSION_CONTROL) },
        onCloseMissionControl = { actions.setOverlay(null) },
        localTime = astronomy.localTime,
        utcTime = astronomy.utcTime,
        satelliteFleet = features.satellite.fleet,
        selectedSatellite = features.satellite.selectedSatellite,
        onSelectSatellite = actions::selectSatellite,
        onOpenLegend = { actions.setOverlay(ExplorerOverlay.LEGEND) },
        onOpenSearch = { actions.setOverlay(ExplorerOverlay.SEARCH) },
        onOpenSpaceWeather = { actions.setOverlay(ExplorerOverlay.SPACE_WEATHER) },
        modifier = Modifier.align(Alignment.TopCenter),
    )

    val country = features.selectedCountry
    val hazard = features.hazards.selectedHazard
    val iss = features.iss.selectedIss
    val satellite = features.satellite.selectedSatellite
    if (country != null &&
        features.globe.showCountryDossier &&
        !features.quiz.isQuizMode &&
        !features.flight.isFlightMode &&
        hazard == null &&
        iss == null &&
        satellite == null
    ) {
        DossierHud(features, country, astronomy.sun, actions)
    }
    hazard?.let { HazardHud(it, actions) }
    iss?.let { IssHud(it, actions) }
    satellite?.let { SatelliteHud(it, features.satellite.nextPassOverSelectedCountry, actions, country) }
    if (features.flight.isFlightMode &&
        features.flight.flightOrigin != null &&
        features.flight.flightDestination != null
    ) {
        FlightHud(features, controls, actions)
    }
    Box(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
    ) {
        ExplorerBottomHud(features, controls, actions)
    }
}

@Composable
private fun DossierHud(
    features: ExplorerFeatures,
    country: Country,
    sunPos: SunPosition,
    actions: ExplorerActions,
) {
    CountryDossierSheet(
        country = country,
        liveDetails = features.dossier.liveDetails,
        isFetchingLive = features.dossier.isFetchingLive,
        allCountries = features.globe.countries,
        onClose = {
            actions.setDossierOpen(false)
            actions.selectCountry(null)
        },
        onNextCountry = { actions.nextCountry(country) },
        onSelectCountry = { actions.selectCountry(it.id) },
        onOpenMeteorology = { actions.setOverlay(ExplorerOverlay.METEOROLOGY) },
        onOpenWorldBank = { actions.setOverlay(ExplorerOverlay.WORLD_BANK) },
        onCompareCountry = { actions.startComparison(country) },
        sunPos = sunPos,
    )
}

@Composable
private fun HazardHud(
    hazard: NasaNaturalEvent,
    actions: ExplorerActions,
) {
    HazardDetailSheet(hazard, onClose = { actions.selectHazard(null) }, onCenterView = {
        actions.selectHazard(null)
        actions.flyTo(hazard.lat, hazard.lng, 2.2f)
    })
}

@Composable
private fun IssHud(
    iss: ISSTelemetry,
    actions: ExplorerActions,
) {
    ISSTelemetryCard(iss, onClose = { actions.clearIss() }, onCenterView = {
        actions.clearIss()
        actions.flyTo(iss.latitude, iss.longitude, 1.6f)
    })
}

@Composable
private fun SatelliteHud(
    satellite: SatelliteTelemetry,
    nextPassOverhead: String?,
    actions: ExplorerActions,
    selectedCountry: Country?,
) {
    LaunchedEffect(satellite.id, selectedCountry?.id) {
        if (selectedCountry != null) {
            actions.calculateNextPassForCountry(selectedCountry.center.lat, selectedCountry.center.lng)
        }
    }

    SatelliteTelemetrySheet(
        satellite = satellite,
        nextPassOverhead = if (selectedCountry != null) nextPassOverhead else null,
        onClose = { actions.selectSatellite(null) },
        onCenterView = {
            actions.selectSatellite(null)
            actions.flyTo(satellite.lat, satellite.lng, 1.6f)
        },
    )
}

@Composable
private fun FlightHud(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    actions: ExplorerActions,
) {
    val origin = features.flight.flightOrigin ?: return
    val destination = features.flight.flightDestination ?: return
    FlightRouteHudCard(
        origin = origin,
        destination = destination,
        distanceKm = features.flight.flightDistanceKm,
        onRandomRoute = {
            actions.randomFlightRoute()
            actions.flyTo(origin.center.lat, origin.center.lng, 1.3f)
        },
        onClose = actions::toggleFlightMode,
        allCountries = features.globe.countries,
        isSupersonic = controls.isSupersonicFlight,
        onToggleSupersonic = actions::toggleSupersonic,
        onSelectOrigin = actions::setFlightOrigin,
        onSelectDestination = actions::setFlightDestination,
    )
}

@Composable
private fun ExplorerBottomHud(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    actions: ExplorerActions,
) {
    val country = features.selectedCountry
    val hazard = features.hazards.selectedHazard
    val iss = features.iss.selectedIss
    val flight = features.flight
    val quiz = features.quiz
    when {
        country != null &&
            !features.globe.showCountryDossier &&
            hazard == null &&
            iss == null &&
            !flight.isFlightMode -> CountryPeekHud(features, country, actions)
        quiz.isQuizMode && quiz.quizTargetCountry != null -> QuizHud(features, actions)
        country == null &&
            hazard == null &&
            iss == null &&
            !flight.isFlightMode &&
            controls.overlay != ExplorerOverlay.TIME_MACHINE -> ExplorerControlsHud(features, controls, actions)
    }
}

@Composable
private fun CountryPeekHud(
    features: ExplorerFeatures,
    country: Country,
    actions: ExplorerActions,
) {
    val details = features.dossier
    CountryPeekBar(
        country = country,
        capital =
            details.liveDetails
                ?.restCountry
                ?.capitalName
                ?.takeIf { details.selectedCountry?.id == country.id && it.isNotBlank() && it != "N/A" }
                ?: country.capital.takeIf { it.isNotBlank() && it != "N/A" },
        isLoading = details.isFetchingLive,
        onExpandDossier = { actions.setDossierOpen(true) },
    )
}

@Composable
private fun QuizHud(
    features: ExplorerFeatures,
    actions: ExplorerActions,
) {
    val quiz = features.quiz
    val target = quiz.quizTargetCountry ?: return
    QuizHudCard(
        targetCountry = target,
        score = quiz.quizScore,
        streak = quiz.quizStreak,
        feedback = quiz.quizFeedback,
        isCorrect = quiz.quizIsCorrect,
        onNextQuestion = actions::nextQuizQuestion,
        onEndQuiz = actions::toggleQuizMode,
    )
}

@Composable
private fun ExplorerControlsHud(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    actions: ExplorerActions,
) {
    FloatingExplorerBar(
        onOpenSearch = { actions.setOverlay(ExplorerOverlay.SEARCH) },
        onExploreRandom = actions::randomCountry,
        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
        onToggleTimeMachine = actions::toggleTimeMachine,
        onOpenNasaCrisis = { actions.setOverlay(ExplorerOverlay.NASA_CRISIS) },
        isFlightMode = features.flight.isFlightMode,
        onToggleFlightMode = {
            actions.toggleFlightMode()
            features.flight.flightOrigin?.let { actions.flyTo(it.center.lat, it.center.lng, 1.3f) }
        },
        isQuizMode = features.quiz.isQuizMode,
        onToggleQuizMode = actions::toggleQuizMode,
        onOpenSpaceWeather = { actions.setOverlay(ExplorerOverlay.SPACE_WEATHER) },
        onToggleMarketCard = actions::toggleMarketCard,
        onOpenSolarSystem = { actions.setOverlay(ExplorerOverlay.SOLAR_SYSTEM) },
    )
}
