package com.dirzaaulia.countries.ui.dossier.weather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.HourlyForecastItem
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DiurnalSolarArcCanvas(
    sunrise: String,
    sunset: String,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawLine(
            color = Color(0x33FFFFFF),
            start = Offset(16f, h - 8f),
            end = Offset(w - 16f, h - 8f),
            strokeWidth = 2f,
        )

        val arcPath = Path()
        arcPath.moveTo(24f, h - 8f)
        arcPath.cubicTo(
            w * 0.25f,
            12f,
            w * 0.75f,
            12f,
            w - 24f,
            h - 8f,
        )

        drawPath(
            path = arcPath,
            color = Color(0x44F59E0B),
            style = Stroke(width = 4f, cap = StrokeCap.Round),
        )
        drawPath(
            path = arcPath,
            color = Color(0xFFF59E0B),
            style = Stroke(width = 2f, cap = StrokeCap.Round),
        )

        drawCircle(
            color = Color(0xFFFFD54F),
            radius = 6f,
            center = Offset(w / 2f, 18f),
        )
        drawCircle(
            color = Color(0x55FFD54F),
            radius = 12f,
            center = Offset(w / 2f, 18f),
        )

        drawCircle(color = Color(0xFFFDE68A), radius = 4f, center = Offset(24f, h - 8f))
        drawCircle(color = Color(0xFFF97316), radius = 4f, center = Offset(w - 24f, h - 8f))
    }
}

@Composable
fun HourlySplineChart(
    hourly: List<HourlyForecastItem>,
    modifier: Modifier = Modifier,
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
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Hour ${item.time}: ${item.tempC}°C • 💧${item.precipitationProb}% Rain",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Scrubbing", color = Color(0xFF64748B), fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Canvas(
            modifier =
                modifier
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
                            onDragCancel = { selectedIndex = null },
                        )
                    }.pointerInput(hourly) {
                        detectTapGestures(
                            onTap = { offset ->
                                val step = size.width / (hourly.size - 1).coerceAtLeast(1)
                                val idx = (offset.x / step).toInt().coerceIn(hourly.indices)
                                selectedIndex = idx
                            },
                        )
                    },
        ) {
            val w = size.width
            val h = size.height - 24f

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

                if (item.precipitationProb > 0) {
                    val barH = (item.precipitationProb / 100f) * 22f
                    drawRect(
                        color = Color(0x5538BDF8),
                        topLeft = Offset(x - 3f, h - barH),
                        size = Size(6f, barH),
                    )
                }
            }

            fillPath.lineTo(w, h)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush =
                    Brush.verticalGradient(
                        listOf(Color(0x3538BDF8), Color.Transparent),
                    ),
            )

            drawPath(
                path = linePath,
                color = Color(0xFF38BDF8),
                style = Stroke(width = 2.4f, cap = StrokeCap.Round),
            )

            selectedIndex?.let { idx ->
                if (idx in hourly.indices) {
                    val x = idx * stepX
                    val normY = ((hourly[idx].tempC - minTemp) / range).toFloat()
                    val y = h - (normY * (h - 20f))

                    drawLine(
                        color = Color(0xFFF59E0B),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.5f,
                    )
                    drawCircle(color = Color(0xFFF59E0B), radius = 5f, center = Offset(x, y))
                    drawCircle(color = Color(0xFFFFFFFF), radius = 2.5f, center = Offset(x, y))
                }
            }
        }
    }
}

@Composable
fun WindCompassCanvas(
    degrees: Double,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = (minOf(size.width, size.height) / 2f) - 6f

        drawCircle(
            color = Color(0x3338BDF8),
            radius = r,
            style = Stroke(width = 2f),
        )

        listOf(0.0, 90.0, 180.0, 270.0).forEach { deg ->
            val rad = deg * (PI / 180.0)
            val start = Offset(cx + (r - 8f) * sin(rad).toFloat(), cy - (r - 8f) * cos(rad).toFloat())
            val end = Offset(cx + r * sin(rad).toFloat(), cy - r * cos(rad).toFloat())
            drawLine(color = Color(0xFF94A3B8), start = start, end = end, strokeWidth = 2f)
        }

        val rad = degrees * (PI / 180.0)
        val needleLen = r - 12f
        val needleHead = Offset(cx + needleLen * sin(rad).toFloat(), cy - needleLen * cos(rad).toFloat())
        val needleTail = Offset(cx - (needleLen * 0.4f) * sin(rad).toFloat(), cy + (needleLen * 0.4f) * cos(rad).toFloat())

        drawLine(color = Color(0xFFEF4444), start = Offset(cx, cy), end = needleHead, strokeWidth = 3f, cap = StrokeCap.Round)
        drawLine(color = Color(0xFF64748B), start = Offset(cx, cy), end = needleTail, strokeWidth = 2f, cap = StrokeCap.Round)
        drawCircle(color = Color(0xFF38BDF8), radius = 4f, center = Offset(cx, cy))
    }
}
