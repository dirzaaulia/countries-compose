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
    androidLibrary {
        namespace = "com.dirzaaulia.countries"
        compileSdk = 37
        minSdk = 29
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.chucker)
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

compose.resources {
    packageOfResClass = "com.dirzaaulia.countries.generated.resources"
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
