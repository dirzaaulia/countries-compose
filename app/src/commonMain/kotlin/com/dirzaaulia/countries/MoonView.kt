package com.dirzaaulia.countries

import com.dirzaaulia.countries.util.formatNumber
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.*

data class ApolloSite(
    val name: String,
    val mission: String,
    val date: String,
    val astronaut: String,
    val lat: Double,
    val lng: Double,
    val significance: String
)

val APOLLO_SITES = listOf(
    ApolloSite(
        name = "Apollo 11",
        mission = "First Human Moon Landing",
        date = "July 20, 1969",
        astronaut = "Neil Armstrong & Buzz Aldrin",
        lat = 0.67408,
        lng = 23.47297,
        significance = "\"That's one small step for man, one giant leap for mankind.\" Landing site: Mare Tranquillitatis."
    ),
    ApolloSite(
        name = "Apollo 12",
        mission = "Precision Lunar Pinpoint",
        date = "November 19, 1969",
        astronaut = "Pete Conrad & Alan Bean",
        lat = -3.01239,
        lng = -23.42157,
        significance = "Landed 180m from Surveyor III probe in Oceanus Procellarum."
    ),
    ApolloSite(
        name = "Apollo 14",
        mission = "Fra Mauro Highlands",
        date = "February 5, 1971",
        astronaut = "Alan Shepard & Edgar Mitchell",
        lat = -3.64530,
        lng = -17.47136,
        significance = "Explored Cone crater and Shepard famously struck two golf balls on the Moon."
    ),
    ApolloSite(
        name = "Apollo 15",
        mission = "First Lunar Roving Vehicle",
        date = "July 30, 1971",
        astronaut = "David Scott & James Irwin",
        lat = 26.13222,
        lng = 3.63386,
        significance = "Explored the dramatic 300m deep Hadley Rille canyon."
    ),
    ApolloSite(
        name = "Apollo 16",
        mission = "Descartes Highlands",
        date = "April 21, 1972",
        astronaut = "John Young & Charles Duke",
        lat = -8.97301,
        lng = 15.50019,
        significance = "First investigation of lunar central highlands plateau."
    ),
    ApolloSite(
        name = "Apollo 17",
        mission = "Final Crewed Mission of Apollo",
        date = "December 11, 1972",
        astronaut = "Eugene Cernan & Harrison Schmitt",
        lat = 20.19080,
        lng = 30.77168,
        significance = "Harrison Schmitt was the first trained scientist (geologist) to walk on the Moon."
    )
)

// Major Lunar Maria (Basaltic volcanic dark plains)
private data class LunarMare(val name: String, val lat: Double, val lng: Double, val radiusDeg: Double)
private val LUNAR_MARIA = listOf(
    LunarMare("Oceanus Procellarum", 18.4, -57.4, 38.0),
    LunarMare("Mare Imbrium", 32.8, -15.6, 26.0),
    LunarMare("Mare Serenitatis", 28.0, 17.5, 16.0),
    LunarMare("Mare Tranquillitatis", 8.5, 31.4, 18.0),
    LunarMare("Mare Crisium", 17.0, 59.1, 12.0),
    LunarMare("Mare Fecunditatis", -7.8, 51.3, 15.0),
    LunarMare("Mare Nectaris", -15.2, 35.5, 10.0),
    LunarMare("Mare Nubium", -21.3, -16.6, 17.0),
    LunarMare("Mare Humorum", -24.4, -38.4, 11.0)
)

// Major Impact Craters with high albedo and ray systems
private data class LunarCrater(val name: String, val lat: Double, val lng: Double, val radiusDeg: Double, val hasRays: Boolean)
private val LUNAR_CRATERS = listOf(
    LunarCrater("Tycho", -43.3, -11.2, 4.0, true),
    LunarCrater("Copernicus", 9.6, -20.1, 4.5, true),
    LunarCrater("Kepler", 8.1, -38.0, 2.8, true),
    LunarCrater("Aristarchus", 23.7, -47.4, 3.0, true),
    LunarCrater("Plato", 51.6, -9.3, 4.2, false),
    LunarCrater("Langrenus", -8.9, 61.1, 5.0, false)
)

