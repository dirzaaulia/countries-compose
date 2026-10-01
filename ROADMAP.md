# 🚀 Countries Compose — Master Roadmap

> **Single Source of Truth** for all feature status, sprint tasks, and system architecture.

---

## Status Legend
| Symbol | Meaning |
|--------|---------|
| ✅ | Fully implemented and working |
| ⚠️ | Implemented but has known gaps / partial |
| 🐛 | Implemented but broken/degraded |
| ⏳ | Planned / Sprint Backlog |

---

## 📚 Modular Documentation Index (Separation of Concerns)

Detailed technical specifications, math proofs, and engine architectures are organized in modular documents:

- 🌌 [01. Deep Space Environment & Moon Explorer](docs/roadmap/01_deep_space_moon.md)
- ☀️ [02. Real-Time Astronomy & Shaders](docs/roadmap/02_astronomy_shaders.md)
- 🛰️ [03. ISS Tracker & NASA EONET Hazards](docs/roadmap/03_iss_eonet_hazards.md)
- 🎮 [04. Gamification & Supersonic Flight Simulator](docs/roadmap/04_gamification_flight.md)
- 🛠️ [05. TopBar, Navigation & Sheet System](docs/roadmap/05_architecture_hud_sheets.md)
- ⚡ [06. Performance Optimization & 3D Engine Polish](docs/roadmap/06_performance_engine_polish.md)
- 🌍 [07. Country Dossier & Search (ADM explorer retired)](docs/roadmap/07_subnational_adm_search.md)
- 🛠️ [08. Refinement, Bug Fixes & Architecture Sprint (Tasks 56–64)](docs/roadmap/08_refinement_sprint_tasks_56_64.md)

---

## 🎯 Master Task Matrix (Tasks 1–64)

