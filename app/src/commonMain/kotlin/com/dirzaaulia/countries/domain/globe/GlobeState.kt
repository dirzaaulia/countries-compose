package com.dirzaaulia.countries.domain.globe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Stable
class GlobeState(
    initialRotationX: Float = 0f,
    initialRotationY: Float = 0f,
    initialZoom: Float = 1.0f,
) {
    constructor(initialRotationX: Double, initialRotationY: Double, initialZoom: Double) : this(
        initialRotationX.toFloat(),
        initialRotationY.toFloat(),
        initialZoom.toFloat(),
    )

    private val _rotationX = Animatable(initialRotationX)
    private val _rotationY = Animatable(initialRotationY)
    private val _zoom = Animatable(initialZoom)

    val rotationX: Float get() = _rotationX.value
    val rotationY: Float get() = _rotationY.value
    val zoom: Float get() = _zoom.value
    val isAnimating: Boolean get() = _rotationX.isRunning || _rotationY.isRunning || _zoom.isRunning

    suspend fun snapTo(
        x: Float,
        y: Float,
        targetZoom: Float? = null,
    ) {
        coroutineScope {
            launch { _rotationX.snapTo(x.coerceIn(-90f, 90f)) }
            launch { _rotationY.snapTo(y) }
            if (targetZoom != null) {
                launch { _zoom.snapTo(targetZoom.coerceIn(0.6f, 4.5f)) }
            }
        }
    }

    suspend fun snapZoom(targetZoom: Float) {
        coroutineScope {
            launch { _zoom.snapTo(targetZoom.coerceIn(0.6f, 4.5f)) }
        }
    }

    suspend fun animateZoom(
        targetZoom: Float,
        durationMs: Int = 300,
    ) {
        coroutineScope {
            launch {
                _zoom.animateTo(
                    targetZoom.coerceIn(0.6f, 4.5f),
                    animationSpec = tween(durationMs),
                )
            }
        }
    }

    suspend fun zoomIn() {
        animateZoom((_zoom.value * 1.35f).coerceAtMost(4.5f))
    }

    suspend fun zoomOut() {
        animateZoom((_zoom.value / 1.35f).coerceAtLeast(0.6f))
    }

    suspend fun resetZoom() {
        animateZoom(1.0f)
    }

    suspend fun flyTo(
        targetLat: Float,
        targetLng: Float,
        targetZoom: Float,
        durationMs: Int = 1200,
    ) {
        coroutineScope {
            // Shortest path for Yaw
            val currentY = _rotationY.value
            val diffY = ((targetLng - currentY + 180f) % 360f + 360f) % 360f - 180f
            val finalTargetLng = currentY + diffY

            launch {
                _zoom.animateTo(targetZoom.coerceIn(0.6f, 4.5f), animationSpec = tween(durationMs))
            }
            launch {
                _rotationX.animateTo(targetLat.coerceIn(-90f, 90f), animationSpec = tween(durationMs))
            }
            launch {
                _rotationY.animateTo(finalTargetLng, animationSpec = tween(durationMs))
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
fun rememberGlobeState(
    initialRotationX: Float = 0f,
    initialRotationY: Float = 0f,
    initialZoom: Float = 1.0f,
): GlobeState = remember { GlobeState(initialRotationX, initialRotationY, initialZoom) }

@Composable
fun rememberGlobeState(
    initialRotationX: Double,
    initialRotationY: Double,
    initialZoom: Double,
): GlobeState =
    remember {
        GlobeState(
            initialRotationX.toFloat(),
            initialRotationY.toFloat(),
            initialZoom.toFloat(),
        )
    }
