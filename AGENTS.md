# Android/Kotlin Development Rules

- Kotlin is the primary language.
- Prefer modern Android APIs and current official Android guidance.
- Prefer Jetpack Compose for new Android UI.
- For KMP projects, keep shared logic in commonMain.
- Do not introduce Android-specific APIs into commonMain.
- Keep platform-specific implementations in the appropriate source set.
- Prefer existing project architecture over introducing a new architecture unnecessarily.
- Reuse existing dependencies before adding new ones.
- Do not invent library APIs.
- When API/library behavior may have changed, use Context7 or authoritative documentation.
- For Android-specific tasks, use relevant Android Skills.
- For KMP/CMP tasks, use the relevant KMP/CMP skill.
- If a specialized skill is needed but not installed, use find-skills to discover one.
- Evaluate discovered skills before installing them.
- Avoid unnecessary refactoring.
- Do not modify unrelated files.
- Verify compilation after significant changes.
- Run relevant tests after modifying shared/business logic.
- Check Android and shared source sets when changing KMP code.
- Prefer incremental changes over large rewrites.
- Completion discipline: Do not report a cross-screen migration or audit as complete after fixing only examples. Inventory every affected shared component and caller, implement the full agreed scope, and verify no audited occurrences remain before declaring completion.

## Project-Specific Rules (Countries)

- Target SDK is 36, Compile SDK is 37, minSdk is 29.
- Gradle Version Catalog (`gradle/libs.versions.toml`) is used for all dependency management.
- Modern Compose stack with Compose Compiler plugin (`org.jetbrains.kotlin.plugin.compose`) and Compose BOM.
- `kotlinx.serialization` is used for JSON parsing and serialization across data models.
- Maintain Material 3 guidelines and Compose best practices.
- Do NOT build full APK (e.g. `assembleDebug`, `packageDebug`, `installDebug`) and do NOT run remote or device tests unless explicitly asked by the user.
- After code updates, only run standard compile tasks to verify changes:
  - `./gradlew :app:compileDebugKotlinAndroid`
  - `./gradlew :app:compileKotlinWasmJs`
- Run these target compile tasks separately, never in one Gradle invocation or concurrently. Wait for one target's command to return before starting the other.
- Shell completion: Treat an observed Gradle `BUILD SUCCESSFUL` marker as a completed verification. Do not poll or wait for wrapper cleanup after that marker; proceed and report success unless the output also contains a build failure or a nonzero exit status.
- Quota Optimization: Be extremely token-efficient, concise, avoid redundant checks, never run multiple compilation cycles unless asked, and avoid unnecessary tool calls.
- AI Reasoning & Thought Formatting: Whenever performing step-by-step reasoning or internal analysis, wrap it inside `<thought>...</thought>` tags so the IDE renders it as a collapsible "Thought" button in the chat interface.
- Single Source of Truth: All feature roadmap items, task progress, and technical architecture plans MUST be read from and updated in `ROADMAP.md` at project root (`D:/Android/Projects/countries-compose/ROADMAP.md`), NOT inside agent-private or isolated brain directories.
- **Clean Code & Modular File Decomposition**: Keep file lengths manageable and maintainable by adhering to Clean Code principles and Single Responsibility Principle (SRP). Avoid creating monolithic files with high line counts (target <= 300-400 lines per file). Break down large composable screens, data layers, or renderers into smaller, focused, reusable sub-components, helper files, or dedicated domain modules while strictly preserving existing functional behavior, architectural invariants, and public APIs.

---

## 🛡️ CRITICAL ARCHITECTURAL INVARIANTS (DO NOT BREAK)

Any AI agent modifying this codebase MUST strictly adhere to the following invariants. Violating these causes immediate regressions:

### 1. Dual-Layer Hybrid Rendering Engine
- **Underneath**: Hardware-accelerated 3D sphere rendered via OpenGL ES on Android (`EarthGLRenderer.kt` in `androidMain`) and WebGL on Web (`PlanetWebGLRenderer.kt` in `wasmJsMain`).
- **On Top**: Jetpack Compose `Canvas` overlay in `commonMain` (`GlobeView.kt`) projecting dynamic vector data: country borders, ISS orbital tracks, flight paths, Apollo landing sites, and weather particles.
- **DO NOT** attempt to replace the 3D OpenGL/WebGL sphere with 2D Canvas drawing, and do not remove the 2D Canvas overlays. Both layers are synchronized via shared camera state (`rotationX`, `rotationY`, `zoom`).

