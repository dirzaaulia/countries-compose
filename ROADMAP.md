# 🚀 Countries Compose — Master Roadmap

> **Single Source of Truth** for all feature status, bugs, and plans.
> Last audited: 2026-09-24. Cross-referenced against actual codebase.

---

## Status Legend
| Symbol | Meaning |
|--------|---------|
| ✅ | Fully implemented and working |
| ⚠️ | Implemented but has known gaps |
| 🐛 | Implemented but broken/degraded |
| ❌ | Not yet started |

---

## 🎯 Sprint Progress & Status

| # | Task | Section | Status | Priority | Notes |
|---|------|---------|--------|----------|-------|
| 1 | Fix Earth↔Moon transition frame drop | §7 | ✅ DONE | - | Pre-warmed textures, `isPageActive` lifecycle pause/resume |
| 2 | Fix Moon phase hourly refresh | §1.4 | ✅ DONE | - | Hourly `LaunchedEffect` refresh loop in `MoonView` |
| 3 | Fix hardcoded "384.4K KM" badge in top bar | §3.3 | ✅ DONE | - | Real `moonInfo.distanceKm` passed to `MissionControlTopBar` |
| 4 | Add EONET hazards periodic refresh | §4.1 | ✅ DONE | - | 30-min background refresh loop in `App.kt` |
| 5 | Country tap auto-centering & adaptive zoom | §8 | ✅ DONE | - | Inverted latitude fixed, mainland centroid, calibrated area zoom |
| 6 | Country dossier compact peek mode | §8 | ✅ DONE | - | Flag + country name peek (68dp), expands smoothly to full sheet |
| 7 | Complete ISS tap → telemetry card + ISS label | §3.2 | ✅ DONE | - | Tap beacon, live telemetry card, 92-min orbital track, floating badge |
| 8 | EONET magnitude & severity display | §4.3 | ✅ DONE | - | Parsed from geometry and displayed in HazardDetailSheet |
| 9 | Animated Flight Path aircraft | §5.2 | ✅ DONE | - | Glowing airplane beacon interpolating along Great Circle arc |
| 10 | Fix initial load country tap selection | §8 | ✅ DONE | - | `rememberUpdatedState` & dynamic keying prevents stale empty closure |
| 11 | Country Dossier Material 3 Bottom Sheet | §8 | ✅ DONE | - | Replaced custom Surface with official `ModalBottomSheet` |
| 12 | Redesign Country Dossier close button | §8 | ✅ DONE | - | Frosted glassmorphic vector cross button replaces raw text character |
| 13 | AABB Bounding Box tap pre-filter | §8 | ✅ DONE | - | Instant bounding box pre-filtering eliminates ray-casting on 95%+ non-candidate countries |
| 14 | Supersonic Aircraft Animation Engine | §5.2, §9.2 | ✅ DONE | - | Dynamic tangent heading rotation, parabolic altitude arc, jet contrails, wingtip strobes, airport rings |
| 15 | HUD Map Legend & Symbology Sheet | §9.1 | ✅ DONE | - | Material 3 modal sheet with all 10 cartographic & space symbologies, linked via TopBar `[ℹ️ Legend]` |
| 16 | Dynamic Day/Night Dossier Theming | §9.3 | ✅ DONE | - | Solar illumination calculation, diurnal/nocturnal glass themes, and solar time badge |
| 17 | Live Weather Atmospheric Motion Overlay | §9.4 | ✅ DONE | - | Procedural Compose particle system (rain, snow, thunderstorms, clouds, heatwave mirage, solar flare) |
| 18 | Open-Meteo Meteorology Station | §9.5 | ✅ DONE | - | 7-day forecast cards, 24h interactive hourly curve, wind compass rose, UV index meter, daylight arc |
| 19 | World Bank Macroeconomic Dashboard | §9.5 | ✅ DONE | - | 5-year GDP trend sparkline, inflation CPI %, unemployment gauge, life expectancy, clean energy share |
| 20 | NASA EONET Planetary Crisis Monitor | §9.5 | ✅ DONE | - | Global crisis feed, category filters, proximity sorting, and tap-to-fly epicenter 3D camera navigation |
| 21 | WASM 3D WebGL Globe & Moon Parity | §9.7 | ✅ DONE | - | Option A implemented via `PlanetWebGLRenderer.kt` with 2K textures, `SphereMesh` VBO/IBO, and GLSL shaders |
| 22 | Stale-While-Revalidate Offline Cache | §9.6 | ✅ DONE | - | In-memory 15-min SWR cache + procedural offline fallback statistics for all countries in `GlobeRepository.kt` |
| 23 | Target Country Quiz Neon Silhouette Glow | §5.1 | ✅ DONE | - | Adaptive pulsing emerald green (correct) / golden amber (target) silhouette halo in `GlobeView.kt` |
| 24 | Flight Simulator Mach 2.2 SST & Country Picker | §5.2 | ✅ DONE | - | Mach 2.2 supersonic mode toggle with Concorde speed metrics + interactive departure/arrival country selector |
| 25 | OpenGL Near/Far Frustum Zoom Clipping Fix | §6.3 | ✅ DONE | - | Expanded near/far clipping planes to ±50,000f in `EarthGLRenderer.kt` and `PlanetWebGLRenderer.kt` |
| 26 | Milky Way Galactic Dust Lane Skybox | §1.1 | ✅ DONE | - | Procedural celestial dust gradient lane rendered across Canvas deep space starfield |
| 33 | Moon Apollo Site Marker Projection Fix | §13.1 | ✅ DONE | **P0 (Critical)** | Corrected forward projection math & positive Euler order in `MoonView.kt` |
| 34 | Dossier Timezone & Solar Time Clarification | §13.2 | ✅ DONE | **P1 (High)** | Civil timezone (`UTC+07:00`) badge, timezone count chip, local solar moved to secondary sub-label |
| 35 | Earth Globe Render Optimization & Back-Face Culling | §13.3 | ✅ DONE | **P1 (High)** | Fast centroid back-face culling eliminates ~60% polygon loops, pre-allocated paths, zero `TransitionSegment` GC churn |
| 36 | Dossier Scroll Lag & Frame Drop Resolution | §13.4 | ✅ DONE | **P1 (High)** | Unified 6 infinite transitions into master clock, reused path buffers in `WeatherAtmosphericOverlay` |
| 37 | Mission Control TopBar UX Revamp | §13.5 | ✅ DONE | **P2 (Medium)** | Compact HUD with 34dp icon buttons & sleek native Material 3 `ModalBottomSheet` for Layers |
| 38 | Flight Route Aircraft Model & Speed Tuning | §13.6 | ✅ DONE | **P2 (Medium)** | Authentic commercial airliner vector silhouette (fuselage, nacelles, strobes) & realistic 14s cruise speed |
| 39 | KMP Vector Animation Upgrade (Compottie / Lottie) | §13.7 | 🎬 ARCH | **P3 (Planned)** | Research & integrate Compottie for free, high-fidelity Lottie animations across Android & Wasm |
| 40 | Static Icons, Logos & App Icon Generation | §13.8 | 🎨 DESIGN | **P3 (Planned)** | Text-to-logo via built-in AI/Recraft, IconKitchen for Android Adaptive & Themed Icons |
| 41 | Moon Zoom Lighting & Highlight Clipping Fix | §13.9 | ✅ DONE | **P0 (Critical)** | Calibrated lunar photometric curve in `GlobeShaders.kt`, eliminating blown-out white glare |
| 42 | Modal Bottom Sheet Frame Drop Elimination | §13.10 | ✅ DONE | **P1 (High)** | Converted `GLSurfaceView` to `RENDERMODE_WHEN_DIRTY` & paused background loops when `isSheetOpen` |
| 43 | Minimalist Close Button Design System | §13.11 | ✅ DONE | **P2 (Medium)** | Created unified `MinimalistCloseButton.kt` (28dp frosted circle, 1.35dp stroke cross) across all sheets & cards |
| 44 | Supersonic Flight Simulator Physics & Clarification | §13.12 | ✅ DONE | **P2 (Medium)** | Wired `isSupersonic` to 5.5s flight loop + red contrails, added descriptive Mach 2.2 metrics & explanation |
| 45 | Comprehensive & Consistent Bottom Sheet Information | §13.13 | ✅ DONE | **P2 (Medium)** | Unified header cards, category badges, drag handles, and metadata chips across all sheets |
| 46 | Orchestrated Country Selection Flight Flow | §13.14 | ✅ DONE | **P1 (High)** | Smooth 3D camera auto-glide centers country first, then slides up dossier sheet once centered |
| 47 | 3D Globe Country Centering & Zoom Offset Fix | §13.15 | ✅ DONE | **P0 (Critical)** | Fixed Euler rotation order to yaw-first ($R_Y \to R_X$), eliminating zoom-dependent latitude drift |
| 48 | WASM Icon & Emoji Glyph Rendering Pipeline (Phase 1) | §13.16 | ✅ DONE | **P1 (High)** | Replaced multi-byte emoji in FlightRouteHudCard & App.kt with WASM-safe ASCII/Latin text. Noto font bundling deferred to Phase 2 (Task 48b). |
| 49 | Adaptive Large Screen Responsive Side Sheet | §13.17 | ⏳ PLANNED | **P1 (High)** | Refactor full-width bottom sheets into Material 3 Side Sheets for desktop/WASM & tablet screens |
| 50 | Zero-Lag Country Centering & Instant Dossier | §13.18 | ✅ DONE | **P1 (High)** | Decoupled network fetch from camera flight. `flyTo()` shortened to 650ms (`FastOutSlowInEasing`). Sheet opens immediately after centering; skeleton shows while live data streams in background. |
| 51 | Close Button (X) Design System Revamp | §13.19 | ✅ DONE | **P2 (Medium)** | Borderless flush icon (36dp touch target, muted slate `#94A3B8`, M3 ripple on press, resolution-independent cross-platform vector stroke). |
| 52 | Fix Globe Blackout Bug on Sheet Dismissal | §13.20 | ✅ DONE | **P0 (Critical)** | Set `preserveEGLContextOnPause = true` on `GLSurfaceView`. Added `AndroidView.update` callback to call `requestRender()` on every recomposition, ensuring the frame is redrawn after sheet dismiss. |
| 53 | Revamp All Floating Cards to Unified Sheet System | §13.21 | ✅ DONE | **P2 (Medium)** | Converted ISS, NASA Hazards, Flight Route, and Apollo site inspector into authentic Material 3 `ModalBottomSheet`s with transparent scrim, drag handle, and minimalist close buttons. Sanitized WASM emojis. |
| 54 | Flight Simulator Speed Toggle & Reactivity Fix | §13.22 | ✅ DONE | **P2 (Medium)** | Wrapped `planeProgress` `animateFloat` in `key(isSupersonic)` block. This destroys and re-creates the `InfiniteTransition` immediately when supersonic mode changes, applying new 5.5s / 14s duration on the spot. |
| 28 | Sub-National Administrative Divisions (ADM1 & ADM2) | §12.2 | ⏳ PLANNED | **P1 (Highest)** | Provinces/States & Cities/Regencies via combined geoBoundaries (polygons) + GeoNames (hierarchy) |
| 29 | Planetary Time Machine (Earth 24h & Seasonal) | §12.3 | ⏳ PLANNED | **P2 (High)** | Diurnal solar terminator scrubbing, night light progression, and seasonal analemma tilt |
| 32 | Global Search & Instant Teleportation HUD | §12.6 | ⏳ PLANNED | **P3 (Medium-High)** | TopBar search bar with fuzzy autocomplete and cinematic camera auto-orbit |
| 30 | Geological & Infrastructure Cartographic Layers | §12.4 | ⏳ PLANNED | **P4 (Medium)** | Tectonic Plates (Ring of Fire), Submarine Internet Cables, and World Timezone bands |
| 27 | Dynamic Delivery for Gamification (Play & WASM) | §12.1 | ⏳ PLANNED | **P5 (Medium-Low)** | Dynamic split APK (`:feature:gamification`) on Android + Asset streaming & CacheStorage on WASM |
| 31 | The Red Planet (3D Mars Explorer) | §12.5 | ⏳ PLANNED | **P6 (Future)** | Page 2 Mars explorer with NASA 2K Viking texture and interactive Rover landing site beacons |

