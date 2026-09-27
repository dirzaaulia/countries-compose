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
        KtorClient --> EclipseApi[Cloudflare Workers Free Eclipse Feed]
        EclipseApi --> NasaCatalog[NASA Eclipse Catalog]
        Repo --> LiveState[Live data with loading and unavailable states]
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
- **Euler Order**: Yaw-first ($R_Y \to R_X$). Implement this by calling `rotateY` first and then `rotateX` (`R_X * R_Y * p`); matrix multiplication applies the rightmost yaw transform to `p` first. This prevents latitude drift when zooming or auto-centering.
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
    │   ├── data/                               # Data layer: API clients, DTOs, repository & in-memory caches
    │   │   ├── eclipse/                        # Cloudflare Workers free NASA eclipse feed
    │   │   ├── geoboundaries/                  # GeoBoundaries API for ADM1 & ADM2 polygons
    │   │   ├── iss/                            # WhereTheISS.at live orbital telemetry API
    │   │   ├── nasa/                           # NASA EONET live planetary hazard client
    │   │   ├── openmeteo/                      # Open-Meteo weather forecast & atmospheric API
    │   │   ├── repository/                     # GlobeRepository implementation & session caching
    │   │   ├── restcountries/                  # REST Countries API client (v5 schema & v3.1 fallback)
    │   │   └── worldbank/                      # World Bank macroeconomic indicators API
    │   ├── domain/                             # Pure business logic, math, entities (Zero JVM/Android deps)
    │   │   ├── astronomy/                      # AstronomyMath, SunPosition, MoonInfo, EclipseFeed
    │   │   ├── country/                        # Country, AdministrativeDivision, LiveCountryDetails, ApolloSite
    │   │   └── globe/                          # SphericalMath, GlobeShaders, SphereMesh, GlobeState
    │   ├── di/                                 # Koin dependency injection modules (appModules, ViewModels)
    │   ├── platform/                           # Cross-platform expect declarations (Globe3DPlatformView, etc.)
    │   ├── ui/                                 # Compose Multiplatform UI layer
    │   │   ├── app/                            # App entrypoint, AppSheetsOverlay, FeatureHudHost
    │   │   ├── components/                     # Reusable design system (AdaptiveInfoSheet, MinimalistCloseButton, SemanticIcon)
    │   │   ├── dossier/                        # Country Dossier, MeteorologyStation, WorldBank, NasaCrisis sheets
    │   │   │   ├── administration/             # Sub-national administrative division sheet host
    │   │   │   ├── components/                 # Dossier card sections & metadata rows
    │   │   │   └── weather/                    # Particle weather overlays (rain, snow, clouds)
    │   │   ├── globe/                          # 2D Canvas vector globe & feature ViewModels
    │   │   ├── hud/                            # TopBar, search, time machine, bottom HUD sheets
    │   │   ├── moon/                           # MoonView explorer, Apollo mission markers, lunar timeline
    │   │   └── overlay/                        # Starfield celestial background
    │   └── util/                               # Pure multiplatform utilities (FormatUtils)
    ├── androidMain/kotlin/com/dirzaaulia/countries/
    │   ├── MainActivity.kt                     # Android entrypoint & Splash Screen lifecycle
    │   └── platform/                           # Android actual implementations (EarthGLRenderer, EGL lifecycle, PlatformSymbols)
    └── wasmJsMain/kotlin/com/dirzaaulia/countries/
        ├── main.kt                             # WebAssembly CanvasBasedWindow entrypoint
        └── platform/                           # WebGL actual implementations (PlanetWebGLRenderer, PlatformSymbols)
```

---

## 4. Domain Repositories & Cache Policy

Repositories are strictly separated into focused, single-responsibility domain classes under `com.dirzaaulia.countries.data.repository`:
- **`CountryRepository`**: Loads and caches GeoJSON countries and earth cloud assets.
- **`CountryDetailRepository`**: Coordinates live country dossier aggregation (REST Countries, World Bank, Open-Meteo, nearby NASA events) with an in-memory 15-minute TTL Stale-While-Revalidate cache and safe fallback data.
- **`HazardRepository`**: Manages NASA EONET natural disaster event fetching and distance filtering with a 30-minute in-memory cache.
- **`IssRepository`**: Fetches real-time International Space Station telemetry coordinates and velocity.
- **`AdministrativeRepository`**: Fetches and caches GeoBoundaries administrative divisions (ADM1 and ADM2).
- **`EclipseRepository`**: Retrieves lunar and solar eclipse feeds.
- **`GlobeRepository`**: Lightweight composite delegating facade retained for backward compatibility.

Cache Policy:
- Cached values are kept in memory for the lifetime of repository instances; no disk persistence or offline mode is provided.
- Failed requests are not cached as successful results. Unavailable fields remain absent/loading rather than fabricated.

---

## 5. UI & Inspector Architecture

All interactive inspectors are standardized around adaptive Material 3 sheets through `AdaptiveInfoSheet.kt`:
- Compact windows use `ModalBottomSheet`; medium and expanded windows use the shared 420dp Material 3-aligned right-side sheet because Compose Multiplatform has no supported Android+Wasm side-sheet API.
- Transparent scrim so the 3D globe remains partially visible beneath.
- Built-in drag handle with standard M3 sheet behavior.
- Standardized header containing the entity title, subtitle/category badge, and `MinimalistCloseButton`.
- When any sheet is open (`isSheetOpen == true`), background animation timers and rendering loops pause to conserve CPU/GPU and eliminate frame stutter.

---

## 6. WebAssembly (WasmJs) Rules

1. **Emoji Prohibition**: Raw UTF-8 emojis (e.g., `🚀`, `✈️`) do not display consistently in WebAssembly CMP builds. Use vector icons from Material Icons, procedural Canvas shapes, or concise Latin/ASCII indicators (`[SST]`, `[INFO]`).
2. **Standard Library Constraints**: No `java.*` or `android.*` imports in `commonMain`. All date/time logic must use `kotlinx-datetime`.

---

## 7. Free Eclipse Feed Architecture

Task 55 uses a separate Cloudflare Workers Free project, not the Android/Wasm app process.

```text
NASA Eclipse Catalog and published path data
        |
        | monthly scheduled fetch
        v
