package com.dirzaaulia.countries

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific 3D hardware-accelerated Earth renderer.
 * On Android: Uses OpenGL ES 2.0 (GLSurfaceView).
 * On WASM/Web: Uses WebGL 2.0/1.0 canvas.
 */
@Composable
expect fun Globe3DPlatformView(
    state: GlobeState,
    modifier: Modifier = Modifier
)
