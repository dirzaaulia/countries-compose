package com.dirzaaulia.countries.platform

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun Globe3DPlatformView(
    state: GlobeState,
    sunPosition: SunPosition,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    var renderer by remember { mutableStateOf<EarthGLRenderer?>(null) }
    var surfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom, sunPosition) {
        renderer?.updateCamera(state.rotationX, state.rotationY, state.zoom)
        renderer?.setSunPosition(sunPosition)
        surfaceView?.requestRender()
    }

    LaunchedEffect(isPageActive) {
        val sv = surfaceView ?: return@LaunchedEffect
        if (isPageActive) {
            sv.onResume()
            sv.requestRender()
        } else {
            sv.onPause()
        }
    }

    LaunchedEffect(renderer) {
        val r = renderer ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val dayBytes = runCatching { Res.readBytes("files/earth_day.jpg") }.getOrNull()
            val nightBytes = runCatching { Res.readBytes("files/earth_night.jpg") }.getOrNull()
            val cloudBytes = runCatching { Res.readBytes("files/earth_clouds.jpg") }.getOrNull()
            if (dayBytes != null) {
                r.setIsMoon(false)
                r.setTextures(dayBytes, nightBytes ?: dayBytes, cloudBytes ?: dayBytes)
                surfaceView?.requestRender()
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
                setZOrderMediaOverlay(true)
                preserveEGLContextOnPause = true
                val earthRenderer =
                    EarthGLRenderer(ctx).apply {
                        setIsMoon(false)
                        setSunPosition(sunPosition)
                    }
                setRenderer(earthRenderer)
                renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                renderer = earthRenderer
                surfaceView = this
            }
        },
        update = { sv ->
            sv.requestRender()
        },
        modifier = modifier,
    )
}

@Composable
actual fun Moon3DPlatformView(
    state: GlobeState,
    phaseAngle: Double,
    subsolarLatitude: Double,
    librationLatitude: Double,
    librationLongitude: Double,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    var renderer by remember { mutableStateOf<EarthGLRenderer?>(null) }
    var surfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom) {
        renderer?.updateCamera(state.rotationX, state.rotationY, state.zoom)
        surfaceView?.requestRender()
    }

    LaunchedEffect(phaseAngle, subsolarLatitude, librationLatitude, librationLongitude, renderer) {
        renderer?.setMoonOrientation(phaseAngle, subsolarLatitude, librationLatitude, librationLongitude)
        surfaceView?.requestRender()
    }

    LaunchedEffect(isPageActive) {
        val sv = surfaceView ?: return@LaunchedEffect
        if (isPageActive) {
            sv.onResume()
            sv.requestRender()
        } else {
            sv.onPause()
        }
    }

    LaunchedEffect(renderer) {
        val r = renderer ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val moonBytes = runCatching { Res.readBytes("files/moon.jpg") }.getOrNull()
            if (moonBytes != null) {
                r.setIsMoon(true)
                r.setTextures(moonBytes)
                surfaceView?.requestRender()
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
                setZOrderMediaOverlay(true)
                preserveEGLContextOnPause = true
                val moonRenderer =
                    EarthGLRenderer(ctx).apply {
                        setIsMoon(true)
                        setMoonOrientation(phaseAngle, subsolarLatitude, librationLatitude, librationLongitude)
                    }
                setRenderer(moonRenderer)
                renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                renderer = moonRenderer
                surfaceView = this
            }
        },
        update = { sv ->
            sv.requestRender()
        },
        modifier = modifier,
    )
}