---

## 🌌 1. Deep Space Environment & Celestial Bodies

### 1.1 Procedural Starfield & Celestial Environment
**Status: ✅ DONE**
- ✅ 260+ deterministic stars on Canvas with twinkling animation (`GlobeView.kt` + `MoonView.kt`)
- ✅ Multi-color star palette: white, blue (`#90CAF9`), yellow (`#FFE082`), warm (`#FFCCBC`)
- ✅ Magnitude-1 stars have soft glow halo
- ✅ Stars masked outside planet disc radius
- ✅ **Milky Way Galactic Dust Lane**: Soft luminous cosmic dust lane gradient band rendered behind globe disc

### 1.2 Full-Screen 3D Moon Explorer (HorizontalPager Page 1)
**Status: ✅ DONE**
- ✅ Dedicated `MoonView.kt` — HorizontalPager page 1
- ✅ NASA LRO equirectangular texture (2048×1024, 2:1 ratio) — no black patches
- ✅ `EarthGLRenderer` reused with `isMoonMode = true`
- ✅ Phase terminator via elongation → sun direction vector (`AstronomyMath.calculateMoonInfo()`)
- ✅ Apollo 11–17 landing site beacons with tap-to-inspect mission dossier card
- ✅ Touch drag (pitch/yaw) + pinch-to-zoom
- ✅ Twinkling starfield outside Moon disc
- ✅ `MoonDetailSheet` — phase emoji, illumination %, orbital distance, orbital period
- ✅ Old 2D orbiting Moon removed from `GlobeView.kt`
- **Note:** Lunar maria and crater textures (Tycho, Copernicus, etc.) are visible via NASA LRO texture — not Canvas-drawn. Correct behavior.

### 1.3 Yearly Lunar Phase Timeline Scrubber
**Status: ✅ DONE**
- ✅ Bottom HUD: phase emoji + name + illumination % + formatted date
- ✅ Slider mapped to day 1–365/366 of current year
- ✅ Month quick-jump chips (Jan–Dec horizontal scroll)
- ✅ Prev/Next day buttons + TODAY reset
- ✅ 3D Moon terminator updates live as slider is dragged
- ✅ Phase angle passed to `Moon3DPlatformView(phaseAngle = displayedMoonInfo.phaseAngle)`

### 1.4 Moon Terminator Real-Time Accuracy
**Status: ✅ DONE**
- ✅ Elongation-based sun vector direction is astronomically correct
- ✅ Real-time phase refresh loop in `MoonView.kt` via hourly `LaunchedEffect` timer
- ❌ Low-priority: Subsolar latitude Y-component (currently `y = 0.0`, real Moon has ±1.5° tilt)
- ❌ Low-priority: Libration ±7° wobble

---

## ☀️ 2. Real-Time Astronomy & Earth Shaders

### 2.1 Live Solar Day/Night Terminator (Earth)
**Status: ✅ DONE**
- ✅ `AstronomyMath.calculateSunPosition()` derives subsolar point from UTC
- ✅ Sun direction vector fed into `EarthGLRenderer` GLSL fragment shader
- ✅ `smoothstep` twilight band + golden-amber sunrise/sunset color

### 2.2 Dynamic Day/Night Adaptive Border Colors
**Status: ✅ DONE**
- ✅ Black borders on lit hemisphere, white/luminous on dark hemisphere
- ✅ Per-segment cross-terminator interpolation in `GlobeView.kt`
- ✅ Unified border rings — no hard cuts at terminator

### 2.3 Atmospheric Glow
**Status: ⚠️ PARTIAL**
- ✅ Blue atmospheric limb glow in GLSL
- ✅ Cloud layer rendered + blended
- ❌ Missing: Full Nishita/Bruneton Rayleigh+Mie physically-based model

### 2.4 Sun Position Refresh on Earth GL
**Status: ✅ DONE**
- ✅ `AstronomyMath.calculateSunPosition()` evaluated dynamically every frame in `EarthGLRenderer.onDrawFrame()`
- ✅ Continuous subsolar vector updates reflect real-time planetary illumination with zero frame stalling

---

## 🛰️ 3. Live ISS Tracker

### 3.1 ISS Telemetry Fetch
**Status: ✅ DONE**
- ✅ `GlobeRepository.fetchISSTelemetry()` → `https://api.wheretheiss.at/v1/satellites/25544`
- ✅ Polls every 6 seconds when Satellites layer ON
- ✅ Data model: `ISSTelemetry(lat, lng, altitude, velocity, visibility, timestamp)`

### 3.2 ISS Rendering on Globe
**Status: ✅ DONE**
- ✅ 3-ring glowing beacon on Canvas at correct orbital altitude (`issRadius = currentRadius × 1.066`)
- ✅ Front-hemisphere only (`p.z > 0`)
- ✅ Direct screen-space and spherical proximity tap detection
- ✅ `ISSTelemetryCard` popup on tap (velocity, altitude, coordinates, daylight/eclipse solar illumination, camera tracking)
- ✅ Floating "🛰️ ISS • [alt] km" badge next to beacon on front hemisphere
- ✅ 92.9-minute orbital ground track path with glowing dashed cyan line

### 3.3 ISS Badge in Top Bar
**Status: ✅ DONE**
- ✅ Real lunar distance from `moonInfo.distanceKm` passed to `MissionControlTopBar`

---

## 🌍 4. NASA EONET Natural Hazards

### 4.1 EONET API Fetch
**Status: ✅ DONE**
- ✅ `GlobeRepository.fetchGlobalNasaEvents()` — full parse of 🔥🌀🌋🧊🌊
- ✅ Periodic 30-minute background polling loop running in `App.kt`
- ✅ Dynamic deduplication and real-time hazard mapping on globe

### 4.2 Hazard Beacons on Globe
**Status: ✅ DONE**
- ✅ Pulsing beacon (animated aura + solid core) per hazard
- ✅ Color-coded by category
- ✅ Tap within 350km → `onHazardSelected(hazard)`

### 4.3 Hazard Detail Sheet
**Status: ✅ DONE**
- ✅ `HazardDetailSheet.kt` — category icon, title, date, lat/lng
- ✅ Magnitude/severity display parsed from EONET geometry (`magnitudeValue` + `magnitudeUnit`)

---

## 🎮 5. Gamification

### 5.1 Geography Quiz
**Status: ✅ DONE**
- ✅ `QuizHudCard.kt` — target country, score + streak, feedback messages
- ✅ +100pts + (streak × 25) bonus; streak reset on wrong
- ✅ Next Question / End Quiz; mutually exclusive with Flight Mode
- ✅ **Target country silhouette glow**: Pulsing emerald green aura on correct answer and golden amber glow on target country in `GlobeView.kt`

