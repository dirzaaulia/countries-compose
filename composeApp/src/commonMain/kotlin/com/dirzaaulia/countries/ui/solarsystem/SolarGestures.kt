package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal suspend fun PointerInputScope.solarSystemGestures(
    onPan: (Offset) -> Unit,
    onOrbit: (Offset) -> Unit,
    onZoom: (Float) -> Unit,
    onTap: (Offset) -> Unit,
    onDoubleTap: (Offset) -> Unit,
) {
    coroutineScope {
        launch {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                var hasPressed: Boolean
                do {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Scroll) {
                        val delta =
                            event.changes
                                .firstOrNull()
                                ?.scrollDelta
                                ?.y ?: 0f
                        if (delta != 0f) {
                            val zoom = if (delta > 0) 0.9f else 1.1f
                            onZoom(zoom)
                            event.changes.forEach { it.consume() }
                        }
                        hasPressed = event.changes.any { it.pressed }
                        continue
                    }
                    val pressedChanges = event.changes.filter { it.pressed }
                    hasPressed = pressedChanges.isNotEmpty()
                    val fingers = pressedChanges.size

                    if (fingers == 1) {
                        val pan = event.calculatePan()
                        if (pan != Offset.Zero) {
                            onOrbit(pan)
                            pressedChanges.first().consume()
                        }
                    } else if (fingers >= 2) {
                        val zoom = event.calculateZoom()
                        val pan = event.calculatePan()
                        if (zoom != 1.0f) onZoom(zoom)
                        if (pan != Offset.Zero) onPan(pan)
                        pressedChanges.forEach { it.consume() }
                    }
                } while (hasPressed)
            }
        }
        launch {
            detectTapGestures(
                onDoubleTap = onDoubleTap,
                onTap = onTap,
            )
        }
    }
}
