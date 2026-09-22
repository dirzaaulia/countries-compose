import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties
import java.io.File

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
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
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
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

compose.resources {
    packageOfResClass = "com.dirzaaulia.countries.generated.resources"
}

android {

    namespace = "com.dirzaaulia.countries"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.dirzaaulia.countries"
        minSdk = 29
        targetSdk = 36
        val propVersionCode = providers.gradleProperty("VERSION_CODE").orNull?.toIntOrNull()
            ?: System.getenv("VERSION_CODE")?.toIntOrNull()
        versionCode = propVersionCode ?: 7
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val localProperties = Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream ->
                load(stream)
            }
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = localProperties.getProperty("KEYSTORE_FILE")
                ?: providers.gradleProperty("KEYSTORE_FILE").orNull
                ?: System.getenv("KEYSTORE_FILE")
                ?: "keystore.jks"
            val keystoreFile = project.rootProject.file(keystorePath)
            val storePass = localProperties.getProperty("KEYSTORE_PASSWORD")
                ?: providers.gradleProperty("KEYSTORE_PASSWORD").orNull
                ?: System.getenv("KEYSTORE_PASSWORD")
                ?: ""
            val alias = localProperties.getProperty("KEY_ALIAS")
                ?: providers.gradleProperty("KEY_ALIAS").orNull
                ?: System.getenv("KEY_ALIAS")
                ?: ""
            val keyPass = localProperties.getProperty("KEY_PASSWORD")
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