Cloudflare Worker Free (normalizes compact event JSON)
        |
        | one KV write: eclipse-feed:v1
        v
Cloudflare Workers KV Free
        |
        | GET /v1/eclipses with CORS
        v
GlobeRepository -> MoonView / Compose Canvas overlay
```

- The Worker is a publisher only. It fetches NASA's existing event summaries, regional-visibility text, global-map URLs, and central-path URLs; it does not run global Besselian/eclipse calculations. This stays within the Workers Free 10 ms CPU limit per invocation.
- A monthly UTC Cron Trigger refreshes the current and next 24 months. No annual app release or manual event-data update is required.
- The app calls the Worker live. No client offline cache or fallback is added; an unavailable feed is displayed as unavailable.
- Use exactly one small KV value and one read per feed request. Current Free limits are 100,000 Worker requests/day, 100,000 KV reads/day, 1,000 KV writes/day, and 1 GB stored data. The design performs one scheduled write per month and remains free within those limits.
- Keep the account on Workers Free with no paid-plan upgrade or billing fallback. Use the free `workers.dev` endpoint; a custom domain may introduce separate domain costs.
- Return `generatedAt`, source URLs, and a schema version. A `/health` response exposes upstream parse failures. Provider limits and NASA page formats can change, so zero cost cannot mean zero future maintenance; it means no recurring spend and no yearly content operation.

---

## 8. Unified Architecture: MVVM with Unidirectional Data Flow (UDF)

The entire project strictly adheres to **MVVM with Unidirectional Data Flow (UDF)**. Hybrid or ad-hoc patterns (such as MVI actions or scattered multiple StateFlows per property) are standardized into the official Android/CMP architecture:

1. **Unified `UiState` Contract**:
   - Each ViewModel defines an immutable `*UiState` data class (e.g., `GlobeUiState`, `DossierUiState`, `HazardUiState`, `IssUiState`, `AdministrativeUiState`, `FlightUiState`, `QuizUiState`).
   - The ViewModel manages a single `private val _uiState = MutableStateFlow(FeatureUiState())` and exposes `val uiState: StateFlow<FeatureUiState> = _uiState.asStateFlow()`.
2. **Explicit UI Events / Methods**:
   - User interactions trigger public ViewModel functions (e.g. `selectCountry(id)`, `toggleFlightMode()`, `handleQuizTap()`).
   - Functions launch work in `viewModelScope` and perform atomic state transitions: `_uiState.value = _uiState.value.copy(...)`.
3. **Decoupled Feature Ownership**:
   - Complex features and sheets own their state via dedicated ViewModels.
   - `App.kt` and `FeatureHudHost` collect states with a single `val state by vm.uiState.collectAsState()` per ViewModel, avoiding prop-drilling and redundant recompositions.
4. **Dependency Injection**:
   - Koin modules in `di/AppModule.kt` register domain repositories (`singleOf`) and feature ViewModels (`viewModelOf`). ViewModels only inject their respective domain repositories.
5. **Multiplatform Isolation**:
   - Pure business logic, repositories, and UI states reside in `commonMain`; platform rendering and lifecycle hooks are isolated in `androidMain` and `wasmJsMain`.

## 9. Maintainability & Idiomatic Kotlin Conventions

- **Idiomatic Kotlin vs. Java OOP Wrappers**:
  - Do NOT wrap API responses in intermediate Java-style DTO classes with manual getter delegates (`class CountryInfo(val response: CountryResponse) { val name get() = response.name }`).
  - Use pure Kotlin extension properties (`RestCountryExtensions.kt`) directly on response models (e.g. `RestCountryResponse.capitalName`, `RestCountryResponse.areaSqKm`) or domain mapping functions (`Country.enrich(rest)`).
- **Strict Import Hygiene**:
  - Star/wildcard imports (`.*`) are prohibited across all source sets (`commonMain`, `androidMain`, `wasmJsMain`, and test suites). Every import must be explicit.
- Follow the existing feature-oriented decomposition under `ui/dossier`, `ui/hud`, `ui/components`, and `ui/overlay`; keep shared visual primitives reusable rather than duplicating variants across sheets.
- Use `AdaptiveInfoSheet` for inspector surfaces and `MinimalistCloseButton` for their standard close affordance. Preserve the platform-size behavior and shared visual conventions described above.
- Keep composable inputs explicit and callbacks at component boundaries. Avoid moving expensive parsing, network work, or geometry calculations into frequently recomposed drawing code.
- Treat the globe rendering, camera math, EGL lifecycle, sheet rendering pause, and flight-transition keying rules in this guide and `AGENTS.md` as compatibility invariants when refactoring.
- Document only practices that are present in the implementation; do not infer offline support from in-memory caches or treat bundled estimates as live API values.

## 10. Verification & Build Commands

For code changes that affect shared or platform Kotlin, use the target compile tasks separately:

```bash
# Android verification
./gradlew :app:compileDebugKotlinAndroid

# WebAssembly (WasmJs) verification
./gradlew :app:compileKotlinWasmJs
```

Do not build APKs or run device/remote tests unless explicitly requested. Documentation-only edits do not require compilation.
