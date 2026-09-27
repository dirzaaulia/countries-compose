# 🌌 1. Deep Space Environment & Celestial Bodies

## 1.1 Procedural Starfield & Celestial Environment
**Status: ✅ DONE**
- 260+ deterministic stars on Canvas with twinkling animation (`GlobeView.kt` + `MoonView.kt`).
- Multi-color star palette: white, blue (`#90CAF9`), yellow (`#FFE082`), warm (`#FFCCBC`).
- Magnitude-1 stars have soft glow halo.
- Stars masked outside planet disc radius.
- **Milky Way Galactic Dust Lane**: Soft luminous cosmic dust lane gradient band rendered behind globe disc.

## 1.2 Full-Screen 3D Moon Explorer (HorizontalPager Page 1)
**Status: ✅ DONE**
- Dedicated `MoonView.kt` — HorizontalPager page 1.
- NASA LRO equirectangular texture (2048×1024, 2:1 ratio) — no black patches.
- `EarthGLRenderer` reused with `isMoonMode = true`.
- Phase terminator via elongation $\to$ sun direction vector (`AstronomyMath.calculateMoonInfo()`).
- Apollo 11–17 landing site beacons with tap-to-inspect mission dossier card.
- Touch drag (pitch/yaw) + pinch-to-zoom.
- Twinkling starfield outside Moon disc.
- `MoonDetailSheet` — phase emoji, illumination %, orbital distance, orbital period.
- Old 2D orbiting Moon removed from `GlobeView.kt`.

## 1.3 Yearly Lunar Phase Timeline Scrubber
**Status: ✅ DONE**
- Bottom HUD: phase emoji + name + illumination % + formatted date.
- Slider mapped to day 1–365/366 of current year.
- Month quick-jump chips (Jan–Dec horizontal scroll).
- Prev/Next day buttons + TODAY reset.
- 3D Moon terminator updates live as slider is dragged.
- Phase angle passed to `Moon3DPlatformView(phaseAngle = displayedMoonInfo.phaseAngle)`.

## 1.4 Moon Terminator Real-Time Accuracy
**Status: ✅ DONE**
- Elongation-based sun vector direction is astronomically correct.
- Real-time phase refresh loop in `MoonView.kt` via hourly `LaunchedEffect` timer.
- *Backlog*: Subsolar latitude Y-component (currently `y = 0.0`, real Moon has $\pm 1.5^\circ$ tilt).
- *Backlog*: Libration $\pm 7^\circ$ wobble.
