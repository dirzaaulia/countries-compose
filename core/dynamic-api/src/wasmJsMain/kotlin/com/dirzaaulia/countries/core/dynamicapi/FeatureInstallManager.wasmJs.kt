package com.dirzaaulia.countries.core.dynamicapi

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

actual class FeatureInstallManager actual constructor() {
    private val _installState = MutableStateFlow<Map<String, FeatureInstallState>>(emptyMap())
    actual val installState: StateFlow<Map<String, FeatureInstallState>> = _installState.asStateFlow()

    actual fun isFeatureInstalled(featureId: String): Boolean = _installState.value[featureId]?.status == FeatureInstallStatus.INSTALLED || true

    actual suspend fun requestFeatureInstall(featureId: String): Boolean {
        _installState.update { current ->
            current + (featureId to FeatureInstallState(featureId = featureId, status = FeatureInstallStatus.DOWNLOADING, progressFraction = 0.2f))
        }
        delay(300)
        _installState.update { current ->
            current + (featureId to FeatureInstallState(featureId = featureId, status = FeatureInstallStatus.DOWNLOADING, progressFraction = 0.6f))
        }
        delay(300)
        _installState.update { current ->
            current + (featureId to FeatureInstallState(featureId = featureId, status = FeatureInstallStatus.INSTALLED, progressFraction = 1.0f))
        }
        return true
    }

    actual suspend fun requestFeatureUninstall(featureId: String): Boolean {
        _installState.update { current ->
            current + (featureId to FeatureInstallState(featureId = featureId, status = FeatureInstallStatus.NOT_INSTALLED, progressFraction = 0f))
        }
        return true
    }
}
