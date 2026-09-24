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
    showBorders: Boolean = true,
    showSatellites: Boolean = true,
    showHazards: Boolean = true,
    issTelemetry: ISSTelemetry? = null,
    hazards: List<NasaNaturalEvent> = emptyList(),
    flightRoute: List<LatLng>? = null,
    onHazardSelected: ((NasaNaturalEvent) -> Unit)? = null,
    onIssSelected: ((ISSTelemetry) -> Unit)? = null,
    sunPos: SunPosition = AstronomyMath.calculateSunPosition(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "strobe")
    val strobeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse),
        label = "starTwinkle"
    )
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "beaconPulse"
    )
    val planeProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing)),
        label = "flightPlane"
    )
    var issScreenPos by remember { mutableStateOf<Offset?>(null) }

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
            isPageActive = isPageActive,
            modifier = Modifier.fillMaxSize()
        )

        // 3. Interactive Gestures & Vector Country / Space Overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
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
                .pointerInput(Unit) {
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
                            if (showSatellites && issTelemetry != null && issScreenPos != null) {
                                val sp = issScreenPos!!
                                val distSq = (offset.x - sp.x) * (offset.x - sp.x) + (offset.y - sp.y) * (offset.y - sp.y)
                                if (distSq <= 38f * 38f) {
                                    onIssSelected?.invoke(issTelemetry)
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
                                p = rotateY(p, cosY, sinY)
                                p = rotateX(p, cosX, sinX)

                                val lat = atan2(p.y, sqrt(p.x * p.x + p.z * p.z)).toDegrees
                                val lng = atan2(p.x, p.z).toDegrees
                                val tappedLatLng = LatLng(lat, lng)

                                // Check ISS Proximity Tap (orbital ground radius)
                                if (showSatellites && issTelemetry != null) {
                                    val issDistance = AstronomyMath.calculateGreatCircleDistance(
                                        tappedLatLng,
                                        LatLng(issTelemetry.latitude, issTelemetry.longitude)
                                    )
                                    if (issDistance < 700.0) {
                                        onIssSelected?.invoke(issTelemetry)
                                        return@detectTapGestures
                                    }
                                }

                                // Check Hazards tap
                                if (showHazards && hazards.isNotEmpty()) {
                                    val nearbyHazard = hazards.find { h ->
                                        AstronomyMath.calculateGreatCircleDistance(tappedLatLng, LatLng(h.lat, h.lng)) < 350.0
                                    }
                                    if (nearbyHazard != null) {
                                        onHazardSelected?.invoke(nearbyHazard)
                                        return@detectTapGestures
                                    }
                                }

                                val clickedCountry = countries.find { country ->
                                    country.polygons.any { poly -> isPointInPolygon(tappedLatLng, poly) }
                                }
                                onCountrySelected(clickedCountry?.id)
                            } else {
                                onCountrySelected(null)
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
                val daylightBordersPath = Path()
                val nightBordersPath = Path()
                val transitionSegments = mutableListOf<TransitionSegment>()

                val colorDayCore = Color(0xFF0F172A)    // Crisp obsidian black
                val colorDayHalo = Color(0x66FFFFFF)    // Soft white halo for daylight visibility
                val colorNightCore = Color(0xFFF8FAFC)  // Luminous ivory white
                val colorNightHalo = Color(0x800B1320)  // Dark slate halo for night visibility

                countries.forEach { country ->
                    if (country.id != selectedCountryId) {
                        country.polygons.forEach { polygon ->
                            for (i in polygon.indices) {
                                val latLngA = polygon[i]
                                val latLngB = polygon[(i + 1) % polygon.size]

                                var pA = latLngToCartesian(latLngA.lat, latLngA.lng, currentRadius.toDouble())
                                pA = rotateX(pA, cosX, sinX)
                                pA = rotateY(pA, cosY, sinY)

                                var pB = latLngToCartesian(latLngB.lat, latLngB.lng, currentRadius.toDouble())
                                pB = rotateX(pB, cosX, sinX)
                                pB = rotateY(pB, cosY, sinY)

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
                                        // Twilight transition segment (Connected gradient blend)
                                        val tA = ((sA + 0.04) / 0.08).coerceIn(0.0, 1.0).toFloat()
                                        val tB = ((sB + 0.04) / 0.08).coerceIn(0.0, 1.0).toFloat()

                                        val coreA = androidx.compose.ui.graphics.lerp(colorNightCore, colorDayCore, tA)
                                        val coreB = androidx.compose.ui.graphics.lerp(colorNightCore, colorDayCore, tB)
                                        val haloA = androidx.compose.ui.graphics.lerp(colorNightHalo, colorDayHalo, tA)
                                        val haloB = androidx.compose.ui.graphics.lerp(colorNightHalo, colorDayHalo, tB)

                                        transitionSegments.add(
                                            TransitionSegment(
                                                p1 = Offset(sxA, syA),
                                                p2 = Offset(sxB, syB),
                                                coreColor1 = coreA,
                                                coreColor2 = coreB,
                                                haloColor1 = haloA,
                                                haloColor2 = haloB
                                            )
                                        )
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

                // 3. Transition segments (connecting seamlessly across the terminator)
                transitionSegments.forEach { seg ->
                    val haloBrush = Brush.linearGradient(listOf(seg.haloColor1, seg.haloColor2), start = seg.p1, end = seg.p2)
                    drawLine(brush = haloBrush, start = seg.p1, end = seg.p2, strokeWidth = 2.4f, cap = StrokeCap.Round)

                    val coreBrush = Brush.linearGradient(listOf(seg.coreColor1, seg.coreColor2), start = seg.p1, end = seg.p2)
                    drawLine(brush = coreBrush, start = seg.p1, end = seg.p2, strokeWidth = 1.3f, cap = StrokeCap.Round)
                }
            }

            // -------------------------------------------------------------
            // C. Selected Country Highlight (vivid glowing neon cyan)
            // -------------------------------------------------------------
            val selectedCountry = countries.find { it.id == selectedCountryId }
            selectedCountry?.let { country ->
                country.polygons.forEach { polygon ->
                    val selectedPath = Path()
                    var inPath = false
                    var allPointsVis = true

                    for (i in polygon.indices) {
                        val latLng = polygon[i]
                        var p = latLngToCartesian(latLng.lat, latLng.lng, currentRadius.toDouble())
                        p = rotateX(p, cosX, sinX)
                        p = rotateY(p, cosY, sinY)

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
                            allPointsVis = false
                        }
                    }

                    if (inPath) {
                        selectedPath.close()
                        drawPath(selectedPath, Color(0x4438BDF8).copy(alpha = strobeAlpha * 0.45f), style = Fill)
                    }

                    // Outer soft glow halo
                    drawPath(
                        path = selectedPath,
                        color = Color(0x5538BDF8),
                        style = Stroke(width = 5.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    // Inner sharp vibrant neon cyan stroke
                    drawPath(
                        path = selectedPath,
                        color = Color(0xFF38BDF8),
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

                for (pt in flightRoute) {
                    var p = latLngToCartesian(pt.lat, pt.lng, (currentRadius * 1.01).toDouble())
                    p = rotateX(p, cosX, sinX)
                    p = rotateY(p, cosY, sinY)

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
                drawPath(arcPath, Color(0xFFF59E0B), style = Stroke(width = 2.5f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)))

                // Animated flight plane traveling along arc
                val indexFloat = planeProgress * (flightRoute.size - 1)
                val idx = indexFloat.toInt().coerceIn(0, flightRoute.size - 2)
                val frac = indexFloat - idx
                val planeLat = flightRoute[idx].lat + (flightRoute[idx + 1].lat - flightRoute[idx].lat) * frac
                val planeLng = flightRoute[idx].lng + (flightRoute[idx + 1].lng - flightRoute[idx].lng) * frac

                var planeP = latLngToCartesian(planeLat, planeLng, (currentRadius * 1.015).toDouble())
                planeP = rotateX(planeP, cosX, sinX)
                planeP = rotateY(planeP, cosY, sinY)

                if (planeP.z > 0.0) {
                    val px = canvasCenter.x + planeP.x.toFloat()
                    val py = canvasCenter.y - planeP.y.toFloat()

                    drawCircle(color = Color(0x66F59E0B), radius = 10f, center = Offset(px, py))
                    drawCircle(color = Color(0xFFF59E0B), radius = 5f, center = Offset(px, py))
                    drawCircle(color = Color.White, radius = 2.5f, center = Offset(px, py))
                }
            }

            // -------------------------------------------------------------
            // E. NASA Live Natural Disaster Hazards
            // -------------------------------------------------------------
            if (showHazards) {
                hazards.forEach { hazard ->
                    var p = latLngToCartesian(hazard.lat, hazard.lng, currentRadius.toDouble())
                    p = rotateX(p, cosX, sinX)
                    p = rotateY(p, cosY, sinY)

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
                    op = rotateX(op, cosX, sinX)
                    op = rotateY(op, cosY, sinY)

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
                p = rotateX(p, cosX, sinX)
                p = rotateY(p, cosY, sinY)

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
