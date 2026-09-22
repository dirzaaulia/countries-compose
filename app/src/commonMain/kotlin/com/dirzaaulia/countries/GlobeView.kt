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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

// 220 deterministic star positions in space
private val STARS = (0..220).map { i ->
    val rng = Random(i * 7919)
    Triple(rng.nextFloat(), rng.nextFloat(), 0.6f + rng.nextFloat() * 1.5f)
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
    showBorders: Boolean = true,
    showSatellites: Boolean = true,
    showHazards: Boolean = true,
    issTelemetry: ISSTelemetry? = null,
    hazards: List<NasaNaturalEvent> = emptyList(),
    flightRoute: List<LatLng>? = null,
    onHazardSelected: ((NasaNaturalEvent) -> Unit)? = null,
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

    val sensitivity = 0.18f

    // Real-time astronomical data
    val sunPos = remember { AstronomyMath.calculateSunPosition() }
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
            STARS.forEachIndexed { idx, (normX, normY, starRadius) ->
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

                    if (inPath && allPointsVis) {
                        selectedPath.close()
                        drawPath(selectedPath, Color(0x2838BDF8).copy(alpha = strobeAlpha * 0.35f), style = Fill)
                    }

                    drawPath(
                        path = selectedPath,
                        color = Color(0x4038BDF8),
                        style = Stroke(width = 4.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        path = selectedPath,
                        color = Color(0xFF38BDF8),
                        style = Stroke(width = 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
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
            // F. Live International Space Station (ISS) Tracker
            // -------------------------------------------------------------
            if (showSatellites && issTelemetry != null) {
                // Altitude of ~420km above Earth (radius * 1.066)
                val issRadius = currentRadius * 1.066
                var p = latLngToCartesian(issTelemetry.latitude, issTelemetry.longitude, issRadius.toDouble())
                p = rotateX(p, cosX, sinX)
                p = rotateY(p, cosY, sinY)

                if (p.z > 0.0) {
                    val ix = canvasCenter.x + p.x.toFloat()
                    val iy = canvasCenter.y - p.y.toFloat()

                    // Glowing satellite marker
                    drawCircle(
                        color = Color(0x3338BDF8),
                        radius = 12f,
                        center = Offset(ix, iy)
                    )
                    drawCircle(
                        color = Color(0xFF38BDF8),
                        radius = 5f,
                        center = Offset(ix, iy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5f,
                        center = Offset(ix, iy)
                    )
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
