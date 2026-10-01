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
**Status: ✅ IMPLEMENTED (runtime performance unmeasured)**
- Elongation-based phase angle drives the Moon terminator on Android and WebGL.
- Real-time phase refresh loop in `MoonView.kt` via hourly `LaunchedEffect` timer.
- The approximately 1.54° lunar equatorial inclination supplies a nonzero subsolar latitude to both renderers.
- Approximate optical libration in latitude and longitude adjusts the Moon model on both targets; Apollo beacons and hit testing use the same yaw-first effective rotation.
- Orientation is calculated when lunar date/position changes, not in a new continuous GL loop. Device and browser visual alignment, absolute astronomical accuracy, and measured frame cost remain unverified.

## 1.5 Heliocentric Solar System & Live Keplerian Orbits (Task 84)
**Status: ✅ DONE**
- Central Sun with procedural dynamic corona canvas animation.
- Millisecond-accurate Keplerian orbital mechanics calculating real-time mean anomaly and heliocentric positions for all planets.
- Interactive 3D orbital canvas with camera gestures and smooth dive transitions into individual celestial bodies.

## 1.6 3D Planetary Night Lighting & Terminator Scrubber (Task 85)
**Status: ✅ DONE**
- High-visibility fragment shader for all 3D planets (Mercury, Venus, Jupiter, Saturn, Uranus, Neptune).
- Widened day/twilight band (~68%), boosted ambient night illumination (~0.45), and soft atmospheric limb glow.
- Real-time day/night solar terminator scrubber HUD (`PlanetScrubberHud.kt`) with RESET LIVE control.

## 1.7 Unified 360° Free Orbital Camera Navigation (Task 86 - P0)
**Status: ⏳ PLANNED**
- Unify camera movement across Earth, Moon, Mars, and all planets to match Solar System free orbital camera mechanics.
- Camera orbits freely in 360° spherical coordinates around the target body without pitch clamping (gimbal-free / quaternion / continuous pitch navigation).
- Moves the viewpoint rather than rotating the sphere model at fixed origin, mirroring NASA's Eyes on the Solar System.

## 1.8 Universal Planetary Dossier & Atmospheric Specs Sheet (Task 87 - P1)
**Status: ⏳ PLANNED**
- M3 `AdaptiveInfoSheet` presenting comprehensive physical dimensions, surface gravity, escape velocity, axial tilt, and solar flux.
- Atmospheric composition breakdown with segmented bar visualization.
- Extreme diurnal temperature ranges and environmental telemetry.

## 1.9 Planetary Surface Features & Historical Mission Landing Sites (Task 88 - P1)
**Status: ⏳ PLANNED**
- 2D-on-3D projection beacons for surface features (Caloris, Maxwell Montes, Great Red Spot, Olympus Mons).
- Historic robotic exploration markers (Venera 9-14, MESSENGER, Voyager 2, Galileo, Cassini plunge).
- Tap-to-inspect mission dossiers with launch dates, instruments, and scientific discoveries.

## 1.10 Natural Satellites & Major Moons System (Task 89 - P1)
**Status: ⏳ PLANNED**
- Major moons orbital carousel and mini-dossiers: Jupiter's Galilean moons (Io, Europa, Ganymede, Callisto), Saturn's moons (Titan, Enceladus), Neptune's Triton.
- Orbital scale, tidal heating, and ocean world / astrobiology habitability ratings.

## 1.11 Saturn 3D Ring System & Interior Core Visualizer (Tasks 90 & 91 - P2)
**Status: ⏳ PLANNED**
- Hardware-accelerated 3D planar ring geometry for Saturn with Cassini Division, Encke Gap, and ring thickness/composition telemetry.
- Interactive X-Ray cross-section HUD displaying internal layers (core, mantle, liquid metallic hydrogen, atmosphere).
