package com.dirzaaulia.countries.platform

import kotlinx.browser.window

private const val KEY_ONBOARDING_COMPLETED = "countries_onboarding_completed"

actual fun isFirstLaunchOnboardingCompleted(): Boolean =
    runCatching {
        window.localStorage.getItem(KEY_ONBOARDING_COMPLETED) == "true"
    }.getOrDefault(false)

actual fun markFirstLaunchOnboardingCompleted() {
    runCatching {
        window.localStorage.setItem(KEY_ONBOARDING_COMPLETED, "true")
    }
}
