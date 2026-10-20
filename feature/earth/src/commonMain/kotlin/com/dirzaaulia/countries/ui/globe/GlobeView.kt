package com.dirzaaulia.countries.ui.globe

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.isPointInPolygon
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toDegrees
import com.dirzaaulia.countries.domain.globe.toRadians
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate
import com.dirzaaulia.countries.platform.Globe3DPlatformView
import com.dirzaaulia.countries.platform.currentEpochMillis
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.ui.overlay.drawAuroralOval
import com.dirzaaulia.countries.ui.overlay.drawCartographicBorders
import com.dirzaaulia.countries.ui.overlay.drawCountryHighlights
import com.dirzaaulia.countries.ui.overlay.drawFlightPathSimulator
import com.dirzaaulia.countries.ui.overlay.drawNasaHazards
import com.dirzaaulia.countries.ui.overlay.drawSatelliteFleet
import com.dirzaaulia.countries.ui.overlay.drawStockExchangesLayer
import com.dirzaaulia.countries.ui.overlay.drawTectonicLayer
import com.dirzaaulia.countries.ui.overlay.drawTimezoneMeridians
import com.dirzaaulia.countries.ui.overlay.drawTrueSizeComparisonOverlay
import com.dirzaaulia.countries.ui.overlay.drawTwilightBands
import com.dirzaaulia.countries.ui.solarsystem.CelestialStarfieldBackground
import com.dirzaaulia.countries.ui.solarsystem.drawDeepSpaceStarfield
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun GlobeView(
    countries: List<Country>,
    selectedCountryId: String?,
    onCountrySelected: (String?) -> Unit,
    state: GlobeState,
    modifier: Modifier = Modifier,
    isPageActive: Boolean = true,
    isSheetOpen: Boolean = false,
    isSupersonic: Boolean = false,
    showBorders: Boolean = true,
    showSatellites: Boolean = true,
    showHazards: Boolean = true,
    showAurora: Boolean = true,
    kpIndex: Double = 3.6,
    satelliteFleet: List<SatelliteTelemetry> = emptyList(),
    selectedSatellite: SatelliteTelemetry? = null,
    onSatelliteSelected: ((SatelliteTelemetry?) -> Unit)? = null,
    issTelemetry: ISSTelemetry? = null,
    hazards: List<NasaNaturalEvent> = emptyList(),
    flightRoute: List<LatLng>? = null,
    quizTargetCountryId: String? = null,
    quizIsCorrect: Boolean? = null,
    onHazardSelected: ((NasaNaturalEvent) -> Unit)? = null,
    onIssSelected: ((ISSTelemetry) -> Unit)? = null,
    sunPos: SunPosition = AstronomyMath.calculateSunPosition(),
    showTimeMachine: Boolean = false,
    comparisonCountryA: Country? = null,
    comparisonCountryB: Country? = null,
    isComparing: Boolean = false,
    isTimezoneLayerActive: Boolean = false,
    selectedMeridianOffset: Int? = null,
    isTectonicLayerActive: Boolean = false,
    tectonicPlates: List<TectonicPlate> = emptyList(),
    earthquakes: List<Earthquake> = emptyList(),
    selectedPlateId: String? = null,
    selectedEarthquakeId: String? = null,
    minMagnitudeFilter: Double = 4.5,
    onEarthquakeSelected: ((Earthquake?) -> Unit)? = null,
    isMarketLayerActive: Boolean = false,
    markets: List<FinancialMarket> = emptyList(),
    selectedMarketId: String? = null,
    currentUtcTimeMillis: Long = 0L,
    onMarketSelected: ((FinancialMarket?) -> Unit)? = null,
    onZoomOutToSolarSystem: (() -> Unit)? = null,
    onManualDragStart: (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    val infiniteTransition = rememberInfiniteTransition(label = "globeTransitions")
    val strobeAlpha by if (!isSheetOpen && isPageActive) {
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.75f,
            animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "alpha",
        )
    } else {
        remember { mutableStateOf(0.5f) }
    }
    val starTwinkle by if (!isSheetOpen && isPageActive) {
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse),
            label = "starTwinkle",
        )
    } else {
        remember { mutableStateOf(0.7f) }
    }
    val beaconPulse by if (!isSheetOpen && isPageActive && (quizTargetCountryId != null || flightRoute != null)) {
        infiniteTransition.animateFloat(
            initialValue = 4f,
            targetValue = 14f,
            animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "beaconPulse",
        )
    } else {
        remember { mutableStateOf(8f) }
    }
    val auroraWaveProgress by if (!isSheetOpen && isPageActive && showAurora) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "auroraWave",
        )
    } else {
        remember { mutableStateOf(0f) }
    }
    val flightDuration = if (isSupersonic) 5500 else 14000
    // key(isSupersonic) ensures the infiniteTransition is re-created immediately when speed mode changes,
    // preventing the old 14s (or 5.5s) cycle from continuing after the user taps the toggle.
    val planeProgress by if (!isSheetOpen && flightRoute != null) {
        key(isSupersonic) {
            rememberInfiniteTransition(label = "flightPlane${if (isSupersonic) "SST" else "Sub"}").animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(flightDuration, easing = LinearEasing)),
                label = "flightPlane",
            )
        }
    } else {
        remember { mutableStateOf(0f) }
    }
    var issScreenPos by remember { mutableStateOf<Offset?>(null) }

    val currentCountries by rememberUpdatedState(countries)
    val currentHazards by rememberUpdatedState(hazards)
    val currentIssTelemetry by rememberUpdatedState(issTelemetry)
    val currentSatelliteFleet by rememberUpdatedState(satelliteFleet)
    val currentShowSatellites by rememberUpdatedState(showSatellites)
    val currentShowHazards by rememberUpdatedState(showHazards)
    val currentOnCountrySelected by rememberUpdatedState(onCountrySelected)
    val currentOnHazardSelected by rememberUpdatedState(onHazardSelected)
    val currentOnIssSelected by rememberUpdatedState(onIssSelected)
    val currentOnSatelliteSelected by rememberUpdatedState(onSatelliteSelected)
    val currentOnZoomOutToSolarSystem by rememberUpdatedState(onZoomOutToSolarSystem)
    val currentOnManualDragStart by rememberUpdatedState(onManualDragStart)
    var lastPanDelta by remember { mutableStateOf(Offset.Zero) }
    var lastPanTime by remember { mutableLongStateOf(0L) }
    val satScreenPositions = remember { mutableStateMapOf<String, Offset>() }

    val daylightBordersPath = remember { Path() }
    val nightBordersPath = remember { Path() }
    val sensitivity = 0.38f

    // Real-time astronomical data
    val sunVector = sunPos.vector

    androidx.compose.runtime.LaunchedEffect(currentUtcTimeMillis, showTimeMachine, state.isDragging) {
        if (showTimeMachine && !state.isDragging) {
            val earthRot =
                com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                    .calculatePlanetRotationAngle(com.dirzaaulia.countries.domain.solarsystem.PlanetId.EARTH, currentUtcTimeMillis)
                    .toFloat()
            state.snapTo(state.rotationX, earthRot, state.zoom)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Deep Space Starfield Background
        CelestialStarfieldBackground(starTwinkle = starTwinkle)

        val currentSunPos by rememberUpdatedState(sunPos)
        val currentIsMarketLayerActive by rememberUpdatedState(isMarketLayerActive)
        val currentMarkets by rememberUpdatedState(markets)
        val currentOnMarketSelected by rememberUpdatedState(onMarketSelected)

        var isTextureLoaded by remember {
            mutableStateOf(
                com.dirzaaulia.countries.data.solarsystem.PlanetTextureProvider
                    .getCachedTexture(com.dirzaaulia.countries.domain.solarsystem.PlanetId.EARTH) != null,
            )
        }

        // 2. 3D Platform Globe (OpenGL ES on Android, WebGL/Canvas on WASM)
        Globe3DPlatformView(
            state = state,
            sunPosition = currentSunPos,
            isPageActive = isPageActive && (!isSheetOpen || state.isAnimating || showTimeMachine),
            onTextureLoaded = { isTextureLoaded = true },
            modifier = Modifier.fillMaxSize(),
        )

        // 3. Interactive Gestures & Vector Country / Space Overlay
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .then(
                        if (!isSheetOpen) {
                            Modifier
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            if (event.type == PointerEventType.Scroll) {
                                                val delta =
                                                    event.changes
                                                        .firstOrNull()
                                                        ?.scrollDelta
                                                        ?.y ?: 0f
                                                if (delta != 0f) {
                                                    val factor = if (delta < 0) 1.15f else 0.87f
                                                    scope.launch {
                                                        state.snapZoom(state.zoom * factor)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                        } else {
                            Modifier
                        },
                    ).pointerInput(countries.isNotEmpty()) {
                        detectTapGestures(
                            onDoubleTap = {
                                scope.launch {
                                    state.stopAnimations()
                                    val targetZoom = if (state.zoom > 1.4f) 1.0f else 2.2f
                                    state.animateZoom(targetZoom)
                                }
                            },
                            onTap = { offset ->
                                val canvasSize = size
                                val canvasCenter = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
                                val baseRadius = minOf(canvasSize.width, canvasSize.height) * 0.38f
                                val currentRadius = baseRadius * state.zoom

                                val dx = (offset.x - canvasCenter.x).toDouble()
                                val dy = (offset.y - canvasCenter.y).toDouble()
                                val d2 = dx * dx + dy * dy

                                // Direct Screen-Space Tap on ISS Badge / Satellite marker
                                val telemetry = currentIssTelemetry
                                if (currentShowSatellites && telemetry != null && issScreenPos != null) {
                                    val sp = issScreenPos!!
                                    val distSq = (offset.x - sp.x) * (offset.x - sp.x) + (offset.y - sp.y) * (offset.y - sp.y)
                                    if (distSq <= 48f * 48f) {
                                        currentOnIssSelected?.invoke(telemetry)
                                        return@detectTapGestures
                                    }
                                }

                                if (d2 <= currentRadius * currentRadius) {
                                    val dz = sqrt(currentRadius * currentRadius - d2)

                                    val radX = (-state.rotationX.toDouble()).toRadians
                                    val radY = (-state.rotationY.toDouble()).toRadians
                                    val cosX = cos(radX)
                                    val sinX = sin(radX)
                                    val cosY = cos(radY)
                                    val sinY = sin(radY)

                                    var p = Point3D(dx, -dy, dz)
                                    p = rotateX(p, cosX, sinX)
                                    p = rotateY(p, cosY, sinY)

                                    val lat = atan2(p.y, sqrt(p.x * p.x + p.z * p.z)).toDegrees
                                    val lng = atan2(p.x, p.z).toDegrees
                                    val tappedLatLng = LatLng(lat, lng)

                                    // Check Fleet Satellites Tap
                                    val fleetList = currentSatelliteFleet
                                    if (currentShowSatellites && fleetList.isNotEmpty()) {
                                        val nearbySat =
                                            fleetList.find { sat ->
                                                AstronomyMath.calculateGreatCircleDistance(tappedLatLng, LatLng(sat.lat, sat.lng)) < 1200.0
                                            }
                                        if (nearbySat != null) {
                                            currentOnSatelliteSelected?.invoke(nearbySat)
                                            return@detectTapGestures
                                        }
                                    }

                                    // Check ISS Proximity Tap (orbital ground radius)
                                    if (currentShowSatellites && telemetry != null) {
                                        val issDistance =
                                            AstronomyMath.calculateGreatCircleDistance(
                                                tappedLatLng,
                                                LatLng(telemetry.latitude, telemetry.longitude),
                                            )
                                        if (issDistance < 1000.0) {
                                            currentOnIssSelected?.invoke(telemetry)
                                            return@detectTapGestures
                                        }
                                    }

                                    // Check Hazards tap
                                    val hazardsList = currentHazards
                                    if (currentShowHazards && hazardsList.isNotEmpty()) {
                                        val nearbyHazard =
                                            hazardsList.find { h ->
                                                AstronomyMath.calculateGreatCircleDistance(tappedLatLng, LatLng(h.lat, h.lng)) < 600.0
                                            }
                                        if (nearbyHazard != null) {
                                            currentOnHazardSelected?.invoke(nearbyHazard)
                                            return@detectTapGestures
                                        }
                                    }

                                    // Check Stock Exchanges Tap
                                    val marketsList = currentMarkets
                                    if (currentIsMarketLayerActive && marketsList.isNotEmpty()) {
                                        val nearbyMarket =
                                            marketsList.find { m ->
                                                AstronomyMath.calculateGreatCircleDistance(tappedLatLng, LatLng(m.lat, m.lng)) < 800.0
                                            }
                                        if (nearbyMarket != null) {
                                            currentOnMarketSelected?.invoke(nearbyMarket)
                                            return@detectTapGestures
                                        }
                                    }

                                    val countryList = currentCountries
                                    val clickedCountry =
                                        countryList.find { country ->
                                            country.boundingBox.contains(tappedLatLng) &&
                                                country.polygons.any { poly -> isPointInPolygon(tappedLatLng, poly) }
                                        }
                                    currentOnCountrySelected(clickedCountry?.id)
                                } else {
                                    currentOnCountrySelected(null)
                                }
                            },
                        )
                    }.pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            state.setDragging(true)
                            currentOnManualDragStart?.invoke()
                            lastPanDelta = Offset.Zero
                            lastPanTime = 0L
                            try {
                                do {
                                    val event = awaitPointerEvent()
                                } while (event.changes.any { it.pressed })
                            } finally {
                                state.setDragging(false)
                                val now = currentEpochMillis()
                                val elapsed = now - lastPanTime
                                if (elapsed in 1..80 && (lastPanDelta.x != 0f || lastPanDelta.y != 0f)) {
                                    val vx = -lastPanDelta.x * (0.45f / state.zoom * 60f)
                                    val vy = -lastPanDelta.y * (0.35f / state.zoom * 60f)
                                    scope.launch {
                                        state.fling(vx, vy)
                                    }
                                }
                            }
                        }
                    }.pointerInput(Unit) {
                        detectTransformGestures(panZoomLock = false) { _, pan, zoomChange, _ ->
                            lastPanDelta = pan
                            lastPanTime = currentEpochMillis()
                            if (com.dirzaaulia.countries.platform.isWasmTarget && zoomChange < 0.98f && state.zoom <= 0.65f) {
                                currentOnZoomOutToSolarSystem?.invoke()
                            }
                            scope.launch {
                                state.stopAnimations()
                                val pitchSens = 0.35f / state.zoom
                                val yawSens = 0.45f / state.zoom
                                val newY = (state.rotationY - pan.x * yawSens) % 360f
                                val newX = (state.rotationX - pan.y * pitchSens).coerceIn(-89f, 89f)
                                val newZoom = if (zoomChange != 1.0f) state.zoom * zoomChange else null
                                state.snapTo(newX, newY, newZoom)
                            }
                        }
                    },
        ) {
            val canvasCenter = center
            val baseRadius = minOf(size.width, size.height) * 0.38f
            val currentRadius = baseRadius * state.zoom

            val radX = state.rotationX.toDouble().toRadians
            val radY = state.rotationY.toDouble().toRadians
            val cosX = cos(radX)
            val sinX = sin(radX)
            val cosY = cos(radY)
            val sinY = sin(radY)

            // 0. Twinkling Stars in Deep Space
            val earthR2 = currentRadius * currentRadius
            drawDeepSpaceStarfield(
                starTwinkle = starTwinkle,
                occludeR2 = earthR2,
                canvasCenter = canvasCenter,
                canvasSize = size,
                cameraYaw = state.rotationY,
                cameraPitch = state.rotationX,
            )

            // 0b. Photographic Twilight Bands (Golden Hour & Blue Hour)
            drawTwilightBands(
                sunVector = sunVector,
                currentRadius = currentRadius,
                canvasCenter = canvasCenter,
                cosX = cosX,
                sinX = sinX,
                cosY = cosY,
                sinY = sinY,
            )

            // 0c. Global 24-Meridian Timezone Grid
            if (isTimezoneLayerActive) {
                drawTimezoneMeridians(
                    selectedMeridianOffset = selectedMeridianOffset,
                    currentRadius = currentRadius,
                    canvasCenter = canvasCenter,
                    cosX = cosX,
                    sinX = sinX,
                    cosY = cosY,
                    sinY = sinY,
                    textMeasurer = textMeasurer,
                )
            }

            // 0d. Tectonic Plates & Fault Lines & Live Earthquakes
            if (isTectonicLayerActive) {
                drawTectonicLayer(
                    plates = tectonicPlates,
                    earthquakes = earthquakes,
                    selectedPlateId = selectedPlateId,
                    selectedEarthquakeId = selectedEarthquakeId,
                    minMagnitudeFilter = minMagnitudeFilter,
                    currentRadius = currentRadius,
                    canvasCenter = canvasCenter,
                    cosX = cosX,
                    sinX = sinX,
                    cosY = cosY,
                    sinY = sinY,
                    strobeAlpha = strobeAlpha,
                    beaconPulse = beaconPulse,
                )
            }

            // 0e. Global Stock Exchanges Layer
            if (isMarketLayerActive && markets.isNotEmpty()) {
                drawStockExchangesLayer(
                    markets = markets,
                    selectedMarketId = selectedMarketId,
                    currentUtcMillis = currentUtcTimeMillis,
                    currentRadius = currentRadius,
                    canvasCenter = canvasCenter,
                    cosX = cosX,
                    sinX = sinX,
                    cosY = cosY,
                    sinY = sinY,
                    strobeAlpha = strobeAlpha,
                    beaconPulse = beaconPulse,
                )
            }

            // A. Dynamic Day/Night Adaptive Cartographic Borders
            if (showBorders) {
                drawCartographicBorders(
                    countries = countries,
                    selectedCountryId = selectedCountryId,
                    currentRadius = currentRadius,
                    canvasCenter = canvasCenter,
                    cosX = cosX,
                    sinX = sinX,
                    cosY = cosY,
                    sinY = sinY,
                    sunVector = sunVector,
                    daylightBordersPath = daylightBordersPath,
                    nightBordersPath = nightBordersPath,
                    isDragging = state.isDragging,
                )
            }

            // C. Selected Country & Quiz Target Highlight
            val highlightCountryId =
                when {
                    quizTargetCountryId != null && quizIsCorrect == true -> quizTargetCountryId
                    quizTargetCountryId != null && selectedCountryId != null -> selectedCountryId
                    else -> selectedCountryId
                }
            val isQuizSuccess = quizTargetCountryId != null && quizIsCorrect == true
            val isQuizTarget = quizTargetCountryId != null

            drawCountryHighlights(
                countries = countries,
                highlightCountryId = highlightCountryId,
                currentRadius = currentRadius,
                canvasCenter = canvasCenter,
                cosX = cosX,
                sinX = sinX,
                cosY = cosY,
                sinY = sinY,
                strobeAlpha = strobeAlpha,
                isQuizSuccess = isQuizSuccess,
                isQuizTarget = isQuizTarget,
            )

            // C2. Head-to-Head True Size Overlay
            if (isComparing && comparisonCountryA != null && comparisonCountryB != null) {
                drawTrueSizeComparisonOverlay(
                    countryA = comparisonCountryA,
                    countryB = comparisonCountryB,
                    currentRadius = currentRadius,
                    canvasCenter = canvasCenter,
                    cosX = cosX,
                    sinX = sinX,
                    cosY = cosY,
                    sinY = sinY,
                    strobeAlpha = strobeAlpha,
                )
            }

            // D. Geodesic Flight Path Simulator
            drawFlightPathSimulator(
                flightRoute = flightRoute,
                planeProgress = planeProgress,
                isSupersonic = isSupersonic,
                currentRadius = currentRadius,
                canvasCenter = canvasCenter,
                cosX = cosX,
                sinX = sinX,
                cosY = cosY,
                sinY = sinY,
            )

            // E. NASA Live Natural Disaster Hazards
            if (showHazards) {
                drawNasaHazards(
                    hazards = hazards,
                    currentRadius = currentRadius,
                    canvasCenter = canvasCenter,
                    cosX = cosX,
                    sinX = sinX,
                    cosY = cosY,
                    sinY = sinY,
                    strobeAlpha = strobeAlpha,
                    beaconPulse = beaconPulse,
                )
            }

            // F. Live Multi-Satellite Fleet & Space Stations Tracker
            if (showSatellites && satelliteFleet.isNotEmpty()) {
                drawSatelliteFleet(
                    fleet = satelliteFleet,
                    selectedSatellite = selectedSatellite,
                    currentRadius = currentRadius,
                    canvasCenter = canvasCenter,
                    cosX = cosX,
                    sinX = sinX,
                    cosY = cosY,
                    sinY = sinY,
                    strobeAlpha = strobeAlpha,
                    textMeasurer = textMeasurer,
                    isDragging = state.isDragging,
                    onSatPosCalculated = { id, pos -> satScreenPositions[id] = pos },
                )
            } else {
                satScreenPositions.clear()
            }

            // G. Space Weather Auroral Oval (Northern & Southern Lights)
            if (showAurora) {
                drawAuroralOval(
                    kpIndex = kpIndex,
                    sunVector = sunVector,
                    waveProgress = auroraWaveProgress,
                    currentRadius = currentRadius,
                    canvasCenter = canvasCenter,
                    cosX = cosX,
                    sinX = sinX,
                    cosY = cosY,
                    sinY = sinY,
                    isDragging = state.isDragging,
                )
            }
        }

        // 4. Interactive Floating Fleet Satellite Badges
        if (showSatellites && satelliteFleet.isNotEmpty()) {
            satelliteFleet.forEach { sat ->
                val pos = satScreenPositions[sat.id]
                if (pos != null) {
                    val isSelected = selectedSatellite?.id == sat.id
                    val satColor = getSatelliteColor(sat.id)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xF20B1220),
                        border = BorderStroke(1.dp, if (isSelected) satColor else satColor.copy(alpha = 0.5f)),
                        shadowElevation = 6.dp,
                        modifier =
                            Modifier
                                .offset { IntOffset((pos.x + 12).roundToInt(), (pos.y - 12).roundToInt()) }
                                .clickable { onSatelliteSelected?.invoke(sat) },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            SemanticIcon(UiSymbol.Iss, sat.acronym, satColor, Modifier.size(13.dp))
                            Text(sat.acronym, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier =
                                    Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(satColor),
                            )
                            Text("${sat.altitudeKm.toInt()} km", color = satColor, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // 4. Floating On-Screen Zoom Controls HUD (Vertical Pill on the right edge)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xDD0B1220),
            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
            shadowElevation = 8.dp,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
        ) {
            Column(
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                // Zoom In (+)
                Box(
                    modifier =
                        Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { scope.launch { state.zoomIn() } },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Zoom in",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(22.dp),
                    )
                }

                // Current Zoom Indicator & Reset to 1.0x
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { scope.launch { state.resetZoom() } }
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val zoomRounded = (round(state.zoom * 10.0) / 10.0)
                    Text(
                        text = "${zoomRounded}x",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                // Zoom Out (−)
                Box(
                    modifier =
                        Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { scope.launch { state.zoomOut() } },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = "Zoom out",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        com.dirzaaulia.countries.ui.solarsystem.CelestialTextureLoadingOverlay(
            planetName = "Earth",
            isLoading = !isTextureLoaded,
        )
    }
}

private fun getSatelliteColor(satelliteId: String): Color =
    when (satelliteId) {
        "ISS" -> Color(0xFF38BDF8)
        "CSS" -> Color(0xFFF59E0B)
        "HST" -> Color(0xFFA855F7)
        "JWST" -> Color(0xFFEAB308)
        "TERRA" -> Color(0xFF10B981)
        "AQUA" -> Color(0xFF06B6D4)
        "AURA" -> Color(0xFF818CF8)
        "LDST8" -> Color(0xFF34D399)
        "LDST9" -> Color(0xFF2DD4BF)
        "ICEST2" -> Color(0xFF67E8F9)
        "OCO2" -> Color(0xFFA3E635)
        "TESS" -> Color(0xFFFB7185)
        "SMAP" -> Color(0xFFF97316)
        else -> Color(0xFF38BDF8)
    }
