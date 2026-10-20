package com.dirzaaulia.countries.core.dynamicapi

import kotlinx.coroutines.flow.StateFlow

expect class FeatureInstallManager() {
    val installState: StateFlow<Map<String, FeatureInstallState>>

    fun isFeatureInstalled(featureId: String): Boolean

    suspend fun requestFeatureInstall(featureId: String): Boolean

    suspend fun requestFeatureUninstall(featureId: String): Boolean
}
