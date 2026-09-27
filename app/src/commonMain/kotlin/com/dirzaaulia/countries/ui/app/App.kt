package com.dirzaaulia.countries.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dirzaaulia.countries.di.appModules
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.globe.rememberGlobeState
import com.dirzaaulia.countries.platform.currentEpochMillis
import com.dirzaaulia.countries.platform.formatLocalTime
import com.dirzaaulia.countries.platform.markStartupReady
import com.dirzaaulia.countries.ui.dossier.DossierViewModel
import com.dirzaaulia.countries.ui.globe.FlightViewModel
import com.dirzaaulia.countries.ui.globe.GlobeView
import com.dirzaaulia.countries.ui.globe.GlobeViewModel
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import com.dirzaaulia.countries.ui.globe.QuizViewModel
import com.dirzaaulia.countries.ui.hud.MissionControlTopBar
import com.dirzaaulia.countries.ui.moon.MoonView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinConfiguration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun App() {
    KoinApplication(
        configuration =
            koinConfiguration(declaration = {
                modules(
                    appModules,
                )
            }),
        content = {
            MaterialTheme(
                colorScheme =
                    darkColorScheme(
                        background = Color(0xFF03060C),
                        surface = Color(0xFF0B1220),
                        primary = Color(0xFF38BDF8),
                    ),
            ) {
                val viewModel: GlobeViewModel = koinViewModel()
                val dossierViewModel: DossierViewModel = koinViewModel()
                val hazardViewModel: HazardViewModel = koinViewModel()
                val issViewModel: IssViewModel = koinViewModel()
                val flightViewModel: FlightViewModel = koinViewModel()
                val quizViewModel: QuizViewModel = koinViewModel()

                val globeStateUi by viewModel.uiState.collectAsState()
                val dossierState by dossierViewModel.uiState.collectAsState()
                val hazardStateUi by hazardViewModel.uiState.collectAsState()
                val issStateUi by issViewModel.uiState.collectAsState()
                val flightStateUi by flightViewModel.uiState.collectAsState()
                val quizStateUi by quizViewModel.uiState.collectAsState()

                val countries = globeStateUi.countries
                val eclipseFeed = globeStateUi.eclipseFeed
                val isEclipseFeedLoading = globeStateUi.isEclipseFeedLoading
                val selectedCountryId = globeStateUi.selectedCountryId
                val showCountryDossier = globeStateUi.showCountryDossier
                val globalHazards = hazardStateUi.globalHazards
                val selectedHazard = hazardStateUi.selectedHazard
                val selectedIss = issStateUi.selectedIss
                val issTelemetry = issStateUi.issTelemetry
                val isFlightMode = flightStateUi.isFlightMode
                val flightRoute = flightStateUi.flightRoute
                val isQuizMode = quizStateUi.isQuizMode
                val quizTargetCountry = quizStateUi.quizTargetCountry
                val quizIsCorrect = quizStateUi.quizIsCorrect

                val globeState = rememberGlobeState()
                val moonState = rememberGlobeState()
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
                val scope = rememberCoroutineScope()

                var currentTimeMillis by remember { mutableStateOf(currentEpochMillis()) }
                var isTimeMachineLive by remember { mutableStateOf(true) }
                LaunchedEffect(isTimeMachineLive) {
                    while (isActive && isTimeMachineLive) {
                        currentTimeMillis = currentEpochMillis()
                        delay(30_000L.milliseconds)
                    }
                }
                val sunPos =
                    remember(currentTimeMillis) {
                        AstronomyMath.calculateSunPosition(currentTimeMillis)
                    }
                val moonInfo =
                    remember(currentTimeMillis) {
                        AstronomyMath.calculateMoonInfo(currentTimeMillis)
                    }
                val utcTimeStr =
                    remember(currentTimeMillis / 1000L) {
                        val utcMillis = ((currentTimeMillis % 86400000L) + 86400000L) % 86400000L
                        val hours = (utcMillis / 3600000L).toInt()
                        val minutes = ((utcMillis % 3600000L) / 60000L).toInt()
                        "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')} UTC"
                    }
                val localTimeStr =
                    remember(currentTimeMillis / 1000L) {
                        formatLocalTime(currentTimeMillis)
                    }

                var showBorders by remember { mutableStateOf(true) }
                var showSatellites by remember { mutableStateOf(true) }
                var showHazards by remember { mutableStateOf(true) }
                var showLegendSheet by remember { mutableStateOf(false) }
                var showMeteorologySheet by remember { mutableStateOf(false) }
                var showWorldBankSheet by remember { mutableStateOf(false) }
                var showNasaCrisisSheet by remember { mutableStateOf(false) }
                var showSearchSheet by remember { mutableStateOf(false) }
                var showTimeMachine by remember { mutableStateOf(false) }
                var showAdministrativeSheet by remember { mutableStateOf(false) }
                var isSupersonicFlight by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    try {
                        viewModel.loadInitialData()
                    } finally {
                        markStartupReady()
                    }
                }
                LaunchedEffect(Unit) {
                    viewModel.loadEclipseFeed()
                    hazardViewModel.load()
                }
                LaunchedEffect(showSatellites) {
                    if (showSatellites) {
                        while (isActive) {
                            issViewModel.refreshISSTelemetry()
                            delay(6000.milliseconds)
                        }
                    }
                }

                val selectedCountry =
                    remember(selectedCountryId, countries) {
                        countries.find { it.id == selectedCountryId }
                    }
                LaunchedEffect(selectedCountry) {
                    dossierViewModel.selectCountry(selectedCountry)
                }
                val isAnySheetOpen =
                    (selectedCountry != null && showCountryDossier) ||
                        showLegendSheet ||
                        showMeteorologySheet ||
                        showWorldBankSheet ||
                        showNasaCrisisSheet ||
                        showSearchSheet ||
                        showAdministrativeSheet ||
                        (showTimeMachine && pagerState.currentPage == 0) ||
                        selectedHazard != null ||
                        selectedIss != null
                LaunchedEffect(isAnySheetOpen) {
                    if (isAnySheetOpen) issViewModel.selectIss(null)
                }
                LaunchedEffect(selectedCountryId, showCountryDossier) {
                    val country = selectedCountry
                    if (country != null && !isQuizMode) {
                        if (showCountryDossier) {
                            globeState.stopAnimations()
                            globeState.snapTo(
                                country.center.lat.toFloat(),
                                -country.center.lng.toFloat(),
                                country.zoomLevel,
                            )
                        } else {
                            globeState.flyTo(
                                targetLat = country.center.lat.toFloat(),
                                targetLng = -country.center.lng.toFloat(),
                                targetZoom = country.zoomLevel,
                                durationMs = 650,
                            )
                        }
                    }
                }

                Box(Modifier.fillMaxSize().background(Color(0xFF03060C))) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        if (page == 0) {
                            GlobeView(
                                countries = countries,
                                selectedCountryId = selectedCountryId,
                                onCountrySelected = { clickedId ->
                                    if (isQuizMode && quizTargetCountry != null) {
                                        if (clickedId != null) {
                                            quizViewModel.handleQuizTap(
                                                clickedId,
                                                countries,
                                            )
                                        }
                                    } else {
                                        viewModel.selectCountry(clickedId, isQuizMode)
                                    }
                                },
                                state = globeState,
                                isPageActive = true,
                                isSheetOpen = isAnySheetOpen,
                                isSupersonic = isSupersonicFlight,
                                showBorders = showBorders,
                                showSatellites = showSatellites,
                                showHazards = showHazards,
                                issTelemetry = issTelemetry,
                                hazards = globalHazards,
                                flightRoute = if (isFlightMode) flightRoute else null,
                                quizTargetCountryId = if (isQuizMode) quizTargetCountry?.id else null,
                                quizIsCorrect = quizIsCorrect,
                                onHazardSelected = hazardViewModel::selectHazard,
                                onIssSelected = issViewModel::selectIss,
                                sunPos = sunPos,
                                showTimeMachine = showTimeMachine,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            MoonView(
                                moonInfo = moonInfo,
                                eclipseFeed = eclipseFeed,
                                isEclipseFeedLoading = isEclipseFeedLoading,
                                state = moonState,
                                isPageActive = true,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }

                    MissionControlTopBar(
                        currentPage = pagerState.currentPage,
                        onSelectPage = { targetPage ->
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    targetPage,
                                )
                            }
                        },
                        showBorders = showBorders,
                        onToggleBorders = { showBorders = !showBorders },
                        showSatellites = showSatellites,
                        onToggleSatellites = { showSatellites = !showSatellites },
                        showHazards = showHazards,
                        onToggleHazards = { showHazards = !showHazards },
                        moonDistanceKm = moonInfo.distanceKm,
                        localTime = localTimeStr,
                        utcTime = utcTimeStr,
                        onOpenLegend = { showLegendSheet = true },
                        onOpenSearch = { showSearchSheet = true },
                    )

                    if (pagerState.currentPage == 0) {
                        FeatureHudHost(
                            countries = countries,
                            selectedCountry = selectedCountry,
                            showCountryDossier = showCountryDossier,
                            sunPos = sunPos,
                            showTimeMachine = showTimeMachine,
                            onShowCountryDossier = viewModel::setShowCountryDossier,
                            onSelectCountry = { viewModel.selectCountry(it, isQuizMode) },
                            onOpenMeteorology = { showMeteorologySheet = true },
                            onOpenWorldBank = { showWorldBankSheet = true },
                            onOpenAdministrative = { showAdministrativeSheet = true },
                            onOpenSearch = { showSearchSheet = true },
                            onToggleTimeMachine = { showTimeMachine = !showTimeMachine },
                            onOpenNasaCrisis = { showNasaCrisisSheet = true },
                            dossierVm = dossierViewModel,
                            hazardVm = hazardViewModel,
                            issVm = issViewModel,
                            flightVm = flightViewModel,
                            quizVm = quizViewModel,
                            onFlyTo = { lat, lng, zoom ->
                                scope.launch {
                                    globeState.flyTo(
                                        lat.toFloat(),
                                        -lng.toFloat(),
                                        zoom,
                                    )
                                }
                            },
                            isSupersonic = isSupersonicFlight,
                            onToggleSupersonic = { isSupersonicFlight = !isSupersonicFlight },
                        )
                    }

                    AppSheetsOverlay(
                        showLegendSheet = showLegendSheet,
                        onCloseLegend = { showLegendSheet = false },
                        showMeteorologySheet = showMeteorologySheet,
                        onCloseMeteorology = { showMeteorologySheet = false },
                        showWorldBankSheet = showWorldBankSheet,
                        onCloseWorldBank = { showWorldBankSheet = false },
                        showNasaCrisisSheet = showNasaCrisisSheet,
                        onCloseNasaCrisis = { showNasaCrisisSheet = false },
                        onFlyToEpicenter = { hazard ->
                            showNasaCrisisSheet = false
                            hazardViewModel.selectHazard(hazard)
                            scope.launch {
                                globeState.flyTo(
                                    hazard.lat.toFloat(),
                                    -hazard.lng.toFloat(),
                                    2.2f,
                                )
                            }
                        },
                        showSearchSheet = showSearchSheet,
                        onCloseSearch = { showSearchSheet = false },
                        onSelectSearchCountry = { country ->
                            showSearchSheet = false
                            viewModel.selectCountry(country.id)
                        },
                        showAdministrativeSheet = showAdministrativeSheet,
                        onCloseAdministrative = { showAdministrativeSheet = false },
                        onCenterDivision = { division ->
                            showAdministrativeSheet = false
                            scope.launch {
                                globeState.flyTo(
                                    division.center.lat.toFloat(),
                                    -division.center.lng.toFloat(),
                                    division.zoomLevel,
                                )
                            }
                        },
                        showTimeMachine = showTimeMachine,
                        currentPage = pagerState.currentPage,
                        isSheetOpen = isAnySheetOpen,
                        currentTimeMillis = currentTimeMillis,
                        isTimeMachineLive = isTimeMachineLive,
                        onScrubStarted = { isTimeMachineLive = false },
                        onEpochSelected = { currentTimeMillis = it },
                        onResetTimeLive = {
                            isTimeMachineLive = true
                            currentTimeMillis = currentEpochMillis()
                        },
                        onCloseTimeMachine = { showTimeMachine = false },
                        selectedCountry = selectedCountry,
                        liveDetails = dossierState.liveDetails,
                        globalHazards = globalHazards,
                        countries = countries,
                    )
                }
            }
        },
    )
}
