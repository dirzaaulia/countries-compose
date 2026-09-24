package com.dirzaaulia.countries

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.dirzaaulia.countries.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun Globe3DPlatformView(
    state: GlobeState,
    isPageActive: Boolean,
    modifier: Modifier
) {
    var renderer by remember { mutableStateOf<EarthGLRenderer?>(null) }
    var surfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom) {
        renderer?.updateCamera(state.rotationX, state.rotationY, state.zoom)
        surfaceView?.requestRender()
    }

    // Pause/resume the GL thread when this page is not visible
    LaunchedEffect(isPageActive) {
        val sv = surfaceView ?: return@LaunchedEffect
        if (isPageActive) {
            sv.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            sv.onResume()
        } else {
            sv.onPause()
        }
    }

    LaunchedEffect(renderer) {
        val r = renderer ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val dayBytes = Res.readBytes("files/earth_day.jpg")
            val nightBytes = Res.readBytes("files/earth_night.jpg")
            val cloudBytes = Res.readBytes("files/earth_clouds.jpg")
            r.setIsMoon(false)
            r.setTextures(dayBytes, nightBytes, cloudBytes)
        }
    }

    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
                setZOrderMediaOverlay(true)
                val earthRenderer = EarthGLRenderer(ctx).apply { setIsMoon(false) }
                setRenderer(earthRenderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                renderer = earthRenderer
                surfaceView = this
            }
        },
        modifier = modifier
    )
}

@Composable
actual fun Moon3DPlatformView(
    state: GlobeState,
    phaseAngle: Double,
    isPageActive: Boolean,
    modifier: Modifier
) {
    var renderer by remember { mutableStateOf<EarthGLRenderer?>(null) }
    var surfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom) {
        renderer?.updateCamera(state.rotationX, state.rotationY, state.zoom)
        surfaceView?.requestRender()
    }

    LaunchedEffect(phaseAngle, renderer) {
        renderer?.setMoonPhaseAngle(phaseAngle)
    }

    // Pause/resume the GL thread when this page is not visible
    LaunchedEffect(isPageActive) {
        val sv = surfaceView ?: return@LaunchedEffect
        if (isPageActive) {
            sv.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            sv.onResume()
        } else {
            sv.onPause()
        }
    }

    LaunchedEffect(renderer) {
        val r = renderer ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val moonBytes = Res.readBytes("files/moon.jpg")
            r.setIsMoon(true)
            r.setTextures(moonBytes)
        }
    }

    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
                setZOrderMediaOverlay(true)
                val moonRenderer = EarthGLRenderer(ctx).apply {
                    setIsMoon(true)
                    setMoonPhaseAngle(phaseAngle)
                }
                setRenderer(moonRenderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                renderer = moonRenderer
                surfaceView = this
            }
        },
        modifier = modifier
    )
}
