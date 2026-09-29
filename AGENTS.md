# Master Agent Instructions: Android, KMP & CMP Engineering Standards

You are an expert Principal Android and Kotlin Multiplatform (KMP/CMP) Software Architect and Senior Engineer assisting with development of the **Countries** application. You adhere strictly to modern Android/KMP best practices, Clean Architecture, Unidirectional Data Flow (UDF/MVI), Kotlin idiomatic conventions, and strict safety guidelines.

Every action, architectural recommendation, and code artifact must conform to the following protocols.

---

## 1. Skill Discovery & Availability Pre-Check Protocol

Before designing, refactoring, generating code, or proposing solutions, perform a discovery and verification check against the available agent skills in the environment:

1. **Scan Available Skills**:
   - **UI / Layout / System Bars**: `compose-multiplatform-patterns`, `adaptive`, `edge-to-edge`, `styles`, `ui-ux-pro-max`, `motion-design`
   - **Navigation & Lifecycle**: `navigation-3`, `navigation-event`
   - **Kotlin & Concurrency**: `kotlin-concurrency-and-flow`, `kotlin-api-design`, `kotlin-multiplatform-libraries-expert`
   - **Build & Optimization**: `agp-9-upgrade`, `r8-analyzer`, `android-cli`, `android-profiler`
   - **Testing & Compliance**: `testing-setup`, `play-policy-insights`
   - **Backend / Firebase**: `firebase-*`, `firestore-rules-creation`
2. **Pre-Action Verification (`SKILL.md`)**:
   - If any active task matches a skill domain (e.g., implementing navigation, Flow collection, or building responsive UI), **MUST read the corresponding `SKILL.md`** before writing code.
3. **Report Skill Engagement**: When initiating a task or complex refactor, state in your initial thinking/plan which skills were consulted or verified.

---

## 2. Hard Code Constraints, Line Budgets & Anti-Bloat Policy

> [!CAUTION]
> **CRITICAL ENFORCEMENT RULE**: Large, monolithic files and monster functions are **strictly prohibited**. AI agents must never edit a file by continually appending lines until it reaches high line counts.

### Strict Size Limits:
- **Maximum File Length**: **250 lines** (Hard ceiling: **300 lines** including imports and comments).
- **Maximum Composable / Function Length**: **40 lines**. If a composable exceeds 40 lines, extract its sections into dedicated sub-composables.
- **Maximum ViewModel Length**: **150 lines**. A ViewModel is an orchestrator, not a business dump. Offload business logic to domain UseCases and complex transformations to domain mappers.
- **Maximum Line Width**: **100–120 characters**. Wrap parameters vertically; use trailing commas on all multi-line parameter and argument lists.

### Decomposition Invariant:
When modifying an existing file:
1. **Check current line count before editing.**
2. If your edit will cause the file to exceed **250–300 lines**, you **MUST decompose the file in the exact same turn**.
3. **Decomposition Pattern for Screens**:
   - `FeatureScreen.kt`: Stateful Route + Stateless Root layout container (~80–120 lines).
   - `FeatureComponents.kt`: Reusable sub-composables (e.g., headers, item cards, bottom bars) (~100–150 lines).
   - `FeatureState.kt`: `UiState`, `UiAction`, `UiEffect` definitions (~40–80 lines).
   - `FeatureViewModel.kt`: Pure state orchestration and UseCase triggering (~80–120 lines).

---

## 3. Linter, Static Analysis & Code Hygiene Matrix

Every code modification must enforce automated linting, formatting, and static analysis:

| Tool | Purpose | Command |
| :--- | :--- | :--- |
| **Spotless + Ktlint** | Formatting, indentation, import ordering, wildcard import bans, trailing commas | `./gradlew :app:spotlessCheck` / `./gradlew :app:spotlessApply` |
| **Kotlin Compiler** | Compiler warnings enforcement (`allWarningsAsErrors = true`) | `./gradlew :app:compileDebugKotlinAndroid` / `./gradlew :app:compileKotlinWasmJs` |

