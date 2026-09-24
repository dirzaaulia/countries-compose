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
- Quota Optimization: Be extremely token-efficient, concise, avoid redundant checks, never run multiple compilation cycles unless asked, and avoid unnecessary tool calls.
- Single Source of Truth: All feature roadmap items, task progress, and technical architecture plans MUST be read from and updated in `ROADMAP.md` at project root (`D:/Android/Projects/countries-compose/ROADMAP.md`), NOT inside agent-private or isolated brain directories.

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

### 5. Unified Modal Bottom Sheet & Close Button Design System
- All inspector and detail interfaces MUST use standard Material 3 `ModalBottomSheet` with drag handle and transparent scrim.
  - Examples: Country Dossier, ISS Telemetry, NASA EONET Hazards, Apollo Site Inspector, Flight Route Card, Map Legend.
- **DO NOT** revert inspectors into floating `Card`s, custom popup `Surface`s, or ad-hoc dialogs.
- **Minimalist Close Button**: Every sheet and top-level overlay MUST use `MinimalistCloseButton.kt` (36dp touch target, resolution-independent vector cross `#94A3B8`). Never use raw text characters like `"X"` or `"✕"`.

### 6. Frame Rate & Battery Optimization (Sheet Open State)
- When a sheet or full overlay is open (`isSheetOpen == true` or `selectedCountry != null`), background animations and GL rendering loops MUST be paused or reduced (`RENDERMODE_WHEN_DIRTY`).
- Infinite transitions must be tied to active lifecycles to avoid 60fps churn on mobile devices.

### 7. Flight Simulator InfiniteTransition Reactivity
- Aircraft animation progress in `GlobeView.kt` is wrapped in `key(isSupersonic)` to properly recreate `rememberInfiniteTransition` when supersonic mode toggles. Do NOT remove this `key()` wrapper, or speed toggle state changes will not take effect dynamically.

### 8. Offline-First Resilience & SWR Cache
- `GlobeRepository.kt` implements Stale-While-Revalidate (SWR) in-memory caching and procedural fallbacks for all countries and metrics.
- Network calls must never block UI rendering or camera flight transitions. The UI must show skeleton/cached data while streaming updates in the background.
