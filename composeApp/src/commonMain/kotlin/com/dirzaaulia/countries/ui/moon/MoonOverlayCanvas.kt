package com.dirzaaulia.countries.ui.moon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toRadians
import com.dirzaaulia.countries.domain.moon.ALL_LUNAR_LANDMARKS
import com.dirzaaulia.countries.domain.moon.LunarLandmark
import com.dirzaaulia.countries.domain.moon.LunarLandmarkType
import com.dirzaaulia.countries.ui.overlay.CELESTIAL_STARS
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun MoonOverlayCanvas(
    state: GlobeState,
    librationLatitude: Double,
    librationLongitude: Double,
    starTwinkle: Float,
    selectedCategory: LunarLandmarkType?,
    selectedLandmark: LunarLandmark?,
    onLandmarkSelected: (LunarLandmark?) -> Unit,
    modifier: Modifier = Modifier,
    sensitivity: Float = 0.38f,
) {
    val scope = rememberCoroutineScope()
    val currentOnLandmarkSelected = rememberUpdatedState(onLandmarkSelected)
    val currentSensitivity = rememberUpdatedState(sensitivity)
    val density = LocalDensity.current

    val visibleLandmarks =
        if (selectedCategory == null) {
            ALL_LUNAR_LANDMARKS
        } else {
            ALL_LUNAR_LANDMARKS.filter { it.type == selectedCategory }
        }

    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                .pointerInput(librationLatitude, librationLongitude, selectedCategory) {
                    val hitRadiusPx = with(density) { 32.dp.toPx() }
                    detectTapGestures { offset ->
                        handleMoonTap(
                            offset = offset,
                            canvasSize = size,
                            state = state,
                            librationLatitude = librationLatitude,
                            librationLongitude = librationLongitude,
                            landmarks = visibleLandmarks,
                            hitRadiusPx = hitRadiusPx,
                            onLandmarkSelected = currentOnLandmarkSelected.value,
                        )
                    }
                }.pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomChange, _ ->
                        scope.launch {
                            state.stopAnimations()
                            val dragFactor = currentSensitivity.value / state.zoom
                            val newY = state.rotationY + pan.x * dragFactor
                            val newX = state.rotationX + pan.y * dragFactor
                            val newZoom = if (zoomChange != 1.0f) state.zoom * zoomChange else null
                            state.snapTo(newX, newY, newZoom)
                        }
                    }
                },
    ) {
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom

        drawCelestialStars(
            starTwinkle = starTwinkle,
            moonRadius = currentRadius,
        )

        drawLandmarkBeacons(
            state = state,
            librationLatitude = librationLatitude,
            librationLongitude = librationLongitude,
            currentRadius = currentRadius.toDouble(),
            starTwinkle = starTwinkle,
            landmarks = visibleLandmarks,
            selectedLandmark = selectedLandmark,
        )
    }
}

private fun handleMoonTap(
    offset: Offset,
    canvasSize: IntSize,
    state: GlobeState,
    librationLatitude: Double,
    librationLongitude: Double,
    landmarks: List<LunarLandmark>,
    hitRadiusPx: Float,
    onLandmarkSelected: (LunarLandmark?) -> Unit,
) {
    val canvasCenter = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val baseRadius = minOf(canvasSize.width, canvasSize.height) * 0.38f
    val currentRadius = baseRadius * state.zoom
    val dx = (offset.x - canvasCenter.x).toDouble()
    val dy = (offset.y - canvasCenter.y).toDouble()
    val d2 = dx * dx + dy * dy

    if (d2 > currentRadius * currentRadius) {
        onLandmarkSelected(null)
        return
    }

    val effectiveRotX = state.rotationX.toDouble() + librationLatitude
    val effectiveRotY = state.rotationY.toDouble() + librationLongitude
    val cosX = cos(effectiveRotX.toRadians)
    val sinX = sin(effectiveRotX.toRadians)
    val cosY = cos(effectiveRotY.toRadians)
    val sinY = sin(effectiveRotY.toRadians)

    var tappedLandmark: LunarLandmark? = null
    var minDistance = Double.MAX_VALUE

    landmarks.forEach { landmark ->
        var p = latLngToCartesian(landmark.lat, landmark.lng, currentRadius.toDouble())
        p = rotateY(p, cosY, sinY)
        p = rotateX(p, cosX, sinX)
        if (p.z > 0.0) {
            val sx = canvasCenter.x + p.x.toFloat()
            val sy = canvasCenter.y - p.y.toFloat()
            val dist = (offset.x - sx).pow(2) + (offset.y - sy).pow(2)
            if (dist < hitRadiusPx * hitRadiusPx && dist < minDistance) {
                minDistance = dist.toDouble()
                tappedLandmark = landmark
            }
        }
    }
    onLandmarkSelected(tappedLandmark)
}