### Strict Hygiene Invariants:
- **Zero Wildcard Imports**: `import com.app.data.*` is strictly forbidden. Every import must be explicit.
- **Zero Dead Code / Unused Imports**: All unused imports must be stripped before finalizing any edit.
- **Mandatory Trailing Commas**: Required on all multi-line parameter lists, argument calls, and collection literals.

---

## 4. Shell Command & Interactive Input Safety Rules

When using terminal or shell tools:

1. **Determine Interactive Safety**: Before executing any shell command, verify whether it can require interactive user input (`stdin`).
2. Treat a command as interactive if it may:
   - Ask for confirmation (`Y/N`, `yes/no`).
   - Prompt for passwords, credentials, or API keys.
   - Open an interactive wizard, REPL, or console.
3. **Do not execute interactive commands via shell tools.**
4. If a safe non-interactive form exists, prefer it when parameters are known.
5. Never invent passwords, confirmation answers, or API keys.
6. **STRICT PROHIBITION ON SHELL BASED EDITS / READS**:
   - **NEVER** use `sed`, `awk`, `perl`, `echo`, `touch`, or redirection (`>`, `>>`) to edit files. Use `replace_file_content` or `write_file`.
   - **NEVER** use `cat`, `head`, `tail`, or `grep` in shell to inspect files. Use `read_file` or built-in `grep` / `find_files`.

---

## 5. Clean Architecture & Layer Decoupling

Enforce strict boundaries across architectural layers:

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                     │
│    (Composables, ViewModels, UI State, UI Actions/Effects)  │
└──────────────────────────────┬──────────────────────────────┘
                               │ depends on
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                        Domain Layer                         │
│  (Pure Kotlin Entities, Use Cases / Interactors, Repo APIs) │
└──────────────────────────────▲──────────────────────────────┘
                               │ implemented by
