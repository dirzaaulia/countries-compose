package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.dirzaaulia.countries.domain.solarsystem.InteriorLayer
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryInterior
import kotlin.math.sqrt

@Composable
fun InteriorCrossSectionCanvas(
    interior: PlanetaryInterior,
    selectedLayerId: String?,
    onLayerSelected: (InteriorLayer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier =
            modifier.pointerInput(interior) {
                detectTapGestures { offset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    val dist = sqrt(dx * dx + dy * dy)

                    // Only consider taps in the bottom hemisphere (since we draw 0 to 180 degrees)
                    // Actually, let's draw full circles for simplicity of hit testing, or just check dist
                    val baseRadius = minOf(size.width, size.height) * 0.45f
                    val pct = dist / baseRadius

                    if (pct <= 1.0f) {
                        // Find the layer
                        val hitLayer = interior.layers.firstOrNull { it.outerRadiusPercentage >= pct }
                        if (hitLayer != null) {
                            onLayerSelected(hitLayer)
                        }
                    }
                }
            },
    ) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.45f

        // Draw from outside in
        interior.layers.reversed().forEach { layer ->
            val layerRadius = baseRadius * layer.outerRadiusPercentage
            val isSelected = layer.id == selectedLayerId

            // Draw full circle for layer fill
            drawCircle(
                color = Color(layer.colorHex).copy(alpha = if (isSelected) 0.8f else 0.4f),
                radius = layerRadius,
                center = canvasCenter,
                style = Fill,
            )

            // Outline
            drawCircle(
                color = Color.White.copy(alpha = if (isSelected) 0.9f else 0.3f),
                radius = layerRadius,
                center = canvasCenter,
                style = Stroke(width = if (isSelected) 3f else 1f),
            )
        }

        // Draw selection highlight ring if a layer is selected
        val selectedLayer = interior.layers.find { it.id == selectedLayerId }
        if (selectedLayer != null) {
            val selRadius = baseRadius * selectedLayer.outerRadiusPercentage
            drawCircle(
                color = Color.White,
                radius = selRadius + 2f,
                center = canvasCenter,
                style = Stroke(width = 2f),
            )
        }
    }
}
