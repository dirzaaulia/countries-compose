package com.dirzaaulia.countries.platform

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

var platformOnboardingContext: Context? = null

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "countries_onboarding_settings")
private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("key_onboarding_completed")

actual fun isFirstLaunchOnboardingCompleted(): Boolean {
    val ctx = platformOnboardingContext ?: return false
    return runBlocking {
        ctx.dataStore.data
            .map { prefs -> prefs[KEY_ONBOARDING_COMPLETED] ?: false }
            .first()
    }
}

actual fun markFirstLaunchOnboardingCompleted() {
    val ctx = platformOnboardingContext ?: return
    runBlocking {
        ctx.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = true
        }
    }
}
