package com.dirzaaulia.countries.ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.data.restcountries.capitalName
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.ui.dossier.CountryDossierSheet
import com.dirzaaulia.countries.ui.dossier.DossierViewModel
import com.dirzaaulia.countries.ui.globe.CountryPeekBar
import com.dirzaaulia.countries.ui.globe.FlightViewModel
import com.dirzaaulia.countries.ui.globe.FloatingExplorerBar
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import com.dirzaaulia.countries.ui.globe.QuizViewModel
import com.dirzaaulia.countries.ui.hud.FlightRouteHudCard
import com.dirzaaulia.countries.ui.hud.HazardDetailSheet
import com.dirzaaulia.countries.ui.hud.ISSTelemetryCard
import com.dirzaaulia.countries.ui.hud.QuizHudCard

/** Owns the Earth feature HUD/sheet composition; camera movement remains coordinated by App. */
@Composable
fun BoxScope.FeatureHudHost(
    countries: List<Country>,
    selectedCountry: Country?,
    showCountryDossier: Boolean,
    sunPos: SunPosition,
    showTimeMachine: Boolean,
    onShowCountryDossier: (Boolean) -> Unit,
    onSelectCountry: (String?) -> Unit,
    onOpenMeteorology: () -> Unit,
    onOpenWorldBank: () -> Unit,
    onOpenAdministrative: () -> Unit,
    onOpenSearch: () -> Unit,
    onToggleTimeMachine: () -> Unit,
    onOpenNasaCrisis: () -> Unit,
    dossierVm: DossierViewModel,
    hazardVm: HazardViewModel,
    issVm: IssViewModel,
    flightVm: FlightViewModel,
    quizVm: QuizViewModel,
    onFlyTo: (Double, Double, Float) -> Unit,
    isSupersonic: Boolean,
    onToggleSupersonic: () -> Unit,
) {
    val dossierState by dossierVm.uiState.collectAsState()
    val hazardState by hazardVm.uiState.collectAsState()
    val issState by issVm.uiState.collectAsState()
    val flightState by flightVm.uiState.collectAsState()
    val quizState by quizVm.uiState.collectAsState()

    val selectedHazard = hazardState.selectedHazard
    val selectedIss = issState.selectedIss
    val flightMode = flightState.isFlightMode
    val origin = flightState.flightOrigin
    val destination = flightState.flightDestination
    val distance = flightState.flightDistanceKm
    val quizMode = quizState.isQuizMode
    val target = quizState.quizTargetCountry
    val score = quizState.quizScore
    val streak = quizState.quizStreak
    val feedback = quizState.quizFeedback
    val correct = quizState.quizIsCorrect

    if (selectedCountry != null && showCountryDossier && !quizMode && !flightMode && selectedHazard == null && selectedIss == null) {
        CountryDossierSheet(
            country = selectedCountry,
            liveDetails = dossierState.liveDetails,
            isFetchingLive = dossierState.isFetchingLive,
            allCountries = countries,
            onClose = {
                onShowCountryDossier(false)
                onSelectCountry(null)
            },
            onNextCountry = { countries.filterNot { it.id == selectedCountry.id }.randomOrNull()?.let { onSelectCountry(it.id) } },
            onSelectCountry = { onSelectCountry(it.id) },
            onOpenMeteorology = onOpenMeteorology,
            onOpenWorldBank = onOpenWorldBank,
            onOpenAdministrativeDivisions = onOpenAdministrative,
            sunPos = sunPos,
        )
    }
    selectedHazard?.let { hazard ->
        HazardDetailSheet(hazard, onClose = { hazardVm.selectHazard(null) }, onCenterView = {
            hazardVm.selectHazard(null)
            onFlyTo(hazard.lat, hazard.lng, 2.2f)
        })
    }
    selectedIss?.let { telemetry ->
        ISSTelemetryCard(telemetry, onClose = { issVm.selectIss(null) }, onCenterView = {
            issVm.selectIss(null)
            onFlyTo(telemetry.latitude, telemetry.longitude, 1.6f)
        })
    }
    if (flightMode && origin != null && destination != null) {
        FlightRouteHudCard(
            origin = origin,
            destination = destination,
            distanceKm = distance,
            onRandomRoute = {
                flightVm.generateRandomFlightRoute(countries)
                origin.let { onFlyTo(it.center.lat, it.center.lng, 1.3f) }
            },
            onClose = { flightVm.toggleFlightMode(countries) },
            allCountries = countries,
            isSupersonic = isSupersonic,
            onToggleSupersonic = onToggleSupersonic,
            onSelectOrigin = flightVm::setFlightOrigin,
            onSelectDestination = flightVm::setFlightDestination,
        )
    }

    Box(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
    ) {
        when {
            selectedCountry != null && !showCountryDossier && selectedHazard == null && selectedIss == null && !flightMode ->
                CountryPeekBar(
                    country = selectedCountry,
                    capital =
                        dossierState.liveDetails
                            ?.restCountry
                            ?.capitalName
                            ?.takeIf { dossierState.selectedCountry?.id == selectedCountry.id && it.isNotBlank() && it != "N/A" }
                            ?: selectedCountry.capital.takeIf { it.isNotBlank() && it != "N/A" },
                    isLoading = dossierState.isFetchingLive,
                    onExpandDossier = { onShowCountryDossier(true) },
                )
            quizMode && target != null ->
                QuizHudCard(
                    targetCountry = target,
                    score = score,
                    streak = streak,
                    feedback = feedback,
                    isCorrect = correct,
                    onNextQuestion = { quizVm.generateNextQuizQuestion(countries) },
                    onEndQuiz = { quizVm.toggleQuizMode(countries) },
                )
            selectedCountry == null && selectedHazard == null && selectedIss == null && !flightMode && !showTimeMachine ->
                FloatingExplorerBar(
                    onOpenSearch = onOpenSearch,
                    onExploreRandom = { countries.randomOrNull()?.let { onSelectCountry(it.id) } },
                    showTimeMachine = showTimeMachine,
                    onToggleTimeMachine = onToggleTimeMachine,
                    onOpenNasaCrisis = onOpenNasaCrisis,
                    isFlightMode = flightMode,
                    onToggleFlightMode = {
                        flightVm.toggleFlightMode(countries)
                        origin?.let { onFlyTo(it.center.lat, it.center.lng, 1.3f) }
                    },
                    isQuizMode = quizMode,
                    onToggleQuizMode = { quizVm.toggleQuizMode(countries) },
                )
        }
    }
}
