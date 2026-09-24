package com.dirzaaulia.countries

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.ui.dossier.CountryDossierSheet
import com.dirzaaulia.countries.ui.dossier.FloatingExplorerBar
import com.dirzaaulia.countries.ui.dossier.MeteorologyStationSheet
import com.dirzaaulia.countries.ui.dossier.NasaCrisisMonitorSheet
import com.dirzaaulia.countries.ui.dossier.WorldBankDashboardSheet
import com.dirzaaulia.countries.ui.hud.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF03060C),
            surface = Color(0xFF0B1220),
            primary = Color(0xFF38BDF8)
        )
    ) {
        val repository = remember { GlobeRepository() }
        var countries by remember { mutableStateOf<List<Country>>(emptyList()) }
        var selectedCountryId by remember { mutableStateOf<String?>(null) }
        val globeState = rememberGlobeState()
        val moonState = rememberGlobeState()
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
        val scope = rememberCoroutineScope()

        // Real-Time Continuous Astronomical Ticker (updates every 30 seconds)
        var currentTimeMillis by remember { mutableStateOf(currentEpochMillis()) }
        LaunchedEffect(Unit) {
            while (isActive) {
                delay(30_000L)
                currentTimeMillis = currentEpochMillis()
            }
        }

        val sunPos = remember(currentTimeMillis / 30_000L) {
            AstronomyMath.calculateSunPosition(currentTimeMillis)
        }
        val moonInfo = remember(currentTimeMillis / 30_000L) {
            AstronomyMath.calculateMoonInfo(currentTimeMillis)
        }

        val utcTimeStr = remember(currentTimeMillis / 60_000L) {
            val utcMillis = ((currentTimeMillis % 86400000L) + 86400000L) % 86400000L
            val hours = (utcMillis / 3600000L).toInt()
            val minutes = ((utcMillis % 3600000L) / 60000L).toInt()
            "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')} UTC"
        }

        val localTimeStr = remember(currentTimeMillis / 60_000L) {
            formatLocalTime(currentTimeMillis)
        }

        var liveDetails by remember { mutableStateOf<LiveCountryDetails?>(null) }
        var isFetchingLive by remember { mutableStateOf(false) }

        // Layer Toggles
        var showBorders by remember { mutableStateOf(true) }
        var showSatellites by remember { mutableStateOf(true) }
        var showHazards by remember { mutableStateOf(true) }
        var isFlightMode by remember { mutableStateOf(false) }
        var isQuizMode by remember { mutableStateOf(false) }
        var showLegendSheet by remember { mutableStateOf(false) }
        var showMeteorologySheet by remember { mutableStateOf(false) }
        var showWorldBankSheet by remember { mutableStateOf(false) }
        var showNasaCrisisSheet by remember { mutableStateOf(false) }

        // Real-time telemetry & hazard state
        var issTelemetry by remember { mutableStateOf<ISSTelemetry?>(null) }
        var selectedIss by remember { mutableStateOf<ISSTelemetry?>(null) }
        var globalHazards by remember { mutableStateOf<List<NasaNaturalEvent>>(emptyList()) }
        var selectedHazard by remember { mutableStateOf<NasaNaturalEvent?>(null) }

        // Flight Simulator State
        var flightOrigin by remember { mutableStateOf<Country?>(null) }
        var flightDestination by remember { mutableStateOf<Country?>(null) }
        var flightRoute by remember { mutableStateOf<List<LatLng>?>(null) }
        var flightDistanceKm by remember { mutableStateOf(0.0) }

        // Geography Quiz State
        var quizTargetCountry by remember { mutableStateOf<Country?>(null) }
        var quizScore by remember { mutableStateOf(0) }
        var quizStreak by remember { mutableStateOf(0) }
        var quizFeedback by remember { mutableStateOf<String?>(null) }
        var quizIsCorrect by remember { mutableStateOf<Boolean?>(null) }

        // Initial Data Load + EONET periodic refresh (every 30 minutes)
        LaunchedEffect(Unit) {
            countries = repository.loadCountries()
            globalHazards = repository.fetchGlobalNasaEvents()
            while (isActive) {
                delay(1_800_000L) // 30 minutes
                globalHazards = repository.fetchGlobalNasaEvents()
            }
        }

        // Periodic ISS Orbit Telemetry Polling (every 6 seconds when layer is active)
        LaunchedEffect(showSatellites) {
            if (showSatellites) {
                while (isActive) {
                    val tele = repository.fetchISSTelemetry()
                    if (tele != null) {
                        issTelemetry = tele
                        if (selectedIss != null) {
                            selectedIss = tele
                        }
                    }
                    delay(6000)
                }
            }
        }

        var showCountryDossier by remember { mutableStateOf(false) }
        var isSupersonicFlight by remember { mutableStateOf(false) }

        val selectedCountry = remember(selectedCountryId, countries) {
            countries.find { it.id == selectedCountryId }
        }

        // Orchestrated Country Selection: Smooth 3D Globe camera flight to center the country FIRST,
        // then open dossier immediately with skeleton loaders while live data streams in the background.
        LaunchedEffect(selectedCountryId) {
            val country = selectedCountry
            if (country != null && !isQuizMode) {
                showCountryDossier = false
                liveDetails = null
                isFetchingLive = true
                // Start network fetch in background — sheet opens before it finishes
                launch {
                    liveDetails = repository.fetchLiveDetails(country)
                    isFetchingLive = false
                }
                // Fly globe to center country, then instantly open dossier
                globeState.flyTo(
                    targetLat = country.center.lat.toFloat(),
                    targetLng = -country.center.lng.toFloat(),
                    targetZoom = country.zoomLevel
                )
                showCountryDossier = true
            } else {
                showCountryDossier = false
                liveDetails = null
                isFetchingLive = false
            }
        }

        // Quiz Generator Function
        fun generateNextQuizQuestion() {
            if (countries.isNotEmpty()) {
                quizTargetCountry = countries.random()
                quizFeedback = null
                quizIsCorrect = null
                selectedCountryId = null
            }
        }

        // Flight Route Generator Function
        fun generateRandomFlightRoute() {
            if (countries.size >= 2) {
                val origin = countries.random()
                val other = countries.filter { it.id != origin.id }
                val destination = other.random()
                flightOrigin = origin
                flightDestination = destination
                flightRoute = AstronomyMath.calculateGreatCircleArc(origin.center, destination.center, 50)
                flightDistanceKm = AstronomyMath.calculateGreatCircleDistance(origin.center, destination.center)
                scope.launch {
                    globeState.flyTo(
                        targetLat = origin.center.lat.toFloat(),
                        targetLng = -origin.center.lng.toFloat(),
                        targetZoom = 1.3f
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF03060C))
        ) {
            // Horizontal Pager: Page 0 = Planet Earth Globe, Page 1 = Moon Explorer
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { page ->
                if (page == 0) {
                    GlobeView(
                        countries = countries,
                        selectedCountryId = selectedCountryId,
                        onCountrySelected = { clickedId ->
                            if (isQuizMode && quizTargetCountry != null) {
                                val tappedCountry = countries.find { it.id == clickedId }
                                if (clickedId == quizTargetCountry?.id) {
                                    quizScore += 100 + (quizStreak * 25)
                                    quizStreak += 1
                                    quizFeedback = "Correct! That is ${quizTargetCountry?.name}!"
                                    quizIsCorrect = true
                                    selectedCountryId = clickedId
                                } else if (tappedCountry != null) {
                                    quizStreak = 0
                                    quizFeedback = "Wrong! That is ${tappedCountry.name}. Keep looking for ${quizTargetCountry?.name}."
                                    quizIsCorrect = false
                                    selectedCountryId = clickedId
                                }
                            } else {
                                selectedCountryId = clickedId
                                selectedHazard = null
                                selectedIss = null
                            }
                        },
                        state = globeState,
                        isPageActive = page == 0,
                        isSheetOpen = (selectedCountry != null && showCountryDossier) ||
                            showLegendSheet || showMeteorologySheet || showWorldBankSheet || showNasaCrisisSheet ||
                            (selectedHazard != null) || (selectedIss != null),
                        isSupersonic = isSupersonicFlight,
                        showBorders = showBorders,
                        showSatellites = showSatellites,
                        showHazards = showHazards,
                        issTelemetry = issTelemetry,
                        hazards = globalHazards,
                        flightRoute = if (isFlightMode) flightRoute else null,
                        quizTargetCountryId = if (isQuizMode) quizTargetCountry?.id else null,
                        quizIsCorrect = quizIsCorrect,
                        onHazardSelected = { hazard ->
                            selectedHazard = hazard
                            selectedCountryId = null
                            selectedIss = null
                        },
                        onIssSelected = { iss ->
                            selectedIss = iss
                            selectedHazard = null
                            selectedCountryId = null
                        },
                        sunPos = sunPos,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    MoonView(
                        moonInfo = moonInfo,
                        state = moonState,
                        isPageActive = page == 1,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Mission Control Top HUD with Celestial Switcher & Compact Layers
            MissionControlTopBar(
                currentPage = pagerState.currentPage,
                onSelectPage = { targetPage ->
                    scope.launch {
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                showBorders = showBorders,
                onToggleBorders = { showBorders = !showBorders },
                showSatellites = showSatellites,
                onToggleSatellites = { showSatellites = !showSatellites },
                showHazards = showHazards,
                onToggleHazards = { showHazards = !showHazards },
                isFlightMode = isFlightMode,
                onToggleFlightMode = {
                    isFlightMode = !isFlightMode
                    if (isFlightMode) {
                        isQuizMode = false
                        generateRandomFlightRoute()
                    } else {
                        flightRoute = null
                    }
                },
                isQuizMode = isQuizMode,
                onToggleQuizMode = {
                    isQuizMode = !isQuizMode
                    if (isQuizMode) {
                        isFlightMode = false
                        flightRoute = null
                        selectedCountryId = null
                        generateNextQuizQuestion()
                    }
                },
                moonDistanceKm = moonInfo.distanceKm,
                localTime = localTimeStr,
                utcTime = utcTimeStr,
                onOpenLegend = { showLegendSheet = true }
            )

            // Bottom Overlay: Country Dossier, Quiz Card, Flight Card, or Hazards (only when on Earth page)
            if (pagerState.currentPage == 0) {
                // Official Material 3 Modal Bottom Sheet for Country Profile Dossier
                if (selectedCountry != null && showCountryDossier && !isQuizMode && !isFlightMode && selectedHazard == null && selectedIss == null) {
                    CountryDossierSheet(
                        country = selectedCountry,
                        liveDetails = liveDetails,
                        isFetchingLive = isFetchingLive,
                        allCountries = countries,
                        onClose = {
                            showCountryDossier = false
                            selectedCountryId = null
                        },
                        onCenterView = {
                            scope.launch {
                                globeState.flyTo(
                                    targetLat = selectedCountry.center.lat.toFloat(),
                                    targetLng = -selectedCountry.center.lng.toFloat(),
                                    targetZoom = selectedCountry.zoomLevel
                                )
                            }
                        },
                        onNextCountry = {
                            val otherCountries = countries.filter { it.id != selectedCountry.id }
                            if (otherCountries.isNotEmpty()) {
                                showCountryDossier = false
                                selectedCountryId = otherCountries.random().id
                            }
                        },
                        onSelectCountry = { neighbor ->
                            showCountryDossier = false
                            selectedCountryId = neighbor.id
                        },
                        onOpenMeteorology = { showMeteorologySheet = true },
                        onOpenWorldBank = { showWorldBankSheet = true },
                        onOpenNasaCrisis = { showNasaCrisisSheet = true },
                        sunPos = sunPos
                    )
                }

                // NASA Natural Hazard Detail Sheet (Unified Modal Bottom Sheet)
                if (selectedHazard != null) {
                    HazardDetailSheet(
                        hazard = selectedHazard!!,
                        onClose = { selectedHazard = null },
                        onCenterView = {
                            scope.launch {
                                globeState.flyTo(
                                    targetLat = selectedHazard!!.lat.toFloat(),
                                    targetLng = -selectedHazard!!.lng.toFloat(),
                                    targetZoom = 1.8f
                                )
                            }
                        }
                    )
                }

                // Live ISS Orbital Telemetry Sheet (Unified Modal Bottom Sheet)
                if (selectedIss != null) {
                    ISSTelemetryCard(
                        telemetry = selectedIss!!,
                        onClose = { selectedIss = null },
                        onCenterView = {
                            scope.launch {
                                globeState.flyTo(
                                    targetLat = selectedIss!!.latitude.toFloat(),
                                    targetLng = -selectedIss!!.longitude.toFloat(),
                                    targetZoom = 1.6f
                                )
                            }
                        }
                    )
                }

                // Geodesic Flight Route Simulator Sheet (Unified Modal Bottom Sheet)
                if (isFlightMode && flightOrigin != null && flightDestination != null) {
                    FlightRouteHudCard(
                        origin = flightOrigin!!,
                        destination = flightDestination!!,
                        distanceKm = flightDistanceKm,
                        onRandomRoute = { generateRandomFlightRoute() },
                        onClose = {
                            isFlightMode = false
                            flightRoute = null
                        },
                        allCountries = countries,
                        isSupersonic = isSupersonicFlight,
                        onToggleSupersonic = { isSupersonicFlight = !isSupersonicFlight },
                        onSelectOrigin = { newOrigin ->
                            flightOrigin = newOrigin
                            if (flightDestination != null) {
                                flightRoute = AstronomyMath.calculateGreatCircleArc(newOrigin.center, flightDestination!!.center, 50)
                                flightDistanceKm = AstronomyMath.calculateGreatCircleDistance(newOrigin.center, flightDestination!!.center)
                            }
                        },
                        onSelectDestination = { newDest ->
                            flightDestination = newDest
                            if (flightOrigin != null) {
                                flightRoute = AstronomyMath.calculateGreatCircleArc(flightOrigin!!.center, newDest.center, 50)
                                flightDistanceKm = AstronomyMath.calculateGreatCircleDistance(flightOrigin!!.center, newDest.center)
                            }
                        }
                    )
                }

                // Bottom HUD: Interactive Quiz Mode or Floating Explorer Bar
                if (selectedCountry == null && selectedHazard == null && selectedIss == null && !isFlightMode) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        if (isQuizMode && quizTargetCountry != null) {
                            QuizHudCard(
                                targetCountry = quizTargetCountry!!,
                                score = quizScore,
                                streak = quizStreak,
                                feedback = quizFeedback,
                                isCorrect = quizIsCorrect,
                                onNextQuestion = { generateNextQuizQuestion() },
                                onEndQuiz = {
                                    isQuizMode = false
                                    selectedCountryId = null
                                }
                            )
                        } else {
                            FloatingExplorerBar(
                                onExploreRandom = {
                                    if (countries.isNotEmpty()) {
                                        val randomCountry = countries.random()
                                        selectedCountryId = randomCountry.id
                                        scope.launch {
                                            globeState.flyTo(
                                                targetLat = randomCountry.center.lat.toFloat(),
                                                targetLng = -randomCountry.center.lng.toFloat(),
                                                targetZoom = randomCountry.zoomLevel
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // HUD Map Legend & Symbology Modal Sheet
            if (showLegendSheet) {
                MissionLegendSheet(
                    onClose = { showLegendSheet = false }
                )
            }

            // Meteorology Station Sheet
            if (showMeteorologySheet && selectedCountry != null) {
                MeteorologyStationSheet(
                    country = selectedCountry,
                    liveDetails = liveDetails,
                    onClose = { showMeteorologySheet = false }
                )
            }

            // World Bank Macroeconomic Dashboard Sheet
            if (showWorldBankSheet && selectedCountry != null) {
                WorldBankDashboardSheet(
                    country = selectedCountry,
                    liveDetails = liveDetails,
                    onClose = { showWorldBankSheet = false }
                )
            }

            // NASA EONET Planetary Crisis Monitor Sheet
            if (showNasaCrisisSheet) {
                NasaCrisisMonitorSheet(
                    hazards = globalHazards,
                    currentCountry = selectedCountry,
                    onClose = { showNasaCrisisSheet = false },
                    onFlyToEpicenter = { hazard ->
                        selectedHazard = hazard
                        selectedCountryId = null
                        selectedIss = null
                        scope.launch {
                            globeState.flyTo(
                                targetLat = hazard.lat.toFloat(),
                                targetLng = -hazard.lng.toFloat(),
                                targetZoom = 2.2f
                            )
                        }
                    }
                )
            }
        }
    }
}


