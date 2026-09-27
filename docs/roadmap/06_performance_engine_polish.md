# ⚡ 7. Performance & 3D Engine Polish

## 7.1 Earth ↔ Moon Transition Optimization
**Status: ✅ DONE**
- Texture pre-warming on launch (`moon.jpg` decoded off the main thread).
- `isPageActive` lifecycle hook pauses inactive `GLSurfaceView` (`onPause`/`onResume`).
- Zero-frame-drop smooth pager scrolling between Earth and Moon.

## 7.2 Earth Globe Polygon Culling & Render Optimization
**Status: ✅ DONE**
- Fast back-face country center culling (`if (cp.z < -0.45) return@forEach`), eliminating ~60% of country polygon loops per frame.
- Pre-allocated and remembered `daylightBordersPath` and `nightBordersPath`.
- Inlined segment rendering eliminating temporary `TransitionSegment` GC churn.

## 7.3 Modal Bottom Sheet Frame Drop Elimination
**Status: ✅ DONE**
- Converted `GLSurfaceView` to `RENDERMODE_WHEN_DIRTY`. The 3D sphere now only renders on camera motion, texture load, or phase angle updates, reducing stationary GPU usage to 0%.
- Added `isSheetOpen: Boolean` gating across `App.kt` and `GlobeView.kt`. Background Canvas transitions and OpenGL frames pause when sheets open.
- Set `preserveEGLContextOnPause = true` on `GLSurfaceView` and added recomposition `requestRender()` callback to eliminate globe blackout on sheet dismissal.

## 7.4 3D Country Centering & Zoom-Dependent Offset Fix
**Status: ✅ DONE**
- Reordered transformation pipeline to **yaw first ($R_Y$), then pitch second ($R_X$)**:
  1. $R_Y(-lng)$ aligns the country with the prime meridian.
  2. $R_X(lat)$ tilts the meridian to screen center.
  3. Result: Screen coordinates are exactly $(0.0, 0.0)$ for any country at any zoom level without latitude drift.

## 7.5 WASM 3D Planet Rendering — WebGL Parity
**Status: ✅ DONE**
- Embedded dedicated HTML `<canvas id="PlanetCanvas">` behind Compose Canvas.
- Native GLSL 1.0 compilation of vertex and fragment shaders.
- Procedural UV sphere generation via `SphereMesh.kt` uploaded to WebGL VBO/IBO buffers.
- Asynchronous JPEG decoding (`earth_day.jpg`, `earth_night.jpg`, `earth_clouds.jpg`, `moon.jpg`) via WebGL mipmapped texture samplers.
