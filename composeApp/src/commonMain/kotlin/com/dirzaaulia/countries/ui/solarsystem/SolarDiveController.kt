package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import com.dirzaaulia.countries.domain.solarsystem.PlanetId

enum class FlightState {
    IDLE,
    IN_FLIGHT,
    APPROACHING_ORBIT,
    TOUCHDOWN_TRANSITION,
}

class SolarDiveController {
    val diveProgress = Animatable(0f)
    var isDiving = false
        private set
    var targetPlanet: PlanetId? = null
        private set
    var sourcePlanet: PlanetId? = null
        private set
    var flightState = FlightState.IDLE
        private set

    suspend fun executeDive(
        fromPlanet: PlanetId?,
        toPlanet: PlanetId,
        onStartHandoff: (PlanetId) -> Unit = {},
        onComplete: (PlanetId) -> Unit,
    ) {
        if (isDiving) return
        isDiving = true
        sourcePlanet = fromPlanet
        targetPlanet = toPlanet
        flightState = FlightState.IN_FLIGHT
        var handoffTriggered = false

        try {
            // Single continuous 1800ms smooth camera dive trajectory
            diveProgress.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 1800, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)),
            ) {
                if (value >= 0.45f && !handoffTriggered) {
                    handoffTriggered = true
                    flightState = FlightState.APPROACHING_ORBIT
                    onStartHandoff(toPlanet)
                }
            }
            flightState = FlightState.TOUCHDOWN_TRANSITION
            onComplete(toPlanet)
        } finally {
            isDiving = false
            flightState = FlightState.IDLE
            targetPlanet = null
            sourcePlanet = null
        }
    }

    suspend fun reset() {
        diveProgress.snapTo(0f)
        flightState = FlightState.IDLE
    }
}