### 5.2 Flight Path Simulator
**Status: ✅ DONE**
- ✅ `FlightRouteHudCard.kt` — origin → destination with km distance
- ✅ `AstronomyMath.calculateGreatCircleArc()` + `calculateGreatCircleDistance()`
- ✅ Glowing amber dashed arc + auto fly-to on activation
- ✅ **Supersonic Aircraft Vector Engine**: Dynamic tangent heading rotation ($\theta = \text{atan2}$), parabolic altitude arc ($r(t) = R \times (1.012 + 0.026 \cdot \sin(t \cdot \pi))$), 6-stage contrail trail afterglow, wingtip nav strobes, and pulsing departure/arrival airport rings
- ✅ **Mach 2.2 SST Speed Metrics**: Toggle between commercial airliner (Mach 0.78 / 850 km/h) and supersonic Concorde corridor (Mach 2.2 / 2,335 km/h)
- ✅ **Interactive Country Selector**: Direct tap-to-change departure and arrival country selection chips

---

## 🛠️ 6. Architecture & Top Bar

### 6.1 HorizontalPager Earth ↔ Moon
**Status: ✅ DONE**
- ✅ `HorizontalPager(pageCount = { 2 })` in `App.kt`
- ✅ Page 0: `GlobeView` (Earth), Page 1: `MoonView` (Moon)
- ✅ Swipe + top-bar pill tab switching

### 6.2 MissionControlTopBar Compact Design
**Status: ✅ DONE**
- ✅ Single 44dp floating glass bar (replaces old 130dp double-row header)
- ✅ Left: Live badge, Center: `[ 🌍 Earth | 🌕 Moon ]` pill, Right: `⛯ Layers` button
- ✅ Expandable glassmorphic layer quick-menu (Borders, Satellites, Hazards, Flight, Quiz)
- ✅ Layer menu hidden on Moon page

### 6.3 Engine Polish Items
| Item | Status | Notes |
| :--- | :--- | :--- |
| Moon texture black patch | ✅ DONE | NASA LRO 2:1 equirectangular 2048×1024 |
| Texture fallback binding | ✅ DONE | Prevents OpenGL sampler validation failures |
| Layers badge text centering | ✅ DONE | `lineHeight + textAlign` fix |
| Max zoom 4.5x/6.0x clipping | ✅ DONE | Expanded near/far planes to ±50,000f in `EarthGLRenderer` & `PlanetWebGLRenderer` |
| Multi-res LOD textures | ❌ TODO | 1K/2K/4K switching at high zoom |
| Haptic feedback | ❌ TODO | Country tap, meridian/equator crossing |

---

## ⚡ 7. Earth ↔ Moon Transition Optimization
**Status: ✅ DONE**
- ✅ Texture pre-warming on launch (`moon.jpg` decoded off the main thread)
- ✅ `isPageActive` lifecycle hook pauses inactive `GLSurfaceView` (`onPause`/`onResume`)
- ✅ Zero-frame-drop smooth pager scrolling between Earth and Moon

---

## 📋 8. Sprint Backlog & High-Impact Enhancements

| Feature | Description | Status |
| :--- | :--- | :--- |
| **HUD Map Legend & Symbology** | Comprehensive color-coded guide for all lines, arcs, hazard icons, and satellites | ✅ DONE |
| **Supersonic Aircraft Animation** | True airplane vector with tangent heading rotation, altitude arc, and contrail | ✅ DONE |
| **Day/Night Atmospheric Dossier** | Dossier sheet adapts background/animation dynamically to country's current sun status | ✅ DONE |
| **Live Weather & Atmospheric Motion** | Rain streaks, snow drift, lightning flashes, solar rays, cloud parallax, and heat haze | ✅ DONE |
| **AABB Bounding Box Tap Pre-Filter** | Skips ray-casting on 95% of countries for instant tap selection | ✅ DONE |
| **Open-Meteo Meteorology Station** | 7-day forecast, 24h hourly curve, wind direction compass dial, UV index | ✅ DONE |
| **World Bank Macroeconomic Dashboard** | Multi-indicator dashboard: GDP trend, inflation, unemployment, life expectancy | ✅ DONE |
| **NASA EONET Planetary Monitor** | Full hazard crisis timeline with severity metrics & tap-to-fly epicenter navigation | ✅ DONE |
| **WASM 3D WebGL Globe & Moon Parity** | Port OpenGL shaders & 2K textures to WebGL context on WasmJS (Option A) | ✅ DONE |
| **Stale-While-Revalidate Offline Cache**| Offline resilience for all external APIs + procedural fallback statistics | ✅ DONE |
| **Milky Way Galactic Dust Lane** | Procedural celestial dust gradient lane rendered across Canvas starfield | ✅ DONE |

---

## 🚀 9. Detailed Technical Specifications: Upcoming Milestone

### 9.1 Unified HUD Cartographic & Space Legend System
**Status: ✅ DONE**
- **Objective**: Provide users with immediate, unambiguous visual clarity for all on-globe symbology.
- **Color Matrix**:
  - 🟦 **Selected Nation**: Neon Cyan (`#00F2FE` / `#38BDF8`) with outer glow aura.
  - ⬛/⬜ **National Borders**: Solar-adaptive (Day: Obsidian Black `#0F172A` with white edge; Night: Luminous Ivory `#F8FAFC` with dark rim).
  - 🟨 **Solar Terminator**: Golden Twilight Arc (`#F59E0B` to `#D97706`).
  - 🟧 **Commercial Flight Path**: Aviation Gold (`#F59E0B`) dashed geodesic arc with origin/destination pulsating airport rings.
  - 🟩 **ISS Orbit Ground Track**: Orbital Teal (`#06B6D4` / `#22D3EE`) micro-dashed 1.8dp path for full 92.9-min LEO pass.
  - 🌋 **Volcano Hazards**: Fiery Crimson (`#EF4444` / `#DC2626`) pulsing beacon.
  - 🔥 **Wildfire Hazards**: Blazing Orange (`#F97316`) pulsing beacon.
  - 🌀 **Severe Storm Hazards**: Electric Violet (`#A855F7`) pulsing beacon.
  - 🧊 **Glacial / Sea Ice Hazards**: Polar Glacier (`#38BDF8` / `#E0F2FE`) pulsing beacon.
  - 🌕 **Apollo Lunar Landing Sites**: Apollo Gold (`#FBBF24`) landing markers on Moon explorer.
- **UI Integration**: `[ℹ️ Legend]` action chip in `MissionControlTopBar` opening a sleek Material 3 HUD Sheet (`MissionLegendSheet.kt`).

### 9.2 Supersonic Commercial Aircraft Animation Engine
**Status: ✅ DONE**
- **Dynamic Tangent Heading (Bearing)**:
  - Vector plane icon points along geodesic tangent: $\theta = \text{atan2}(p_{y2} - p_{y1}, p_{x2} - p_{x1})$.
- **Parabolic Cruising Altitude Arc**:
  - Realistic climb, cruise, and descent: $r(t) = R_{\text{globe}} \times (1.012 + 0.026 \cdot \sin(t \cdot \pi))$.
- **Fading Contrail & Strobe Lights**:
  - 6-segment jet exhaust trail fading smoothly from $55\%$ opacity down to $0\%$.
  - Alternating port (red) and starboard (green) navigation strobes blinking on wingtips.
  - Pulsing amber/cyan concentric rings marking departure and arrival airports.

### 9.3 Day/Night Atmospheric Dossier Theming & Animation
**Status: ✅ DONE**
- **Solar Illumination Check**:
  - Evaluated via `AstronomyMath.isDaylight()` using dot product with twilight margin: $\vec{N} \cdot \vec{S} > -0.06$.
- **Daylight Theme**:
  - Sunlit azure/amber gradient header background (`#300284C7` $\to$ `#18F59E0B`).
  - Live `☀️ Daylight • ~HH:mm Solar` status badge.
- **Nighttime Theme**:
  - Deep midnight starlight gradient header (`#38312E81` $\to$ `#181E1B4B`).
  - Live `🌙 Nighttime • ~HH:mm Solar` status badge.

### 9.4 Dynamic Weather Particle & Atmospheric Engine
**Status: ✅ DONE**
- **Platform Strategy**:
  - Built directly on **Compose Multiplatform Canvas & Math Shaders** (`WeatherAtmosphericOverlay.kt`) for 60–120fps hardware-accelerated cross-platform execution on Android & WasmJS without heavy external runtime overhead.
- **Weather Condition Visuals**:
  - ⛈️ **Thunderstorms (95–99)**: Screen strobe with randomized branching electric lightning bolts.
  - 🌧️ **Rain / Drizzle (51–67, 80–82)**: Angled falling velocity streaks with expanding ground splash ripples.
  - ❄️ **Snowfall (71–77, 85–86)**: Drifting circular flakes with harmonic sine-wave horizontal wind sway.
  - ☀️ **Clear / Sunny (0)**: Pulsing corona rays with soft lens flare sweeps.
  - ☁️ **Cloudy / Overcast / Fog (1–48)**: Multi-layered drifting volumetric cloud silhouettes with parallax depth.
  - 🌡️ **Extreme Heat / Heatwave ($\ge 32^\circ\text{C}$)**: Shimmering atmospheric thermal mirage waves (rising vertical sine-wave heat haze distortion).

