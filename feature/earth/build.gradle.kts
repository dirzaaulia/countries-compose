import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
}

kotlin {
    android {
        namespace = "com.dirzaaulia.countries.feature.earth"
        compileSdk = 37
        minSdk = 29
        androidResources.enable = true
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            api(project(":core:network"))
            api(project(":core:ui"))
            api(project(":feature:solarsystem"))
            api(project(":feature:weather"))
            api(project(":feature:satellite"))
            api(project(":feature:hazards"))
            api(project(":feature:economy"))
            api(project(":feature:quiz"))
            api(project(":feature:flight"))
            api(project(":feature:comparison"))
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.materialIconsExtended)
            api(compose.ui)
            api(compose.components.resources)
            api(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
            api(libs.androidx.lifecycle.viewmodel)
            api(libs.coil.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.ktor.client.okhttp)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint("1.5.0")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.5.0")
    }
}
