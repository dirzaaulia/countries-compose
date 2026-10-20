plugins {
    id("com.android.dynamic-feature")
    alias(libs.plugins.spotless)
}

android {
    namespace = "com.dirzaaulia.countries.dfm.mars"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":androidApp"))
    implementation(project(":feature:mars"))
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
