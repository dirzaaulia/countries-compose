import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

kotlin {
    android {
        namespace = "com.dirzaaulia.countries.composeapp"
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
            api(project(":core:dynamic-api"))
            api(project(":feature:earth"))
            api(project(":feature:moon"))
            api(project(":feature:mars"))
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
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.serialization.kotlinx.json)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
            api(libs.androidx.lifecycle.viewmodel)
            api(libs.coil.compose)
            api(libs.coil.network.ktor)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.ktor.client.okhttp)
            compileOnly(libs.chucker)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit)
        }
    }
}

dependencies {
    detektPlugins(libs.detekt.compose.rules)
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

detekt {
    buildUponDefaultConfig = true
    allRules = false
    parallel = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}
