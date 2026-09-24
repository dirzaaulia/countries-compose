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

        val selectedCountry = remember(selectedCountryId, countries) {
            countries.find { it.id == selectedCountryId }
        }

        LaunchedEffect(selectedCountryId) {
            val country = selectedCountry
            if (country != null && !isQuizMode) {
                globeState.flyTo(
                    targetLat = country.center.lat.toFloat(),
                    targetLng = -country.center.lng.toFloat(),
                    targetZoom = country.zoomLevel
                )
                isFetchingLive = true
                liveDetails = repository.fetchLiveDetails(country)
                isFetchingLive = false
            } else {
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
                                    quizFeedback = "🎉 Correct! That is ${quizTargetCountry?.name}!"
                                    quizIsCorrect = true
                                    selectedCountryId = clickedId
                                } else if (tappedCountry != null) {
                                    quizStreak = 0
                                    quizFeedback = "❌ That is ${tappedCountry.name}! Keep looking for ${quizTargetCountry?.name}."
                                    quizIsCorrect = false
                                    selectedCountryId = clickedId
                                }
                            } else {
                                selectedCountryId = clickedId
                                selectedHazard = null
                                selectedIss = null
                                val tapped = countries.find { it.id == clickedId }
                                if (tapped != null) {
                                    scope.launch {
                                        globeState.flyTo(
                                            targetLat = tapped.center.lat.toFloat(),
                                            targetLng = -tapped.center.lng.toFloat(),
                                            targetZoom = tapped.zoomLevel
                                        )
                                    }
                                }
                            }
                        },
                        state = globeState,
                        isPageActive = page == 0,
                        showBorders = showBorders,
                        showSatellites = showSatellites,
                        showHazards = showHazards,
                        issTelemetry = issTelemetry,
                        hazards = globalHazards,
                        flightRoute = if (isFlightMode) flightRoute else null,
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
                utcTime = utcTimeStr
            )

            // Bottom Overlay: Country Dossier, Quiz Card, Flight Card, or Hazards (only when on Earth page)
            if (pagerState.currentPage == 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                when {
                    // A. Geography Challenge Quiz Mode
                    isQuizMode && quizTargetCountry != null -> {
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
                    }

                    // B. Geodesic Flight Route Simulator Card
                    isFlightMode && flightOrigin != null && flightDestination != null -> {
                        FlightRouteHudCard(
                            origin = flightOrigin!!,
                            destination = flightDestination!!,
                            distanceKm = flightDistanceKm,
                            onRandomRoute = { generateRandomFlightRoute() },
                            onClose = {
                                isFlightMode = false
                                flightRoute = null
                            }
                        )
                    }

                    // C. NASA Natural Hazard Detail Sheet
                    selectedHazard != null -> {
                        HazardDetailSheet(
                            hazard = selectedHazard!!,
                            onClose = { selectedHazard = null }
                        )
                    }

                    // D. Live ISS Orbital Telemetry Card
                    selectedIss != null -> {
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

                    // E. Country Profile Dossier
                    selectedCountry != null -> {
                        CountryDossierSheet(
                            country = selectedCountry,
                            liveDetails = liveDetails,
                            isFetchingLive = isFetchingLive,
                            allCountries = countries,
                            onClose = { selectedCountryId = null },
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
                                    val nextCountry = otherCountries.random()
                                    selectedCountryId = nextCountry.id
                                    scope.launch {
                                        globeState.flyTo(
                                            targetLat = nextCountry.center.lat.toFloat(),
                                            targetLng = -nextCountry.center.lng.toFloat(),
                                            targetZoom = nextCountry.zoomLevel
                                        )
                                    }
                                }
                            },
                            onSelectCountry = { neighbor ->
                                selectedCountryId = neighbor.id
                                scope.launch {
                                    globeState.flyTo(
                                        targetLat = neighbor.center.lat.toFloat(),
                                        targetLng = -neighbor.center.lng.toFloat(),
                                        targetZoom = neighbor.zoomLevel
                                    )
                                }
                            }
                        )
                    }

                    // F. Default Floating Explorer Bar
                    else -> {
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
    }
}
}