### 9.5 Complete API Deep-Dive Dashboards
**Status: ✅ DONE**
- **Open-Meteo Meteorology Station** (`MeteorologyStationSheet.kt`):
  - Accessible via "View Meteorology Station ↗" in the dossier weather card.
  - Hero Weather Card: Current temp, Feels Like index, wind speed, humidity, and particle motion overlay.
  - 7-Day Daily Forecast: High/low temperature spreads, precipitation probability %, and weather icons.
  - 24-Hour Hourly Curve: Touch-scrubbing temperature spline chart with precipitation probability bar overlay.
  - Wind Direction & Speed: Analog compass rose with red needle pointing along wind bearing, speed in km/h and knots.
  - Atmospheric Sensors Grid: Barometric surface pressure (hPa), UV index gauge, and status badges.
  - Diurnal Daylight Arc: Real-time solar trajectory arc showing sunrise, solar noon elevation, and sunset.

- **World Bank Macroeconomic Dashboard** (`WorldBankDashboardSheet.kt`):
  - Accessible via "View Macroeconomic Analysis ↗" in the dossier demographics card.
  - Executive Macro Card: GDP per Capita ($ USD), total GDP, Income Group classification, and economic tier.
  - 5-Year Growth Trends: Historical sparklines for 5-year GDP per capita trajectory and annual CPI inflation rate %.
  - Demographics & Human Capital: Life expectancy at birth with progress gauge and unemployment rate meter.
  - Ecological Footprint & Energy Transition: Renewable energy share (% of total) vs CO2 emissions (metric tons per capita).

- **NASA EONET Planetary Hazard Crisis Monitor** (`NasaCrisisMonitorSheet.kt`):
  - Accessible via "Crisis Monitor ↗" in the dossier live status strip.
  - Real-Time Global Crisis Feed: Live catalogue of active natural disasters grouped by category (🌋 Volcanoes, 🔥 Wildfires, 🌀 Cyclones/Storms, 🧊 Glacial/Sea Ice, 🌊 Floods).
  - Disaster Severity Index: Magnitude readings, units, detection dates, and proximity distance from observer country.
  - One-Tap Epicenter Fly-To: Smooth camera auto-navigation to coordinates with auto-zoom to 2.2x and active beacon highlight pulse.

### 9.6 Performance & Stability Architecture
- **AABB Pre-Filter (✅ DONE)**: Instant country selection by evaluating 4-point bounding box before ray-casting.
- **Stale-While-Revalidate Cache (✅ DONE)**: 15-minute in-memory cache with background asynchronous revalidation and procedural offline fallback profiles in `GlobeRepository.kt`.

