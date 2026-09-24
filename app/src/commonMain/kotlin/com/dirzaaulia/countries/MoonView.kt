package com.dirzaaulia.countries

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.hud.ApolloMissionSheet
import com.dirzaaulia.countries.ui.hud.MoonDetailSheet
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private fun getDaysInMonths(year: Int): IntArray {
    val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    return intArrayOf(31, if (isLeap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
}

private fun epochMillisToYearAndDay(epochMillis: Long): Triple<Int, Int, Int> {
    var days = epochMillis / 86400000L
    var year = 1970
    while (true) {
        val leap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        val yearDays = if (leap) 366 else 365
        if (days >= yearDays) {
            days -= yearDays
            year++
        } else {
            break
        }
    }
    val dayOfYear = days.toInt() + 1
    val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    val daysInMonth = intArrayOf(31, if (isLeap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var d = dayOfYear
    var month = 0
    for (m in 0 until 12) {
        if (d <= daysInMonth[m]) {
            month = m
            break
        }
        d -= daysInMonth[m]
    }
    val dayOfMonth = d
    return Triple(year, dayOfYear, dayOfMonth)
}

private fun dayOfYearToEpochMillis(year: Int, dayOfYear: Int, hour: Int = 12): Long {
    var days = 0L
    for (y in 1970 until year) {
        val leap = (y % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        days += if (leap) 366 else 365
    }
    days += (dayOfYear - 1)
    return days * 86400000L + hour * 3600000L
}

private fun formatDayAndMonth(year: Int, dayOfYear: Int): String {
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    val daysInMonth = intArrayOf(31, if (isLeap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var d = dayOfYear.coerceIn(1, if (isLeap) 366 else 365)
    for (m in 0 until 12) {
        if (d <= daysInMonth[m]) {
            return "$d ${monthNames[m]} $year"
        }
        d -= daysInMonth[m]
    }
    return "31 Dec $year"
}

private fun getMonthFromDayOfYear(year: Int, dayOfYear: Int): Int {
    val daysInMonth = getDaysInMonths(year)
    var d = dayOfYear
    for (m in 0 until 12) {
        if (d <= daysInMonth[m]) return m
        d -= daysInMonth[m]
    }
    return 11
}

private fun getFirstDayOfMonth(year: Int, monthIndex: Int): Int {
    val daysInMonth = getDaysInMonths(year)
    var day = 1
    for (m in 0 until monthIndex.coerceIn(0, 11)) {
        day += daysInMonth[m]
    }
    return day
}

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

@Composable
fun MoonView(
    moonInfo: MoonInfo,
    modifier: Modifier = Modifier,
    isPageActive: Boolean = true,
    state: GlobeState = remember { GlobeState(initialRotationX = 0f, initialRotationY = 0f, initialZoom = 1.0f) },
    sensitivity: Float = 0.38f
) {
    val scope = rememberCoroutineScope()
    var selectedApolloSite by remember { mutableStateOf<ApolloSite?>(null) }
    var showMoonDetailSheet by remember { mutableStateOf(false) }

    // Hourly refresh so the Moon terminator stays accurate throughout the day
    var now by remember { mutableStateOf(currentEpochMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3_600_000L)
            now = currentEpochMillis()
        }
    }
    val (currentYear, todayDayOfYear, _) = remember(now) { epochMillisToYearAndDay(now) }
    var selectedDayOfYear by remember { mutableStateOf(todayDayOfYear) }
    val isLeapYear = (currentYear % 4 == 0 && currentYear % 100 != 0) || (currentYear % 400 == 0)
    val totalDaysInYear = if (isLeapYear) 366 else 365

    // Dynamic Moon phase computed for selected day of year (1 Jan to 31 Dec)
    val displayedMoonInfo = remember(selectedDayOfYear, currentYear) {
        if (selectedDayOfYear == todayDayOfYear) {
            moonInfo
        } else {
            val millis = dayOfYearToEpochMillis(currentYear, selectedDayOfYear)
            AstronomyMath.calculateMoonInfo(millis)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "moonStarTwinkle")
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Reverse),
        label = "moonStarTwinkle"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF020408))
    ) {
        // 1. Hardware Accelerated 3D Moon Sphere (OpenGL ES / WebGL with NASA texture & sun lighting)
        Moon3DPlatformView(
            state = state,
            phaseAngle = displayedMoonInfo.phaseAngle,
            isPageActive = isPageActive,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Interactive Apollo Landing Sites, Stars & Gesture Control Overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val canvasCenter = Offset(size.width / 2f, size.height / 2f)
                        val baseRadius = minOf(size.width, size.height) * 0.38f
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
                                p = rotateY(p, cosY, sinY)
                                p = rotateX(p, cosX, sinX)
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
            val baseRadius = minOf(size.width, size.height) * 0.38f
            val currentRadius = baseRadius * state.zoom

            // 0. Twinkling Stars in Deep Space
            val moonR2 = currentRadius * currentRadius
            CELESTIAL_STARS.forEachIndexed { idx, (normX, normY, starRadius) ->
                val sx = normX * size.width
                val sy = normY * size.height
                val dx = sx - canvasCenter.x
                val dy = sy - canvasCenter.y
                if (dx * dx + dy * dy > moonR2 + 10f) {
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

            val radX = state.rotationX.toDouble().toRadians
            val radY = state.rotationY.toDouble().toRadians
            val cosX = cos(radX); val sinX = sin(radX)
            val cosY = cos(radY); val sinY = sin(radY)

            fun project(lat: Double, lng: Double, radius: Double = currentRadius.toDouble()): Point3D? {
                var p = latLngToCartesian(lat, lng, radius)
                p = rotateY(p, cosY, sinY)
                p = rotateX(p, cosX, sinX)
                return if (p.z > 0.0) p else null
            }

            // Interactive Apollo Landing Sites (Glowing Target Beacons)
            APOLLO_SITES.forEach { site ->
                val p = project(site.lat, site.lng)
                if (p != null) {
                    val ax = canvasCenter.x + p.x.toFloat()
                    val ay = canvasCenter.y - p.y.toFloat()
                    val isSelected = selectedApolloSite?.name == site.name

                    // Pulsing golden beacon
                    drawCircle(
                        color = if (isSelected) Color(0xFFFFD54F) else Color(0xFF38BDF8).copy(alpha = 0.50f),
                        radius = if (isSelected) 14f else 8f,
                        center = Offset(ax, ay),
                        style = Stroke(width = if (isSelected) 2.5f else 1.5f)
                    )
                    drawCircle(
                        color = if (isSelected) Color(0xFFFFD54F) else Color(0xFF38BDF8),
                        radius = if (isSelected) 5.5f else 3.5f,
                        center = Offset(ax, ay),
                        style = Fill
                    )
                }
            }
        }

        // 3. Apollo Mission Inspector Sheet (Unified Modal Bottom Sheet)
        if (selectedApolloSite != null) {
            ApolloMissionSheet(
                site = selectedApolloSite!!,
                onClose = { selectedApolloSite = null }
            )
        }

        // Moon Astronomy Detail Sheet
        if (showMoonDetailSheet) {
            MoonDetailSheet(
                moonInfo = displayedMoonInfo,
                onClose = { showMoonDetailSheet = false }
            )
        }

        // 4. Yearly Lunar Phase Scrubber HUD (1 Jan to 31 Dec)
        if (selectedApolloSite == null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xEE0B1220),
                border = BorderStroke(1.dp, Color(0x3338BDF8)),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    // Header: Phase Info & Date / Today Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable { showMoonDetailSheet = true }
                        ) {
                            Text(
                                text = displayedMoonInfo.phaseEmoji,
                                fontSize = 28.sp
                            )
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${displayedMoonInfo.phaseName} • ${(displayedMoonInfo.illuminatedFraction * 100).toInt()}%",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ⓘ",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = formatDayAndMonth(currentYear, selectedDayOfYear),
                                    color = Color(0xFFFFD54F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Prev day button
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2238BDF8))
                                    .clickable {
                                        if (selectedDayOfYear > 1) selectedDayOfYear--
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("‹", color = Color(0xFF38BDF8), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            // Next day button
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2238BDF8))
                                    .clickable {
                                        if (selectedDayOfYear < totalDaysInYear) selectedDayOfYear++
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("›", color = Color(0xFF38BDF8), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            // Today reset button if navigated away
                            if (selectedDayOfYear != todayDayOfYear) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0x3338BDF8),
                                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                    modifier = Modifier.clickable { selectedDayOfYear = todayDayOfYear }
                                ) {
                                    Text(
                                        text = "TODAY",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Scrubber Slider: 1 Jan to 31 Dec
                    Slider(
                        value = selectedDayOfYear.toFloat(),
                        onValueChange = { selectedDayOfYear = it.toInt().coerceIn(1, totalDaysInYear) },
                        valueRange = 1f..totalDaysInYear.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFD54F),
                            activeTrackColor = Color(0xFFFFD54F),
                            inactiveTrackColor = Color(0x3338BDF8)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                    )

                    // Month Quick Chips (Jan - Dec)
                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        monthNames.forEachIndexed { mIdx, mName ->
                            val isCurrentMonth = getMonthFromDayOfYear(currentYear, selectedDayOfYear) == mIdx
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrentMonth) Color(0xFFFFD54F).copy(alpha = 0.25f) else Color(0x15FFFFFF),
                                border = if (isCurrentMonth) BorderStroke(1.dp, Color(0xFFFFD54F)) else null,
                                modifier = Modifier.clickable {
                                    selectedDayOfYear = getFirstDayOfMonth(currentYear, mIdx)
                                }
                            ) {
                                Text(
                                    text = mName,
                                    color = if (isCurrentMonth) Color(0xFFFFD54F) else Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