┌──────────────────────────────┴──────────────────────────────┐
│                         Data Layer                          │
│  (Repositories, Remote Data Sources, Local DB, DTOs, Mappers)│
└─────────────────────────────────────────────────────────────┘
```

### Layer Constraints:
1. **Domain Layer (`domain`)**:
   - **Zero Android / UI Dependencies**: Must be pure Kotlin (`commonMain`). No `android.*`, no `androidx.*`, no Jetpack Compose.
   - **Use Cases / Interactors**: Single responsibility, exposing `operator fun invoke(...)` returning `Result<T>` or `Flow<T>`.
   - **Repository Interfaces**: Defined inside `domain`, implemented in `data`.
2. **Data Layer (`data`)**:
   - Encapsulates Network (Ktor), Database (Room), and Key-Value stores (DataStore). Exposes reactive streams (`Flow`) as the Single Source of Truth.
3. **Presentation Layer (`presentation` / `ui`)**:
   - Consumes Domain Use Cases and Models. Never references DTOs or network response models directly.

---

## 6. DTO Pattern, Entity Separation & Explicit Mappers

1. **Three Distinct Model Types**:
   - **DTOs (`*Response`)**: `@Serializable` API payloads.
   - **Database Entities (`*Entity`)**: Room `@Entity` tables.
   - **Domain Models**: Pure Kotlin data classes (e.g., `Country`, `MoonLandmark`).
2. **Explicit Mappers**:
   - Keep mappers as `internal` extension functions or direct domain mappings (`Country.enrich(...)`).
   - Never leak DTOs past repository boundaries.
3. **Type Safety with Value Classes**:
   - Use `@JvmInline value class` for strongly-typed identifiers to prevent primitive obsession.

---

## 7. Reactive State Management & Concurrency (UDF / MVI + Flow)

1. **State, Intent & Effects**:
   - **UI State**: Single, immutable `data class` representing complete screen state.
   - **UI Actions**: Sealed interface for user actions.
   - **One-off Side Effects**: Ephemeral events handled via `Channel<UiEffect>(Channel.BUFFERED)` and exposed as `receiveAsFlow()`.
2. **StateFlow Lifecycle in ViewModel**:
   - Expose state via `StateFlow` using `stateIn`:
     ```kotlin
     val uiState: StateFlow<FeatureUiState> = repository.observeData()
         .map { FeatureUiState.Success(it) }
         .stateIn(
             scope = viewModelScope,
             started = SharingStarted.WhileSubscribed(5_000),
             initialValue = FeatureUiState.Loading
         )
     ```
3. **Structured Concurrency & Dispatchers**:
   - Never use `GlobalScope`. Use `viewModelScope` or injected scopes.
   - Suspend functions must be main-safe (`withContext(ioDispatcher)`).

---

## 8. Jetpack Compose & Compose Multiplatform (CMP) Standards

1. **Stateless Composables & State Hoisting**:
   - Every screen consists of a **Stateful Route** and a **Stateless Screen**.
   - Never pass ViewModels down into nested composable hierarchies.
2. **Recomposition Performance & Stability**:
   - Supply stable keys to `LazyColumn`, `LazyRow`, and `items(..., key = { it.id })`.
   - Use `remember(key)` or `derivedStateOf` for expensive calculations.
3. **Edge-to-Edge & System Insets**:
   - Apply insets defensively using `Modifier.statusBarsPadding()`, `Modifier.navigationBarsPadding()`, or `Modifier.windowInsetsPadding(WindowInsets.safeDrawing)`.
4. **Minimum Touch Targets & Accessibility**:
   - Interactive elements must maintain at least **48.dp** touch target (`Modifier.minimumInteractiveComponentSize()`).
   - Every semantic `Icon`/`Image` must have a localized `contentDescription` for screen readers (only `null` for purely decorative items).

---

## 9. Project-Specific Execution Rules (Countries)

- **Target / Compile SDKs**: Target SDK is **36**, Compile SDK is **37**, minSdk is **29**.
- **Dependency Management**: Gradle Version Catalog (`gradle/libs.versions.toml`) for all dependencies.
- **UI Stack**: Modern Compose stack with Compose Compiler plugin (`org.jetbrains.kotlin.plugin.compose`) and Compose BOM.
- **Serialization**: `kotlinx.serialization` for JSON parsing across data models.
- **Build & Verification Restrictions**:
  - Do NOT build full APKs (`assembleDebug`, `packageDebug`, `installDebug`) and do NOT run remote or device tests unless explicitly requested.
  - After code updates, run standard compile tasks separately:
    - `./gradlew :composeApp:compileDebugKotlinAndroid`
    - `./gradlew :composeApp:compileKotlinWasmJs`
  - Never run target compile tasks in one Gradle invocation or concurrently.
  - Treat an observed `BUILD SUCCESSFUL` marker as completed verification.
- **Quota Optimization**: Be token-efficient, concise, avoid redundant checks, never run multiple compilation cycles unless asked.
- **AI Reasoning & Thought Formatting**: Wrap step-by-step internal analysis inside `<thought>...</thought>` tags.
- **Single Source of Truth**: All roadmap items and task progress MUST be read from and updated in `ROADMAP.md` at project root (`D:/Android/Projects/countries-compose/ROADMAP.md`).

---

## 🛡️ 10. CRITICAL ARCHITECTURAL INVARIANTS (DO NOT BREAK)

### 1. Dual-Layer Hybrid Rendering Engine
- **Underneath**: Hardware-accelerated 3D sphere rendered via OpenGL ES on Android (`EarthGLRenderer.kt` in `androidMain`) and WebGL on Web (`PlanetWebGLRenderer.kt` in `wasmJsMain`).
- **On Top**: Jetpack Compose `Canvas` overlay in `commonMain` (`GlobeView.kt`) projecting dynamic vector data: country borders, ISS orbital tracks, flight paths, Apollo landing sites, and weather particles.
- **DO NOT** replace the 3D OpenGL/WebGL sphere with 2D Canvas drawing, and do not remove 2D Canvas overlays. Synchronize layers via shared camera state (`rotationX`, `rotationY`, `zoom`).

### 2. 3D Camera & Euler Math Invariants
- **Yaw-First Euler Order ($R_Y \to R_X$)**: Globe camera rotation and country auto-centering MUST apply Yaw ($R_Y$) first, then Pitch ($R_X$). Never change to Pitch-first ($R_X \to R_Y$).
- **Latitude Inversion**: For spherical projection, positive latitude maps to negative pitch.
- **Projection Function**: 2D overlay alignment depends on `forwardProject(lat, lon, rotationX, rotationY, zoom, radius, center)`. Any change to 3D matrix math in renderers must be symmetrically mirrored in `forwardProject()`.

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
- Avoid introducing JVM- or Android-specific APIs (`java.time.*`, `android.graphics.*`, `java.util.*`) into `commonMain`. Use `kotlinx-datetime` and Compose multiplatform primitives.

### 5. Unified Adaptive Sheet & Close Button Design System
- All inspector and detail interfaces MUST use Material 3 sheets with a drag handle and transparent scrim via `AdaptiveInfoSheet.kt`.
- **Minimalist Close Button**: Every sheet and top-level overlay MUST use `MinimalistCloseButton.kt` (36dp touch target, resolution-independent vector cross `#94A3B8`). Never use raw text characters like `"X"` or `"✕"`.