### 2. 3D Camera & Euler Math Invariants
- **Yaw-First Euler Order ($R_Y \to R_X$)**: Globe camera rotation and country auto-centering MUST apply Yaw ($R_Y$) first, then Pitch ($R_X$). Never change to Pitch-first ($R_X \to R_Y$), as that causes latitude drift and misaligned centering depending on the zoom level.
- **Latitude Inversion**: For spherical projection, positive latitude maps to negative pitch. Do not invert signs without checking `forwardProject()` in `GlobeView.kt` and `AstronomyMath.kt`.
- **Projection Function**: 2D overlay alignment depends on `forwardProject(lat, lon, rotationX, rotationY, zoom, radius, center)`. Any change to 3D matrix math in the renderers must be symmetrically mirrored in `forwardProject()`.

### 3. Android EGL Lifecycle & Black Screen Prevention
- In `Globe3DPlatformView.android.kt`, the `GLSurfaceView` MUST maintain:
  1. `preserveEGLContextOnPause = true`
  2. `renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY`
  3. `AndroidView.update` MUST call `glView.requestRender()` on recomposition.
- **DO NOT** delete these settings. Omitting them causes the globe to render completely black when returning from modal bottom sheets.

### 4. WASM (WebAssembly) Compatibility & Emoji Ban
- Compose Multiplatform for Web (`wasmJs`) currently **does not render multi-byte emoji glyphs** natively without custom bundled fallback fonts (displays empty boxes / "tofu").
- **NEVER** insert raw multi-byte emojis (e.g. ✈️, 🚀, 🌍, 🛰️, ℹ️) into shared UI or HUD components. Always use:
  - Material Icons (`Icons.Default.*` / `Icons.Outlined.*`),
  - Clean ASCII/Latin text (e.g., `[MACH 2.2]`, `[INFO]`, `LAT`, `LON`), or
  - Procedural Compose vector graphics / canvas paths.
- Avoid introducing JVM- or Android-specific APIs (such as `java.time.*`, `android.graphics.*`, `java.util.*`) into `commonMain`. Use `kotlinx-datetime` and Compose multiplatform primitives.

### 5. Unified Adaptive Sheet & Close Button Design System
- All inspector and detail interfaces MUST use Material 3 sheets with a drag handle and transparent scrim:
  - Compact windows: standard `ModalBottomSheet`.
  - Medium and expanded windows: supported Material 3 adaptive side sheet.
  - Examples: Country Dossier, ISS Telemetry, NASA EONET Hazards, Apollo Site Inspector, Flight Route Card, Map Legend.
- **DO NOT** revert inspectors into floating `Card`s, custom popup `Surface`s, or ad-hoc dialogs.
- **Minimalist Close Button**: Every sheet and top-level overlay MUST use `MinimalistCloseButton.kt` (36dp touch target, resolution-independent vector cross `#94A3B8`). Never use raw text characters like `"X"` or `"✕"`.

### 6. Frame Rate & Battery Optimization (Sheet Open State)
- When a sheet or full overlay is open (`isSheetOpen == true` or `selectedCountry != null`), background animations and GL rendering loops MUST be paused or reduced (`RENDERMODE_WHEN_DIRTY`).
- Infinite transitions must be tied to active lifecycles to avoid 60fps churn on mobile devices.

### 7. Flight Simulator InfiniteTransition Reactivity
- Aircraft animation progress in `GlobeView.kt` is wrapped in `key(isSupersonic)` to properly recreate `rememberInfiniteTransition` when supersonic mode toggles. Do NOT remove this `key()` wrapper, or speed toggle state changes will not take effect dynamically.

### 8. Live Data Loading & Session Cache
- The app has no guaranteed offline mode; network-backed details may be unavailable without connectivity.
- `GlobeRepository.kt` may reuse successfully fetched live country details, administrative divisions, division weather, and derived ADM2 filters from in-memory caches for the lifetime of its repository instance. These caches are not persisted to disk and do not constitute offline support; failed fetches are not cached as successful results.
- Country dossiers may display bundled country estimates (including GDP per capita) when corresponding live World Bank values are unavailable. Treat such values as estimates, not live metrics; other unavailable live fields should remain absent/loading/unavailable rather than being fabricated.
- Network calls must not block UI rendering or camera flight transitions. Show loading/skeleton state while live data loads, then show available values or an unavailable/empty state when no value exists.

