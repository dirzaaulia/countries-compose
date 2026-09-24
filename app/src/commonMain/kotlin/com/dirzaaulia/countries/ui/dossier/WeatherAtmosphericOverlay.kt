package com.dirzaaulia.countries.ui.dossier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class RainDrop(val xRel: Float, val speed: Float, val length: Float, val alpha: Float)
private data class SnowFlake(val xRel: Float, val speed: Float, val radius: Float, val seed: Float)
private data class CloudCluster(val yRel: Float, val speed: Float, val baseRadius: Float, val alpha: Float)
private data class HeatWave(val xRel: Float, val speed: Float, val phase: Float, val height: Float)

@Composable
fun WeatherAtmosphericOverlay(
    weatherCode: Int?,
    tempC: Double?,
    modifier: Modifier = Modifier
) {
    // Single unified master clock eliminates 6 concurrent Compose transitions
    val transition = rememberInfiniteTransition(label = "weatherMaster")
    val masterProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(25200, easing = LinearEasing)),
        label = "weatherMasterClock"
    )

    val rainProgress = (masterProgress * 28f) % 1f
    val snowProgress = (masterProgress * 6.3f) % 1f
    val cloudProgress = (masterProgress * 1.8f) % 1f
    val heatProgress = (masterProgress * 14f) % 1f
    val sunPulse = 0.85f + 0.30f * (sin(masterProgress * 10.5 * 2 * PI).toFloat() * 0.5f + 0.5f)
    val lightningStrobe = (masterProgress * 7.875f) % 1f

    // Pre-computed deterministic particles
    val rainDrops = remember {
        val rng = Random(42)
        List(32) {
            RainDrop(
                xRel = rng.nextFloat(),
                speed = 0.7f + rng.nextFloat() * 0.6f,
                length = 14f + rng.nextFloat() * 12f,
                alpha = 0.35f + rng.nextFloat() * 0.45f
            )
        }
    }

    val snowFlakes = remember {
        val rng = Random(1337)
        List(28) {
            SnowFlake(
                xRel = rng.nextFloat(),
                speed = 0.4f + rng.nextFloat() * 0.6f,
                radius = 2.0f + rng.nextFloat() * 2.8f,
                seed = rng.nextFloat() * 10f
            )
        }
    }

    val clouds = remember {
        listOf(
            CloudCluster(yRel = 0.25f, speed = 0.6f, baseRadius = 24f, alpha = 0.18f),
            CloudCluster(yRel = 0.45f, speed = 1.0f, baseRadius = 32f, alpha = 0.24f),
            CloudCluster(yRel = 0.65f, speed = 1.3f, baseRadius = 28f, alpha = 0.16f)
        )
    }

    val heatWaves = remember {
        val rng = Random(999)
        List(14) {
            HeatWave(
                xRel = it / 14f + rng.nextFloat() * 0.05f,
                speed = 0.8f + rng.nextFloat() * 0.4f,
                phase = rng.nextFloat() * 2f * PI.toFloat(),
                height = 35f + rng.nextFloat() * 25f
            )
        }
    }

    val isHeatWave = (tempC != null && tempC >= 32.0)
    val isThunder = weatherCode in listOf(95, 96, 99)
    val isRain = weatherCode in listOf(51, 53, 55, 61, 63, 65, 66, 67, 80, 81, 82) || isThunder
    val isSnow = weatherCode in listOf(71, 73, 75, 77, 85, 86)
    val isCloudy = weatherCode in listOf(1, 2, 3, 45, 48)

    val heatWavePath = remember { Path() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Extreme Heat Mirage Shimmer Haze
        if (isHeatWave) {
            heatWavePath.reset()
            heatWaves.forEach { hw ->
                val baseY = h * 0.95f
                val startX = hw.xRel * w
                heatWavePath.moveTo(startX, baseY)

                val steps = 8
                val stepH = hw.height / steps
                for (s in 1..steps) {
                    val progressY = baseY - s * stepH
                    val waveOffset = sin((heatProgress * 2 * PI + hw.phase + s * 0.8f).toDouble()).toFloat() * 6f
                    heatWavePath.lineTo(startX + waveOffset, progressY)
                }
            }
            drawPath(
                path = heatWavePath,
                color = Color(0xFFF59E0B).copy(alpha = 0.25f),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )
        }

        // 2. Parallax Drifting Clouds
        if (isCloudy || isRain || isThunder) {
            clouds.forEach { cl ->
                val progress = (cloudProgress * cl.speed) % 1.3f - 0.2f
                val cx = progress * w
                val cy = cl.yRel * h

                drawCircle(
                    color = Color(0xFF94A3B8).copy(alpha = cl.alpha),
                    radius = cl.baseRadius * 1.5f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color(0xFFE2E8F0).copy(alpha = cl.alpha * 0.8f),
                    radius = cl.baseRadius * 1.1f,
                    center = Offset(cx + cl.baseRadius * 0.8f, cy - cl.baseRadius * 0.3f)
                )
                drawCircle(
                    color = Color(0xFF64748B).copy(alpha = cl.alpha * 0.9f),
                    radius = cl.baseRadius * 1.3f,
                    center = Offset(cx - cl.baseRadius * 0.7f, cy + cl.baseRadius * 0.2f)
                )
            }
        }

        // 3. Falling Rain Streaks & Impact Ripples
        if (isRain) {
            rainDrops.forEach { drop ->
                val currProgress = (rainProgress * drop.speed + drop.xRel) % 1f
                val startX = drop.xRel * w + currProgress * 15f
                val startY = currProgress * h
                val endX = startX + 5f
                val endY = startY + drop.length

                drawLine(
                    color = Color(0xFF38BDF8).copy(alpha = drop.alpha),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.8f,
                    cap = StrokeCap.Round
                )

                // Splash ripples when hitting the bottom
                if (currProgress > 0.88f) {
                    val splashFrac = (currProgress - 0.88f) / 0.12f
                    drawCircle(
                        color = Color(0xFF7DD3FC).copy(alpha = (1f - splashFrac) * 0.5f),
                        radius = splashFrac * 8f,
                        center = Offset(endX, h - 2f),
                        style = Stroke(width = 1f)
                    )
                }
            }
        }

        // 4. Harmonic Swaying Snowfall
        if (isSnow) {
            snowFlakes.forEach { flake ->
                val currProgress = (snowProgress * flake.speed + flake.seed) % 1f
                val sway = sin((currProgress * 4 * PI + flake.seed).toDouble()).toFloat() * 14f
                val sx = flake.xRel * w + sway
                val sy = currProgress * h

                drawCircle(
                    color = Color(0xFFF8FAFC).copy(alpha = 0.75f),
                    radius = flake.radius,
                    center = Offset(sx, sy)
                )
            }
        }

        // 5. Electric Thunderstorm Lightning
        if (isThunder) {
            // Periodic flash trigger
            val flashCycle = (lightningStrobe * 5f) % 1f
            if (flashCycle > 0.88f) {
                val flashAlpha = ((flashCycle - 0.88f) / 0.12f) * 0.35f
                drawRect(color = Color(0xFF93C5FD).copy(alpha = flashAlpha))

                // Forked lightning bolt
                val boltPath = Path()
                boltPath.moveTo(w * 0.55f, 0f)
                boltPath.lineTo(w * 0.52f, h * 0.3f)
                boltPath.lineTo(w * 0.60f, h * 0.45f)
                boltPath.lineTo(w * 0.48f, h * 0.75f)
                boltPath.lineTo(w * 0.53f, h * 1.0f)

                drawPath(
                    path = boltPath,
                    color = Color.White.copy(alpha = 0.9f),
                    style = Stroke(width = 2.4f, cap = StrokeCap.Round)
                )
                drawPath(
                    path = boltPath,
                    color = Color(0xFF38BDF8).copy(alpha = 0.45f),
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
            }
        }

        // 6. Clear Sunlit Corona & Solar Lens Flare
        if (weatherCode == 0 || (weatherCode == null && !isRain && !isSnow)) {
            val sunCenter = Offset(w * 0.88f, h * 0.22f)
            val coronaRadius = 32f * sunPulse

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x55FDE047),
                        Color(0x22F59E0B),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = coronaRadius * 2.2f
                ),
                radius = coronaRadius * 2.2f,
                center = sunCenter
            )

            // Solar ray sweeps
            for (i in 0 until 8) {
                val angle = (i * (PI / 4) + sunPulse * 0.2).toFloat()
                val r1 = coronaRadius * 0.9f
                val r2 = coronaRadius * 1.5f
                drawLine(
                    color = Color(0x66FDE047),
                    start = Offset(sunCenter.x + cos(angle) * r1, sunCenter.y + sin(angle) * r1),
                    end = Offset(sunCenter.x + cos(angle) * r2, sunCenter.y + sin(angle) * r2),
                    strokeWidth = 1.5f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
