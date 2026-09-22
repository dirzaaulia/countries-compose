package com.dirzaaulia.countries

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
    modifier: Modifier
) {
    var renderer by remember { mutableStateOf<EarthGLRenderer?>(null) }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom) {
        renderer?.updateCamera(state.rotationX, state.rotationY, state.zoom)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            val dayBytes = Res.readBytes("files/earth_day.jpg")
            val nightBytes = Res.readBytes("files/earth_night.jpg")
            val cloudBytes = Res.readBytes("files/earth_clouds.jpg")
            renderer?.setTextures(dayBytes, nightBytes, cloudBytes)
        }
    }

    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(2)
                val earthRenderer = EarthGLRenderer(ctx)
                setRenderer(earthRenderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                renderer = earthRenderer
            }
        },
        modifier = modifier
    )
}
