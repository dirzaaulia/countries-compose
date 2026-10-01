package com.dirzaaulia.countries.ui.mars

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toRadians
import com.dirzaaulia.countries.domain.mars.MarsLandmark
import com.dirzaaulia.countries.ui.mars.MARS_LANDMARKS
import com.dirzaaulia.countries.ui.overlay.drawDeepSpaceStarfield
import com.dirzaaulia.countries.ui.theme.extendedColors
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun MarsOverlayCanvas(
    state: GlobeState,
    starTwinkle: Float,
    selectedLandmark: MarsLandmark?,
    onLandmarkSelected: (MarsLandmark?) -> Unit,
    sensitivity: Float = 0.38f,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val currentOnLandmarkSelected = rememberUpdatedState(onLandmarkSelected)
    val currentSensitivity = rememberUpdatedState(sensitivity)
    val density = LocalDensity.current

    val roboticColor = MaterialTheme.extendedColors.categoryRobotic
    val geologicalColor = MaterialTheme.extendedColors.categoryGeological
    val polarColor = MaterialTheme.extendedColors.categoryPolar
    val defaultColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(
        modifier =
            modifier
                .pointerInput(Unit) {
                    val hitRadiusPx = with(density) { 32.dp.toPx() }
                    detectTapGestures { tapOffset ->
                        handleMarsTap(
                            offset = tapOffset,
                            canvasSize = size,
                            state = state,
                            hitRadiusPx = hitRadiusPx,
                            onLandmarkSelected = currentOnLandmarkSelected.value,
                        )
                    }
                }.pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomChange, _ ->
                        scope.launch {
                            state.stopAnimations()
                            val dragFactor = currentSensitivity.value / state.zoom
                            val newY = (state.rotationY - pan.x * dragFactor) % 360f
                            val newX = (state.rotationX - pan.y * dragFactor) % 360f
                            val newZoom = if (zoomChange != 1.0f) state.zoom * zoomChange else null
                            state.snapTo(newX, newY, newZoom)
                        }
                    }
                },
    ) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom
        val marsR2 = currentRadius * currentRadius

        drawDeepSpaceStarfield(starTwinkle, marsR2, canvasCenter, size)

        MARS_LANDMARKS.forEach { landmark ->
            val isSelected = landmark.id == selectedLandmark?.id
            drawLandmarkBeacon(
                landmark = landmark,
                state = state,
                radius = currentRadius,
                canvasCenter = canvasCenter,
                starTwinkle = starTwinkle,
                isSelected = isSelected,
                roboticColor = roboticColor,
                geologicalColor = geologicalColor,
                polarColor = polarColor,
                defaultColor = defaultColor,
            )
        }
    }
}

private fun handleMarsTap(
    offset: Offset,
    canvasSize: IntSize,
    state: GlobeState,
    hitRadiusPx: Float,
    onLandmarkSelected: (MarsLandmark?) -> Unit,
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

    val effectiveRotX = state.rotationX.toDouble()
    val effectiveRotY = state.rotationY.toDouble()
    val cosX = cos(effectiveRotX.toRadians)
    val sinX = sin(effectiveRotX.toRadians)
    val cosY = cos(effectiveRotY.toRadians)
    val sinY = sin(effectiveRotY.toRadians)

    var tappedLandmark: MarsLandmark? = null
    var minDistance = Double.MAX_VALUE

    MARS_LANDMARKS.forEach { landmark ->
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

private fun DrawScope.drawLandmarkBeacon(
    landmark: MarsLandmark,
    state: GlobeState,
    radius: Float,
    canvasCenter: Offset,
    starTwinkle: Float,
    isSelected: Boolean,
    roboticColor: Color,
    geologicalColor: Color,
    polarColor: Color,
    defaultColor: Color,
) {
    val effectiveRotX = state.rotationX.toDouble()
    val effectiveRotY = state.rotationY.toDouble()
    val cosX = cos(effectiveRotX.toRadians)
    val sinX = sin(effectiveRotX.toRadians)
    val cosY = cos(effectiveRotY.toRadians)
    val sinY = sin(effectiveRotY.toRadians)

    var p = latLngToCartesian(landmark.lat, landmark.lng, radius.toDouble())
    p = rotateY(p, cosY, sinY)
    p = rotateX(p, cosX, sinX)

    if (p.z > 0.0) {
        val ax = canvasCenter.x + p.x.toFloat()
        val ay = canvasCenter.y - p.y.toFloat()
        val center = Offset(ax, ay)

        val color =
            when (landmark.category) {
                "ROVER", "LANDER" -> roboticColor
                "MOUNTAIN", "CANYON", "BASIN" -> geologicalColor
                "POLAR_CAP" -> polarColor
                else -> defaultColor
            }

        val isFeature = landmark.category != "ROVER" && landmark.category != "LANDER"

        if (!isFeature) {
            drawCircle(
                color = color.copy(alpha = if (isSelected) 0.45f * starTwinkle else 0.25f),
                radius = if (isSelected) 26f else 16f,
                center = center,
            )
        }

        drawCircle(
            color = color.copy(alpha = if (isSelected) 0.9f else 0.65f),
            radius =
                if (isSelected) {
                    16f
                } else if (isFeature) {
                    9f
                } else {
                    11f
                },
            center = center,
            style =
                androidx.compose.ui.graphics.drawscope
                    .Stroke(width = if (isSelected) 2.5f else 1.8f),
        )

        if (!isFeature) {
            drawCircle(
                color = color,
                radius = if (isSelected) 8f else 5.5f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Fill,
            )
        }

        drawCircle(
            color = Color.White.copy(alpha = if (isFeature) 0.5f else 1.0f),
            radius = if (isSelected) 4f else 2.5f,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Fill,
        )
    }
}