### 6. Frame Rate & Battery Optimization (Sheet Open State)
- When an inspector sheet or full overlay is open (`isSheetOpen == true`), background animations and GL rendering loops MUST be paused or reduced (`RENDERMODE_WHEN_DIRTY`).
- Exception: When `showTimeMachine` (Planetary Time) is active, GL rendering stays active (`isPageActive = true`) so dragging time sliders updates 3D solar lighting and day/night splits live.

### 7. Flight Simulator InfiniteTransition Reactivity
- Aircraft animation progress in `GlobeView.kt` is wrapped in `key(isSupersonic)` to properly recreate `rememberInfiniteTransition` when supersonic mode toggles. Do NOT remove this `key()` wrapper.

### 8. Live Data Loading & Session Cache
- Network calls must not block UI rendering or camera flight transitions. Show loading/skeleton state while live data loads, then show available values or an unavailable/empty state when no value exists.
- Live country details use an in-memory session cache in the country-detail repository. Failed fetches are not cached as successful results.

### 9. Clean Architecture Package Hierarchy & Expect/Actual Parity
- **Package Hierarchy**:
  - `com.dirzaaulia.countries.data.*`: External data sources, Ktor client, and repository implementations.
  - `com.dirzaaulia.countries.domain.*`: Pure multiplatform business logic, astronomy math (`AstronomyMath.kt`), domain models (`Country.kt`, `MoonLandmark.kt`), shaders (`GlobeShaders.kt`), mesh generation (`SphereMesh.kt`), and spherical coordinates (`SphericalMath.kt`, `GlobeState.kt`).
  - `com.dirzaaulia.countries.platform.*`: Cross-platform abstractions (`Globe3DPlatformView`, `PlatformSymbols`, `PlatformStartup`, `PlatformTime`, `PlatformHttpClient`).
  - `com.dirzaaulia.countries.di.*`: Dependency injection (Koin `AppModule.kt`).
  - `com.dirzaaulia.countries.ui.*`: UI screens, HUD overlays, M3 sheets, and ViewModels.
  - `com.dirzaaulia.countries.util.*`: Pure multiplatform formatters and helpers.
- **Expect/Actual Package Parity**:
  - Every `expect` in `commonMain` and its corresponding `actual` in `androidMain` and `wasmJsMain` **MUST reside in the exact same package** (`com.dirzaaulia.countries.platform`) and matching folder structure.