@Composable
fun MoonView(
    moonInfo: MoonInfo,
    modifier: Modifier = Modifier,
    state: GlobeState = remember { GlobeState(initialRotationX = 0f, initialRotationY = 0f, initialZoom = 1.0f) },
    sensitivity: Float = 0.38f
) {
    val scope = rememberCoroutineScope()
    var selectedApolloSite by remember { mutableStateOf<ApolloSite?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF020408))
    ) {
        // 1. Deep Space Cosmic Starfield
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starCount = 95
            val seed = 1337L
            val random = kotlin.random.Random(seed)
            for (i in 0 until starCount) {
                val sx = random.nextFloat() * size.width
                val sy = random.nextFloat() * size.height
                val r = if (i % 8 == 0) 1.8f else if (i % 3 == 0) 1.2f else 0.8f
                val alpha = 0.35f + (random.nextFloat() * 0.55f)
                val color = if (i % 6 == 0) Color(0xFFBAE6FD) else if (i % 11 == 0) Color(0xFFFEF08A) else Color.White
                drawCircle(color = color.copy(alpha = alpha), radius = r, center = Offset(sx, sy))
            }
        }

        // 2. Interactive 3D Moon Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val canvasCenter = Offset(size.width / 2f, size.height / 2f)
                        val baseRadius = minOf(size.width, size.height) * 0.36f
                        val currentRadius = baseRadius * state.zoom
                        val dx = (offset.x - canvasCenter.x).toDouble()
                        val dy = (offset.y - canvasCenter.y).toDouble()
                        val d2 = dx * dx + dy * dy

                        if (d2 <= currentRadius * currentRadius) {
                            val radX = state.rotationX.toDouble().toRadians
                            val radY = state.rotationY.toDouble().toRadians
                            val cosX = cos(radX); val sinX = sin(radX)
                            val cosY = cos(radY); val sinY = sin(radY)

                            // Check Apollo Site taps
                            var tappedSite: ApolloSite? = null
                            var minDistance = Double.MAX_VALUE
                            APOLLO_SITES.forEach { site ->
                                var p = latLngToCartesian(site.lat, site.lng, currentRadius.toDouble())
                                p = rotateX(p, cosX, sinX)
                                p = rotateY(p, cosY, sinY)
                                if (p.z > 0.0) {
                                    val sx = canvasCenter.x + p.x.toFloat()
                                    val sy = canvasCenter.y - p.y.toFloat()
                                    val dist = (offset.x - sx).pow(2) + (offset.y - sy).pow(2)
                                    if (dist < 34f * 34f && dist < minDistance) {
                                        minDistance = dist.toDouble()
                                        tappedSite = site
                                    }
                                }
                            }
                            selectedApolloSite = tappedSite
                        } else {
                            selectedApolloSite = null
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomChange, _ ->
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
            val baseRadius = minOf(size.width, size.height) * 0.36f
            val currentRadius = baseRadius * state.zoom

            val radX = state.rotationX.toDouble().toRadians
            val radY = state.rotationY.toDouble().toRadians
            val cosX = cos(radX); val sinX = sin(radX)
            val cosY = cos(radY); val sinY = sin(radY)

            // A. Outer Ethereal Lunar Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x35E2E8F0),
                        Color(0x1594A3B8),
                        Color(0x0594A3B8),
                        Color.Transparent
                    ),
                    center = canvasCenter,
                    radius = currentRadius * 1.35f
                ),
                radius = currentRadius * 1.35f,
                center = canvasCenter
            )

            // B. Unlit Regolith Base Disc (velvety charcoal dark side with subtle earthshine)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF080D18)),
                    center = canvasCenter,
                    radius = currentRadius
                ),
                radius = currentRadius,
                center = canvasCenter
            )

            // C. 3D Projector Helper
            fun project(lat: Double, lng: Double, radius: Double = currentRadius.toDouble()): Point3D? {
                var p = latLngToCartesian(lat, lng, radius)
                p = rotateX(p, cosX, sinX)
                p = rotateY(p, cosY, sinY)
                return if (p.z > 0.0) p else null
            }

            // D. Lunar Maria (Dark Basaltic Seas)
            LUNAR_MARIA.forEach { mare ->
                val p = project(mare.lat, mare.lng)
                if (p != null) {
                    val mx = canvasCenter.x + p.x.toFloat()
                    val my = canvasCenter.y - p.y.toFloat()
                    val mareRadius = (currentRadius * (mare.radiusDeg / 90.0) * (p.z / currentRadius).coerceIn(0.2, 1.0)).toFloat()
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xCC0B1320), Color(0x880F172A), Color.Transparent),
                            center = Offset(mx, my),
                            radius = mareRadius
                        ),
                        radius = mareRadius,
                        center = Offset(mx, my)
                    )
                }
            }

            // E. Real-Time Astronomical Phase Lighting & Terminator
            val illum = moonInfo.illuminatedFraction.toFloat()
            val phaseAngle = moonInfo.phaseAngle
            val isWaxing = phaseAngle in 0.0..180.0
            val sunDirectionSign = if (isWaxing) 1.0 else -1.0

            val lightCenterOffset = Offset(
                canvasCenter.x + (currentRadius * 0.45f * sunDirectionSign).toFloat(),
                canvasCenter.y
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xEEF8FAFC),
                        Color(0xCCE2E8F0),
                        Color(0x8894A3B8),
                        Color.Transparent
                    ),
                    center = lightCenterOffset,
                    radius = currentRadius * (0.85f + illum * 0.55f)
                ),
                radius = currentRadius,
                center = canvasCenter
            )

            // F. Major Craters & Tycho Ray System
            LUNAR_CRATERS.forEach { crater ->
                val p = project(crater.lat, crater.lng)
                if (p != null) {
                    val cx = canvasCenter.x + p.x.toFloat()
                    val cy = canvasCenter.y - p.y.toFloat()
                    val cRadius = (currentRadius * (crater.radiusDeg / 90.0)).toFloat().coerceAtLeast(3.5f)

                    // Draw Tycho bright ejecta ray streamers across the globe
                    if (crater.hasRays && crater.name == "Tycho") {
                        val rayAngles = listOf(15f, 45f, 85f, 130f, 175f, 220f, 260f, 310f)
                        rayAngles.forEach { deg ->
                            val rad = (deg.toDouble()).toRadians
                            val rayLength = currentRadius * 0.85f
                            val ex = cx + (rayLength * cos(rad)).toFloat()
                            val ey = cy + (rayLength * sin(rad)).toFloat()
                            drawLine(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0x44F8FAFC), Color(0x10FFFFFF), Color.Transparent),
                                    start = Offset(cx, cy),
                                    end = Offset(ex, ey)
                                ),
                                start = Offset(cx, cy),
                                end = Offset(ex, ey),
                                strokeWidth = 1.8f
                            )
                        }
                    }

                    // Crater outer rim
                    drawCircle(
                        color = Color(0x66FFFFFF),
                        radius = cRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.6f)
                    )
                    // Crater central peak highlight
                    drawCircle(
                        color = Color(0xAAFFFFFF),
                        radius = (cRadius * 0.35f).coerceAtLeast(1.5f),
                        center = Offset(cx, cy),
                        style = Fill
                    )
                }
            }

            // G. Historic Apollo Landing Sites (Interactive Glowing Markers)
            APOLLO_SITES.forEach { site ->
                val p = project(site.lat, site.lng)
                if (p != null) {
                    val ax = canvasCenter.x + p.x.toFloat()
                    val ay = canvasCenter.y - p.y.toFloat()
                    val isSelected = selectedApolloSite?.name == site.name

                    // Pulsing golden beacon
                    drawCircle(
                        color = if (isSelected) Color(0xFFFFD54F) else Color(0xFF38BDF8).copy(alpha = 0.40f),
                        radius = if (isSelected) 14f else 8f,
                        center = Offset(ax, ay),
                        style = Stroke(width = if (isSelected) 2.5f else 1.5f)
                    )
                    drawCircle(
                        color = if (isSelected) Color(0xFFFFD54F) else Color(0xFF38BDF8),
                        radius = if (isSelected) 5f else 3.5f,
                        center = Offset(ax, ay),
                        style = Fill
                    )
                }
            }

            // H. Spherical Limb Outline
            drawCircle(
                color = Color(0x4494A3B8),
                radius = currentRadius,
                center = canvasCenter,
                style = Stroke(width = 1.5f)
            )
        }

        // 3. Floating Apollo Mission Inspector Card
        AnimatedVisibility(
            visible = selectedApolloSite != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            selectedApolloSite?.let { site ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xEE0B1220),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("🚀", fontSize = 24.sp)
                                Column {
                                    Text(
                                        text = site.name,
                                        color = Color(0xFFFFD54F),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = site.mission,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF))
                                    .clickable { selectedApolloSite = null },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✕", color = Color.White, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = site.significance,
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Astronauts: ${site.astronaut}",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = site.date,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 4. Compact Real-Time Lunar Telemetry HUD (Bottom Bar)
        if (selectedApolloSite == null) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xDD0B1324),
                border = BorderStroke(1.dp, Color(0x3338BDF8)),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(moonInfo.phaseEmoji, fontSize = 28.sp)
                    Column {
                        Text(
                            text = "${moonInfo.phaseName} • ${(moonInfo.illuminatedFraction * 100).toInt()}% Illuminated",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Earth Distance: ${formatNumber(moonInfo.distanceKm)} km • 6 Apollo Sites",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