### 9. Clean Architecture Package Hierarchy & Expect/Actual Parity
- **Package Hierarchy**:
  - `com.dirzaaulia.countries.data.*`: External data sources, Ktor client, and repository implementations.
  - `com.dirzaaulia.countries.domain.*`: Pure multiplatform business logic, astronomy math (`AstronomyMath.kt`), domain models (`Country.kt`, `ApolloSite.kt`), shaders (`GlobeShaders.kt`), mesh generation (`SphereMesh.kt`), and spherical coordinates (`SphericalMath.kt`, `GlobeState.kt`).
  - `com.dirzaaulia.countries.platform.*`: Cross-platform abstractions (`Globe3DPlatformView`, `PlatformSymbols`, `PlatformStartup`, `PlatformTime`, `PlatformHttpClient`).
  - `com.dirzaaulia.countries.di.*`: Dependency injection (Koin `AppModule.kt`).
  - `com.dirzaaulia.countries.ui.*`: UI screens, HUD overlays, M3 sheets, and ViewModels.
  - `com.dirzaaulia.countries.util.*`: Pure multiplatform formatters and helpers.
- **Expect/Actual Package Parity**:
  - Every `expect` in `commonMain` and its corresponding `actual` in `androidMain` and `wasmJsMain` **MUST reside in the exact same package** (`com.dirzaaulia.countries.platform`) and matching folder structure. Package mismatch causes immediate unresolved reference errors during multiplatform compilation.

### 10. Pure Multiplatform API Hygiene (Zero JVM Leaks in commonMain)
- `commonMain` compiles to both Android (JVM) and Web (WASM).
- **NEVER** import or use JVM- or Android-specific APIs in `commonMain`:
  - ❌ `java.util.*` (e.g. `toSortedMap()`, `Date`, `Calendar`, `Collections`) $\to$ ✅ Use Kotlin stdlib primitives (`.entries.sortedBy { it.key }`).
  - ❌ `java.time.*` $\to$ ✅ Use `kotlinx-datetime` or `PlatformTime`.
  - ❌ `android.graphics.*`, `android.view.*` $\to$ ✅ Use Compose Multiplatform primitives or isolate in `androidMain`.
- When in doubt, consult the `compose-multiplatform-patterns` skill.

### 11. Idiomatic Kotlin & CMP Architectural Patterns (No Java OOP Anti-Patterns)
- **Separated Domain Repositories (No Monolithic God-Repositories)**:
  - Repositories must be domain-focused with single responsibility: `CountryRepository`, `CountryDetailRepository`, `HazardRepository`, `IssRepository`, `AdministrativeRepository`, and `EclipseRepository`.
  - ❌ Never pile all domain APIs into a single monolithic repository.
- **Unified Architectural Pattern: MVVM with Unidirectional Data Flow (UDF)**:
  - Do NOT mix ad-hoc MVI intents/reducers with MVVM, and do NOT scatter multiple individual `StateFlow`s for every single screen property.
  - Every ViewModel defines an immutable `FeatureUiState` data class with sensible defaults.
  - Exposes a single `val uiState: StateFlow<FeatureUiState> = _uiState.asStateFlow()`.
  - Public ViewModel methods act as event handlers modifying `_uiState.value = _uiState.value.copy(...)`.
- **Zero Java-Style DTO Wrappers**:
  - ❌ DO NOT wrap API data transfer objects in intermediate Java-style classes with boilerplate getter delegates (e.g., `class CountryInfo(val response: CountryResponse) { val name get() = response.name }`).
  - ✅ Use Kotlin **extension properties/functions** (`val CountryResponse.officialName: String get() = ...`) or direct domain model mapping (`Country.enrich(...)`).
  - Keep response models clean, data-oriented, and decorated with idiomatic extensions.
- **Dedicated ViewModel State Ownership (No Top-Level Prop-Drilling)**:
  - Complex feature sheets and HUD modules MUST NOT have their state and fetch operations monolithic in `GlobeViewModel` or prop-drilled 15 levels down from `App.kt`.
  - Feature domains MUST have dedicated ViewModels (e.g. `DossierViewModel`, `HazardViewModel`, `IssViewModel`, `FlightViewModel`, `QuizViewModel`, `AdministrativeViewModel`).
  - Feature ViewModels own their coroutine jobs, loading states, and domain interactions, exposing unidirectional `StateFlow<UiState>`.
- **Zero Wildcard Imports**:
  - Never use wildcard (`*`) imports in any Kotlin source set (`commonMain`, `androidMain`, `wasmJsMain`). Every import must be explicit.
- **Single Responsibility File Decomposition**:
  - Maintain strict SRP: keep file sizes manageable ($\le 300\text{--}400$ lines). Break down monolithic composables into focused sub-components under feature component packages (e.g., `ui/dossier/components/`).
