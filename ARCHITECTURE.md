# 🏛️ Architecture Guide — Countries Compose

> Technical architecture, render pipeline, and module guide for **Countries Compose**.
> All AI agents and developers must consult this document before proposing architectural or structural changes.

---

## 1. High-Level System Overview

Countries Compose is an interactive 3D planetary exploration and geopolitical intelligence workstation built with **Compose Multiplatform (CMP)** targeting **Android** and **WebAssembly (WasmJs)**.

```mermaid
graph TD
    UI[App.kt / Mission Control HUD] --> Page[HorizontalPager]
    Page --> Page0[Page 0: GlobeView - Earth]
    Page --> Page1[Page 1: MoonView - Moon]

    subgraph Dual-Layer Rendering Engine
        Page0 --> CanvasLayer[Compose Canvas Vector Overlay (2D)]
        Page0 --> GLView[Globe3DPlatformView (expect/actual)]
        GLView -->|Android| AndroidGL[EarthGLRenderer.kt - OpenGL ES 2.0]
        GLView -->|WasmJs| WebGL[PlanetWebGLRenderer.kt - WebGL]
    end

    subgraph Data & Telemetry Services
        Repo[GlobeRepository.kt] --> KtorClient[Ktor HTTP Client]
        KtorClient --> EONET[NASA EONET]
        KtorClient --> ISS[WhereTheISS.at]
        KtorClient --> Weather[Open-Meteo API]
        KtorClient --> WorldBank[World Bank Data]
        Repo --> SWRCache[(In-Memory SWR Cache + Offline Fallbacks)]
    end

    subgraph Inspector & Sheet System
        UI --> M3Sheets[Material 3 ModalBottomSheet System]
        M3Sheets --> Dossier[Country Dossier]
        M3Sheets --> ISSCard[ISS Telemetry Sheet]
        M3Sheets --> HazardSheet[NASA EONET Sheet]
        M3Sheets --> FlightSheet[Flight Simulator HUD Sheet]
        M3Sheets --> ApolloSheet[Apollo Site Mission Sheet]
    end
```

---

## 2. Dual-Layer Hybrid Rendering Engine

A key technical innovation in Countries Compose is the **synchronized dual-layer engine**:

| Layer | Technology | Responsibilities | Location |
|---|---|---|---|
| **Layer 0 (Underneath)** | Hardware-accelerated OpenGL ES 2.0 (Android) / WebGL (WasmJs) | Photorealistic textured 3D sphere, day/night solar terminator shading, atmospheric limb scattering, 2K/4K planetary textures | `EarthGLRenderer.kt`, `PlanetWebGLRenderer.kt` |
| **Layer 1 (On Top)** | Jetpack Compose `Canvas` | High-frequency vector overlays: country borders, ISS orbital path & position beacon, animated flight routes, Apollo markers, dynamic weather particles | `GlobeView.kt`, `MoonView.kt` |

### Synchronization & Projection Math
Both layers share identical camera state:
- `rotationX`: Pitch angle (degrees)
- `rotationY`: Yaw angle (degrees)
- `zoom`: Scale factor

In `GlobeView.kt`, the vector overlay uses `forwardProject(lat, lon, rotationX, rotationY, zoom, radius, center)` to transform geographic coordinates into 2D screen space:
- **Euler Order**: Yaw-first ($R_Y \to R_X$). This prevents latitude drift when zooming or auto-centering.
- **Back-Face Culling**: Polygons whose surface normals face away from the camera ($z \le 0$) are culled before rendering to maintain 60 FPS.

---

## 3. Directory & Source Set Layout

```text
countries-compose/
├── AGENTS.md                                   # Strict developer and agent behavioral rules & invariants
├── ARCHITECTURE.md                             # Technical architecture manual (this file)
├── ROADMAP.md                                  # Single source of truth for features, bugs & tasks
├── gradle/libs.versions.toml                   # Gradle Version Catalog
└── app/src/
    ├── commonMain/kotlin/com/dirzaaulia/countries/
    │   ├── App.kt                              # Main entrypoint, HorizontalPager, state coordination
    │   ├── GlobeView.kt                        # Earth page: Canvas overlay, camera gesture handling
    │   ├── MoonView.kt                         # Moon page: Apollo markers, timeline scrubber
    │   ├── AstronomyMath.kt                    # Subsolar points, orbital mechanics, terminator math
    │   ├── GeoJsonModels.kt                    # MultiPolygon country border models & decoders
    │   ├── GlobeRepository.kt                  # Ktor HTTP client, SWR cache & procedural fallbacks
    │   ├── GlobeShaders.kt                     # Shared GLSL vertex & fragment shader code
    │   ├── ui/
    │   │   ├── components/
    │   │   │   └── MinimalistCloseButton.kt    # Unified standard close button (cross-platform vector)
    │   │   ├── dossier/                        # Country Dossier sheets & widgets
    │   │   │   ├── CountryDossierSheet.kt      # Main country inspection sheet
    │   │   │   ├── WeatherAtmosphericOverlay.kt# Live particle overlay (rain, snow, clouds)
    │   │   │   └── MacroeconomicDashboard.kt   # World Bank GDP sparklines & metrics
    │   │   └── hud/                            # Mission Control HUD components
    │   │       ├── MissionControlTopBar.kt     # Top status bar, altitude & distance badges
    │   │       ├── FlightRouteHudCard.kt       # Flight simulator bottom sheet
    │   │       ├── HazardDetailSheet.kt        # NASA EONET crisis detail sheet
    │   │       └── IssTelemetrySheet.kt        # ISS live tracking sheet
    ├── androidMain/kotlin/com/dirzaaulia/countries/
    │   ├── Globe3DPlatformView.android.kt      # Android GLSurfaceView container (EGL lifecycle)
    │   └── EarthGLRenderer.kt                  # Android OpenGL ES 2.0 planet renderer
    └── wasmJsMain/kotlin/com/dirzaaulia/countries/
        ├── Globe3DPlatformView.wasmJs.kt       # Wasm HTML5 Canvas container
        └── PlanetWebGLRenderer.kt              # Wasm WebGL planet renderer
```

---

## 4. UI & Inspector Architecture

All interactive inspectors are standardized around Material 3 `ModalBottomSheet`:
- Transparent scrim so the 3D globe remains partially visible beneath.
- Built-in drag handle with standard M3 snap points (peek, half-expanded, fully expanded).
- Standardized header containing the entity title, subtitle/category badge, and `MinimalistCloseButton`.
- When any sheet is open (`isSheetOpen == true`), background animation timers and rendering loops pause to conserve CPU/GPU and eliminate frame stutter.

---

## 5. WebAssembly (WasmJs) Rules

1. **Emoji Prohibition**: Raw UTF-8 emojis (e.g., `🚀`, `✈️`) do not display consistently in WebAssembly CMP builds. Use vector icons from Material Icons, procedural Canvas shapes, or concise Latin/ASCII indicators (`[SST]`, `[INFO]`).
2. **Standard Library Constraints**: No `java.*` or `android.*` imports in `commonMain`. All date/time logic must use `kotlinx-datetime`.

---

## 6. Verification & Build Commands

Always verify changes using standard Kotlin compilation tasks:

```bash
# Android verification
./gradlew :app:compileDebugKotlinAndroid

# WebAssembly (WasmJs) verification
./gradlew :app:compileKotlinWasmJs
```

*(Never run `assembleDebug`, `installDebug`, or device tests unless explicitly asked by the user).*
