package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import com.dirzaaulia.countries.domain.globe.GlobeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow

suspend fun PointerInputScope.handlePlanetMouseAndTouchGestures(
    state: GlobeState,
    scope: CoroutineScope,
) {
    coroutineScope {
        // 1. Mouse Scroll Wheel Zoom Listener
        launch {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Scroll) {
                        val delta =
                            event.changes
                                .firstOrNull()
                                ?.scrollDelta
                                ?.y ?: 0f
                        if (delta != 0f) {
                            val factor = 1.15f.pow(-delta.coerceIn(-3f, 3f))
                            val newZoom = (state.zoom * factor).coerceIn(0.6f, 4.5f)
                            scope.launch { state.snapZoom(newZoom) }
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
            }
        }

        // 2. Drag & Pinch Gestures (Left-drag to Orbit, 2-finger Pinch to Zoom)
        launch {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                state.setDragging(true)
                try {
                    do {
                        val event = awaitPointerEvent()
                        val pressedChanges = event.changes.filter { it.pressed }
                        val fingers = pressedChanges.size

                        if (fingers == 1) {
                            val pan = event.calculatePan()
                            if (pan != Offset.Zero) {
                                val pitchSens = 0.35f / state.zoom
                                val yawSens = 0.45f / state.zoom
                                val newX = (state.rotationX - pan.y * pitchSens).coerceIn(-89f, 89f)
                                val newY = (state.rotationY - pan.x * yawSens) % 360f
                                scope.launch {
                                    state.stopAnimations()
                                    state.snapTo(newX, newY, state.zoom)
                                }
                                pressedChanges.first().consume()
                            }
                        } else if (fingers >= 2) {
                            val zoom = event.calculateZoom()
                            val pan = event.calculatePan()
                            if (zoom != 1.0f) {
                                val newZoom = (state.zoom * zoom).coerceIn(0.6f, 4.5f)
                                scope.launch { state.snapZoom(newZoom) }
                            }
                            if (pan != Offset.Zero) {
                                val pitchSens = 0.35f / state.zoom
                                val yawSens = 0.45f / state.zoom
                                val newX = (state.rotationX - pan.y * pitchSens).coerceIn(-89f, 89f)
                                val newY = (state.rotationY - pan.x * yawSens) % 360f
                                scope.launch { state.snapTo(newX, newY, state.zoom) }
                            }
                            pressedChanges.forEach { it.consume() }
                        }
                    } while (pressedChanges.isNotEmpty())
                } finally {
                    state.setDragging(false)
                }
            }
        }
    }
}

fun Modifier.handlePlanetKeyboardNavigation(
    state: GlobeState,
    scope: CoroutineScope,
    onResetCamera: () -> Unit = { scope.launch { state.snapTo(0f, 0f, 1f) } },
): Modifier =
    this.onKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
        val pitchSens = 4.0f / state.zoom
        val yawSens = 5.0f / state.zoom
        when (event.key) {
            Key.DirectionUp, Key.W -> {
                val newX = (state.rotationX + pitchSens).coerceIn(-89f, 89f)
                scope.launch { state.snapTo(newX, state.rotationY, state.zoom) }
                true
            }
            Key.DirectionDown, Key.S -> {
                val newX = (state.rotationX - pitchSens).coerceIn(-89f, 89f)
                scope.launch { state.snapTo(newX, state.rotationY, state.zoom) }
                true
            }
            Key.DirectionLeft, Key.A -> {
                val newY = (state.rotationY + yawSens) % 360f
                scope.launch { state.snapTo(state.rotationX, newY, state.zoom) }
                true
            }
            Key.DirectionRight, Key.D -> {
                val newY = (state.rotationY - yawSens) % 360f
                scope.launch { state.snapTo(state.rotationX, newY, state.zoom) }
                true
            }
            Key.Equals, Key.NumPadAdd -> {
                scope.launch { state.zoomIn() }
                true
            }
            Key.Minus, Key.NumPadSubtract -> {
                scope.launch { state.zoomOut() }
                true
            }
            Key.R -> {
                onResetCamera()
                true
            }
            else -> false
        }
    }
