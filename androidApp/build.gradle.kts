import java.util.Properties

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream ->
                load(stream)
            }
        }
    }

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.spotless)
    alias(libs.plugins.gpp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.dirzaaulia.countries"
    compileSdk = 37
    dynamicFeatures +=
        setOf(
            ":dfm:mars",
            ":dfm:deepspace",
            ":dfm:weather",
            ":dfm:satellite",
            ":dfm:hazards",
            ":dfm:economy",
            ":dfm:moon",
            ":dfm:quiz",
            ":dfm:flight",
            ":dfm:comparison",
        )

    defaultConfig {
        applicationId = "com.dirzaaulia.countries"
        minSdk = 29
        targetSdk = 37

        val versionPropsFile = rootProject.file("version.properties")
        val versionProps =
            Properties().apply {
                if (!versionPropsFile.exists()) {
                    versionPropsFile.writeText("VERSION_MAJOR=1\nVERSION_MINOR=0\nVERSION_PATCH=0\nVERSION_BUILD=7\n")
                }
                if (versionPropsFile.exists()) {
                    versionPropsFile.inputStream().use { load(it) }
                }
            }

        val major = versionProps.getProperty("VERSION_MAJOR", "1").toInt()
        val minor = versionProps.getProperty("VERSION_MINOR", "0").toInt()
        val patch = versionProps.getProperty("VERSION_PATCH", "0").toInt()
        val buildNumber = (
            System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()
                ?: versionProps.getProperty("VERSION_BUILD", "7").toInt()
        )

        val targetTrack = providers.gradleProperty("track").getOrElse("internal")

        versionCode = (major * 1_000_000) + (minor * 10_000) + (patch * 100) + buildNumber
        versionName =
            when (targetTrack.lowercase()) {
                "production", "prod" -> "$major.$minor.$patch"
                "beta" -> "$major.$minor.$patch-beta.$buildNumber"
                "alpha" -> "$major.$minor.$patch-alpha.$buildNumber"
                else -> "$major.$minor.$patch-internal.$buildNumber"
            }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

dependencies {
    implementation(project(":composeApp"))
    implementation(project(":core:dynamic-api"))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
    debugImplementation(libs.androidx.compose.ui.test.manifest)
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

play {
    val serviceAccountPath =
        localProperties.getProperty("PLAY_SERVICE_ACCOUNT")
            ?: System.getenv("PLAY_SERVICE_ACCOUNT")
            ?: "service-account.json"
    val credFile = project.rootProject.file(serviceAccountPath)
    if (credFile.exists()) {
        serviceAccountCredentials.set(credFile)
        resolutionStrategy.set(com.github.triplet.gradle.androidpublisher.ResolutionStrategy.AUTO)
    } else {
        resolutionStrategy.set(com.github.triplet.gradle.androidpublisher.ResolutionStrategy.IGNORE)
    }
    defaultToAppBundles.set(true)
    track.set("internal")
}

tasks.register("incrementBuildNumber") {
    doLast {
        val versionPropsFile = rootProject.file("version.properties")
        val versionProps = Properties()
        if (versionPropsFile.exists()) {
            versionPropsFile.inputStream().use { versionProps.load(it) }
            val current = versionProps.getProperty("VERSION_BUILD", "7").toInt()
            val newBuild = current + 1
            versionProps.setProperty("VERSION_BUILD", newBuild.toString())
            versionPropsFile.outputStream().use { versionProps.store(it, "Auto-incremented by build runner") }
            println("🚀 Incremented build number to $newBuild")
            
            val major = versionProps.getProperty("VERSION_MAJOR", "1").toInt()
            val minor = versionProps.getProperty("VERSION_MINOR", "0").toInt()
            val patch = versionProps.getProperty("VERSION_PATCH", "0").toInt()
            val targetTrack = providers.gradleProperty("track").getOrElse("internal")
            
            val versionName = when (targetTrack.lowercase()) {
                "production", "prod" -> "$major.$minor.$patch"
                "beta" -> "$major.$minor.$patch-beta.$newBuild"
                "alpha" -> "$major.$minor.$patch-alpha.$newBuild"
                else -> "$major.$minor.$patch-internal.$newBuild"
            }
            
            val appInfoSheetFile = rootProject.file("core/ui/src/commonMain/kotlin/com/dirzaaulia/countries/ui/components/AppInfoSheet.kt")
            if (appInfoSheetFile.exists()) {
                val content = appInfoSheetFile.readText()
                val updatedContent = content.replace(Regex("""text = "v[\d\.\-a-zA-Z]+""""), """text = "v$versionName"""")
                appInfoSheetFile.writeText(updatedContent)
                println("🚀 Updated AppInfoSheet.kt version to v$versionName")
            }
        }
    }
}

tasks.matching { it.name.startsWith("publish") && it.name.contains("Bundle") }.configureEach {
    dependsOn("incrementBuildNumber")
}
