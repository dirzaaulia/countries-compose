package com.dirzaaulia.countries

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import kotlin.math.*

@Composable
fun GlobeView(
    countries: List<Country>,
    selectedCountryId: String?,
    onCountrySelected: (String?) -> Unit,
    state: GlobeState
) {
    val scope = rememberCoroutineScope()
    
    // Smooth strobe/pulse effects
    val infiniteTransition = rememberInfiniteTransition(label = "strobe")
    val strobeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    val markerRadiusPulse by infiniteTransition.animateFloat(
        initialValue = 4f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "markerPulse"
    )

    val sensitivity = 0.25f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        scope.launch { state.stopAnimations() }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    // Drag logic now uses the latest values from state
                    val dragFactor = sensitivity / state.zoom
                    val newY = state.rotationY + dragAmount.x * dragFactor
                    val newX = state.rotationX + dragAmount.y * dragFactor
                    scope.launch {
                        state.snapTo(newX, newY)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val canvasSize = size
                    val canvasCenter = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
                    val baseRadius = minOf(canvasSize.width, canvasSize.height) * 0.4f
                    val currentRadius = baseRadius * state.zoom
                    
                    val dx = (offset.x - canvasCenter.x).toDouble()
                    val dy = (offset.y - canvasCenter.y).toDouble()
                    
                    val d2 = dx * dx + dy * dy
                    if (d2 <= currentRadius * currentRadius) {
                        val dz = sqrt(currentRadius * currentRadius - d2)
                        
                        val radX = Math.toRadians(-state.rotationX.toDouble())
                        val radY = Math.toRadians(-state.rotationY.toDouble())
                        val cosX = cos(radX); val sinX = sin(radX)
                        val cosY = cos(radY); val sinY = sin(radY)

                        var p = Point3D(dx, -dy, dz) 
                        p = rotateY(p, cosY, sinY)
                        p = rotateX(p, cosX, sinX)
                        
                        val lat = Math.toDegrees(atan2(p.y, sqrt(p.x * p.x + p.z * p.z)))
                        val lng = Math.toDegrees(atan2(p.x, p.z))
                        
                        val tappedLatLng = LatLng(lat, lng)
                        val clickedCountry = countries.find { country ->
                            country.polygons.any { poly -> isPointInPolygon(tappedLatLng, poly) }
                        }
                        onCountrySelected(clickedCountry?.id)
                    }
                }
            }
    ) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.4f
        val currentRadius = baseRadius * state.zoom
        
        val radX = Math.toRadians(state.rotationX.toDouble())
        val radY = Math.toRadians(state.rotationY.toDouble())
        val cosX = cos(radX); val sinX = sin(radX)
        val cosY = cos(radY); val sinY = sin(radY)

        drawCircle(color = Color(0xFF1A237E), radius = currentRadius, center = canvasCenter)
        
        countries.forEach { country ->
            val isSelected = country.id == selectedCountryId
            country.polygons.forEach { polygon ->
                val path = Path()
                var first = true
                var hasVisiblePoints = false
                
                polygon.forEach { latLng ->
                    var p = latLngToCartesian(latLng.lat, latLng.lng, currentRadius.toDouble())
                    p = rotateX(p, cosX, sinX)
                    p = rotateY(p, cosY, sinY)
                    
                    if (p.z > 0) {
                        hasVisiblePoints = true
                        val screenX = canvasCenter.x + p.x.toFloat()
                        val screenY = canvasCenter.y - p.y.toFloat()
                        if (first) { path.moveTo(screenX, screenY); first = false }
                        else { path.lineTo(screenX, screenY) }
                    }
                }
                
                if (!first && hasVisiblePoints) {
                    path.close()
                    if (isSelected) drawPath(path, Color.Yellow.copy(alpha = strobeAlpha), style = Fill)
                    drawPath(path, if (isSelected) Color.Yellow else Color.Green, style = Stroke(width = 1f))
                }
            }
            
            if (isSelected) {
                var pCenter = latLngToCartesian(country.center.lat, country.center.lng, currentRadius.toDouble())
                pCenter = rotateX(pCenter, cosX, sinX)
                pCenter = rotateY(pCenter, cosY, sinY)
                if (pCenter.z > 0) {
                    val markerX = canvasCenter.x + pCenter.x.toFloat()
                    val markerY = canvasCenter.y - pCenter.y.toFloat()
                    drawCircle(Color.Yellow.copy(alpha = 0.5f), radius = markerRadiusPulse * 2f, center = Offset(markerX, markerY), style = Stroke(width = 2f))
                    drawCircle(Color.Yellow, radius = 4f, center = Offset(markerX, markerY), style = Fill)
                }
            }
        }
        drawCircle(color = Color.Black.copy(alpha = 0.3f), radius = currentRadius, center = canvasCenter, style = Stroke(width = 15f))
    }
}
