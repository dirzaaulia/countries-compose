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
- After code updates, only run standard compile tasks (e.g. `./gradlew :app:compileDebugKotlinAndroid` and `./gradlew :app:compileKotlinWasmJs`) to ensure there are no compilation errors.
- Quota Optimization: Be extremely token-efficient, concise, avoid redundant checks, never run multiple compilation cycles unless asked, and avoid unnecessary tool calls.
- Single Source of Truth: All feature roadmap items, task progress, and technical architecture plans MUST be read from and updated in `ROADMAP.md` at project root (`D:/Android/Projects/countries-compose/ROADMAP.md`), NOT inside agent-private or isolated brain directories.
