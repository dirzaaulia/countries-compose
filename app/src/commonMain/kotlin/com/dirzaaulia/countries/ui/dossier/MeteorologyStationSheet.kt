package com.dirzaaulia.countries.ui.dossier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.Country
import com.dirzaaulia.countries.DailyForecastItem
import com.dirzaaulia.countries.HourlyForecastItem
import com.dirzaaulia.countries.LiveCountryDetails
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeteorologyStationSheet(
    country: Country,
    liveDetails: LiveCountryDetails?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val details = liveDetails ?: return

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color(0xF2070D18),
        tonalElevation = 16.dp,
        scrimColor = Color.Black.copy(alpha = 0.72f),
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                color = Color(0x4438BDF8),
                shape = RoundedCornerShape(3.dp)
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(country.flagEmoji, fontSize = 28.sp)
                    Column {
                        Text(
                            text = "METEOROLOGY STATION",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "${country.name} (${country.capital.ifEmpty { "Capital" }})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                MinimalistCloseButton(onClick = onClose)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Hero Atmospheric Station Card with Particle Overlay
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0x300F172A),
                border = BorderStroke(1.dp, Color(0x3338BDF8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    WeatherAtmosphericOverlay(
                        weatherCode = details.weatherCode,
                        tempC = details.weatherTempC,
                        modifier = Modifier.matchParentSize()
                    )

                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${details.weatherTempC?.toInt() ?: 20}°",
                                        color = Color.White,
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "C",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = details.weatherDescription ?: "Fair Conditions",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                val feelsLike = ((details.weatherTempC ?: 20.0) + ((details.weatherHumidity ?: 50) - 50) * 0.05).toInt()
                                Text(
                                    text = "Feels like $feelsLike°C • Wind Chill index normal",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = details.weatherIcon ?: "☀️",
                                fontSize = 48.sp
                            )
                        }

                        // Mini metrics row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SensorBadge(label = "HUMIDITY", value = "${details.weatherHumidity ?: 50}%", icon = "💧")
                            SensorBadge(label = "WIND", value = "${details.weatherWindSpeed?.toInt() ?: 12} km/h", icon = "💨")
                            SensorBadge(label = "UV INDEX", value = "${details.uvIndex ?: 4.0}", icon = "☀️")
                            SensorBadge(label = "PRESSURE", value = "${details.surfacePressureHpa?.toInt() ?: 1013} hPa", icon = "⏲️")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Diurnal Daylight Arc
            Text(
                text = "☀️ DIURNAL SOLAR TRAJECTORY ARC",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x350F172A),
                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    DiurnalSolarArcCanvas(
                        sunrise = details.sunrise ?: "06:00",
                        sunset = details.sunset ?: "18:00",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🌅 Sunrise ${details.sunrise ?: "06:00"}",
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "☀️ Solar Noon",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "🌇 Sunset ${details.sunset ?: "18:00"}",
                            color = Color(0xFFF97316),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. 24-Hour Interactive Hourly Temperature & Precipitation Curve
            Text(
                text = "📈 24-HOUR HOURLY TEMPERATURE & RAIN PROBABILITY",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x350F172A),
                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    HourlySplineChart(
                        hourly = details.hourlyForecast,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. 7-Day Forecast Cards
            Text(
                text = "📅 7-DAY SYNOPTIC OUTLOOK",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                details.dailyForecast.forEach { day ->
                    DailyForecastRow(day = day)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Atmospheric Sensors & Wind Direction Compass Rose
            Text(
                text = "🧭 WIND VECTOR & ATMOSPHERIC SENSORS",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Wind Compass Rose
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x350F172A),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "WIND BEARING",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        WindCompassCanvas(
                            degrees = details.windDirectionDeg ?: 45.0,
                            modifier = Modifier.size(90.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val knots = ((details.weatherWindSpeed ?: 12.0) * 0.539957).toInt()
                        Text(
                            text = "${(details.windDirectionDeg ?: 45.0).toInt()}° • $knots kts",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Barometric & UV Sensors
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x350F172A),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column {
                            Text("BAROMETRIC PRESSURE", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${details.surfacePressureHpa?.toInt() ?: 1013} hPa",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if ((details.surfacePressureHpa ?: 1013.0) >= 1013.0) "High Pressure (Stable)" else "Low Pressure (Unstable)",
                                color = Color(0xFF10B981),
                                fontSize = 10.sp
                            )
                        }

                        HorizontalDivider(color = Color(0x22FFFFFF))

                        Column {
                            Text("SOLAR UV INDEX", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            val uv = details.uvIndex ?: 3.0
                            val (uvText, uvCol) = when {
                                uv <= 2.9 -> "Low" to Color(0xFF10B981)
                                uv <= 5.9 -> "Moderate" to Color(0xFFF59E0B)
                                uv <= 7.9 -> "Very High" to Color(0xFFEF4444)
                                else -> "Extreme" to Color(0xFF9333EA)
                            }
                            Text(
                                text = "$uv ($uvText)",
                                color = uvCol,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Protection: Sunglasses & SPF", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorBadge(label: String, value: String, icon: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0x221E293B),
        border = BorderStroke(1.dp, Color(0x18FFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 12.sp)
            Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color(0xFF64748B), fontSize = 8.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DailyForecastRow(day: DailyForecastItem) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x250F172A),
        border = BorderStroke(1.dp, Color(0x18FFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = day.dayName,
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(76.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(day.weatherIcon, fontSize = 16.sp)
                if (day.precipitationProb > 0) {
                    Text(
                        text = "💧${day.precipitationProb}%",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Min/Max Spread
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${day.tempMin.toInt()}°",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )
                // Color bar
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF38BDF8), Color(0xFFF59E0B))
                            )
                        )
                )
                Text(
                    text = "${day.tempMax.toInt()}°",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DiurnalSolarArcCanvas(
    sunrise: String,
    sunset: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Ground baseline
        drawLine(
            color = Color(0x33FFFFFF),
            start = Offset(16f, h - 8f),
            end = Offset(w - 16f, h - 8f),
            strokeWidth = 2f
        )

        // Arc path
        val arcPath = Path()
        arcPath.moveTo(24f, h - 8f)
        arcPath.cubicTo(
            w * 0.25f, 12f,
            w * 0.75f, 12f,
            w - 24f, h - 8f
        )

        // Glow dashed arc
        drawPath(
            path = arcPath,
            color = Color(0x44F59E0B),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
        drawPath(
            path = arcPath,
            color = Color(0xFFF59E0B),
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )

        // Solar Noon marker dot at apex
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = 6f,
            center = Offset(w / 2f, 18f)
        )
        drawCircle(
            color = Color(0x55FFD54F),
            radius = 12f,
            center = Offset(w / 2f, 18f)
        )

        // Sunrise & Sunset dots
        drawCircle(color = Color(0xFFFDE68A), radius = 4f, center = Offset(24f, h - 8f))
        drawCircle(color = Color(0xFFF97316), radius = 4f, center = Offset(w - 24f, h - 8f))
    }
}

@Composable
private fun HourlySplineChart(
    hourly: List<HourlyForecastItem>,
    modifier: Modifier = Modifier
) {
    if (hourly.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Hourly forecast loading...", color = Color(0xFF64748B), fontSize = 11.sp)
        }
        return
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        selectedIndex?.let { idx ->
            if (idx in hourly.indices) {
                val item = hourly[idx]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hour ${item.time}: ${item.tempC}°C • 💧${item.precipitationProb}% Rain",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Scrubbing", color = Color(0xFF64748B), fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Canvas(
            modifier = modifier
                .pointerInput(hourly) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val step = size.width / (hourly.size - 1).coerceAtLeast(1)
                            val idx = (offset.x / step).toInt().coerceIn(hourly.indices)
                            selectedIndex = idx
                        },
                        onDrag = { change, _ ->
                            val step = size.width / (hourly.size - 1).coerceAtLeast(1)
                            val idx = (change.position.x / step).toInt().coerceIn(hourly.indices)
                            selectedIndex = idx
                        },
                        onDragEnd = { selectedIndex = null },
                        onDragCancel = { selectedIndex = null }
                    )
                }
                .pointerInput(hourly) {
                    detectTapGestures(
                        onTap = { offset ->
                            val step = size.width / (hourly.size - 1).coerceAtLeast(1)
                            val idx = (offset.x / step).toInt().coerceIn(hourly.indices)
                            selectedIndex = idx
                        }
                    )
                }
        ) {
            val w = size.width
            val h = size.height - 24f // leave room for labels

            val minTemp = hourly.minOf { it.tempC } - 2.0
            val maxTemp = hourly.maxOf { it.tempC } + 2.0
            val range = (maxTemp - minTemp).coerceAtLeast(1.0)

            val stepX = w / (hourly.size - 1).coerceAtLeast(1)

            val linePath = Path()
            val fillPath = Path()

            hourly.forEachIndexed { i, item ->
                val x = i * stepX
                val normY = ((item.tempC - minTemp) / range).toFloat()
                val y = h - (normY * (h - 20f))

                if (i == 0) {
                    linePath.moveTo(x, y)
                    fillPath.moveTo(x, h)
                    fillPath.lineTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }

                // Draw precipitation probability bars at bottom
                if (item.precipitationProb > 0) {
                    val barH = (item.precipitationProb / 100f) * 22f
                    drawRect(
                        color = Color(0x5538BDF8),
                        topLeft = Offset(x - 3f, h - barH),
                        size = androidx.compose.ui.geometry.Size(6f, barH)
                    )
                }
            }

            fillPath.lineTo(w, h)
            fillPath.close()

            // Fill under spline
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(Color(0x3538BDF8), Color.Transparent)
                )
            )

            // Temperature curve
            drawPath(
                path = linePath,
                color = Color(0xFF38BDF8),
                style = Stroke(width = 2.4f, cap = StrokeCap.Round)
            )

            // Scrubbing cursor
            selectedIndex?.let { idx ->
                if (idx in hourly.indices) {
                    val x = idx * stepX
                    val normY = ((hourly[idx].tempC - minTemp) / range).toFloat()
                    val y = h - (normY * (h - 20f))

                    drawLine(
                        color = Color(0xFFF59E0B),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.5f
                    )
                    drawCircle(color = Color(0xFFF59E0B), radius = 5f, center = Offset(x, y))
                    drawCircle(color = Color(0xFFFFFFFF), radius = 2.5f, center = Offset(x, y))
                }
            }
        }
    }
}

@Composable
private fun WindCompassCanvas(
    degrees: Double,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = (minOf(size.width, size.height) / 2f) - 6f

        // Dial outer rim
        drawCircle(
            color = Color(0x3338BDF8),
            radius = r,
            style = Stroke(width = 2f)
        )

        // Cardinal ticks
        listOf(0.0, 90.0, 180.0, 270.0).forEach { deg ->
            val rad = deg * (kotlin.math.PI / 180.0)
            val start = Offset(cx + (r - 8f) * sin(rad).toFloat(), cy - (r - 8f) * cos(rad).toFloat())
            val end = Offset(cx + r * sin(rad).toFloat(), cy - r * cos(rad).toFloat())
            drawLine(color = Color(0xFF94A3B8), start = start, end = end, strokeWidth = 2f)
        }

        // Bearing needle
        val rad = degrees * (kotlin.math.PI / 180.0)
        val needleLen = r - 12f
        val needleHead = Offset(cx + needleLen * sin(rad).toFloat(), cy - needleLen * cos(rad).toFloat())
        val needleTail = Offset(cx - (needleLen * 0.4f) * sin(rad).toFloat(), cy + (needleLen * 0.4f) * cos(rad).toFloat())

        // Red North / arrow tip
        drawLine(
            color = Color(0xFFEF4444),
            start = Offset(cx, cy),
            end = needleHead,
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        // Slate tail
        drawLine(
            color = Color(0xFF64748B),
            start = Offset(cx, cy),
            end = needleTail,
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // Center hub
        drawCircle(color = Color.White, radius = 4f, center = Offset(cx, cy))
    }
}
