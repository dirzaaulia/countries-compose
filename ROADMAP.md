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

| # | Task | Section | Status | Notes |
|---|------|---------|--------|-------|
| 1 | Fix Earth↔Moon transition frame drop | §7 | ✅ DONE | Pre-warmed textures, `isPageActive` lifecycle pause/resume |
| 2 | Fix Moon phase hourly refresh | §1.4 | ✅ DONE | Hourly `LaunchedEffect` refresh loop in `MoonView` |
| 3 | Fix hardcoded "384.4K KM" badge in top bar | §3.3 | ✅ DONE | Real `moonInfo.distanceKm` passed to `MissionControlTopBar` |
| 4 | Add EONET hazards periodic refresh | §4.1 | ✅ DONE | 30-min background refresh loop in `App.kt` |
| 5 | Country tap auto-centering & adaptive zoom | §8 | ✅ DONE | Inverted latitude fixed, mainland centroid, calibrated area zoom |
| 6 | Country dossier compact peek mode | §8 | ✅ DONE | Flag + country name peek (68dp), expands smoothly to full sheet |
| 7 | Complete ISS tap → telemetry card + ISS label | §3.2 | ✅ DONE | Tap beacon, live telemetry card, 92-min orbital track, floating badge |
| 8 | EONET magnitude & severity display | §4.3 | ✅ DONE | Parsed from geometry and displayed in HazardDetailSheet |
| 9 | Animated Flight Path aircraft | §5.2 | ✅ DONE | Glowing airplane beacon interpolating along Great Circle arc |

---

## 🌌 1. Deep Space Environment & Celestial Bodies

### 1.1 Procedural Starfield (Canvas Overlay)
**Status: ⚠️ PARTIAL**
- ✅ 260+ deterministic stars on Canvas with twinkling animation (`GlobeView.kt` + `MoonView.kt`)
- ✅ Multi-color star palette: white, blue (`#90CAF9`), yellow (`#FFE082`), warm (`#FFCCBC`)
- ✅ Magnitude-1 stars have soft glow halo
- ✅ Stars masked outside planet disc radius
- ❌ Missing: Milky Way galactic dust lane behind globe
- ❌ Missing: Real star catalog (Yale BSC) — currently deterministic seed, not real coordinates
- ❌ Missing: Sidereal time alignment rotation

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
**Status: ⚠️ PARTIAL / 🐛 BUG**
- ✅ Elongation-based sun vector direction is astronomically correct
- 🐛 **Stale phase on launch**: `now = remember { currentEpochMillis() }` captured once; never refreshes
  - **Fix**: Add `LaunchedEffect(Unit) { while(true) { delay(3_600_000L); refresh() } }` in `MoonView.kt`
- ❌ Missing: Subsolar latitude Y-component (always `y = 0.0`, real Moon has ±1.5° tilt)
- ❌ Missing: Libration ±7° wobble — very low priority

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
**Status: 🐛 BUG**
- `App.kt` recomputes `sunPos` hourly → passes to `GlobeView` → `Globe3DPlatformView`
- 🐛 `EarthGLRenderer.setSunDirection()` only called on Compose recomposition — GL terminator **does not visually update** between recompositions
- **Fix**: Add coroutine timer in `Globe3DPlatformView.android.kt` updating `renderer.sunDirection` directly via `AtomicReference` every hour

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
**Status: ⚠️ PARTIAL**
- ✅ `GlobeRepository.fetchGlobalNasaEvents()` — full parse of 🔥🌀🌋🧊🌊
- ✅ Called once at launch
- ❌ Missing: Periodic refresh (stale after launch)
  - **Fix**: `while(true) { delay(1_800_000L); globalHazards = repo.fetchGlobalNasaEvents() }` in `App.kt`

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
- ⚠️ Missing: Target country silhouette glow visual cue in quiz mode
- ❌ Missing: Haptic/audio feedback

### 5.2 Flight Path Simulator
**Status: ✅ DONE**
- ✅ `FlightRouteHudCard.kt` — origin → destination with km distance
- ✅ `AstronomyMath.calculateGreatCircleArc()` + `calculateGreatCircleDistance()`
- ✅ Glowing amber dashed arc + auto fly-to on activation
- ❌ Missing: Manual country selection (always random)
- ❌ Missing: Animated plane/dot along arc

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
| Max zoom 4.5x clipping | ❌ TODO | Expand near/far planes in `EarthGLRenderer` |
| Multi-res LOD textures | ❌ TODO | 1K/2K/4K switching at high zoom |
| Haptic feedback | ❌ TODO | Country tap, meridian/equator crossing |

---

## ⚡ 7. Earth ↔ Moon Transition — Frame Drop

**Status: 🐛 BUG (diagnosed, not yet fixed)**

### Root Causes
1. **Dual `GLSurfaceView` at 60fps** — Both Earth (3 textures) + Moon render simultaneously during swipe
2. **Cold-start Moon texture decode mid-swipe** — `moon.jpg` decoded in `LaunchedEffect(renderer)` after `AndroidView.factory` fires; spikes CPU during animation
3. **`RENDERMODE_CONTINUOUSLY`** on both — competes with Compose pager animation frame budget
4. **Heavy Canvas recomposition** — 260+ stars + beacons + borders during pager scroll

### Planned Fixes (Priority Order)
| Fix | Approach | Gain |
| :--- | :--- | :--- |
| **Pre-warm Moon texture** | Decode `moon.jpg` at `App.kt` launch, cache `ByteArray` | Moderate |
| **Pause off-screen renderer** | `onPause()` inactive `GLSurfaceView` when its page not visible | High |
| **`RENDERMODE_WHEN_DIRTY` during swipe** | Switch both renderers while `pagerState.isScrollInProgress` | Moderate |
| **Reduce star draw count** | Drop to minimal stars while swiping | Low |
| **Shared EGL context** (future) | Share GL context to avoid duplicate GPU texture uploads | High (complex) |

---

## 📋 8. Backlog / Future Ideas

| Feature | Description | Priority |
| :--- | :--- | :--- |
| **ISS Orbital Ground Track** | 90-min future pass arc on globe | Medium |
| **ISS Telemetry Tap Card** | Tap beacon → velocity, altitude, country, visibility | Medium |
| **ISS Crew Count** | Open Notify API crew count in ISS detail | Low |
| **Animated Flight Plane** | Dot animates along Great Circle arc | Medium |
| **Manual Flight Route** | User picks origin + destination | Medium |
| **EONET Magnitude Display** | Show `magnitudeValue` in `HazardDetailSheet` | Low |
| **Quiz Country Silhouette Glow** | Target country glow differs from normal selection | Low |
| **Capital Cities 3D Pins** | Pins for world capitals + local time | Medium |
| **Milky Way Skybox** | Galactic dust lane behind globe | Low |
| **Real Star Catalog (Yale BSC)** | True star positions + sidereal rotation | Low |
| **Moon Libration** | ±7° face wobble | Very Low |
| **Full Rayleigh+Mie Atmosphere** | Nishita/Bruneton scattering model | Low |