@Composable
actual fun Mars3DPlatformView(
    state: GlobeState,
    sunPosition: SunPosition,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    var renderer by remember { mutableStateOf<EarthGLRenderer?>(null) }
    var surfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom, sunPosition) {
        renderer?.updateCamera(state.rotationX, state.rotationY, state.zoom)
        renderer?.setSunPosition(sunPosition)
        surfaceView?.requestRender()
    }

    LaunchedEffect(isPageActive) {
        val sv = surfaceView ?: return@LaunchedEffect
        if (isPageActive) {
            sv.onResume()
            sv.requestRender()
        } else {
            sv.onPause()
        }
    }

    LaunchedEffect(renderer) {
        val r = renderer ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val marsBytes =
                runCatching { Res.readBytes("files/mars_2k.jpg") }.getOrNull()
                    ?: runCatching { Res.readBytes("files/moon.jpg") }.getOrNull()
            if (marsBytes != null) {
                r.setIsMars(true)
                r.setTextures(marsBytes)
                surfaceView?.requestRender()
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
                setZOrderMediaOverlay(true)
                preserveEGLContextOnPause = true
                val marsRenderer =
                    EarthGLRenderer(ctx).apply {
                        setIsMars(true)
                        setSunPosition(sunPosition)
                    }
                setRenderer(marsRenderer)
                renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                renderer = marsRenderer
                surfaceView = this
            }
        },
        update = { sv ->
            sv.requestRender()
        },
        modifier = modifier,
    )
}

@Composable
actual fun Planet3DPlatformView(
    planetId: PlanetId,
    state: GlobeState,
    sunPosition: SunPosition,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    if (planetId == PlanetId.EARTH) {
        Globe3DPlatformView(state, sunPosition, isPageActive, modifier)
        return
    }
    if (planetId == PlanetId.MOON) {
        Moon3DPlatformView(state = state, isPageActive = isPageActive, modifier = modifier)
        return
    }
    if (planetId == PlanetId.MARS) {
        Mars3DPlatformView(state, sunPosition, isPageActive, modifier)
        return
    }

    val planetTypeFloat =
        remember(planetId) {
            when (planetId) {
                PlanetId.MERCURY -> 3f
                PlanetId.VENUS -> 4f
                PlanetId.JUPITER -> 5f
                PlanetId.SATURN -> 6f
                PlanetId.URANUS -> 7f
                PlanetId.NEPTUNE -> 8f
                else -> 1f
            }
        }

    var renderer by remember { mutableStateOf<EarthGLRenderer?>(null) }
    var surfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom, sunPosition) {
        renderer?.updateCamera(state.rotationX, state.rotationY, state.zoom)
        renderer?.setSunPosition(sunPosition)
        surfaceView?.requestRender()
    }

    LaunchedEffect(isPageActive) {
        val sv = surfaceView ?: return@LaunchedEffect
        if (isPageActive) {
            sv.onResume()
            sv.requestRender()
        } else {
            sv.onPause()
        }
    }

    LaunchedEffect(renderer, planetId) {
        val r = renderer ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val fileName =
                when (planetId) {
                    PlanetId.MERCURY -> "files/mercury.jpg"
                    PlanetId.VENUS -> "files/venus.jpg"
                    PlanetId.JUPITER -> "files/jupiter.jpg"
                    PlanetId.SATURN -> "files/saturn.jpg"
                    PlanetId.URANUS -> "files/uranus.jpg"
                    PlanetId.NEPTUNE -> "files/neptune.jpg"
                    else -> "files/moon.jpg"
                }
            val bytes =
                runCatching { Res.readBytes(fileName) }.getOrNull()
                    ?: runCatching { Res.readBytes("files/moon.jpg") }.getOrNull()
            if (bytes != null) {
                r.setPlanetType(planetTypeFloat)
                r.setTextures(bytes)
                surfaceView?.requestRender()
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
                setZOrderMediaOverlay(true)
                preserveEGLContextOnPause = true
                val planetTypeFloat =
                    when (planetId) {
                        PlanetId.EARTH -> 0f
                        PlanetId.MOON -> 1f
                        PlanetId.MARS -> 2f
                        PlanetId.MERCURY -> 3f
                        PlanetId.VENUS -> 4f
                        PlanetId.JUPITER -> 5f
                        PlanetId.SATURN -> 6f
                        PlanetId.URANUS -> 7f
                        PlanetId.NEPTUNE -> 8f
                        else -> 0f
                    }
                val planetRenderer =
                    EarthGLRenderer(ctx).apply {
                        setPlanetType(planetTypeFloat)
                        setSunPosition(sunPosition)
                    }
                setRenderer(planetRenderer)
                renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                renderer = planetRenderer
                surfaceView = this
            }
        },
        update = { sv -> sv.requestRender() },
        modifier = modifier,
    )
}