| # | Task Description | Spec Link | Status | Priority | Core Deliverables |
|---|------------------|-----------|--------|----------|-------------------|
| 1 | Fix Earth↔Moon transition frame drop | [06. Perf](docs/roadmap/06_performance_engine_polish.md#71-earth--moon-transition-optimization) | ✅ DONE | - | Texture pre-warming & `isPageActive` lifecycle pause |
| 2 | Fix Moon phase hourly refresh | [01. Moon](docs/roadmap/01_deep_space_moon.md#14-moon-terminator-real-time-accuracy) | ✅ DONE | - | Hourly `LaunchedEffect` refresh loop in `MoonView` |
| 3 | Fix hardcoded distance badge in top bar | [03. ISS](docs/roadmap/03_iss_eonet_hazards.md#33-iss-badge-in-top-bar) | ✅ DONE | - | Real `moonInfo.distanceKm` passed to `MissionControlTopBar` |
| 4 | Add EONET hazards periodic refresh | [03. Hazards](docs/roadmap/03_iss_eonet_hazards.md#41-eonet-api-fetch) | ✅ DONE | - | 30-min background refresh loop in `App.kt` |
| 5 | Country tap auto-centering & adaptive zoom | [07. Dossier](docs/roadmap/07_subnational_adm_search.md#81-country-dossier-material-3-sheet) | ✅ DONE | - | Mainland centroid, calibrated area zoom |
| 6 | Country dossier compact peek mode | [07. Dossier](docs/roadmap/07_subnational_adm_search.md#81-country-dossier-material-3-sheet) | ✅ DONE | - | 68dp peek bar expanding to M3 sheet |
| 7 | ISS tap $\to$ telemetry card & orbital track | [03. ISS](docs/roadmap/03_iss_eonet_hazards.md#32-iss-rendering-on-globe) | ✅ DONE | - | Glowing 3-ring beacon, telemetry card, 92-min orbital track |
| 8 | EONET magnitude & severity display | [03. Hazards](docs/roadmap/03_iss_eonet_hazards.md#43-hazard-detail-sheet) | ✅ DONE | - | Magnitude values & severity in `HazardDetailSheet` |
| 9 | Animated Flight Path aircraft | [04. Flight](docs/roadmap/04_gamification_flight.md#52-supersonic-commercial-aircraft-animation-engine) | ✅ DONE | - | Glowing airplane beacon interpolating along Great Circle arc |
| 10 | Fix initial load country tap selection | [07. Dossier](docs/roadmap/07_subnational_adm_search.md#81-country-dossier-material-3-sheet) | ✅ DONE | - | `rememberUpdatedState` prevents stale empty closure |
| 11 | Country Dossier Material 3 Bottom Sheet | [05. Sheets](docs/roadmap/05_architecture_hud_sheets.md) | ✅ DONE | - | Replaced custom Surface with official `ModalBottomSheet` |
| 12 | Redesign Country Dossier close button | [05. Sheets](docs/roadmap/05_architecture_hud_sheets.md#63-minimalist-close-button-design-system) | ✅ DONE | - | Frosted glassmorphic vector cross button |
| 13 | AABB Bounding Box tap pre-filter | [06. Perf](docs/roadmap/06_performance_engine_polish.md) | ✅ DONE | - | Bounding box pre-filtering eliminates 95%+ raycasting |
| 14 | Supersonic Aircraft Animation Engine | [04. Flight](docs/roadmap/04_gamification_flight.md#52-supersonic-commercial-aircraft-animation-engine) | ✅ DONE | - | Dynamic bearing, altitude arc, jet contrails, strobes |
| 15 | HUD Map Legend & Symbology Sheet | [05. HUD](docs/roadmap/05_architecture_hud_sheets.md) | ✅ DONE | - | M3 modal sheet with 10 cartographic & space symbologies |
| 16 | Dynamic Day/Night Dossier Theming | [02. Astronomy](docs/roadmap/02_astronomy_shaders.md) | ✅ DONE | - | Solar illumination calculation & diurnal/nocturnal themes |
| 17 | Live Weather Atmospheric Motion Overlay | [07. Dossier](docs/roadmap/07_subnational_adm_search.md) | ✅ DONE | - | Procedural Compose particle system for weather conditions |
| 18 | Open-Meteo Meteorology Station | [07. Dossier](docs/roadmap/07_subnational_adm_search.md) | ✅ DONE | - | 7-day forecast cards, 24h curve, wind compass, UV index |
| 19 | World Bank Macroeconomic Dashboard | [07. Dossier](docs/roadmap/07_subnational_adm_search.md) | ✅ DONE | - | GDP trend sparkline, CPI inflation %, unemployment gauge |
| 20 | NASA EONET Planetary Crisis Monitor | [03. Hazards](docs/roadmap/03_iss_eonet_hazards.md) | ✅ DONE | - | Global crisis feed, category filters, tap-to-fly epicenter |
| 21 | WASM 3D WebGL Globe & Moon Parity | [06. Perf](docs/roadmap/06_performance_engine_polish.md#75-wasm-3d-planet-rendering--webgl-parity) | ✅ DONE | - | `PlanetWebGLRenderer.kt` with 2K textures, VBO/IBO, GLSL |
| 22 | Session Live-Data Cache | [07. Dossier](docs/roadmap/07_subnational_adm_search.md) | ✅ DONE | - | Repository-instance memory caches reuse successful details |
| 23 | Target Country Quiz Neon Silhouette Glow | [04. Quiz](docs/roadmap/04_gamification_flight.md#51-geography-quiz-mode) | ✅ DONE | - | Adaptive emerald (correct) / golden amber (target) halo |
| 24 | Flight Simulator Mach 2.2 SST | [04. Flight](docs/roadmap/04_gamification_flight.md#52-supersonic-commercial-aircraft-animation-engine) | ✅ DONE | - | Concorde Mach 2.2 mode toggle & interactive country selector |
| 25 | OpenGL Frustum Zoom Clipping Fix | [06. Perf](docs/roadmap/06_performance_engine_polish.md) | ✅ DONE | - | Expanded near/far planes to $\pm 50,000f$ in GL/WebGL |
| 26 | Milky Way Galactic Dust Lane Skybox | [01. Space](docs/roadmap/01_deep_space_moon.md#11-procedural-starfield--celestial-environment) | ✅ DONE | - | Procedural cosmic dust gradient lane on starfield Canvas |
| 28 | Sub-National Administrative Divisions (ADM1 & ADM2) | [07. ADM](docs/roadmap/07_subnational_adm_search.md#82-sub-national-administrative-divisions-adm1--adm2) | RETIRED | - | ADM explorer removed by product choice; country borders and dossier remain |
| 29 | Planetary Time Machine (24h Solar Scrubber) | [02. Solar](docs/roadmap/02_astronomy_shaders.md) | ✅ DONE | - | Real-time 24h & seasonal solar terminator scrubber |
| 32 | Global Search & Instant Teleportation HUD | [07. Search](docs/roadmap/07_subnational_adm_search.md#83-global-search--instant-teleportation-hud) | ✅ DONE | - | Diacritic-insensitive subsequence search bar in TopBar |
| 33 | Moon Apollo Site Marker Projection Fix | [01. Moon](docs/roadmap/01_deep_space_moon.md#12-full-screen-3d-moon-explorer-horizontalpager-page-1) | ✅ DONE | P0 | Positive Euler order & forward projection math in `MoonView` |
| 34 | Dossier Timezone & Solar Time Clarification | [02. Solar](docs/roadmap/02_astronomy_shaders.md) | ✅ DONE | P1 | Civil timezone (`UTC+07:00`) badge & solar time sub-label |
| 35 | Earth Globe Culling & Back-Face Performance | [06. Perf](docs/roadmap/06_performance_engine_polish.md#72-earth-globe-polygon-culling--render-optimization) | ✅ DONE | P1 | Fast centroid back-face culling eliminates ~60% polygon loops |
| 36 | Dossier Scroll Lag & Frame Drop Resolution | [06. Perf](docs/roadmap/06_performance_engine_polish.md#73-modal-bottom-sheet-frame-drop-elimination) | ✅ DONE | P1 | Unified master clock & reused path buffers |
| 37 | Mission Control TopBar UX Revamp | [05. TopBar](docs/roadmap/05_architecture_hud_sheets.md#62-missioncontroltopbar-compact-design) | ✅ DONE | P2 | Mobile-first 44dp bar, search button & M3 layer sheet |
| 38 | Flight Route Aircraft Model & Speed Tuning | [04. Flight](docs/roadmap/04_gamification_flight.md#52-supersonic-commercial-aircraft-animation-engine) | ✅ DONE | P2 | Commercial airliner vector silhouette & 14s cruise loop |
| 41 | Moon Zoom Lighting & Photometric Fix | [06. Perf](docs/roadmap/06_performance_engine_polish.md) | ✅ DONE | P0 | Calibrated lunar photometric curve, eliminating white glare |
| 42 | Modal Bottom Sheet Frame Drop Elimination | [06. Perf](docs/roadmap/06_performance_engine_polish.md#73-modal-bottom-sheet-frame-drop-elimination) | ✅ DONE | P1 | `RENDERMODE_WHEN_DIRTY` & background thread pauses |
| 43 | Minimalist Close Button Design System | [05. Sheets](docs/roadmap/05_architecture_hud_sheets.md#63-minimalist-close-button-design-system) | ✅ DONE | P2 | Unified `MinimalistCloseButton.kt` across all 11 sheets |
| 44 | Supersonic Flight Physics & Metrics | [04. Flight](docs/roadmap/04_gamification_flight.md#52-supersonic-commercial-aircraft-animation-engine) | ✅ DONE | P2 | 5.5s flight loop, Mach 2.2 Concorde metrics & red contrails |
| 45 | Startup Off-Thread JSON Parsing & DEX Crash Fix | [06. Perf](docs/roadmap/06_performance_engine_polish.md) | ✅ DONE | P0 | Moved `defaultJson` to companion object & offloaded parsing |
| 46 | Orchestrated Country Selection Flight Flow | [07. Dossier](docs/roadmap/07_subnational_adm_search.md#81-country-dossier-material-3-sheet) | ✅ DONE | P1 | 3D camera auto-glide centers country before opening sheet |
| 47 | 3D Globe Country Centering Yaw-First Fix | [06. Perf](docs/roadmap/06_performance_engine_polish.md#74-3d-country-centering--zoom-dependent-offset-fix) | ✅ DONE | P0 | Fixed Euler rotation order to yaw-first ($R_Y \to R_X$) |
| 48 | WASM Vector & SVG Flag Pipeline | [05. Sheets](docs/roadmap/05_architecture_hud_sheets.md) | ✅ DONE | P0 | Vector icons replace multi-byte WASM emojis; FlagCDN SVG flags |
| 49 | Adaptive Large Screen Responsive Side Sheet | [05. Sheets](docs/roadmap/05_architecture_hud_sheets.md#64-adaptive-large-screen--desktop-side-sheet-system) | ✅ DONE | P2 | Shared `AdaptiveInfoSheet` presents 420dp side-sheet on desktop |
| 50 | Zero-Lag Country Centering & Instant Dossier | [07. Dossier](docs/roadmap/07_subnational_adm_search.md#81-country-dossier-material-3-sheet) | ✅ DONE | P1 | Decoupled network fetch; 650ms `FastOutSlowInEasing` flight |
| 51 | Close Button (X) Design System Revamp | [05. Sheets](docs/roadmap/05_architecture_hud_sheets.md#63-minimalist-close-button-design-system) | ✅ DONE | P2 | Borderless flush icon with 36dp touch target & M3 ripple |
| 52 | Fix Globe Blackout Bug on Sheet Dismissal | [06. Perf](docs/roadmap/06_performance_engine_polish.md#73-modal-bottom-sheet-frame-drop-elimination) | ✅ DONE | P0 | `preserveEGLContextOnPause = true` & recomposition `requestRender()` |
| 53 | Revamp All Floating Cards into M3 Sheets | [05. Sheets](docs/roadmap/05_architecture_hud_sheets.md) | ✅ DONE | P2 | Converted ISS, Hazards, Flight Route, Apollo cards to M3 Sheets |
| 54 | Flight Simulator Speed Toggle Reactivity Fix | [04. Flight](docs/roadmap/04_gamification_flight.md#52-supersonic-commercial-aircraft-animation-engine) | ✅ DONE | P2 | `key(isSupersonic)` rebuilds transition on speed toggle |
| 55 | Eclipse Visibility & NASA Feed | [01. Moon](docs/roadmap/01_deep_space_moon.md) | ✅ DONE | P0 | Live NASA-derived Cloudflare feed, eclipse cards & previews |
| 56 | Fix Camera Centering for ISS & EONET Hazards | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#141-fix-globe-camera-centering-for-iss--nasa-eonet-hazards-task-56--bug--p0) | ✅ DONE | P0 | Added `isAnimating` state; 3D globe stays 100% synced during flight |
| 57 | Revamp EONET UI & Move Crisis Monitor to HUD | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#142-revamp-eonet-ui--move-planetary-crisis-monitor-to-global-hud-task-57--ux--p1) | ✅ DONE | P1 | Glassmorphic epicenter buttons, horizontal category `LazyRow`, promoted Crisis Monitor to global HUD |
| 58 | TopBar Badge Removal & Bottom HUD Control Center | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#143-mission-control-badge-removal--bottom-control-center-unification-task-58--ux--p2) | ✅ DONE | P2 | Removed count badge; unified Search, Random, and Time Machine in bottom HUD bar |
| 59 | Emphasize Globe & Moon Beacons / Dots | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#144-emphasized-beacon--dot-symbology-task-59--visual--p1) | ✅ DONE | P1 | Enlarged touch targets, double glowing aura rings, and white core dots for ISS, EONET, Apollo & capitals |
| 60 | Pre-fetch & Progressive Loading for Dossier | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#145-country-dossier-progressive-pre-fetching--loading-flow-task-60--perf--p1) | ✅ DONE | P1 | Concurrent data pre-fetching during camera flight (`fetchJob.join()`) |
| 61 | Dynamic Day-Night Split Card Background | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#146-diurnalnocturnal-day-night-split-card-backgrounds-task-61--visual--p2) | ✅ DONE | P2 | Real-time solar illumination day/night split gradient header card & status pill |
| 62 | HTML Entity & Vector Icon Revamp Engine | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#147-html-entity--vector-icon-revamp-engine-task-62--bug--p2) | ✅ DONE | P2 | Decodes HTML entities & converted all raw text/emoji symbols to vector `SemanticIcon`s |
| 63 | In-Memory Cache & Per-Domain TTL Strategy | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#148-in-memory-cache--per-domain-ttl-strategy-task-63--architecture--p2) | ✅ DONE | P2 | In-memory cache with 15-min weather TTL and 30-min NASA hazard TTL; historical ADM cache retired with task 28 |
| 64 | Lottie Cleanup & 1:1 Weather Animation Strategy | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md#149-lottie-library-cleanup--11-weather-animation-strategy-task-64--motion--p3) | ✅ DONE | P3 | Removed Compottie & .lottie; 1:1 weather animation strategy |
| 65 | Clean Code & Modular Decomposition & Bug Fix Sprint | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md) | ✅ DONE | P1 | Modularize large files into clean SRP sub-components; fix flag rendering, dossier scroll/padding & instant peek sheet |
| 66 | MVVM Refactor & REST Countries API v5 Migration | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md) | ✅ DONE | P0 | Completed App host separation: `App()` only bootstraps Koin/theme; `PlanetaryExplorerRoute` owns existing feature ViewModels, screen controls and effects, while `PlanetaryExplorerScreen`, `FeatureHudHost`, globe and sheet adapters render Earth/Moon without acquiring ViewModels. `DossierViewModel` retains country-detail ownership; the historical ADM host was retired with task 28. REST Countries v5 schema/auth and v3.1 fallback were already complete and unchanged. Corrected ISS selection/sheet interaction and inactive pager-page rendering. `:composeApp:spotlessCheck`, `:composeApp:compileAndroidMain`, and `:composeApp:compileKotlinWasmJs` passed; device/runtime testing was not requested or performed. |
| 67 | Clean Architecture Package Structure & Multiplatform Import Overhaul | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md) | ✅ DONE | P0 | Unified package hierarchy (`data`, `domain`, `di`, `platform`, `ui`, `util`), synchronized `expect`/`actual` platform packages across targets, restored `GlobeState` animations, fixed WASM `toSortedMap` multiplatform sorting, and verified clean compilation on both Android and WebAssembly targets. |
| 68 | Project-Wide Wildcard Import Elimination & Strict Hygiene Audit | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md) | ✅ DONE | P0 | Audited and eliminated 100% of star/wildcard imports (`.*`) across the entire repository (`commonMain`, `androidMain`, `wasmJsMain`, and test suites). Replaced with explicit named imports; resolved all hidden/ambiguous references and verified dual-target compilation. |
| 69 | Country Dossier Sheet Opening Frame Drop | [06. Perf](docs/roadmap/06_performance_engine_polish.md#73-modal-bottom-sheet-frame-drop-elimination) | ✅ DONE | P1 | Bounded keyed LazyColumn prevents composition of off-screen items on sheet open. |
| 70 | REST Countries v5 UI Mapping & Dossier Modularization | [07. Dossier](docs/roadmap/07_subnational_adm_search.md) | ✅ DONE | P0 | Completed mapping all 31 response domains of REST Countries v5 into modular SRP components (`DossierRestCountrySections.kt` and `DossierRestExtendedSections.kt`). Eliminated legacy duplicate details section in `DossierSections.kt`, formatted numeric/boolean data, prevented UI flooding from translations/demonyms, maintained WASM emoji safety, and verified dual-target compilation. |
| 71 | Dead Code Elimination, Idiomatic Kotlin DTO Extensions & Dossier ViewModel | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md) | ✅ DONE | P0 | Purged dead code trees (`RestCountryDataTree`, `RestCountryResponseTree`) and Java-style wrapper (`RestCountryInfo`). Created `RestCountryExtensions.kt` with pure Kotlin extension properties. Introduced `DossierViewModel` to decouple country selection and live fetching from `GlobeViewModel`. Enforced idiomatic Kotlin & CMP standards across `AGENTS.md`, `ARCHITECTURE.md`, and `.agents/rules/clean_architecture_cmp.md`. Verified Android and WASM dual compilation. |
| 72 | Netlify Countries Middleware & Initial Rendering Reduction | [06. Perf](docs/roadmap/06_performance_engine_polish.md) | ✅ DONE | P1 | Netlify function with Upstash TTL cache and off-main GeoJSON parsing. |
| 73 | Domain Repositories Separation & MVVM+UDF Architecture Standardization | [08. Refinement](docs/roadmap/08_refinement_sprint_tasks_56_64.md) | ✅ DONE | P0 | Separated monolithic `GlobeRepository` into 6 focused domain repositories originally (`CountryRepository`, `CountryDetailRepository`, `HazardRepository`, `IssRepository`, `AdministrativeRepository`, `EclipseRepository`); the ADM repository was later retired with task 28. Standardized all ViewModels strictly on MVVM with Unidirectional Data Flow (`*UiState` data classes + single `uiState: StateFlow`). Decomposed monolithic `GlobeViewModel.kt` into dedicated SRP files (`HazardViewModel`, `IssViewModel`, `AdministrativeViewModel`, `FlightViewModel`, `QuizViewModel`, `GlobeViewModel`); the ADM ViewModel was later retired with task 28. Enforced guidelines across `AGENTS.md`, `ARCHITECTURE.md`, and `.agents/rules/clean_architecture_cmp.md`. Verified dual-target compilation. |
| 74 | REST Countries Request Wiring & Architecture | [06. Perf](docs/roadmap/06_performance_engine_polish.md) | ✅ DONE | P0 | Decoupled country selection to `DossierViewModel` with reactive state stream. |
| 75 | Country Dossier Sheet Scrolling & REST Redesign | [07. Dossier](docs/roadmap/07_subnational_adm_search.md) | ✅ DONE | P1 | Material 3 `AdaptiveInfoSheet` with full-screen expansion, themed stat cards, and vector icons. |
| 76 | Android Compose Raw Resource Packaging | [06. Perf](docs/roadmap/06_performance_engine_polish.md) | ✅ DONE | P0 | Enabled Android resource processing for KMP library and assets. |
| 77 | Moon subsolar latitude tilt and optical libration | [01. Moon](docs/roadmap/01_deep_space_moon.md#14-moon-terminator-real-time-accuracy) | ✅ IMPLEMENTED | P2 | Added approximate lunar subsolar latitude and optical libration to shared astronomy; Android OpenGL ES and WebGL use the same yaw-first Moon rotation and Sun direction, while Apollo markers and hit testing share the effective rotation. Extracted Moon UI into smaller files. `:composeApp:spotlessCheck`, `:composeApp:compileAndroidMain`, and `:composeApp:compileKotlinWasmJs` passed. Visual/astronomical accuracy and runtime frame cost have not been measured on devices or browsers. |
| 78 | Earth Globe Rotation Performance Balancing vs Moon/Mars | [06. Perf](docs/roadmap/06_performance_engine_polish.md) | ⏳ PLANNED | P1 | Research and optimize touch drag responsiveness, frame pacing, and vector canvas culling on Earth so rotation feels as fluid, light, and immediate as Moon and Mars. |
| 79 | Head-to-Head Country Comparison & True Size Overlay | [07. Dossier](docs/roadmap/07_subnational_adm_search.md) | ✅ DONE | P1 | Side-by-side macroeconomic & climate comparison sheet with geodesic "True Size" vector overlay projected directly on 3D globe to dispel Mercator distortion |
| 80 | Global Timezone & Solar Clock Matrix | [02. Solar](docs/roadmap/02_astronomy_shaders.md) | ✅ DONE | P1 | Interactive 24-meridian longitudinal grid, real-time Golden/Blue Hour twilight bands, and global financial market overlap tracker syncing with 24h solar scrubber |
| 81 | Tectonic Plates & Seismic Fault Lines | [03. Hazards](docs/roadmap/03_iss_eonet_hazards.md) | ✅ DONE | P1 | Lithospheric plate boundaries, Pacific Ring of Fire volcanic aura, live USGS earthquakes with depth color-coding & shockwave rings, and tectonic/seismic detail sheets |
| 82 | Lunar Heritage & Selenological Geology Layer | [01. Moon](docs/roadmap/01_deep_space_moon.md) | ✅ DONE | P1 | International lunar mission heritage archive (Apollo, Soviet Luna, Chang'e, Chandrayaan-3, SLIM, CLPS), geological Maria & Craters, far-side tidally locked badge, and Category Filter Bar |
| 83 | Financial Market Stock Exchanges Globe Layer | [02. Solar](docs/roadmap/02_astronomy_shaders.md) | ✅ DONE | P1 | Dynamic Remote Config stock exchange bundle provider with in-app defaults, 3D Globe vector layer with status-colored beacons ([OPEN], [SOON], [CLOSED]), exchange/local timezone dual displays, and "Fly to Exchange" 3D camera auto-glide |
| 84 | Heliocentric Solar System & Live Keplerian Planetary Orbits (Initial Launch Screen) | [01. Deep Space](docs/roadmap/01_deep_space_moon.md) | ✅ DONE | P0 | Initial app entry point: Sun at center with procedural corona, live millisecond-accurate Keplerian orbital mechanics (Mercury to Saturn), 3D interactive gesture navigation, and smooth dive transitions into Earth, Moon, and Mars |
| 85 | 3D Planetary Night Lighting Enhancement & Day/Night Terminator Scrubber | [01. Deep Space](docs/roadmap/01_deep_space_moon.md) | ✅ DONE | P1 | Reduced harsh dark region and boosted night ambient lighting (~0.42–0.45) across all 3D rendered planets (Mercury to Neptune) with soft atmospheric limb glow in GlobeShaders; added real-time day/night solar terminator scrubber HUD with RESET LIVE action to GenericPlanetView |
| 86 | Unified 360° Free Orbital Camera Navigation (NASA Eyes Style) | [01. Deep Space](docs/roadmap/01_deep_space_moon.md) | ⏳ PLANNED | P0 | Unify camera movement across all celestial bodies (Earth, Moon, Mars, and planets) to follow Solar System 360° free orbital camera: camera orbits freely around the celestial body without pitch clamping, replacing fixed-origin sphere rotation. |
| 87 | Universal Planetary Dossier & Atmospheric Specs Sheet | [01. Deep Space](docs/roadmap/01_deep_space_moon.md) | ⏳ PLANNED | P1 | Comprehensive physical specs sheet (mass, gravity, escape velocity, axial tilt, solar distance) with atmospheric gas composition bars and diurnal temperature extremes for all planets. |
| 88 | Planetary Surface Features & Historical Mission Landing Sites | [01. Deep Space](docs/roadmap/01_deep_space_moon.md) | ⏳ PLANNED | P1 | Interactive 2D-on-3D markers for prominent surface features (Caloris, Maxwell Montes, Great Red Spot) and historic robotic probes (Venera, MESSENGER, Voyager, Galileo). |
| 89 | Natural Satellites & Major Moons System | [01. Deep Space](docs/roadmap/01_deep_space_moon.md) | ⏳ PLANNED | P1 | Interactive orbital satellite carousel for Jupiter (Galilean moons), Saturn (Titan, Enceladus), and Neptune (Triton) with habitability & orbital scale telemetry. |
| 90 | Saturn 3D Ring System Geometry & Telemetry | [01. Deep Space](docs/roadmap/01_deep_space_moon.md) | ⏳ PLANNED | P2 | Hardware-accelerated 3D planar ring geometry with Cassini/Encke divisions, opacity shaders, particle composition, and Roche limit calculation. |
| 91 | Cutaway Planetary Interior Core Visualizer | [01. Deep Space](docs/roadmap/01_deep_space_moon.md) | ⏳ PLANNED | P2 | Interactive X-Ray cross-section HUD revealing crust, silicate mantle, liquid metallic hydrogen, and core layers for terrestrial, gas, and ice giants. |






