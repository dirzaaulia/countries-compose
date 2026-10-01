package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.ui.geometry.Offset
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
