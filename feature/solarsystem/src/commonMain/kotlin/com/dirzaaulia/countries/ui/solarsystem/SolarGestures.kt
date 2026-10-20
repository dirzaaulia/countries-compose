package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow

suspend fun PointerInputScope.solarSystemGestures(
    onPan: (Offset) -> Unit,
    onOrbit: (Offset) -> Unit,
    onZoom: (Float) -> Unit,
    onTap: (Offset) -> Unit,
    onDoubleTap: (Offset) -> Unit,
) {
    coroutineScope {
        // 1. NASA Eyes style middle-wheel mouse scroll listener (active continuously)
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
                            // Negative delta (roll up/forward) zooms in; positive delta zooms out
                            val factor = 1.15f.pow(-delta.coerceIn(-3f, 3f))
                            onZoom(factor)
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
            }
        }

        // 2. Drag gestures: Left-drag to Orbit, Right/Middle-drag to Pan, 2-finger Pan/Zoom
        launch {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                do {
                    val event = awaitPointerEvent()
                    val pressedChanges = event.changes.filter { it.pressed }
                    val fingers = pressedChanges.size

                    if (fingers == 1) {
                        val pan = event.calculatePan()
                        if (pan != Offset.Zero) {
                            val buttons = event.buttons
                            if (buttons.isSecondaryPressed || buttons.isTertiaryPressed) {
                                onPan(pan)
                            } else {
                                onOrbit(pan)
                            }
                            pressedChanges.first().consume()
                        }
                    } else if (fingers >= 2) {
                        val zoom = event.calculateZoom()
                        val pan = event.calculatePan()
                        if (zoom != 1.0f) onZoom(zoom)
                        if (pan != Offset.Zero) onPan(pan)
                        pressedChanges.forEach { it.consume() }
                    }
                } while (pressedChanges.isNotEmpty())
            }
        }

        // 3. Planet selection & dive taps
        launch {
            detectTapGestures(
                onDoubleTap = onDoubleTap,
                onTap = onTap,
            )
        }
    }
}
