package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.dirzaaulia.countries.domain.globe.OrbitCamera
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
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
fun DrawScope.drawSolarStarfield(twinklePhase: Float) {
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
fun DrawScope.drawMilkyWayLane(driftPhase: Float) {
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

/** Deterministic 3D spherical star positions in deep space on unit sphere (x, y, z, radius, colorIdx). */
internal data class CelestialStar(
    val x: Float,
    val y: Float,
    val z: Float,
    val radius: Float,
    val colorIdx: Int,
)

internal val CELESTIAL_STARS: List<CelestialStar> =
    (0..420).map { i ->
        val rng = Random(i * 7919 + 31)
        val theta = rng.nextFloat() * 2f * PI.toFloat()
        val u = rng.nextFloat() * 2f - 1f
        val r = sqrt(1f - u * u)
        CelestialStar(
            x = r * cos(theta),
            y = u,
            z = r * sin(theta),
            radius = 0.6f + rng.nextFloat() * 1.8f,
            colorIdx = i,
        )
    }

/**
 * Draws a true 3D celestial starfield in deep space.
 * All stars are anchored in 3D world space on the celestial sphere.
 * Seamlessly tracks camera orbital pitch and yaw in true 3D perspective.
 * Stars directly occluded by the solid planet disk [occludeR2] are clipped.
 */
fun DrawScope.drawDeepSpaceStarfield(
    starTwinkle: Float,
    occludeR2: Float,
    canvasCenter: Offset,
    canvasSize: Size,
    cameraYaw: Float = 0f,
    cameraPitch: Float = 0f,
) {
    val viewMatrix = FloatArray(16)
    OrbitCamera.computeViewMatrix(viewMatrix, cameraPitch, cameraYaw)
    val v0 = viewMatrix[0]
    val v1 = viewMatrix[1]
    val v2 = viewMatrix[2]
    val v4 = viewMatrix[4]
    val v5 = viewMatrix[5]
    val v6 = viewMatrix[6]
    val v8 = viewMatrix[8]
    val v9 = viewMatrix[9]
    val v10 = viewMatrix[10]

    val starScale = maxOf(canvasSize.width, canvasSize.height) * 0.72f

    CELESTIAL_STARS.forEach { star ->
        // Multiply by camera view matrix directly (identical to GPU vertex shader)
        val xEye = v0 * star.x + v4 * star.y + v8 * star.z
        val yEye = v1 * star.x + v5 * star.y + v9 * star.z
        val zEye = v2 * star.x + v6 * star.y + v10 * star.z

        // Only draw stars in the forward hemisphere of the camera view
        if (zEye > 0f) {
            val sx = canvasCenter.x + xEye * starScale
            val sy = canvasCenter.y - yEye * starScale
            val dx = sx - canvasCenter.x
            val dy = sy - canvasCenter.y

            if (dx * dx + dy * dy > occludeR2) {
                val alpha = if (star.colorIdx % 2 == 0) starTwinkle else (1.4f - starTwinkle).coerceIn(0.25f, 1f)
                val starColor =
                    when {
                        star.colorIdx % 7 == 0 -> Color(0xFF90CAF9) // electric blue
                        star.colorIdx % 11 == 0 -> Color(0xFFFFE082) // warm amber
                        star.colorIdx % 13 == 0 -> Color(0xFFFFCCBC) // deep orange
                        else -> Color.White
                    }
                if (star.radius > 1.6f) {
                    drawCircle(
                        color = starColor.copy(alpha = alpha * 0.35f),
                        radius = star.radius * 2.5f,
                        center = Offset(sx, sy),
                    )
                }
                drawCircle(
                    color = starColor.copy(alpha = alpha * 0.92f),
                    radius = star.radius * 1.3f,
                    center = Offset(sx, sy),
                )
            }
        }
    }
}
