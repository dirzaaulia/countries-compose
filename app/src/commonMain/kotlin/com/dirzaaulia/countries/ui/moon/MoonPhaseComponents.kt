package com.dirzaaulia.countries.ui.moon

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.dirzaaulia.countries.domain.astronomy.EclipseEvent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

@Composable
fun LunarPhaseIcon(
    phaseAngle: Double,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.semantics { this.contentDescription = contentDescription }) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val phaseRadians = phaseAngle * PI / 180.0
        val terminatorScale = cos(phaseRadians).toFloat()
        val isWaxing = phaseAngle < 180.0
        val illuminatedPath = Path()

        for (index in 0..48) {
            val y = -radius + (2f * radius * index / 48f)
            val edge = sqrt((radius * radius - y * y).coerceAtLeast(0f))
            val x = if (isWaxing) edge else -edge
            val point = Offset(center.x + x, center.y + y)
            if (index == 0) illuminatedPath.moveTo(point.x, point.y) else illuminatedPath.lineTo(point.x, point.y)
        }
        for (index in 48 downTo 0) {
            val y = -radius + (2f * radius * index / 48f)
            val edge = sqrt((radius * radius - y * y).coerceAtLeast(0f))
            val x = if (isWaxing) terminatorScale * edge else -terminatorScale * edge
            illuminatedPath.lineTo(center.x + x, center.y + y)
        }
        illuminatedPath.close()

        drawCircle(color = Color(0xFF0F172A), radius = radius, center = center)
        drawPath(color = Color(0xFFFFD54F), path = illuminatedPath)
        drawCircle(
            color = Color(0xFF64748B),
            radius = radius,
            center = center,
            style = Stroke(width = 1.2f),
        )
    }
}

@Composable
fun EclipseAppearancePreview(
    event: EclipseEvent,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f
        val coverage = event.magnitude.coerceIn(0.0, 1.0).toFloat()

        if (event.kind == "solar") {
            drawCircle(Color(0x5540C4FF), radius * 1.12f, center)
            drawCircle(Color(0xFFFFD54F), radius * 0.78f, center)
            val moonRadius =
                when {
                    event.type == "Annular" -> radius * 0.57f
                    event.magnitude >= 1.0 -> radius * 0.82f
                    else -> radius * 0.72f
                }
            val moonOffset = if (event.magnitude >= 1.0 || event.type == "Annular") 0f else radius * (1f - coverage)
            drawCircle(Color(0xFF020408), moonRadius, center + Offset(moonOffset, 0f))
        } else {
            drawCircle(Color(0xFFF97316), radius * 0.82f, center)
            val shadowRadius = radius * (0.46f + coverage * 0.5f)
            val shadowOffset = radius * (1f - coverage) * 0.85f
            drawCircle(Color(0xFF1E293B), shadowRadius, center + Offset(shadowOffset, 0f))
            if (event.type == "Total") drawCircle(Color(0xFF7F1D1D), radius * 0.7f, center)
        }
        drawCircle(Color(0xFF94A3B8), radius * 0.96f, center, style = Stroke(width = 1.2f))
    }
}
