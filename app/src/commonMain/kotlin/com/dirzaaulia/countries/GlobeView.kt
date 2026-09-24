package com.dirzaaulia.countries

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

// Deterministic celestial starfield positions in deep space
internal val CELESTIAL_STARS = (0..260).map { i ->
    val rng = Random(i * 7919)
    Triple(rng.nextFloat(), rng.nextFloat(), 0.7f + rng.nextFloat() * 1.6f)
}

private data class TransitionSegment(
    val p1: Offset,
    val p2: Offset,
    val coreColor1: Color,
    val coreColor2: Color,
    val haloColor1: Color,
    val haloColor2: Color
)

@Composable
fun GlobeView(
    countries: List<Country>,
    selectedCountryId: String?,
    onCountrySelected: (String?) -> Unit,
    state: GlobeState,
    isPageActive: Boolean = true,
    isSheetOpen: Boolean = false,
    isSupersonic: Boolean = false,
    showBorders: Boolean = true,
    showSatellites: Boolean = true,
    showHazards: Boolean = true,
    issTelemetry: ISSTelemetry? = null,
    hazards: List<NasaNaturalEvent> = emptyList(),
    flightRoute: List<LatLng>? = null,
    quizTargetCountryId: String? = null,
    quizIsCorrect: Boolean? = null,
    onHazardSelected: ((NasaNaturalEvent) -> Unit)? = null,
    onIssSelected: ((ISSTelemetry) -> Unit)? = null,
    sunPos: SunPosition = AstronomyMath.calculateSunPosition(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "globeTransitions")
    val strobeAlpha by if (!isSheetOpen) {
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.75f,
            animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "alpha"
        )
    } else {
        remember { mutableStateOf(0.5f) }
    }
    val starTwinkle by if (!isSheetOpen) {
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse),
            label = "starTwinkle"
        )
    } else {
        remember { mutableStateOf(0.7f) }
    }
    val beaconPulse by if (!isSheetOpen && (quizTargetCountryId != null || flightRoute != null)) {
        infiniteTransition.animateFloat(
            initialValue = 4f,
            targetValue = 14f,
            animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "beaconPulse"
        )
    } else {
        remember { mutableStateOf(8f) }
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
                label = "flightPlane"
            )
        }
    } else {
        remember { mutableStateOf(0f) }
    }
    var issScreenPos by remember { mutableStateOf<Offset?>(null) }

    val currentCountries by rememberUpdatedState(countries)
    val currentHazards by rememberUpdatedState(hazards)
    val currentIssTelemetry by rememberUpdatedState(issTelemetry)
    val currentShowSatellites by rememberUpdatedState(showSatellites)
    val currentShowHazards by rememberUpdatedState(showHazards)
    val currentOnCountrySelected by rememberUpdatedState(onCountrySelected)
    val currentOnHazardSelected by rememberUpdatedState(onHazardSelected)
    val currentOnIssSelected by rememberUpdatedState(onIssSelected)

    val daylightBordersPath = remember { Path() }
    val nightBordersPath = remember { Path() }
    val sensitivity = 0.18f

    // Real-time astronomical data
    val sunVector = sunPos.vector

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Deep Space Starfield Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF070E1E),
                        Color(0xFF03060C),
                        Color(0xFF010205)
                    ),
                    center = center,
                    radius = size.maxDimension * 0.9f
                )
            )

            // Milky Way Galactic Dust Lane (diagonal celestial dust band)
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x15312E81),
                        Color(0x281E1B4B),
                        Color(0x180284C7),
                        Color.Transparent
                    ),
                    start = Offset(0f, size.height * 0.15f),
                    end = Offset(size.width, size.height * 0.85f)
                )
            )

            // Twinkling stars in deep space
            CELESTIAL_STARS.forEachIndexed { idx, (normX, normY, starRadius) ->
                val x = normX * size.width
                val y = normY * size.height
                val alpha = if (idx % 2 == 0) starTwinkle else (1.4f - starTwinkle).coerceIn(0.2f, 1f)
                val starColor = when {
                    idx % 7 == 0 -> Color(0xFF90CAF9)
                    idx % 11 == 0 -> Color(0xFFFFE082)
                    idx % 13 == 0 -> Color(0xFFFFCCBC)
                    else -> Color.White
                }
                drawCircle(
                    color = starColor.copy(alpha = alpha * 0.85f),
                    radius = starRadius,
                    center = Offset(x, y)
                )
            }
        }

        // 2. 3D Platform Globe (OpenGL ES on Android, WebGL/Canvas on WASM)
        Globe3DPlatformView(
            state = state,
            isPageActive = isPageActive && !isSheetOpen,
            modifier = Modifier.fillMaxSize()
        )

        // 3. Interactive Gestures & Vector Country / Space Overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (!isSheetOpen) {
                        Modifier
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.type == PointerEventType.Scroll) {
                                            val delta = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
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
                    } else Modifier
                )
                .pointerInput(countries.isNotEmpty()) {
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
                                if (distSq <= 38f * 38f) {
                                    currentOnIssSelected?.invoke(telemetry)
                                    return@detectTapGestures
                                }
                            }

                            if (d2 <= currentRadius * currentRadius) {
                                val dz = sqrt(currentRadius * currentRadius - d2)

                                val radX = (-state.rotationX.toDouble()).toRadians
                                val radY = (-state.rotationY.toDouble()).toRadians
                                val cosX = cos(radX); val sinX = sin(radX)
                                val cosY = cos(radY); val sinY = sin(radY)

                                var p = Point3D(dx, -dy, dz)
                                p = rotateX(p, cosX, sinX)
                                p = rotateY(p, cosY, sinY)

                                val lat = atan2(p.y, sqrt(p.x * p.x + p.z * p.z)).toDegrees
                                val lng = atan2(p.x, p.z).toDegrees
                                val tappedLatLng = LatLng(lat, lng)

                                // Check ISS Proximity Tap (orbital ground radius)
                                if (currentShowSatellites && telemetry != null) {
                                    val issDistance = AstronomyMath.calculateGreatCircleDistance(
                                        tappedLatLng,
                                        LatLng(telemetry.latitude, telemetry.longitude)
                                    )
                                    if (issDistance < 700.0) {
                                        currentOnIssSelected?.invoke(telemetry)
                                        return@detectTapGestures
                                    }
                                }

                                // Check Hazards tap
                                val hazardsList = currentHazards
                                if (currentShowHazards && hazardsList.isNotEmpty()) {
                                    val nearbyHazard = hazardsList.find { h ->
                                        AstronomyMath.calculateGreatCircleDistance(tappedLatLng, LatLng(h.lat, h.lng)) < 350.0
                                    }
                                    if (nearbyHazard != null) {
                                        currentOnHazardSelected?.invoke(nearbyHazard)
                                        return@detectTapGestures
                                    }
                                }

                                val countryList = currentCountries
                                val clickedCountry = countryList.find { country ->
                                    country.boundingBox.contains(tappedLatLng) &&
                                        country.polygons.any { poly -> isPointInPolygon(tappedLatLng, poly) }
                                }
                                currentOnCountrySelected(clickedCountry?.id)
                            } else {
                                currentOnCountrySelected(null)
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures(panZoomLock = false) { _, pan, zoomChange, _ ->
                        scope.launch {
                            state.stopAnimations()
                            val dragFactor = sensitivity / state.zoom
                            val newY = state.rotationY + pan.x * dragFactor
                            val newX = state.rotationX + pan.y * dragFactor
                            val newZoom = if (zoomChange != 1.0f) state.zoom * zoomChange else null
                            state.snapTo(newX, newY, newZoom)
                        }
                    }
                }
        ) {
            val canvasCenter = center
            val baseRadius = minOf(size.width, size.height) * 0.38f
            val currentRadius = baseRadius * state.zoom

            val radX = state.rotationX.toDouble().toRadians
            val radY = state.rotationY.toDouble().toRadians
            val cosX = cos(radX); val sinX = sin(radX)
            val cosY = cos(radY); val sinY = sin(radY)

            // 0. Twinkling Stars in Deep Space (Rendered on top of GL background)
            val earthR2 = currentRadius * currentRadius
            CELESTIAL_STARS.forEachIndexed { idx, (normX, normY, starRadius) ->
                val sx = normX * size.width
                val sy = normY * size.height
                val dx = sx - canvasCenter.x
                val dy = sy - canvasCenter.y
                if (dx * dx + dy * dy > earthR2 + 10f) {
                    val alpha = if (idx % 2 == 0) starTwinkle else (1.4f - starTwinkle).coerceIn(0.25f, 1f)
                    val starColor = when {
                        idx % 7 == 0 -> Color(0xFF90CAF9)
                        idx % 11 == 0 -> Color(0xFFFFE082)
                        idx % 13 == 0 -> Color(0xFFFFCCBC)
                        else -> Color.White
                    }
                    if (starRadius > 1.6f) {
                        drawCircle(
                            color = starColor.copy(alpha = alpha * 0.35f),
                            radius = starRadius * 2.5f,
                            center = Offset(sx, sy)
                        )
                    }
                    drawCircle(
                        color = starColor.copy(alpha = alpha * 0.92f),
                        radius = starRadius * 1.3f,
                        center = Offset(sx, sy)
                    )
                }
            }

            // -------------------------------------------------------------
            // A. Dynamic Day/Night Adaptive Cartographic Borders
            // -------------------------------------------------------------
            if (showBorders) {
                daylightBordersPath.reset()
                nightBordersPath.reset()

                val colorDayCore = Color(0xFF0F172A)    // Crisp obsidian black
                val colorDayHalo = Color(0x66FFFFFF)    // Soft white halo for daylight visibility
                val colorNightCore = Color(0xFFF8FAFC)  // Luminous ivory white
                val colorNightHalo = Color(0x800B1320)  // Dark slate halo for night visibility

                countries.forEach { country ->
                    if (country.id != selectedCountryId) {
                        // Fast back-face centroid culling: skip entire country if on far side of Earth
                        var cp = latLngToCartesian(country.center.lat, country.center.lng, 1.0)
                        cp = rotateY(cp, cosY, sinY)
                        cp = rotateX(cp, cosX, sinX)
                        if (cp.z < -0.45) return@forEach

                        country.polygons.forEach { polygon ->
                            for (i in polygon.indices) {
                                val latLngA = polygon[i]
                                val latLngB = polygon[(i + 1) % polygon.size]

                                var pA = latLngToCartesian(latLngA.lat, latLngA.lng, currentRadius.toDouble())
                                pA = rotateY(pA, cosY, sinY)
                                pA = rotateX(pA, cosX, sinX)

                                var pB = latLngToCartesian(latLngB.lat, latLngB.lng, currentRadius.toDouble())
                                pB = rotateY(pB, cosY, sinY)
                                pB = rotateX(pB, cosX, sinX)

                                // Both vertices must be on the front hemisphere facing the camera
                                if (pA.z > 0.0 && pB.z > 0.0) {
                                    val sxA = canvasCenter.x + pA.x.toFloat()
                                    val syA = canvasCenter.y - pA.y.toFloat()
                                    val sxB = canvasCenter.x + pB.x.toFloat()
                                    val syB = canvasCenter.y - pB.y.toFloat()

                                    // Compute solar illumination factors in Earth coordinate space
                                    val normA = latLngToCartesian(latLngA.lat, latLngA.lng, 1.0)
                                    val normB = latLngToCartesian(latLngB.lat, latLngB.lng, 1.0)
                                    val sA = normA.x * sunVector.x + normA.y * sunVector.y + normA.z * sunVector.z
                                    val sB = normB.x * sunVector.x + normB.y * sunVector.y + normB.z * sunVector.z

                                    if (sA >= 0.04 && sB >= 0.04) {
                                        // Daylight segment (Black)
                                        daylightBordersPath.moveTo(sxA, syA)
                                        daylightBordersPath.lineTo(sxB, syB)
                                    } else if (sA <= -0.04 && sB <= -0.04) {
                                        // Night segment (White)
                                        nightBordersPath.moveTo(sxA, syA)
                                        nightBordersPath.lineTo(sxB, syB)
                                    } else {
                                        // Twilight transition segment (Rendered inline with zero GC allocations)
                                        val tA = ((sA + 0.04) / 0.08).coerceIn(0.0, 1.0).toFloat()
                                        val tB = ((sB + 0.04) / 0.08).coerceIn(0.0, 1.0).toFloat()

                                        val coreA = androidx.compose.ui.graphics.lerp(colorNightCore, colorDayCore, tA)
                                        val coreB = androidx.compose.ui.graphics.lerp(colorNightCore, colorDayCore, tB)
                                        val haloA = androidx.compose.ui.graphics.lerp(colorNightHalo, colorDayHalo, tA)
                                        val haloB = androidx.compose.ui.graphics.lerp(colorNightHalo, colorDayHalo, tB)

                                        val p1 = Offset(sxA, syA)
                                        val p2 = Offset(sxB, syB)
                                        val haloBrush = Brush.linearGradient(listOf(haloA, haloB), start = p1, end = p2)
                                        drawLine(brush = haloBrush, start = p1, end = p2, strokeWidth = 2.4f, cap = StrokeCap.Round)
                                        val coreBrush = Brush.linearGradient(listOf(coreA, coreB), start = p1, end = p2)
                                        drawLine(brush = coreBrush, start = p1, end = p2, strokeWidth = 1.3f, cap = StrokeCap.Round)
                                    }
                                }
                            }
                        }
                    }
                }

                // 1. Daylight borders: subtle light halo + crisp obsidian black core
                drawPath(daylightBordersPath, colorDayHalo, style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(daylightBordersPath, colorDayCore, style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))

                // 2. Night borders: dark slate halo + luminous ivory white core
                drawPath(nightBordersPath, colorNightHalo, style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(nightBordersPath, colorNightCore, style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }

            // -------------------------------------------------------------
            // C. Selected Country & Quiz Target Highlight
            // -------------------------------------------------------------
            val highlightCountryId = when {
                quizTargetCountryId != null && quizIsCorrect == true -> quizTargetCountryId
                quizTargetCountryId != null && selectedCountryId != null -> selectedCountryId
                else -> selectedCountryId
            }

            val highlightCountry = countries.find { it.id == highlightCountryId }
            val isQuizSuccess = quizTargetCountryId != null && quizIsCorrect == true
            val isQuizTarget = quizTargetCountryId != null

            val auraFillColor = when {
                isQuizSuccess -> Color(0x5510B981)
                isQuizTarget -> Color(0x44F59E0B)
                else -> Color(0x4438BDF8)
            }
            val auraOuterGlow = when {
                isQuizSuccess -> Color(0x6610B981)
                isQuizTarget -> Color(0x66F59E0B)
                else -> Color(0x5538BDF8)
            }
            val auraCoreStroke = when {
                isQuizSuccess -> Color(0xFF10B981)
                isQuizTarget -> Color(0xFFF59E0B)
                else -> Color(0xFF38BDF8)
            }

            highlightCountry?.let { country ->
                country.polygons.forEach { polygon ->
                    val selectedPath = Path()
                    var inPath = false

                    for (i in polygon.indices) {
                        val latLng = polygon[i]
                        var p = latLngToCartesian(latLng.lat, latLng.lng, currentRadius.toDouble())
                        p = rotateY(p, cosY, sinY)
                        p = rotateX(p, cosX, sinX)

                        if (p.z > 0.0) {
                            val sx = canvasCenter.x + p.x.toFloat()
                            val sy = canvasCenter.y - p.y.toFloat()
                            if (!inPath) {
                                selectedPath.moveTo(sx, sy)
                                inPath = true
                            } else {
                                selectedPath.lineTo(sx, sy)
                            }
                        } else {
                            inPath = false
                        }
                    }

                    if (inPath) {
                        selectedPath.close()
                        drawPath(selectedPath, auraFillColor.copy(alpha = strobeAlpha * 0.45f), style = Fill)
                    }

                    // Outer soft glow halo
                    drawPath(
                        path = selectedPath,
                        color = auraOuterGlow,
                        style = Stroke(width = 5.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    // Inner sharp vibrant neon stroke
                    drawPath(
                        path = selectedPath,
                        color = auraCoreStroke,
                        style = Stroke(width = 2.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            // -------------------------------------------------------------
            // D. Geodesic Flight Path Simulator (Great Circle Arc)
            // -------------------------------------------------------------
            if (flightRoute != null && flightRoute.size >= 2) {
                val arcPath = Path()
                var inArc = false

                // 1. Great circle flight arc
                for (pt in flightRoute) {
                    var p = latLngToCartesian(pt.lat, pt.lng, (currentRadius * 1.01).toDouble())
                    p = rotateY(p, cosY, sinY)
                    p = rotateX(p, cosX, sinX)

                    if (p.z > 0.0) {
                        val sx = canvasCenter.x + p.x.toFloat()
                        val sy = canvasCenter.y - p.y.toFloat()
                        if (!inArc) {
                            arcPath.moveTo(sx, sy)
                            inArc = true
                        } else {
                            arcPath.lineTo(sx, sy)
                        }
                    } else {
                        inArc = false
                    }
                }

                // Glowing flight arc
                drawPath(arcPath, Color(0x40F59E0B), style = Stroke(width = 5f, cap = StrokeCap.Round))
                drawPath(
                    arcPath,
                    Color(0xFFF59E0B),
                    style = Stroke(
                        width = 2.5f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
                    )
                )

                // 2. Departure & Arrival Radar Pulse Rings
                val departure = flightRoute.first()
                var depP = latLngToCartesian(departure.lat, departure.lng, (currentRadius * 1.008).toDouble())
                depP = rotateY(depP, cosY, sinY)
                depP = rotateX(depP, cosX, sinX)
                if (depP.z > 0.0) {
                    val dx = canvasCenter.x + depP.x.toFloat()
                    val dy = canvasCenter.y - depP.y.toFloat()
                    val depPulse = (planeProgress * 3f) % 1f
                    drawCircle(color = Color(0x6610B981), radius = 4f + depPulse * 12f, center = Offset(dx, dy), style = Stroke(width = 1.5f))
                    drawCircle(color = Color(0xFF10B981), radius = 3.5f, center = Offset(dx, dy))
                }

                val arrival = flightRoute.last()
                var arrP = latLngToCartesian(arrival.lat, arrival.lng, (currentRadius * 1.008).toDouble())
                arrP = rotateY(arrP, cosY, sinY)
                arrP = rotateX(arrP, cosX, sinX)
                if (arrP.z > 0.0) {
                    val ax = canvasCenter.x + arrP.x.toFloat()
                    val ay = canvasCenter.y - arrP.y.toFloat()
                    val arrPulse = ((planeProgress * 3f) + 0.5f) % 1f
                    drawCircle(color = Color(0x6638BDF8), radius = 4f + arrPulse * 12f, center = Offset(ax, ay), style = Stroke(width = 1.5f))
                    drawCircle(color = Color(0xFF38BDF8), radius = 3.5f, center = Offset(ax, ay))
                }

                // 3. Supersonic Aircraft Position & Parabolic Altitude Arc
                val altitudeFactor = 1.012 + 0.026 * sin(planeProgress * PI)
                val indexFloat = planeProgress * (flightRoute.size - 1)
                val idx = indexFloat.toInt().coerceIn(0, flightRoute.size - 2)
                val frac = indexFloat - idx
                val planeLat = flightRoute[idx].lat + (flightRoute[idx + 1].lat - flightRoute[idx].lat) * frac
                val planeLng = flightRoute[idx].lng + (flightRoute[idx + 1].lng - flightRoute[idx].lng) * frac

                var planeP = latLngToCartesian(planeLat, planeLng, (currentRadius * altitudeFactor).toDouble())
                planeP = rotateY(planeP, cosY, sinY)
                planeP = rotateX(planeP, cosX, sinX)

                if (planeP.z > 0.0) {
                    val px = canvasCenter.x + planeP.x.toFloat()
                    val py = canvasCenter.y - planeP.y.toFloat()

                    // Compute forward tangent heading
                    val nextProgress = (planeProgress + 0.015f).coerceAtMost(1.0f)
                    val nIndexFloat = nextProgress * (flightRoute.size - 1)
                    val nIdx = nIndexFloat.toInt().coerceIn(0, flightRoute.size - 2)
                    val nFrac = nIndexFloat - nIdx
                    val nLat = flightRoute[nIdx].lat + (flightRoute[nIdx + 1].lat - flightRoute[nIdx].lat) * nFrac
                    val nLng = flightRoute[nIdx].lng + (flightRoute[nIdx + 1].lng - flightRoute[nIdx].lng) * nFrac

                    var nextP = latLngToCartesian(nLat, nLng, (currentRadius * altitudeFactor).toDouble())
                    nextP = rotateY(nextP, cosY, sinY)
                    nextP = rotateX(nextP, cosX, sinX)
                    val nx = canvasCenter.x + nextP.x.toFloat()
                    val ny = canvasCenter.y - nextP.y.toFloat()

                    val headingRad = atan2(ny - py, nx - px)
                    val headingDeg = (headingRad * 180.0 / PI).toFloat()

                    // 4. Jet Contrail Trail Afterglow
                    for (step in 1..6) {
                        val trailProg = (planeProgress - step * 0.012f).coerceAtLeast(0f)
                        val tIndexFloat = trailProg * (flightRoute.size - 1)
                        val tIdx = tIndexFloat.toInt().coerceIn(0, flightRoute.size - 2)
                        val tFrac = tIndexFloat - tIdx
                        val tLat = flightRoute[tIdx].lat + (flightRoute[tIdx + 1].lat - flightRoute[tIdx].lat) * tFrac
                        val tLng = flightRoute[tIdx].lng + (flightRoute[tIdx + 1].lng - flightRoute[tIdx].lng) * tFrac

                        var trailP = latLngToCartesian(tLat, tLng, (currentRadius * (1.012 + 0.026 * sin(trailProg * PI))).toDouble())
                        trailP = rotateY(trailP, cosY, sinY)
                        trailP = rotateX(trailP, cosX, sinX)
                        if (trailP.z > 0.0) {
                            val tx = canvasCenter.x + trailP.x.toFloat()
                            val ty = canvasCenter.y - trailP.y.toFloat()
                            val alpha = (0.55f * (1f - step / 7f)).coerceIn(0f, 1f)
                            val trailColor = if (isSupersonic) Color(0xFFEF4444) else Color(0xFFF59E0B)
                            drawCircle(
                                color = trailColor.copy(alpha = alpha),
                                radius = (4.5f - step * 0.5f).coerceAtLeast(1.5f),
                                center = Offset(tx, ty)
                            )
                        }
                    }

                    // 5. Supersonic Aircraft Vector Silhouette & Navigation Strobes
                    withTransform({
                        rotate(degrees = headingDeg, pivot = Offset(px, py))
                    }) {
                        // Authentic Commercial Airliner Vector Silhouette (Fuselage, Swept Wings, Tail Fin)
                        val airlinerPath = Path().apply {
                            // Rounded aerodynamic nose cone
                            moveTo(px + 18f, py)
                            // Port cockpit & fuselage
                            cubicTo(px + 13f, py - 3f, px + 5f, py - 3.2f, px + 2f, py - 3.2f)
                            // Port swept airliner wing leading edge
                            lineTo(px - 7f, py - 19f)
                            // Port winglet tip
                            lineTo(px - 9.5f, py - 19f)
                            // Port wing trailing edge
                            lineTo(px - 7f, py - 3.2f)
                            // Port rear fuselage
                            lineTo(px - 14f, py - 2.2f)
                            // Port horizontal tailplane
                            lineTo(px - 18.5f, py - 8f)
                            lineTo(px - 20.5f, py - 8f)
                            // Tail cone apex
                            lineTo(px - 19f, py)
                            // Starboard horizontal tailplane
                            lineTo(px - 20.5f, py + 8f)
                            lineTo(px - 18.5f, py + 8f)
                            // Starboard rear fuselage
                            lineTo(px - 14f, py + 2.2f)
                            // Starboard wing trailing edge
                            lineTo(px - 7f, py + 3.2f)
                            // Starboard winglet tip
                            lineTo(px - 9.5f, py + 19f)
                            // Starboard swept airliner wing leading edge
                            lineTo(px - 7f, py + 19f)
                            // Starboard forward fuselage
                            lineTo(px + 2f, py + 3.2f)
                            // Starboard cockpit to nose
                            cubicTo(px + 5f, py + 3.2f, px + 13f, py + 3f, px + 18f, py)
                            close()
                        }

                        // Aircraft outer atmospheric glow & solid fuselage fill
                        drawPath(airlinerPath, color = Color(0x66F59E0B), style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        drawPath(airlinerPath, color = Color(0xFF0F172A), style = Fill)
                        drawPath(airlinerPath, color = Color(0xFFFDE68A), style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))

                        // Dual Underwing Jet Turbofan Engine Nacelles
                        val enginePulse = (sin(planeProgress * 30.0) * 0.5 + 0.5).toFloat()
                        // Port Engine
                        drawCircle(color = Color(0xFF1E293B), radius = 2.4f, center = Offset(px - 2f, py - 8f))
                        drawCircle(color = Color(0xFF38BDF8).copy(alpha = 0.7f + 0.3f * enginePulse), radius = 1.8f, center = Offset(px - 3.5f, py - 8f))
                        // Starboard Engine
                        drawCircle(color = Color(0xFF1E293B), radius = 2.4f, center = Offset(px - 2f, py + 8f))
                        drawCircle(color = Color(0xFF38BDF8).copy(alpha = 0.7f + 0.3f * enginePulse), radius = 1.8f, center = Offset(px - 3.5f, py + 8f))

                        // Cockpit Windscreen Slit
                        drawLine(color = Color(0xFF38BDF8), start = Offset(px + 10f, py - 1.8f), end = Offset(px + 10f, py + 1.8f), strokeWidth = 1.5f, cap = StrokeCap.Round)

                        // FAA-Standard Blinking Navigation Strobes
                        val strobeOn = sin(planeProgress * 20.0) > 0.0
                        if (strobeOn) {
                            // Port Wingtip (Red)
                            drawCircle(color = Color(0xFFEF4444), radius = 2.2f, center = Offset(px - 8f, py - 19f))
                            // Starboard Wingtip (Green)
                            drawCircle(color = Color(0xFF10B981), radius = 2.2f, center = Offset(px - 8f, py + 19f))
                            // Tail Beacon Strobe (White Flash)
                            drawCircle(color = Color.White, radius = 2.0f, center = Offset(px - 19f, py))
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // E. NASA Live Natural Disaster Hazards
            // -------------------------------------------------------------
            if (showHazards) {
                hazards.forEach { hazard ->
                    var p = latLngToCartesian(hazard.lat, hazard.lng, currentRadius.toDouble())
                    p = rotateY(p, cosY, sinY)
                    p = rotateX(p, cosX, sinX)

                    if (p.z > 0.0) {
                        val hx = canvasCenter.x + p.x.toFloat()
                        val hy = canvasCenter.y - p.y.toFloat()

                        val hazardColor = when {
                            hazard.category.contains("Volcano", ignoreCase = true) -> Color(0xFFEF4444)
                            hazard.category.contains("Fire", ignoreCase = true) -> Color(0xFFF97316)
                            else -> Color(0xFF06B6D4)
                        }

                        // Pulsating outer aura
                        drawCircle(
                            color = hazardColor.copy(alpha = 0.35f),
                            radius = beaconPulse,
                            center = Offset(hx, hy)
                        )
                        // Core beacon
                        drawCircle(
                            color = hazardColor,
                            radius = 4f,
                            center = Offset(hx, hy)
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // F. Live International Space Station (ISS) Tracker & Orbit
            // -------------------------------------------------------------
            if (showSatellites && issTelemetry != null) {
                // Altitude of ~420km above Earth (radius * 1.066)
                val issRadius = currentRadius * 1.066

                // Orbital Ground Track (~92.9 minute LEO orbit, inclination 51.6 deg)
                val orbitPath = Path()
                var inOrbit = false
                val incRad = 51.6.toRadians
                val lat0Rad = issTelemetry.latitude.toRadians.coerceIn(-incRad + 0.001, incRad - 0.001)
                val u0 = asin((sin(lat0Rad) / sin(incRad)).coerceIn(-1.0, 1.0))

                for (step in 0..72) {
                    val t = step * (92.9 / 72.0)
                    val u = u0 + (step * (2 * PI / 72.0))
                    val orbitLat = asin((sin(incRad) * sin(u)).coerceIn(-1.0, 1.0)).toDegrees
                    val earthRotDeg = t * (360.0 / 1440.0)
                    val orbitLng = ((issTelemetry.longitude + (u - u0).toDegrees * cos(incRad) - earthRotDeg + 540.0) % 360.0) - 180.0

                    var op = latLngToCartesian(orbitLat, orbitLng, issRadius.toDouble())
                    op = rotateY(op, cosY, sinY)
                    op = rotateX(op, cosX, sinX)

                    if (op.z > 0.0) {
                        val ox = canvasCenter.x + op.x.toFloat()
                        val oy = canvasCenter.y - op.y.toFloat()
                        if (!inOrbit) {
                            orbitPath.moveTo(ox, oy)
                            inOrbit = true
                        } else {
                            orbitPath.lineTo(ox, oy)
                        }
                    } else {
                        inOrbit = false
                    }
                }

                drawPath(
                    path = orbitPath,
                    color = Color(0x3338BDF8),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )
                drawPath(
                    path = orbitPath,
                    color = Color(0x8838BDF8),
                    style = Stroke(
                        width = 1.8f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                )

                // Current ISS Satellite Position
                var p = latLngToCartesian(issTelemetry.latitude, issTelemetry.longitude, issRadius.toDouble())
                p = rotateY(p, cosY, sinY)
                p = rotateX(p, cosX, sinX)

                if (p.z > 0.0) {
                    val ix = canvasCenter.x + p.x.toFloat()
                    val iy = canvasCenter.y - p.y.toFloat()

                    issScreenPos = Offset(ix, iy)

                    // Glowing satellite beacon
                    drawCircle(
                        color = Color(0x3338BDF8).copy(alpha = strobeAlpha * 0.6f),
                        radius = 16f,
                        center = Offset(ix, iy)
                    )
                    drawCircle(
                        color = Color(0x6638BDF8),
                        radius = 9f,
                        center = Offset(ix, iy)
                    )
                    drawCircle(
                        color = Color(0xFF38BDF8),
                        radius = 4.5f,
                        center = Offset(ix, iy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.2f,
                        center = Offset(ix, iy)
                    )
                } else {
                    issScreenPos = null
                }
            } else {
                issScreenPos = null
            }
        }

        // 4. Interactive Floating ISS Badge
        if (showSatellites && issTelemetry != null && issScreenPos != null) {
            val pos = issScreenPos!!
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xE60B1220),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x8838BDF8)),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .offset { IntOffset((pos.x + 14).roundToInt(), (pos.y - 14).roundToInt()) }
                    .clickable { onIssSelected?.invoke(issTelemetry) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🛰️ ISS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("•", color = Color(0xFF38BDF8), fontSize = 10.sp)
                    Text("${issTelemetry.altitudeKm.toInt()} km", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // 4. Floating On-Screen Zoom Controls HUD (Vertical Pill on the right edge)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xDD0B1220),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Zoom In (+)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { scope.launch { state.zoomIn() } },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = Color(0xFF38BDF8), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                // Current Zoom Indicator & Reset to 1.0x
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { scope.launch { state.resetZoom() } }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val zoomRounded = (round(state.zoom * 10.0) / 10.0)
                    Text(
                        text = "${zoomRounded}x",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Zoom Out (−)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { scope.launch { state.zoomOut() } },
                    contentAlignment = Alignment.Center
                ) {
                    Text("−", color = Color(0xFF38BDF8), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