private fun DrawScope.drawCelestialStars(
    starTwinkle: Float,
    moonRadius: Float,
) {
    val canvasCenter = center
    val moonR2 = moonRadius * moonRadius

    CELESTIAL_STARS.forEachIndexed { idx, (normX, normY, starRadius) ->
        val sx = normX * size.width
        val sy = normY * size.height
        val dx = sx - canvasCenter.x
        val dy = sy - canvasCenter.y
        if (dx * dx + dy * dy > moonR2 + 10f) {
            val alpha = if (idx % 2 == 0) starTwinkle else (1.4f - starTwinkle).coerceIn(0.25f, 1f)
            val starColor =
                when {
                    idx % 7 == 0 -> Color(0xFF90CAF9)
                    idx % 11 == 0 -> Color(0xFFFFE082)
                    idx % 13 == 0 -> Color(0xFFFFCCBC)
                    else -> Color.White
                }
            if (starRadius > 1.6f) {
                drawCircle(
                    color = starColor.copy(alpha = alpha * 0.35f),
                    radius = starRadius * 2.5f,
                    center = Offset(sx, sy),
                )
            }
            drawCircle(
                color = starColor.copy(alpha = alpha * 0.92f),
                radius = starRadius * 1.3f,
                center = Offset(sx, sy),
            )
        }
    }
}

private fun DrawScope.drawLandmarkBeacons(
    state: GlobeState,
    librationLatitude: Double,
    librationLongitude: Double,
    currentRadius: Double,
    starTwinkle: Float,
    landmarks: List<LunarLandmark>,
    selectedLandmark: LunarLandmark?,
) {
    val canvasCenter = center
    val effectiveRotX = state.rotationX.toDouble() + librationLatitude
    val effectiveRotY = state.rotationY.toDouble() + librationLongitude
    val cosX = cos(effectiveRotX.toRadians)
    val sinX = sin(effectiveRotX.toRadians)
    val cosY = cos(effectiveRotY.toRadians)
    val sinY = sin(effectiveRotY.toRadians)

    landmarks.forEach { landmark ->
        var p = latLngToCartesian(landmark.lat, landmark.lng, currentRadius)
        p = rotateY(p, cosY, sinY)
        p = rotateX(p, cosX, sinX)
        if (p.z > 0.0) {
            val ax = canvasCenter.x + p.x.toFloat()
            val ay = canvasCenter.y - p.y.toFloat()
            val isSelected = selectedLandmark?.id == landmark.id

            drawLandmarkBeacon(
                center = Offset(ax, ay),
                isSelected = isSelected,
                starTwinkle = starTwinkle,
                type = landmark.type,
            )
        }
    }
}

private fun DrawScope.drawLandmarkBeacon(
    center: Offset,
    isSelected: Boolean,
    starTwinkle: Float,
    type: LunarLandmarkType,
) {
    val beaconColor =
        when (type) {
            LunarLandmarkType.CREWED_APOLLO -> Color(0xFFF59E0B)
            LunarLandmarkType.HISTORIC_ROBOTIC -> Color(0xFFF43F5E)
            LunarLandmarkType.MODERN_INTERNATIONAL -> Color(0xFF06B6D4)
            LunarLandmarkType.COMMERCIAL_CLPS -> Color(0xFF10B981)
            LunarLandmarkType.LUNAR_MARE -> Color(0xFFC084FC)
            LunarLandmarkType.IMPACT_CRATER -> Color(0xFF94A3B8)
            LunarLandmarkType.ARTEMIS_SOUTH_POLE -> Color(0xFF8B5CF6)
        }

    val isGeology = type == LunarLandmarkType.LUNAR_MARE || type == LunarLandmarkType.IMPACT_CRATER

    if (!isGeology) {
        drawCircle(
            color = beaconColor.copy(alpha = if (isSelected) 0.45f * starTwinkle else 0.25f),
            radius = if (isSelected) 26f else 16f,
            center = center,
        )
    }

    drawCircle(
        color = beaconColor.copy(alpha = if (isSelected) 0.9f else 0.65f),
        radius =
            if (isSelected) {
                16f
            } else if (isGeology) {
                9f
            } else {
                11f
            },
        center = center,
        style = Stroke(width = if (isSelected) 2.5f else 1.8f),
    )

    if (!isGeology) {
        drawCircle(
            color = beaconColor,
            radius = if (isSelected) 8f else 5.5f,
            center = center,
            style = Fill,
        )
    }

    drawCircle(
        color = Color.White.copy(alpha = if (isGeology) 0.5f else 1.0f),
        radius = if (isSelected) 4f else 2.5f,
        center = center,
        style = Fill,
    )
}
