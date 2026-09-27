import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

kotlin {
    androidTarget()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("countries")
        browser {
            commonWebpackConfig {
                outputFileName = "countries.js"
            }
        }
        binaries.executable()
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
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
}

compose.resources {
    packageOfResClass = "com.dirzaaulia.countries.generated.resources"
}

android {

    namespace = "com.dirzaaulia.countries"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.dirzaaulia.countries"
        minSdk = 29
        targetSdk = 37
        val propVersionCode =
            providers.gradleProperty("VERSION_CODE").orNull?.toIntOrNull()
                ?: System.getenv("VERSION_CODE")?.toIntOrNull()
        versionCode = propVersionCode ?: 7

        val propVersionName =
            providers.gradleProperty("VERSION_NAME").orNull
                ?: System.getenv("VERSION_NAME")
        versionName = propVersionName ?: "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val localProperties =
        Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { stream ->
                    load(stream)
                }
            }
        }

    signingConfigs {
        create("release") {
            val keystorePath =
                localProperties.getProperty("KEYSTORE_FILE")
                    ?: providers.gradleProperty("KEYSTORE_FILE").orNull
                    ?: System.getenv("KEYSTORE_FILE")
                    ?: "keystore.jks"
            val keystoreFile = project.rootProject.file(keystorePath)
            val storePass =
                localProperties.getProperty("KEYSTORE_PASSWORD")
                    ?: providers.gradleProperty("KEYSTORE_PASSWORD").orNull
                    ?: System.getenv("KEYSTORE_PASSWORD")
                    ?: ""
            val alias =
                localProperties.getProperty("KEY_ALIAS")
                    ?: providers.gradleProperty("KEY_ALIAS").orNull
                    ?: System.getenv("KEY_ALIAS")
                    ?: ""
            val keyPass =
                localProperties.getProperty("KEY_PASSWORD")
                    ?: providers.gradleProperty("KEY_PASSWORD").orNull
                    ?: System.getenv("KEY_PASSWORD")
                    ?: ""

            if (keystoreFile.exists() && storePass.isNotEmpty()) {
                storeFile = keystoreFile
                storePassword = storePass
                keyAlias = alias
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile?.exists() == true) {
                signingConfig = releaseSigning
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

dependencies {
    detektPlugins(libs.detekt.compose.rules)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    parallel = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}
