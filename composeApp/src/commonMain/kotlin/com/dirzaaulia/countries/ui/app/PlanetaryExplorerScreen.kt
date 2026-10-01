package com.dirzaaulia.countries.ui.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.ui.mars.MarsView
import com.dirzaaulia.countries.ui.moon.MoonView
import com.dirzaaulia.countries.ui.solarsystem.GenericPlanetView
import com.dirzaaulia.countries.ui.solarsystem.SolarSystemView
import kotlinx.coroutines.launch

@Composable
fun PlanetaryExplorerScreen(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    astronomy: ExplorerAstronomy,
    camera: GlobeState,
    moonCamera: GlobeState,
    marsCamera: GlobeState,
    activePage: Int,
    actions: ExplorerActions,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(Color(0xFF03060C))) {
        AnimatedContent(
            targetState = activePage,
            transitionSpec = {
                if (initialState == 0 && targetState > 0) {
                    (fadeIn(tween(800, easing = LinearEasing)) + scaleIn(initialScale = 0.95f, animationSpec = tween(800))) togetherWith
                        (fadeOut(tween(800, easing = LinearEasing)) + scaleOut(targetScale = 1.12f, animationSpec = tween(800)))
                } else if (initialState > 0 && targetState == 0) {
                    (fadeIn(tween(600, easing = LinearEasing)) + scaleIn(initialScale = 1.08f, animationSpec = tween(600))) togetherWith
                        (fadeOut(tween(600, easing = LinearEasing)) + scaleOut(targetScale = 0.95f, animationSpec = tween(600)))
                } else {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                }
            },
            label = "spaceContinuumTransition",
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 ->
                    SolarSystemView(
                        currentTimeMillis = controls.currentTimeMillis,
                        onDiveToPlanet = { planetId, pitch, yaw ->
                            val targetPage =
                                when (planetId) {
                                    PlanetId.EARTH -> {
                                        actions.scope.launch { camera.snapTo(pitch, yaw, camera.zoom) }
                                        1
                                    }
                                    PlanetId.MOON -> {
                                        actions.scope.launch { moonCamera.snapTo(pitch, yaw, moonCamera.zoom) }
                                        2
                                    }
                                    PlanetId.MARS -> {
                                        actions.scope.launch { marsCamera.snapTo(pitch, yaw, marsCamera.zoom) }
                                        3
                                    }
                                    PlanetId.MERCURY -> {
                                        actions.scope.launch { camera.snapTo(pitch, yaw, camera.zoom) }
                                        4
                                    }
                                    PlanetId.VENUS -> {
                                        actions.scope.launch { camera.snapTo(pitch, yaw, camera.zoom) }
                                        5
                                    }
                                    PlanetId.JUPITER -> {
                                        actions.scope.launch { camera.snapTo(pitch, yaw, camera.zoom) }
                                        6
                                    }
                                    PlanetId.SATURN -> {
                                        actions.scope.launch { camera.snapTo(pitch, yaw, camera.zoom) }
                                        7
                                    }
                                    PlanetId.URANUS -> {
                                        actions.scope.launch { camera.snapTo(pitch, yaw, camera.zoom) }
                                        8
                                    }
                                    PlanetId.NEPTUNE -> {
                                        actions.scope.launch { camera.snapTo(pitch, yaw, camera.zoom) }
                                        9
                                    }
                                    else -> 1
                                }
                            actions.selectPage(targetPage)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )

                1 -> ExplorerGlobe(features, controls, astronomy, camera, page, actions)

                2 ->
                    MoonView(
                        moonInfo = astronomy.moon,
                        modifier = Modifier.fillMaxSize(),
                        onBackToSolarSystem = { actions.selectPage(0) },
                        eclipseFeed = features.globe.eclipseFeed,
                        isEclipseFeedLoading = features.globe.isEclipseFeedLoading,
                        isPageActive = true,
                        state = moonCamera,
                    )

                3 ->
                    MarsView(
                        state = marsCamera,
                        modifier = Modifier.fillMaxSize(),
                        onBackToSolarSystem = { actions.selectPage(0) },
                        sunPosition = astronomy.sun,
                        isPageActive = true,
                    )

                4 -> GenericPlanetView(PlanetId.MERCURY, camera, { actions.selectPage(0) })
                5 -> GenericPlanetView(PlanetId.VENUS, camera, { actions.selectPage(0) })
                6 -> GenericPlanetView(PlanetId.JUPITER, camera, { actions.selectPage(0) })
                7 -> GenericPlanetView(PlanetId.SATURN, camera, { actions.selectPage(0) })
                8 -> GenericPlanetView(PlanetId.URANUS, camera, { actions.selectPage(0) })
                else -> GenericPlanetView(PlanetId.NEPTUNE, camera, { actions.selectPage(0) })
            }
        }

        FeatureHudHost(features, controls, astronomy, activePage, actions)

        ExplorerSheets(features, controls, activePage, actions)
    }
}
