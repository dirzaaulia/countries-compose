package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

/** Deterministic deep-space star positions (normX, normY, radius). */
internal val SOLAR_STARS: List<Triple<Float, Float, Float>> =
    (0..360).map { i ->
        val rng = Random(i * 9311 + 17)
        Triple(rng.nextFloat(), rng.nextFloat(), 0.6f + rng.nextFloat() * 1.8f)
    }

/**
 * Draws a full-screen procedural starfield optimised for the Solar System view.
 * [twinklePhase] in [0f, 1f] drives even/odd alternating flicker.
 */
internal fun DrawScope.drawSolarStarfield(twinklePhase: Float) {
    SOLAR_STARS.forEachIndexed { idx, (normX, normY, radius) ->
        val sx = normX * size.width
        val sy = normY * size.height
        val alpha =
            if (idx % 2 == 0) {
                twinklePhase.coerceIn(0.25f, 1f)
            } else {
                (1.4f - twinklePhase).coerceIn(0.25f, 1f)
            }
        val starColor =
            when {
                idx % 7 == 0 -> Color(0xFF90CAF9) // electric blue
                idx % 11 == 0 -> Color(0xFFFFE082) // warm amber
                idx % 13 == 0 -> Color(0xFFFFCCBC) // deep orange
                else -> Color.White
            }
        if (radius > 1.6f) {
            drawCircle(color = starColor.copy(alpha = alpha * 0.3f), radius = radius * 2.4f, center = Offset(sx, sy))
        }
        drawCircle(color = starColor.copy(alpha = alpha * 0.9f), radius = radius * 1.2f, center = Offset(sx, sy))
    }
}

/** Renders a pristine deep space canvas. */
internal fun DrawScope.drawMilkyWayLane(driftPhase: Float) {
    // Pristine deep space background
}

/**
 * Renders an immersive deep-space starfield background with cosmic nebula gradients
 * and twinkling stars. Shared across all celestial views (Solar System, Earth, Moon, Mars, Planets).
 */
@Composable
fun CelestialStarfieldBackground(
    starTwinkle: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush =
                Brush.radialGradient(
                    colors =
                        listOf(
                            Color(0xFF070E1E),
                            Color(0xFF03060C),
                            Color(0xFF010205),
                        ),
                    center = center,
                    radius = size.maxDimension * 0.9f,
                ),
        )
        drawRect(
            brush =
                Brush.linearGradient(
                    colors =
                        listOf(
                            Color.Transparent,
                            Color(0x15312E81),
                            Color(0x281E1B4B),
                            Color(0x180284C7),
                            Color.Transparent,
                        ),
                    start = Offset(0f, size.height * 0.15f),
                    end = Offset(size.width, size.height * 0.85f),
                ),
        )
        drawSolarStarfield(starTwinkle)
    }
}