### 9.7 WASM 3D Planet Rendering — WebGL Parity (Option A: Chosen Architecture)
**Status: ✅ DONE (Option A Implemented)**
- **Architecture & Implementation Details**:
  - Implemented in [`PlanetWebGLRenderer.kt`](file:///d:/Android/Projects/countries-compose/app/src/wasmJsMain/kotlin/com/dirzaaulia/countries/PlanetWebGLRenderer.kt) and driven via [`Globe3DPlatformView.wasmJs.kt`](file:///d:/Android/Projects/countries-compose/app/src/wasmJsMain/kotlin/com/dirzaaulia/countries/Globe3DPlatformView.wasmJs.kt).
  - **Milestone 1 — WebGL Context & Canvas Lifecycle**:
    - Embedded dedicated HTML `<canvas id="PlanetCanvas">` directly behind Compose Canvas with transparent background and zero pointer interference (`pointer-events: none`).
  - **Milestone 2 — Shader Compilation**:
    - Native GLSL 1.0 compilation of [`GlobeShaders.VERTEX_SHADER`](file:///d:/Android/Projects/countries-compose/app/src/commonMain/kotlin/com/dirzaaulia/countries/GlobeShaders.kt#L5-L21) and [`GlobeShaders.FRAGMENT_SHADER`](file:///d:/Android/Projects/countries-compose/app/src/commonMain/kotlin/com/dirzaaulia/countries/GlobeShaders.kt#L23-L101).
  - **Milestone 3 — Sphere Mesh Buffer Upload**:
    - Procedural UV sphere generation via [`SphereMesh.kt`](file:///d:/Android/Projects/countries-compose/app/src/commonMain/kotlin/com/dirzaaulia/countries/SphereMesh.kt) uploaded to WebGL VBO & IBO buffers.
  - **Milestone 4 — 2K Texture Loading & Binding**:
    - Real-time asynchronous JPEG decoding (`earth_day.jpg`, `earth_night.jpg`, `earth_clouds.jpg`, `moon.jpg`) via Blob URLs and WebGL mipmapped texture samplers (`TEXTURE0`, `TEXTURE1`, `TEXTURE2`).
  - **Milestone 5 — Camera Uniforms & Interactivity**:
    - Column-major 4x4 matrix camera projection matched 1:1 with Android's `EarthGLRenderer.kt` and `GlobeView.kt`.
    - Live subsolar illumination vector and lunar phase angle terminator calculations.
  - **Expected Outcome**:
    - **100% visual parity** with Android (photorealistic 3D Earth, night city light clusters, animated cloud cover, and cratered Moon) running at 60 FPS in modern browsers.

---

## 🔮 10. Next Sprints Master Plan

| Sprint | Milestone Focus | Key Deliverables |
| :--- | :--- | :--- |
| **Sprint 1** | **HUD Polish, Tap Perf, Motion & WASM WebGL** | ✅ AABB pre-filter, ✅ Supersonic aircraft, ✅ HUD Legend, ✅ Day/Night theming, ✅ Weather atmospheric overlay, ✅ WASM WebGL 3D planet parity |
| **Sprint 2** | **Deep-Dive API Stations** | ✅ `MeteorologyStationSheet` (7-day forecast & hourly curve), ✅ `WorldBankDashboardSheet` (macro trend gauges), ✅ `NasaCrisisMonitorSheet` (global disaster feed + fly-to-epicenter) |
| **Sprint 3** | **Offline Resilience & Caching** | ✅ Stale-While-Revalidate memory caching + procedural offline fallback statistics in `GlobeRepository` |
| **Sprint 4** | **Gamification & Simulator Expansion** | ✅ Target country silhouette neon glow in quiz mode, ✅ Mach 2.2 SST speed mode & manual country selector for Flight Simulator |
| **Sprint 5** | **Engine Polish** | ✅ Max zoom 4.5x/6.0x clipping fix in OpenGL & WebGL frustum (±50,000f near/far planes) |
| **Sprint 6** | **Celestial Visual Polish** | ✅ Milky Way galactic dust lane soft atmospheric band across deep space Canvas starfield |

---

## 🚀 11. CI/CD & Google Play Deployment Architecture

- **Workflow Configuration**: `.github/workflows/play_console_deploy.yml`
- **Dynamic Track Deployment**:
  - Supports on-demand dispatch to: `internal`, `alpha`, `beta`, and `production`.
  - Configurable staged rollout percentage ($1\% \to 100\%$).
- **Version Name Suffixing**:
  - Automatically matches the destination track: `${versionName}-${track}.${versionCode}` (e.g. `1.2.0-beta.42`).
- **Dynamic Workflow Run Title**:
  - Workflow run title displays the exact deployment metadata: `Deploy [Track] - v[VersionName] (Code [VersionCode])`.
- **Release Automation**:
  - Automated release notes generation from commit log.
  - Secure keystore decoding and Google Play Service Account JSON authentication.

---

## 🔮 12. Next Phase Architectural Initiatives & Technical Specifications

### 🏆 Ranked Roadmap Priority Matrix

| Priority | Task | Target Scope | User Value & Engineering Rationale |
| :---: | :--- | :--- | :--- |
| **P1** | **Task 28: Sub-National Administrative Explorer** | ADM1 (Provinces/States) + ADM2 (Regencies/Cities) | **Highest Exploration Depth**. Allows users to tap into any country (e.g., Indonesia $\to$ 38 Provinces $\to$ Kabupaten/Kota) and highlight real sub-national boundaries on the 3D globe. Combines GeoNames + geoBoundaries. |
| **P2** | **Task 29: Planetary Time Machine** | Earth 24h & 365-Day Seasonal Scrubber | **Highest Visual/Aesthetic Impact**. Extends Moon timeline scrubbing to Earth. 100% mathematical with zero external API dependencies; live night-light cluster illumination and seasonal polar tilt. |
| **P3** | **Task 32: Global Search & Teleportation HUD** | Fuzzy Search Bar in TopBar | **High Navigation Utility**. Enables instantaneous teleportation to any country, capital, landmark, or province with smooth 3D camera auto-orbit and auto-zoom. |
| **P4** | **Task 30: Geological & Cartographic Layers** | Tectonic Plates + Submarine Cables + Timezones | **Rich Planetary Symbology**. Adds interactive static GeoJSON layers (~100-200 KB) correlating the Ring of Fire with live NASA EONET volcano/earthquake events. |
| **P5** | **Task 27: Dynamic Feature Delivery for Gamification** | Play Feature Delivery (`:feature:gamification`) + WASM Asset Streaming | **App Binary Size Optimization**. Decouples Flight Missions, Quiz databases, and SFX into an on-demand download module to keep base app payload small. |
| **P6** | **Task 31: 3D Mars Explorer** | Page 2 in HorizontalPager | **Planetary Expansion**. Reuses `EarthGLRenderer` / `PlanetWebGLRenderer` with NASA JPL 2K Viking texture and interactive rover landing site beacons (*Perseverance*, *Curiosity*). |
| **Backlog** | **Haptic Feedback Engine (§6.3)** | Haptic Engine | Low Priority for now. Platform-specific tactile clicks on rotation/borders. |
| **Backlog** | **Moon Axial Tilt & Libration (§1.4)** | Astronomical Parity | Low Priority for now. $\pm 1.5^\circ$ subsolar latitude Y-component and $\pm 7^\circ$ libration wobble. |

---

### 12.1 Dynamic Feature Delivery Architecture for Gamification (Priority P5)
- **Objective**: Keep the core app download size minimal by packaging all Gamification modes (Quiz, Flight Missions, Audio SFX, Trivia Databases, Trophy Assets) into an on-demand dynamic module.
- **Android Implementation (Play Feature Delivery)**:
  - Uses the `com.android.dynamic-feature` Gradle plugin with `:feature:gamification`.
  - Driven by Google Play Core `SplitInstallManager`.
  - On user tap: checks `installedModules`. If missing, presents a transparent confirmation dialog with download size, streams the split APK with progress bar, and loads dynamically into the running ClassLoader without app restart (`SplitInstallHelper.updateAppInfo()`).
- **WasmJS Equivalent (On-Demand Asset Pack Streaming)**:
  - Because Kotlin/Wasm compiler outputs a single monolithic binary, the equivalent web best practice streams assets asynchronously via Ktor/HTTP.
  - Downloads the trivia database, SFX audio, and mission vectors on-demand.
  - Persists assets permanently in the browser's `CacheStorage` / IndexedDB API so subsequent launches are instantaneous.
- **Unified KMP Interface**:
  - `FeatureDeliveryManager` defined in `commonMain` so Compose UI displays identical confirmation prompts, progress indicators, and cancel/retry controls on all platforms.

---

### 12.2 Sub-National Administrative Hierarchy (ADM1 & ADM2 Focused) (Priority P1)
- **Scope Decision & Level Boundaries**:
  - **ADM1 (Primary)**: First-order administrative divisions — Provinces, States, Prefectures, Regions, Departements, Cantons (e.g., *Jawa Barat*, *California*, *Bavaria*, *Tokyo*, *Île-de-France*).
  - **ADM2 (Secondary)**: Second-order administrative divisions — Regencies, Municipalities, Counties, Districts, Cities (e.g., *Kota Bandung*, *Kabupaten Bogor*, *Los Angeles County*, *Munich*).
  - **ADM3 & ADM4 (Subdistricts / Villages / Kelurahan / Desas)**: **Strictly SKIPPED**.
    - *Rationale*: Millions of micro-polygons globally create 50MB+ payload overhead, irregular definitions across nations, severe browser/mobile memory bloat, and UI clutters at planetary scale. ADM2 provides the ideal fidelity.
- **Hybrid Architecture (Option A + Option B Integration)**:
  1. **Option A — GeoNames Web Services (`api.geonames.org`) for Metadata & Hierarchy**:
     - **Endpoints**:
       - `http://api.geonames.org/childrenJSON?geonameId={countryGeonameId}` (lists all ADM1 entities).
       - `http://api.geonames.org/childrenJSON?geonameId={adm1GeonameId}` (lists constituent ADM2 entities).
     - **Extracted Fields**: Official English name, native/local name, ISO-3166-2 code, administrative capital city, population, and exact centroid coordinates (`lat`, `lng`).
     - **Role**: Powers the fast, searchable hierarchy navigation tree inside `CountryDossierSheet.kt` and provides precise camera target coordinates for smooth auto-centering.
  2. **Option B — geoBoundaries (`geoboundaries.org` - William & Mary) for Vector Polygons**:
     - **Open License**: CC BY 4.0 standardized global boundary vectors.
     - **Endpoints**:
       - `https://www.geoboundaries.org/api/current/gbOpen/{ISO3}/ADM1/` $\to$ Returns metadata + direct CDN download URL for simplified GeoJSON.
       - `https://www.geoboundaries.org/api/current/gbOpen/{ISO3}/ADM2/` $\to$ Returns metadata + direct CDN download URL for ADM2 GeoJSON.
     - **Role**: Supplies coordinate boundary polygon rings for Canvas rendering.
- **Data Models & Caching Strategy**:
  - **Data Class**:
    ```kotlin
    data class AdministrativeDivision(
        val id: String,
        val code: String, // e.g. "ID-JB"
        val name: String, // e.g. "Jawa Barat"
        val level: AdminLevel, // ADM1 or ADM2
        val parentCode: String,
        val capital: String?,
        val population: Long?,
        val centroidLat: Double,
        val centroidLng: Double,
        val boundaryPolygons: List<List<Pair<Double, Double>>> = emptyList()
    )
    ```
  - **Stale-While-Revalidate Caching**:
    - Hierarchy and boundary GeoJSON cached in memory and indexed by `[ISO3]_[LEVEL]` with 24-hour expiration in `GlobeRepository.kt`.
    - Simplified geometry formats used (< 500 KB per country) to guarantee 60fps rendering without stalling main UI threads.
- **Interactive UI/UX Flow**:
  1. **Country Dossier Sheet**: User taps a country (e.g., Indonesia). An "Administrative Divisions" tab or button is displayed.
  2. **Provinces List (ADM1)**: Displays list of 38 Provinces with badges (population, capital).
  3. **Province Selection (e.g. Jawa Barat)**:
     - 3D Globe camera smoothly animates and centers on West Java centroid (`lat: -6.9, lng: 107.6`) with calibrated zoom (~3.5x).
     - Globe Canvas renders the luminous boundary outline and subtle translucent neon fill for that province.
     - Dossier sheet drills down to show its 27 constituent Kabupaten & Kota (ADM2).
  4. **City/Regency Selection (e.g. Kota Bandung)**:
     - Camera auto-zooms into the city/regency with localized telemetry card.
     - Highlight mesh shifts to the selected regency.

---

### 12.3 Planetary Time Machine (Earth 24h & Seasonal Scrubber) (Priority P2)
- **Concept**: Extend the Moon's timeline scrubber to Planet Earth with a 24-hour diurnal and 365-day seasonal time slider.
- **Visual Dynamics**:
  - Solar terminator smoothly sweeps across continents in real time as the slider is dragged.
  - City light clusters illuminate sequentially across time zones (Asia $\to$ Europe $\to$ Americas).
  - Real-time seasonal axial tilt simulation (visualizing Arctic midnight sun vs Antarctic polar night).
- **Implementation**: 100% mathematical using existing `AstronomyMath.calculateSunPosition(epochMillis)`. Zero external network dependencies.

---

### 12.4 Geological & Infrastructure Cartographic Layers (Priority P4)
- **Tectonic Plates & The Ring of Fire**:
  - Open-source GeoJSON (`PB2002` / USGS, ~115 KB) rendering fault lines (convergent, divergent, transform).
  - Visually correlates with NASA EONET volcanic and earthquake crisis beacons.
- **Global Submarine Internet Cables**:
  - TeleGeography open dataset (~180 KB GeoJSON) mapping 550+ transoceanic fiber-optic cable lines and landing stations.
- **World Timezone Meridians**:
  - UTC-12 to UTC+14 longitudinal bands with live local solar clocks displayed over each zone.

---

### 12.5 The Red Planet — 3D Mars Explorer (Page 2) (Priority P6)
- **Concept**: Expand HorizontalPager to 3 pages: `[ 🌍 Earth | 🌕 Moon | 🔴 Mars ]`.
- **Implementation**: Reuses existing `EarthGLRenderer` / `PlanetWebGLRenderer` with NASA JPL 2K Viking MDIM 2.1 equirectangular map.
- **Landmarks**: Interactive beacons for Olympus Mons, Valles Marineris, and Rover landing sites (*Perseverance*, *Curiosity*, *Opportunity*, *Spirit*).

---

### 12.6 Global Search & Teleportation HUD (Priority P3)
- **Concept**: Search bar in Mission Control top bar supporting fuzzy search across country names, capital cities, ISO codes, and landmarks.
- **Action**: Tap on search result executes smooth 3D camera auto-orbit, centering, zoom calibration, and dossier display.

---

### 12.7 Backlog Status Notes
- **Haptic Feedback Engine (§6.3)**: Categorized as Low Priority for now.
- **Moon Axial Tilt & Libration Wobble (§1.4)**: Categorized as Low Priority for now.

---

## 🛠️ 13. System Audit, UX Polish & Diagnostic Analysis (Testing Feedback)

### 13.1 Moon Apollo Site Highlight Marker Rotation Fix (Task 33 — 🐛 Bug / P0)
**Status: ✅ DONE**
- **Root Cause Identified**:
  - In `MoonView.kt` (lines 352–362), the forward projection helper `project()` incorrectly used negated angles `(-state.rotationX)` and `(-state.rotationY)` and inverted the Euler transform order (`rotateY` first, then `rotateX`).
  - This was accidentally inverted from the tap unprojection math. In contrast, `EarthGLRenderer.kt` and `GlobeView.kt` evaluate `rotateX` first, then `rotateY` using positive rotation angles.
  - As a result, when rotating the Moon on screen, the Apollo site markers drifted or rotated in the wrong relative direction rather than sticking to their lunar surface coordinates.
- **Resolution**:
  - Corrected `MoonView.kt` forward projection to use positive `state.rotationX`, `state.rotationY`, and execute `rotateX` followed by `rotateY`, restoring 1:1 synchronization with the underlying OpenGL sphere.

### 13.2 Dossier Timezone & "19:49 Solar" Explanation (Task 34 — ⚠️ UX / P1)
**Status: ✅ DONE**
- **What "19:49 Solar" Means**:
  - It is **Local Mean Solar Time** derived from the country's geographical centroid longitude ($L$).
  - For Indonesia, the geographic center is approximately $118^\circ\text{E}$ (in the Makassar Strait between Kalimantan and Sulawesi).
  - Since Earth rotates $15^\circ$ per hour, $118^\circ / 15^\circ \approx +7.86\text{ hours}$ ahead of UTC. At 11:52 UTC (18:52 WIB), the true solar position at that longitude is $\approx 19:43$ to $19:49$ Solar.
- **UX Issue**:
  - Users expect **Civil Standard Timezones** (e.g. Western Indonesia Time `WIB (UTC+7)` in Jakarta, `WITA (UTC+8)` in Bali, `WIT (UTC+9)` in Papua). Raw solar time creates confusion without clear civil context.
- **Resolution**:
  - Display primary civil standard time badge: `Daylight • 19:46 (UTC+07:00)` along with a `"${country.timezones.size} Timezones"` chip.
  - Relocated local mean solar time to an explanatory sub-tag (`Solar: ~$localSolarTimeStr`) within Center Coordinates.

### 13.3 Earth Globe Render Optimization vs. Moon Smoothness (Task 35 — ⚡ Perf / P1)
**Status: ✅ DONE**
- **Root Cause Identified**:
  - **Moon View is Buttery Smooth** because `MoonView.kt` only renders 6 Apollo landing sites and background stars on its Canvas. The OpenGL sphere handles the rest with zero CPU overhead.
  - **Earth View Drops Frames** because `GlobeView.kt` iterates through **all 250+ countries and thousands of polygon vertices every single frame** inside `onDraw`:
    1. Evaluates double-precision `latLngToCartesian`, `rotateX`, `rotateY`, and dot products with `sunVector` for thousands of border segments.
    2. Allocates hundreds of temporary `TransitionSegment` and `Offset` objects per frame, triggering continuous Garbage Collection (GC) pauses on Android.
- **Resolution**:
  - Added fast back-face country center culling (`if (cp.z < -0.45) return@forEach`), eliminating ~60% of country polygon iterations per frame.
  - Pre-allocated and remembered `daylightBordersPath` and `nightBordersPath`.
  - Inlined twilight border segment rendering with direct `drawLine`, completely eliminating temporary `TransitionSegment` allocations in `onDraw`.

### 13.4 Dossier Bottom Sheet Frame Drop & Scroll Sluggishness (Task 36 — ⚡ Perf / P1)
**Status: ✅ DONE**
- **Root Causes**:
  1. **Debug Mode Overhead**: In Android Compose `debug` builds, compiler optimizations and R8 are disabled, recomposition tracking hooks are active, and debug runtime checks add overhead (often 3x–8x slower than Release mode).
  2. **Un-recycled Vertical Column**: `CountryDossierSheet.kt` uses `Column(Modifier.verticalScroll())`. Every child card, badge, canvas, and sparkline is measured and rendered simultaneously without view recycling.
  3. **Concurrent Background Rendering**: The 3D globe underneath and continuous transitions in `WeatherAtmosphericOverlay` run concurrently during scrolling.
- **Resolution**:
  - Unified 6 concurrent infinite transition loops in `WeatherAtmosphericOverlay` into a single master clock (`weatherMasterClock`).
  - Pre-allocated and reused `heatWavePath` buffer via `.reset()`, eliminating allocation churn.

### 13.5 Mission Control TopBar UX Revamp (Task 37 — 🎨 UX / P2)
**Status: ✅ DONE**
- **UX Bottleneck**:
  - On typical Android phone widths (360dp–400dp), the TopBar tries to fit:
    - Live UTC + Local Time status badge (~100dp)
    - Celestial Switcher Capsule `[ 🌍 Earth | 🌕 Moon ]` (~130dp)
    - Legend button + Layers button with badge (~150dp)
  - This exceeds screen width, causing layout cramming and visual clutter.
- **Resolution**:
  - Compacted TopBar: Replaced text button `[Legend]` with sleek circular icon button `[ℹ️]` (`size(34.dp)`).
  - Replaced intrusive top dropdown menu with a native Material 3 `ModalBottomSheet` for Layers, providing thumb-friendly switch toggles and clean visual spacing.

### 13.6 Flight Simulator Aircraft Model & Speed Tuning (Task 38 — ✈️ Visual / P2)
**Status: ✅ DONE**
- **Issues Identified**:
  - **Speed**: The animation loop was set to 4.5 seconds, darting too quickly to appreciate across global arcs.
  - **Visual Model**: Delta-wing triangle resembled a cursor rather than an aircraft.
- **Resolution**:
  - Slowed flight duration from 4,500ms to **14,000ms** (14 seconds) for realistic cruising.
  - Designed an authentic commercial airliner vector silhouette (aerodynamic fuselage, swept wings, dual underwing turbofan engine nacelles with cyan pulse, cockpit windscreen, and FAA-standard blinking wingtip strobes).

### 13.7 AI Prompt-to-Animation Architecture & MCP Evaluation (Task 39 — 🎬 Arch / P3)
- **Objective**: Establish a seamless workflow where the user simply types a natural language prompt (e.g. *"create a realistic storm cloud with branching lightning strikes"*, *"airplane flying with glowing jet contrails"*, or *"golden trophy bursting with confetti"*), and an AI tool/MCP automatically generates a production-ready vector animation.

#### 1. The Official Lottie MCP Solution (Free & Recommended)
LottieFiles provides two official Model Context Protocol (MCP) servers:
1. **Lottie Creator MCP (`npx @lottiefiles/creator-mcp@latest`)**:
   - **How it works**: Connects directly to our AI assistant. When the user describes what animation they want, the AI sends structured commands through the MCP bridge to the Lottie Creator engine, which automatically builds the vector shapes, sets keyframes, adjusts easing curves, tunes timing, and exports the production `.json` or `.lottie` file into the repository!
   - **Pricing**: **100% FREE** for core creator usage. No subscription or credit card needed.
2. **LottieFiles Hosted MCP (`https://mcp.lottiefiles.com/mcp`)**:
   - **How it works**: A remote hosted MCP server that queries 100,000+ free community animations.
   - **Prompt Workflow**: *"Find and download a weather radar pulse animation"* $\to$ AI queries the library via MCP and imports the `.lottie` file directly into `composeResources`.
   - **Pricing**: **100% FREE** for public community assets.

#### 2. Pricing & Platform Comparison for Text-to-Animation

| Platform / Tool | Type | Pricing / Tier | AI Prompt-to-Animation Quality | KMP (Android + Wasm) Support |
| :--- | :--- | :--- | :--- | :--- |
| **Lottie Creator MCP** | Local MCP Server | **100% Free** | High (builds vector keyframes & layers directly from prompts) | **Native** via Compottie |
| **LottieFiles Hosted MCP** | Cloud MCP Server | **Free** (100k+ assets) | High (retrieves & adapts existing animations) | **Native** via Compottie |
| **Direct AI Code Synthesis** (Claude/Antigravity) | Procedural Canvas / Lottie JSON | **Free** (included with agent) | Medium-High (good for math/particle effects, harder for characters) | **Native** (pure Kotlin/Compose) |
| **Jitter.video AI** | Cloud Web Tool | Freemium ($14/mo for 4K/Lottie export) | High (prompt-to-motion) | Export requires paid tier |
| **SVGator AI** | Cloud Web Tool | Freemium ($16–$28/mo for mobile/web export) | High | Export requires paid tier |
| **Rive (`rive.app`)** | Interactive Runtime | Free for 3 files ($32/mo Pro) | State-of-the-art interactive state machines | Complex WasmJS binding required |

#### 3. How the End-to-End Workflow Operates in Countries Compose
1. **MCP Configuration**: Configure `mcp_config.json` with `@lottiefiles/creator-mcp`.
2. **User Command**: The user says: *"Create an animated weather icon for thunderstorms with branching electric lightning and dark drifting rain clouds"*.
3. **Automatic Generation**: The AI instructs the MCP to generate the animation in Lottie format and saves `weather_thunderstorm.lottie` into `app/src/commonMain/composeResources/files/animations/`.
4. **Cross-Platform Playback**:
   - In `commonMain`, **Compottie** (`io.github.alexzhirkevich:compottie`) renders the animation seamlessly on both Android and WebAssembly Canvas:
     ```kotlin
     val composition by rememberLottieComposition {
         LottieCompositionSpec.JsonString(Res.readBytes("files/animations/thunderstorm.json").decodeToString())
     }
     LottieAnimation(composition, progress = { progress }, modifier = Modifier.size(64.dp))
     ```
   - **Result**: Hardware-accelerated, scalable vector animations with zero subscription costs.

#### 4. Previewing Animations Without Running the App
- **Method A — In-Chat HTML Artifact (Zero Setup)**: Whenever an animation is generated or downloaded, the AI assistant creates a self-contained interactive HTML player artifact. The user can scrub, play/pause, adjust speeds (0.5x, 1x, 2x), and inspect colors directly inside the IDE chat window before writing code.
- **Method B — IDE Plugin**:
  - *VS Code / Antigravity IDE*: Install the **"Lottie Preview"** or **"LottieFiles"** extension. Clicking any `.json` or `.lottie` file in the project explorer opens an interactive preview tab right inside the editor.
  - *Android Studio / IntelliJ*: Install **"Lottie Animation Preview"** from JetBrains Marketplace.
- **Method C — Web Drag-and-Drop**: Drop any `.json` or `.lottie` file directly into **[lottiefiles.com/preview](https://lottiefiles.com/preview)** to inspect frame rates, vector layers, and timing at 60 FPS.

#### 5. Animated Icon Ecosystems for Micro-Interactions
- **[Lordicon](https://lordicon.com/)**: 16,000+ interactive animated UI icons (weather, navigation, search, space, flights). Offers 2,500+ free icons with customizable stroke widths, colors, and animation triggers (loop, hover, click). Exports directly to Lottie JSON.
- **[UseAnimations](https://useanimations.com/)**: 100% free open-source (MIT) minimalist animated icons for app micro-interactions (morphing hamburger-to-back arrows, search ripples, checkmarks).
- **LottieFiles Icon Packs**: Free community sets for dynamic weather conditions, space exploration, and travel.

---

### 13.8 Static Icons, Logos & App Icon Architecture (Task 40 — 🎨 Design / P3)
- **Objective**: Establish an automated, prompt-driven pipeline for generating static logos, branding assets, system icons, and Android Adaptive App Icons without needing complex manual graphic design software.

#### 1. AI Prompt-to-Logo & Vector Generation
- **Built-in Agent Image Synthesis (`generate_image` / Imagen 3)**:
  - Generates photorealistic or sleek vector-style app logos, promotional branding banners, and feature graphics directly from prompts (e.g. *"Minimalist futuristic glowing 3D Earth globe with thin orbital gold satellite ring, dark obsidian glassmorphism, 1:1 app icon"*).
- **[Recraft.ai](https://recraft.ai/) (Prompt-to-SVG Vector)**:
  - Generates clean, layered, scalable **vector SVG files** from text prompts with customizable palettes and styles (flat vector, icon, 3D clay, line art).
  - Free tier offers daily generation credits and commercial licensing rights.
- **Open-Source Static Icon Sets**:
  - **[Lucide Icons](https://lucide.dev/)**: 1,500+ ultra-clean, modern geometric SVG icons (100% free / ISC license).
  - **Google Material Symbols**: Official variable vector icons with optical weight, grade, and fill controls.

#### 2. Android Adaptive Icon & Themed Icon Pipeline
- **[IconKitchen](https://iconkitchen.com/) (by Roman Nurik / Google Android Dev)**:
  - **100% Free & Open-Source** web tool tailored specifically for modern Android standards.
  - **Input**: Provide the generated SVG logo or PNG graphic.
  - **Output Assets**:
    1. **Adaptive Foreground Layer** (`ic_launcher_foreground.xml` or `.png`): Centered branding icon with safe-zone margin.
    2. **Adaptive Background Layer** (`ic_launcher_background.xml`): Dynamic radial gradient or solid cosmic dark slate.
    3. **Monochrome Themed Icon Layer** (`ic_launcher_monochrome.xml`): High-contrast silhouette layer supporting **Android 13+ Material You dynamic color theming** on Google Pixel and modern Android devices.
    4. **Legacy & Web Assets**: Generates `mipmap-xxxhdpi` icon sets, circular icons, web `favicon.ico`, and Apple touch icons in a single download bundle.

---

### 13.9 Moon Zoom Lighting & Highlight Clipping Fix (Task 41 — 🐛 Bug / P0)
**Status: ✅ DONE**
- **Root Cause Identified**:
  - In `GlobeShaders.kt`, Moon fragment lighting multiplied `dayColor.rgb` by `(diffuse * 1.1)`. Because high-albedo lunar highlands in the NASA LRO texture already reach 0.85–0.95 brightness, multiplying by 1.1 pushed values well past 1.0 (white clipping), causing an intense, blinding white washout across the screen when zooming in.
- **Resolution**:
  - Re-calibrated Moon photometric reflectance curve to match real lunar regolith (albedo ~0.12).
  - Clamped diffuse to `0.78` max: `float lunarLight = mix(0.04, 0.78, diffuse);`
  - Added power contrast curve `pow(litMoon, vec3(1.10))` to preserve deep crater shadows and geological topography even at maximum zoom without white clipping.

### 13.10 Modal Bottom Sheet Frame Drop Elimination (Task 42 — ⚡ Perf / P1)
**Status: ✅ DONE**
- **Root Cause Identified**:
  - `GLSurfaceView` on Android was set to `RENDERMODE_CONTINUOUSLY`, forcing a dedicated OpenGL thread to render 60/120 FPS continuously in an infinite loop. When a `ModalBottomSheet` opened, Android's SurfaceFlinger had to composite the transparent GL overlay buffer behind the sheet scrim while Compose was animating the slide-up.
  - Simultaneously, `GlobeView.kt` was running continuous infinite transitions for flights/strobes, redrawing the entire 2D Canvas every frame.
- **Resolution**:
  - Converted `GLSurfaceView` to `RENDERMODE_WHEN_DIRTY`. The 3D sphere now only renders on camera motion, texture load, or phase angle updates, reducing stationary GPU usage to 0%.
  - Added `isSheetOpen: Boolean` gating across `App.kt` and `GlobeView.kt`. When any bottom sheet is open, background Canvas transitions and OpenGL frames pause completely, giving Compose 100% CPU/GPU resources for a silky smooth 120 FPS sheet slide animation.

### 13.11 Minimalist Close Button Design System (Task 43 — 🎨 Design / P2)
**Status: ✅ DONE**
- **Root Cause Identified**:
  - Disparate close buttons across sheets: raw text `"✕"` characters looked misaligned and chunky, while previous canvas implementations had heavy 2dp strokes and thick dark borders.
- **Resolution**:
  - Created a unified `MinimalistCloseButton.kt` component: `size(28.dp)`, translucent frosted circular pill (`Color(0x22FFFFFF)`), and a delicate 1.35dp stroke cross with rounded caps.
  - Applied across all 11 sheets and cards (`CountryDossierSheet`, `MeteorologyStationSheet`, `WorldBankDashboardSheet`, `NasaCrisisMonitorSheet`, `MissionLegendSheet`, `CartographicLayersSheet`, `HazardDetailSheet`, `ISSTelemetryCard`, `MoonDetailSheet`, `FlightRouteHudCard`, and Apollo landing site cards).

### 13.12 Supersonic Flight Simulator Physics & Clarification (Task 44 — ✈️ Visual / P2)
**Status: ✅ DONE**
- **Clarification**:
  - "Engage Supersonic" toggles between standard commercial subsonic cruising (850 km/h / Mach 0.78, ~14h long haul) and Supersonic Transport (SST Concorde, 2,335 km/h / Mach 2.2, ~3.5h long haul).
- **Resolution**:
  - Wired `isSupersonic` state to `GlobeView.kt`: When supersonic mode is active, the aircraft speeds up to a fast **5.5s** cruise across the globe and emits glowing red/amber Mach 2.2 supersonic shockwave contrails.
  - Updated button label and card subtitle with explicit metrics: `"⚡ Mach 2.2 Concorde (2,335 km/h • Fast SST)"` vs `"🚀 Subsonic Flight (850 km/h • Standard)"`.

### 13.13 Comprehensive & Consistent Bottom Sheet Information (Task 45 — 🎨 UX / P2)
**Status: ✅ DONE**
- **Resolution**:
  - Standardized header hierarchy: Category pill/tag with letter-spacing, bold title, and `MinimalistCloseButton`.
  - Unified rounded glassmorphic card containers, consistent drag handle styling, and rich metadata chips across all sheets.

### 13.14 Orchestrated Country Selection Flight Flow (Task 46 — 🎬 UX / P1)
**Status: ✅ DONE**
- **Issue**:
  - Tapping a country previously displayed the bottom sheet immediately on frame 0 while the camera was still rotating in the background, obscuring the globe and causing visual clash.
- **Resolution**:
  - Orchestrated a two-phase flow in `App.kt`:
    1. Country selected $\to$ Camera auto-glides and rotates the 3D globe to place the country dead-center on the screen.
    2. Background details (weather and macroeconomic data) are fetched in parallel.
    3. Once the camera finishes centering, the `CountryDossierSheet` smoothly slides up with all details loaded!
### 13.15 3D Globe Country Centering & Zoom-Dependent Offset Fix (Task 47 — 🐛 Bug / P0)
**Status: ✅ DONE**
- **Issue**:
  - Tapping a country or triggering auto-centering caused high-latitude countries (e.g. UK, France, Norway, New Zealand) to rotate off-center, with the displacement drastically worsening as zoom increased.
- **Mathematical Root Cause**:
  - The transformation previously applied pitch ($R_X$) *first* and yaw ($R_Y$) *second*.
  - Because longitude is a rotation around Earth's polar $Y$-axis, applying $R_X$ first tilted the polar axis in camera space, causing subsequent yaw rotations to spin around a tilted axis.
  - This introduced an off-axis displacement of $\Delta x = R \sin(\lambda) (\cos(\phi) - 1)$. For equatorial countries ($\phi \approx 0^\circ$), $\cos(\phi) = 1$, giving $\Delta x = 0$. However, for countries at latitudes $\phi \approx 45^\circ$–$60^\circ$, $\Delta x$ was $-0.3R$ to $-0.5R$. As zoom level ($R$) increased by 2x–4.5x, this displacement magnified into hundreds of pixels off-center!
- **Resolution**:
  - Reordered the transformation pipeline to **yaw first ($R_Y$), then pitch second ($R_X$)**:
    1. Rotating around Earth's polar axis by $R_Y(-lng)$ aligns the country with the prime meridian ($x_1 = 0, y_1 = R \sin(lat), z_1 = R \cos(lat)$).
    2. Rotating around the camera horizontal axis by $R_X(lat)$ tilts the meridian to screen center ($x_2 = 0, y_2 = 0, z_2 = R$).
    3. Result: **Screen coordinates are exactly $(0.0, 0.0)$ for any country at any zoom level**.
  - Synchronized across all 4 rendering and projection layers:
    1. `EarthGLRenderer.kt`: Model matrix multiplies $R_X \times R_Y$, which applies $R_Y$ first to vertex coordinates.
    2. `PlanetWebGLRenderer.kt`: Aligned WebGL model matrix to apply $R_Y$ first then $R_X$.
    3. `GlobeView.kt`: All forward vector projections (borders, highlight polygons, flights, hazards, ISS) now apply `rotateY` first then `rotateX`. Tap unprojection inverts the transformation with `rotateX(-rotX)` first then `rotateY(-rotY)`.
    4. `MoonView.kt`: Apollo landing site hit detection and beacon projections updated to `rotateY` first then `rotateX`.

---

### 13.16 WASM Icon & Emoji Glyph Rendering Pipeline (Task 48 — 🌐 WASM / P1)
**Status: ⏳ PLANNED**
- **Issue**:
  - On Compose WebAssembly (`wasmJs`), many unicode emojis and symbols render as unrendered empty tofu boxes (`□`) or are invisible.
- **Root Cause**:
  - Compose Multiplatform for WebAssembly uses Skiko Canvas rendering. Unlike native Android (which provides system-level color emoji fonts via `NotoColorEmojiCompat`), browser Canvas/Skia requires explicit font loading or vector icon assets. System fonts vary wildly across host OSes (Windows, macOS, Linux, ChromeOS), leading to missing glyph tables.
- **Implementation Strategy**:
  1. **Option A (Vector Icons)**: Replace unicode emoji strings across HUDs, tabs, and dossier badges with Compose Vector Icons / Lucide SVG vector assets (100% resolution-independent, crisp, guaranteed to render on all platforms).
  2. **Option B (WebAssembly Font Bundling)**: Configure `wasmJs` font family resolution in `App.kt` / `theme` to bundle a lightweight subset of Noto Color Emoji or Twemoji web font via Compose Resources.
  3. **Priority Areas**:
     - Weather condition icons (rain, clouds, lightning, sun)
     - Mission Control top bar badges (satellite, radiation, radar, layers)
     - Country Dossier stat chips (population, area, currency, language)
     - Moon phase badges and Apollo landing sites

---

### 13.17 Adaptive Large Screen & Desktop Responsive Side Sheet (Task 49 — 📱 Responsive / P1)
**Status: ⏳ PLANNED**
- **Issue**:
  - On larger screens (desktop browsers on WASM, tablets, foldables, and landscape mode), full-width bottom sheets look stretched, block the entire viewport, and obscure the 3D globe.
- **Architecture & Implementation Plan**:
  1. **Adaptive Breakpoint Detection**:
     - Use `BoxWithConstraints` or `WindowWidthSizeClass`:
       - Compact (`< 600dp`): Native `ModalBottomSheet` anchored to the bottom.
       - Medium & Expanded (`>= 600dp` / Desktop WASM): Native Material 3 **Side Sheet** (or anchored right panel, `width = 420.dp`) with smooth horizontal slide animation (`slideInHorizontally { it }`).
  2. **Non-Blocking Dual Pane**:
     - In Side Sheet mode, the 3D Globe remains visible and fully interactive on the left, while deep-dive data (Country Dossier, Meteorology, World Bank, EONET Hazards) is displayed in the side panel on the right.
  3. **Unified Scaffolding**:
     - Create an adaptive wrapper `AdaptiveInfoSheet` that swaps between `ModalBottomSheet` (mobile) and `ModalSideSheet` / `PermanentSideSheet` (desktop/tablet) seamlessly based on screen width.

---

### 13.18 Zero-Lag Country Centering & Instant Dossier Presentation (Task 50 — ⚡ Perf / P1)
**Status: ⏳ PLANNED**
- **Issue**:
  - The delay between tapping a country, rotating the globe, and opening the bottom sheet feels too sluggish and takes too long.
- **Root Cause**:
  - In `App.kt`:
    1. `detailsJob.join()` is called before opening the sheet. If network latency to Open-Meteo or World Bank is 800ms–2000ms, the entire UI is blocked waiting.
    2. Camera flight animation in `GlobeState.kt` was set to `1500ms` (zoom) and `1200ms` (rotation) with an extra `120ms` delay.
- **Implementation Strategy**:
  1. **Decouple Network from UI Flow**:
     - Do NOT wait for `detailsJob.join()` before opening the sheet! Open the dossier sheet immediately once the camera flight reaches the destination.
     - Show sleek shimmer/skeleton loaders for weather and economic data while network streams in asynchronously in the background.
  2. **Tune Flight Animation Timing**:
     - Reduce camera auto-glide duration from `1200ms/1500ms` to a snappy **600ms–750ms** with `FastOutSlowInEasing`.
     - Eliminate the redundant `delay(120)`.
  3. **Result**:
     - Instantaneous response: Globe pivots crisply to the selected country in under a second and the dossier immediately slides open.

---

### 13.19 Close Button (X) Design System Revamp & Alternatives (Task 51 — 🎨 Design / P2)
**Status: ⏳ PLANNED**
- **Issue**:
  - User expressed dissatisfaction with the current close button design across bottom sheets.
- **Design Options for User Review**:
  - **Option 1: Modern Borderless Flush Icon**:
    - Remove the circular frosted container completely.
    - Use a clean, sleek 20dp vector cross in muted slate (`#94A3B8`) that brightens to `#F8FAFC` on hover/press with a subtle translucent circular ripple.
  - **Option 2: Minimalist Capsule / Pill Button**:
    - A slim, rounded pill with a subtle chevron or micro-cross: e.g. `24dp` height, dark glass background (`#1E293B`), soft inner glow.
  - **Option 3: Integrated Swipe/Tap Drag-Handle**:
    - A prominent, elegant Material 3 drag handle at the top of the sheet that doubles as a tap-to-dismiss target, eliminating the separate (X) button altogether for a 100% clean sheet header.
  - **Option 4: Floating Top-Right Mini Badge**:
    - Ultra-small 22dp circle with an inverted cross, semi-transparent dark obsidian backdrop.

---

### 13.20 Fix Globe Blackout Bug on Bottom Sheet Dismissal (Task 52 — 🐛 Bug / P0)
**Status: ⏳ PLANNED**
- **Issue**:
  - After dismissing the country dossier bottom sheet, the 3D globe surface turns completely black.
- **Root Cause**:
  - In `Globe3DPlatformView.android.kt`, switching to `GLSurfaceView.RENDERMODE_WHEN_DIRTY` paired with `isPageActive && !isSheetOpen`:
    1. When the sheet is open, `isSheetOpen = true` stops rendering.
    2. When the sheet is dismissed, the OpenGL surface is not automatically instructed to redraw its frame (`requestRender()`), leaving the buffer empty or cleared.
    3. Furthermore, when `ModalBottomSheet` dismisses, Android's `SurfaceFlinger` removes the sheet window/scrim layer; without a forced `requestRender()`, the GL surface displays the cleared black framebuffer.
- **Implementation Strategy**:
  1. Trigger an immediate `requestRender()` upon `isSheetOpen` transitioning from `true` to `false`.
  2. Ensure `GLSurfaceView` retains its EGL context and back-buffer across window scrim transitions (`preserveEGLContextOnPause = true`).
  3. Add a post-dismissal dirty frame request in `LaunchedEffect(isSheetOpen)`.

---

### 13.21 Revamp All Remaining Floating Cards into Unified Sheet System (Task 53 — 🎨 UX / P2)
**Status: ✅ DONE**
- **Completed Migrations**:
  1. **ISS Telemetry Card** (`ISSTelemetryCard.kt`): Converted to Material 3 `ModalBottomSheet` with drag handle, transparent scrim, telemetry tiles, and fly-to camera tracking.
  2. **NASA Hazard Detail Sheet** (`HazardDetailSheet.kt`): Converted to Material 3 `ModalBottomSheet` with category badge, epicenter coordinates, severity gauge, and fly-to epicenter action.
  3. **Flight Route Simulator** (`FlightRouteHudCard.kt`): Converted to Material 3 `ModalBottomSheet` with transparent scrim allowing live observation of the great-circle arc, interactive departure/arrival picker, and instant supersonic toggle.
  4. **Apollo Mission Inspector** (`ApolloMissionSheet.kt`): Replaced floating `AnimatedVisibility` card in `MoonView.kt` with a dedicated Material 3 `ModalBottomSheet`. Added tap-to-inspect `MoonDetailSheet` on Moon header.
  5. **Floating Explorer Bar & Quiz Card**: Removed multi-byte emojis to ensure clean rendering on WASM, unified header with `MinimalistCloseButton`.

---

### 13.22 Flight Simulator Speed Toggle & State Reactivity Fix (Task 54 — ✈️ Visual / P2)
**Status: ⏳ PLANNED**
- **Issue**:
  - When tapping "Subsonic Flight" / "Engage Supersonic", the user observed the airplane animation was not speeding up.
- **Root Cause Analysis**:
  1. In `FlightRouteHudCard.kt`, check whether the button click mutates `isSupersonic` state properly or if it is shadowed by local state.
  2. In `GlobeView.kt`, the aircraft progress loop uses `infiniteTransition.animateFloat(...)` with a duration based on `if (isSupersonic) 5500 else 14000`. In Compose, changing the duration parameter in `infiniteRepeatable` does not automatically restart or accelerate an existing ongoing transition without keying.
- **Implementation Strategy**:
  1. Use `key(isSupersonic)` on the flight transition so toggling modes instantly creates the accelerated 5.5s tween without waiting for the old 14s cycle to finish.
  2. Provide clear visual cues: label clearly toggles between `"⚡ Switch to Mach 2.2 SST"` and `"✈️ Switch to Subsonic"`, with an instantaneous supersonic sonic boom burst effect.









