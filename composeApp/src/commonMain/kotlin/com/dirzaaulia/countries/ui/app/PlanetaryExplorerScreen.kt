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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallManager
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallState
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallStatus
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.ui.components.DynamicMissionConsentDialog
import com.dirzaaulia.countries.ui.components.DynamicMissionDownloadCard
import com.dirzaaulia.countries.ui.mars.MarsView
import com.dirzaaulia.countries.ui.moon.MoonView
import com.dirzaaulia.countries.ui.solarsystem.GenericPlanetView
import com.dirzaaulia.countries.ui.solarsystem.SolarSystemView
import kotlinx.coroutines.launch

data class PlanetSnapCoords(
    val planetId: PlanetId,
    val pitch: Float,
    val yaw: Float,
)

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
    installManager: FeatureInstallManager = remember { FeatureInstallManager() },
    modifier: Modifier = Modifier,
) {
    val installStates by installManager.installState.collectAsState()
    var pendingDiveTarget by remember { mutableStateOf<Pair<PlanetId, Int>?>(null) }
    var pendingSnapCoords by remember { mutableStateOf<PlanetSnapCoords?>(null) }
    var pendingFeatureId by remember { mutableStateOf<String?>(null) }
    var pendingMissionTitle by remember { mutableStateOf<String>("Feature Module") }
    var pendingOnSuccessAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showConsentDialog by remember { mutableStateOf(false) }

    val promptInstall: (String, String, () -> Unit) -> Unit = { fid, title, onSuccess ->
        pendingFeatureId = fid
        pendingMissionTitle = title
        pendingOnSuccessAction = onSuccess
        showConsentDialog = true
    }

    com.dirzaaulia.countries.platform.PlatformBackHandler(enabled = activePage > 0) {
        actions.selectPage(0)
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (!com.dirzaaulia.countries.platform
                .isFirstLaunchOnboardingCompleted()
        ) {
            actions.setOverlay(ExplorerOverlay.ONBOARDING)
        }
    }

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
                        isFeatureInstalled = { planetId ->
                            val fid =
                                when (planetId) {
                                    PlanetId.MARS -> "mars"
                                    PlanetId.MOON -> "moon"
                                    PlanetId.MERCURY, PlanetId.VENUS, PlanetId.JUPITER, PlanetId.SATURN, PlanetId.URANUS, PlanetId.NEPTUNE -> "deepspace"
                                    else -> null
                                }
                            fid == null || installManager.isFeatureInstalled(fid)
                        },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                        onDiveToPlanet = { planetId, pitch, yaw ->
                            val targetPage =
                                when (planetId) {
                                    PlanetId.EARTH -> 1
                                    PlanetId.MOON -> 2
                                    PlanetId.MARS -> 3
                                    PlanetId.MERCURY -> 4
                                    PlanetId.VENUS -> 5
                                    PlanetId.JUPITER -> 6
                                    PlanetId.SATURN -> 7
                                    PlanetId.URANUS -> 8
                                    PlanetId.NEPTUNE -> 9
                                    else -> 1
                                }

                            val featureId =
                                when (planetId) {
                                    PlanetId.MARS -> "mars"
                                    PlanetId.MOON -> "moon"
                                    PlanetId.MERCURY, PlanetId.VENUS, PlanetId.JUPITER, PlanetId.SATURN, PlanetId.URANUS, PlanetId.NEPTUNE -> "deepspace"
                                    else -> null
                                }

                            val title =
                                when (planetId) {
                                    PlanetId.MARS -> "Mars Expedition"
                                    PlanetId.MOON -> "Moon Explorer"
                                    else -> "Deep Space Telemetry"
                                }

                            if (featureId != null && !installManager.isFeatureInstalled(featureId)) {
                                pendingDiveTarget = planetId to targetPage
                                pendingSnapCoords = PlanetSnapCoords(planetId, pitch, yaw)
                                promptInstall(featureId, title) {}
                            } else {
                                actions.scope.launch {
                                    when (planetId) {
                                        PlanetId.MOON -> moonCamera.snapTo(pitch, yaw, moonCamera.zoom)
                                        PlanetId.MARS -> marsCamera.snapTo(pitch, yaw, marsCamera.zoom)
                                        else -> camera.snapTo(pitch, yaw, camera.zoom)
                                    }
                                }
                                actions.selectPage(targetPage)
                            }
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
                        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
                        onToggleTimeMachine = { actions.toggleTimeMachine() },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                        isPageActive = true,
                        state = moonCamera,
                    )

                3 ->
                    MarsView(
                        state = marsCamera,
                        modifier = Modifier.fillMaxSize(),
                        onBackToSolarSystem = { actions.selectPage(0) },
                        sunPosition = astronomy.sun,
                        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
                        onToggleTimeMachine = { actions.toggleTimeMachine() },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                        isPageActive = true,
                    )

                4 ->
                    GenericPlanetView(
                        PlanetId.MERCURY,
                        camera,
                        { actions.selectPage(0) },
                        sunPosition =
                            com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                                .calculatePlanetSunPosition(PlanetId.MERCURY, controls.currentTimeMillis),
                        epochMillis = controls.currentTimeMillis,
                        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
                        onToggleTimeMachine = { actions.toggleTimeMachine() },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                    )

                5 ->
                    GenericPlanetView(
                        PlanetId.VENUS,
                        camera,
                        { actions.selectPage(0) },
                        sunPosition =
                            com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                                .calculatePlanetSunPosition(PlanetId.VENUS, controls.currentTimeMillis),
                        epochMillis = controls.currentTimeMillis,
                        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
                        onToggleTimeMachine = { actions.toggleTimeMachine() },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                    )

                6 ->
                    GenericPlanetView(
                        PlanetId.JUPITER,
                        camera,
                        { actions.selectPage(0) },
                        sunPosition =
                            com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                                .calculatePlanetSunPosition(PlanetId.JUPITER, controls.currentTimeMillis),
                        epochMillis = controls.currentTimeMillis,
                        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
                        onToggleTimeMachine = { actions.toggleTimeMachine() },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                    )

                7 ->
                    GenericPlanetView(
                        PlanetId.SATURN,
                        camera,
                        { actions.selectPage(0) },
                        sunPosition =
                            com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                                .calculatePlanetSunPosition(PlanetId.SATURN, controls.currentTimeMillis),
                        epochMillis = controls.currentTimeMillis,
                        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
                        onToggleTimeMachine = { actions.toggleTimeMachine() },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                    )

                8 ->
                    GenericPlanetView(
                        PlanetId.URANUS,
                        camera,
                        { actions.selectPage(0) },
                        sunPosition =
                            com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                                .calculatePlanetSunPosition(PlanetId.URANUS, controls.currentTimeMillis),
                        epochMillis = controls.currentTimeMillis,
                        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
                        onToggleTimeMachine = { actions.toggleTimeMachine() },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                    )

                else ->
                    GenericPlanetView(
                        PlanetId.NEPTUNE,
                        camera,
                        { actions.selectPage(0) },
                        sunPosition =
                            com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                                .calculatePlanetSunPosition(PlanetId.NEPTUNE, controls.currentTimeMillis),
                        epochMillis = controls.currentTimeMillis,
                        showTimeMachine = controls.overlay == ExplorerOverlay.TIME_MACHINE,
                        onToggleTimeMachine = { actions.toggleTimeMachine() },
                        showControlsCard = controls.showControlsGuide,
                        onToggleControlsCard = actions::toggleControlsGuide,
                        onOpenSettings = { actions.setOverlay(ExplorerOverlay.SETTINGS) },
                    )
            }
        }

        FeatureHudHost(features, controls, astronomy, activePage, actions)

        ExplorerSheets(features, controls, activePage, actions, installManager)

        if (showConsentDialog && pendingFeatureId != null) {
            DynamicMissionConsentDialog(
                missionTitle = pendingMissionTitle,
                onConfirmDownload = {
                    showConsentDialog = false
                    val fid = pendingFeatureId.toString()
                    val onSuccess = pendingOnSuccessAction
                    actions.scope.launch {
                        val success = installManager.requestFeatureInstall(fid)
                        if (success) {
                            pendingSnapCoords?.let { coords: PlanetSnapCoords ->
                                when (coords.planetId) {
                                    PlanetId.MOON -> moonCamera.snapTo(coords.pitch, coords.yaw, moonCamera.zoom)
                                    PlanetId.MARS -> marsCamera.snapTo(coords.pitch, coords.yaw, marsCamera.zoom)
                                    else -> camera.snapTo(coords.pitch, coords.yaw, camera.zoom)
                                }
                            }
                            pendingDiveTarget?.let { target: Pair<PlanetId, Int> -> actions.selectPage(target.second) }
                            onSuccess?.invoke()
                            pendingFeatureId = null
                            pendingDiveTarget = null
                            pendingSnapCoords = null
                            pendingOnSuccessAction = null
                        }
                    }
                },
                onDismiss = {
                    showConsentDialog = false
                    pendingFeatureId = null
                    pendingDiveTarget = null
                    pendingSnapCoords = null
                    pendingOnSuccessAction = null
                },
            )
        } else if (pendingFeatureId != null) {
            val fid = pendingFeatureId.toString()
            val featureState = installStates[fid] ?: FeatureInstallState(featureId = fid, status = FeatureInstallStatus.DOWNLOADING)
            DynamicMissionDownloadCard(
                missionTitle = pendingMissionTitle.uppercase(),
                installState = featureState,
                onRetry = {
                    actions.scope.launch {
                        val success = installManager.requestFeatureInstall(fid)
                        if (success) {
                            pendingSnapCoords?.let { coords: PlanetSnapCoords ->
                                when (coords.planetId) {
                                    PlanetId.MOON -> moonCamera.snapTo(coords.pitch, coords.yaw, moonCamera.zoom)
                                    PlanetId.MARS -> marsCamera.snapTo(coords.pitch, coords.yaw, marsCamera.zoom)
                                    else -> camera.snapTo(coords.pitch, coords.yaw, camera.zoom)
                                }
                            }
                            pendingDiveTarget?.let { target: Pair<PlanetId, Int> -> actions.selectPage(target.second) }
                            pendingOnSuccessAction?.invoke()
                            pendingFeatureId = null
                            pendingDiveTarget = null
                            pendingSnapCoords = null
                            pendingOnSuccessAction = null
                        }
                    }
                },
                onClose = {
                    pendingFeatureId = null
                    pendingDiveTarget = null
                    pendingSnapCoords = null
                    pendingOnSuccessAction = null
                },
            )
        }
    }
}
