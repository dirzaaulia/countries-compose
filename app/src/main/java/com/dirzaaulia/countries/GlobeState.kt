package com.dirzaaulia.countries

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Stable
class GlobeState(
    initialRotationX: Float = 0f,
    initialRotationY: Float = 0f,
    initialZoom: Float = 1.0f
) {
    private val _rotationX = Animatable(initialRotationX)
    private val _rotationY = Animatable(initialRotationY)
    private val _zoom = Animatable(initialZoom)

    val rotationX: Float get() = _rotationX.value
    val rotationY: Float get() = _rotationY.value
    val zoom: Float get() = _zoom.value

    suspend fun snapTo(x: Float, y: Float) {
        coroutineScope {
            launch { _rotationX.snapTo(x.coerceIn(-90f, 90f)) }
            launch { _rotationY.snapTo(y) }
        }
    }

    suspend fun flyTo(targetLat: Float, targetLng: Float, targetZoom: Float) {
        coroutineScope {
            // Shortest path for Yaw
            val currentY = _rotationY.value
            val diffY = ((targetLng - currentY + 180f) % 360f + 360f) % 360f - 180f
            val finalTargetLng = currentY + diffY

            launch {
                _zoom.animateTo(targetZoom, animationSpec = tween(1500))
            }
            launch {
                _rotationX.animateTo(targetLat.coerceIn(-90f, 90f), animationSpec = tween(1200))
            }
            launch {
                _rotationY.animateTo(finalTargetLng, animationSpec = tween(1200))
            }
        }
    }

    suspend fun stopAnimations() {
        coroutineScope {
            launch { _rotationX.stop() }
            launch { _rotationY.stop() }
            launch { _zoom.stop() }
        }
    }
}

@Composable
fun rememberGlobeState(): GlobeState {
    return remember { GlobeState() }
}
