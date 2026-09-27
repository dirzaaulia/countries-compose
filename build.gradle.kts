// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
}

// Universal Gradle Line Budget Gate Task
tasks.register("checkLineBudget") {
    group = "verification"
    description = "Enforces clean architecture line limits (max 400 lines per Kotlin file)."
    notCompatibleWithConfigurationCache("Accesses project layout at execution time")
    doLast {
        val maxLines = 400
        val bloatedFiles = fileTree(rootDir) {
            include("**/src/**/*.kt")
            exclude(
                "**/build/**",
                "**/.gradle/**",
                "**/generated/**",
                "**/.idea/**",
                "**/platform/PlanetWebGLRenderer.kt",
                "**/platform/EarthGLRenderer.kt",
                "**/moon/MoonView.kt",
                "**/globe/GlobeView.kt",
                "**/overlay/GlobeOverlays.kt"
            )
        }.files.filter { it.readLines().size > maxLines }

        if (bloatedFiles.isNotEmpty()) {
            val message = buildString {
                appendLine("\n" + "=".repeat(75))
                appendLine("❌ BUILD BLOCKED: LINE BUDGET VIOLATION (Max allowed: $maxLines lines)")
                appendLine("=".repeat(75))
                appendLine("The following files are monolithic and must be decomposed before building:\n")
                bloatedFiles.forEach { file ->
                    val lineCount = file.readLines().size
                    val relativePath = file.relativeTo(rootDir).path
                    appendLine("  [FAIL] $relativePath -> $lineCount lines (+${lineCount - maxLines} over limit)")
                }
                appendLine("\n🔧 Required Decomposition Steps:")
                appendLine("  1. Screen files  -> Split into *Screen.kt, *Components.kt, *State.kt")
                appendLine("  2. ViewModels    -> Offload logic to Domain UseCases or Data Mappers")
                appendLine("  3. Composables   -> Extract sub-sections into dedicated helper composables")
                appendLine("=".repeat(75))
            }
            throw GradleException(message)
        }
    }
}

gradle.projectsEvaluated {
    allprojects {
        tasks.matching { task ->
            task.name in listOf("preBuild", "assemble", "build") ||
            task.name.startsWith("assemble") ||
            task.name.startsWith("compile") && task.name.endsWith("Kotlin")
        }.configureEach {
            dependsOn(":checkLineBudget")
        }
    }
}
