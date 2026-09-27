# ☀️ 2. Real-Time Astronomy & Earth Shaders

## 2.1 Live Solar Day/Night Terminator (Earth)
**Status: ✅ DONE**
- `AstronomyMath.calculateSunPosition()` derives subsolar point from UTC.
- Sun direction vector fed into `EarthGLRenderer` GLSL fragment shader.
- `smoothstep` twilight band + golden-amber sunrise/sunset color.

## 2.2 Dynamic Day/Night Adaptive Border Colors
**Status: ✅ DONE**
- Black borders on lit hemisphere, white/luminous on dark hemisphere.
- Per-segment cross-terminator interpolation in `GlobeView.kt`.
- Unified border rings — no hard cuts at terminator.

## 2.3 Atmospheric Glow
**Status: ⚠️ PARTIAL**
- Blue atmospheric limb glow in GLSL.
- Cloud layer rendered + blended.
- *Planned*: Full Nishita/Bruneton Rayleigh+Mie physically-based model.

## 2.4 Sun Position Refresh on Earth GL
**Status: ✅ DONE**
- `AstronomyMath.calculateSunPosition()` evaluated dynamically every frame in `EarthGLRenderer.onDrawFrame()`.
- Continuous subsolar vector updates reflect real-time planetary illumination with zero frame stalling.
