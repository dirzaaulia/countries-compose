package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryFeature
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryFeatureType
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryFeaturesCatalog
import com.dirzaaulia.countries.ui.solarsystem.drawDeepSpaceStarfield
import com.dirzaaulia.countries.ui.theme.extendedColors
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun GenericPlanetOverlayCanvas(
    planetId: PlanetId,
    state: GlobeState,
    starTwinkle: Float,
    selectedFeature: PlanetaryFeature?,
    onFeatureSelected: (PlanetaryFeature?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnFeatureSelected = rememberUpdatedState(onFeatureSelected)
    val density = LocalDensity.current

    val roboticColor = MaterialTheme.extendedColors.categoryRobotic
    val geologicalColor = MaterialTheme.extendedColors.categoryGeological
    val defaultColor = MaterialTheme.colorScheme.onSurfaceVariant

    val features = remember(planetId) { PlanetaryFeaturesCatalog.getFeaturesForPlanet(planetId) }

    Canvas(
        modifier =
            modifier
                .pointerInput(planetId) {
                    val hitRadiusPx = with(density) { 40.dp.toPx() }
                    detectTapGestures { tapOffset ->
                        handleFeatureTap(
                            offset = tapOffset,
                            canvasSize = size,
                            state = state,
                            hitRadiusPx = hitRadiusPx,
                            features = features,
                            onFeatureSelected = currentOnFeatureSelected.value,
                        )
                    }
                },
    ) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom
        val r2 = currentRadius * currentRadius

        drawDeepSpaceStarfield(
            starTwinkle = starTwinkle,
            occludeR2 = r2,
            canvasCenter = canvasCenter,
            canvasSize = size,
            cameraYaw = state.rotationY,
            cameraPitch = state.rotationX,
        )

        features.forEach { feature ->
            val isSelected = feature.id == selectedFeature?.id
            drawFeatureBeacon(
                feature = feature,
                state = state,
                radius = currentRadius,
                canvasCenter = canvasCenter,
                starTwinkle = starTwinkle,
                isSelected = isSelected,
                roboticColor = roboticColor,
                geologicalColor = geologicalColor,
                defaultColor = defaultColor,
            )
        }
    }
}

private fun handleFeatureTap(
    offset: Offset,
    canvasSize: IntSize,
    state: GlobeState,
    hitRadiusPx: Float,
    features: List<PlanetaryFeature>,
    onFeatureSelected: (PlanetaryFeature?) -> Unit,
) {
    val canvasCenter = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val baseRadius = minOf(canvasSize.width, canvasSize.height) * 0.38f
    val currentRadius = baseRadius * state.zoom
    val maxRadius = (currentRadius + hitRadiusPx).toDouble()
    val dx = (offset.x - canvasCenter.x).toDouble()
    val dy = (offset.y - canvasCenter.y).toDouble()
    val d2 = dx * dx + dy * dy

    if (d2 > maxRadius * maxRadius) {
        onFeatureSelected(null)
        return
    }

    val effectiveRotX = state.rotationX.toDouble()
    val effectiveRotY = state.rotationY.toDouble()
    val cosX = cos(effectiveRotX.toRadians)
    val sinX = sin(effectiveRotX.toRadians)
    val cosY = cos(effectiveRotY.toRadians)
    val sinY = sin(effectiveRotY.toRadians)

    var tappedFeature: PlanetaryFeature? = null
    var minDistance = Double.MAX_VALUE

    features.forEach { feature ->
        var p = latLngToCartesian(feature.lat, feature.lng, currentRadius.toDouble())
        p = rotateY(p, cosY, sinY)
        p = rotateX(p, cosX, sinX)
        if (p.z > 0.0) {
            val sx = canvasCenter.x + p.x.toFloat()
            val sy = canvasCenter.y - p.y.toFloat()
            val dist = (offset.x - sx).pow(2) + (offset.y - sy).pow(2)
            if (dist < hitRadiusPx * hitRadiusPx && dist < minDistance) {
                minDistance = dist.toDouble()
                tappedFeature = feature
            }
        }
    }
    onFeatureSelected(tappedFeature)
}

private fun DrawScope.drawFeatureBeacon(
    feature: PlanetaryFeature,
    state: GlobeState,
    radius: Float,
    canvasCenter: Offset,
    starTwinkle: Float,
    isSelected: Boolean,
    roboticColor: Color,
    geologicalColor: Color,
    defaultColor: Color,
) {
    val effectiveRotX = state.rotationX.toDouble()
    val effectiveRotY = state.rotationY.toDouble()
    val cosX = cos(effectiveRotX.toRadians)
    val sinX = sin(effectiveRotX.toRadians)
    val cosY = cos(effectiveRotY.toRadians)
    val sinY = sin(effectiveRotY.toRadians)

    var p = latLngToCartesian(feature.lat, feature.lng, radius.toDouble())
    p = rotateY(p, cosY, sinY)
    p = rotateX(p, cosX, sinX)

    if (p.z > 0.0) {
        val ax = canvasCenter.x + p.x.toFloat()
        val ay = canvasCenter.y - p.y.toFloat()
        val center = Offset(ax, ay)

        val color =
            when (feature.type) {
                PlanetaryFeatureType.LANDER, PlanetaryFeatureType.ROVER, PlanetaryFeatureType.PROBE, PlanetaryFeatureType.ORBITER -> roboticColor
                PlanetaryFeatureType.BASIN, PlanetaryFeatureType.CRATER, PlanetaryFeatureType.MOUNTAIN, PlanetaryFeatureType.TERRA -> geologicalColor
                else -> defaultColor
            }

        val isGeological = feature.type in listOf(PlanetaryFeatureType.BASIN, PlanetaryFeatureType.CRATER, PlanetaryFeatureType.MOUNTAIN, PlanetaryFeatureType.TERRA, PlanetaryFeatureType.STORM, PlanetaryFeatureType.ATMOSPHERE)

        if (!isGeological) {
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
                } else if (isGeological) {
                    9f
                } else {
                    11f
                },
            center = center,
            style = Stroke(width = if (isSelected) 2.5f else 1.8f),
        )

        if (!isGeological) {
            drawCircle(
                color = color,
                radius = if (isSelected) 8f else 5.5f,
                center = center,
            )
        }

        drawCircle(
            color = Color.White.copy(alpha = if (isGeological) 0.5f else 1.0f),
            radius = if (isSelected) 4f else 2.5f,
            center = center,
        )
    }
}
