package com.dirzaaulia.countries.domain.globe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
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
    private val _isDragging = mutableStateOf(false)
    private val _isFlinging = mutableStateOf(false)

    val rotationX: Float get() = _rotationX.value
    val rotationY: Float get() = _rotationY.value
    val zoom: Float get() = _zoom.value
    val isAnimating: Boolean get() = _rotationX.isRunning || _rotationY.isRunning || _zoom.isRunning || _isFlinging.value
    val isDragging: Boolean get() = _isDragging.value
    val isFlinging: Boolean get() = _isFlinging.value

    fun setDragging(dragging: Boolean) {
        _isDragging.value = dragging
        if (dragging) _isFlinging.value = false
    }

    suspend fun snapTo(
        x: Float,
        y: Float,
        targetZoom: Float? = null,
    ) {
        _rotationX.snapTo(x)
        _rotationY.snapTo(y)
        if (targetZoom != null) {
            _zoom.snapTo(targetZoom.coerceIn(0.6f, 4.5f))
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
            val currentY = _rotationY.value
            val diffY = ((targetLng - currentY + 180f) % 360f + 360f) % 360f - 180f
            val finalTargetLng = currentY + diffY

            val currentX = _rotationX.value
            val diffX = ((targetLat - currentX + 180f) % 360f + 360f) % 360f - 180f
            val finalTargetLat = currentX + diffX

            launch {
                _zoom.animateTo(targetZoom.coerceIn(0.6f, 4.5f), animationSpec = tween(durationMs))
            }
            launch {
                _rotationX.animateTo(finalTargetLat, animationSpec = tween(durationMs))
            }
            launch {
                _rotationY.animateTo(finalTargetLng, animationSpec = tween(durationMs))
            }
        }
    }

    suspend fun fling(
        velocityX: Float,
        velocityY: Float,
        damping: Float = 0.92f,
    ) {
        if (kotlin.math.abs(velocityX) < 5f && kotlin.math.abs(velocityY) < 5f) return
        _isFlinging.value = true
        var vx = velocityX
        var vy = velocityY
        val dt = 16f / 1000f
        try {
            while (kotlin.math.abs(vx) > 0.5f || kotlin.math.abs(vy) > 0.5f) {
                if (_isDragging.value) break
                val newY = (_rotationY.value + vx * dt) % 360f
                val newX = (_rotationX.value + vy * dt).coerceIn(-89f, 89f)
                _rotationX.snapTo(newX)
                _rotationY.snapTo(newY)
                vx *= damping
                vy *= damping
                kotlinx.coroutines.delay(16)
            }
        } finally {
            _isFlinging.value = false
        }
    }

    suspend fun stopAnimations() {
        _isFlinging.value = false
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
