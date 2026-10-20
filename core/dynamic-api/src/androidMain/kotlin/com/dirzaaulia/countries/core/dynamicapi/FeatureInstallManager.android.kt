package com.dirzaaulia.countries.core.dynamicapi

import android.content.Context
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

var dynamicApiContext: Context? = null

actual class FeatureInstallManager actual constructor() {
    private val splitInstallManager: SplitInstallManager? by lazy {
        dynamicApiContext?.let { SplitInstallManagerFactory.create(it) }
    }

    private val _installState = MutableStateFlow<Map<String, FeatureInstallState>>(emptyMap())
    actual val installState: StateFlow<Map<String, FeatureInstallState>> = _installState.asStateFlow()

    private val listener =
        SplitInstallStateUpdatedListener { state ->
            val featureNames = state.moduleNames()
            val status =
                when (state.status()) {
                    SplitInstallSessionStatus.INSTALLED -> FeatureInstallStatus.INSTALLED
                    SplitInstallSessionStatus.DOWNLOADING, SplitInstallSessionStatus.INSTALLING -> FeatureInstallStatus.DOWNLOADING
                    SplitInstallSessionStatus.FAILED, SplitInstallSessionStatus.CANCELED -> FeatureInstallStatus.FAILED
                    else -> FeatureInstallStatus.DOWNLOADING
                }
            val bytesDownloaded = state.bytesDownloaded()
            val totalBytesToDownload = state.totalBytesToDownload()
            val progress =
                if (totalBytesToDownload > 0L) {
                    (bytesDownloaded.toFloat() / totalBytesToDownload.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }

            featureNames.forEach { featureId ->
                _installState.update { current ->
                    current + (
                        featureId to
                            FeatureInstallState(
                                featureId = featureId,
                                status = status,
                                progressFraction = progress,
                                bytesDownloaded = bytesDownloaded,
                                totalBytesToDownload = totalBytesToDownload,
                                errorMessage = if (status == FeatureInstallStatus.FAILED) "Download failed (${state.errorCode()})" else null,
                            )
                    )
                }
            }
        }

    init {
        splitInstallManager?.registerListener(listener)
    }

    actual fun isFeatureInstalled(featureId: String): Boolean {
        val manager = splitInstallManager ?: return true
        return manager.installedModules.contains(featureId)
    }

    actual suspend fun requestFeatureInstall(featureId: String): Boolean {
        val manager = splitInstallManager ?: return true
        if (isFeatureInstalled(featureId)) {
            _installState.update { current ->
                current + (
                    featureId to
                        FeatureInstallState(
                            featureId = featureId,
                            status = FeatureInstallStatus.INSTALLED,
                            progressFraction = 1f,
                        )
                )
            }
            return true
        }

        _installState.update { current ->
            current + (
                featureId to
                    FeatureInstallState(
                        featureId = featureId,
                        status = FeatureInstallStatus.DOWNLOADING,
                        progressFraction = 0.05f,
                    )
            )
        }

        val request =
            SplitInstallRequest
                .newBuilder()
                .addModule(featureId)
                .build()

        return suspendCancellableCoroutine { continuation ->
            manager
                .startInstall(request)
                .addOnSuccessListener {
                    _installState.update { current ->
                        current + (
                            featureId to
                                FeatureInstallState(
                                    featureId = featureId,
                                    status = FeatureInstallStatus.INSTALLED,
                                    progressFraction = 1f,
                                )
                        )
                    }
                    if (continuation.isActive) continuation.resume(true)
                }.addOnFailureListener { exception ->
                    _installState.update { current ->
                        current + (
                            featureId to
                                FeatureInstallState(
                                    featureId = featureId,
                                    status = FeatureInstallStatus.FAILED,
                                    progressFraction = 0f,
                                    errorMessage = exception.message ?: "Failed to install dynamic module",
                                )
                        )
                    }
                    if (continuation.isActive) continuation.resume(false)
                }
        }
    }

    actual suspend fun requestFeatureUninstall(featureId: String): Boolean {
        val manager = splitInstallManager ?: return true
        return suspendCancellableCoroutine { continuation ->
            manager
                .deferredUninstall(listOf(featureId))
                .addOnSuccessListener {
                    _installState.update { current ->
                        current + (
                            featureId to
                                FeatureInstallState(
                                    featureId = featureId,
                                    status = FeatureInstallStatus.NOT_INSTALLED,
                                    progressFraction = 0f,
                                )
                        )
                    }
                    if (continuation.isActive) continuation.resume(true)
                }.addOnFailureListener {
                    if (continuation.isActive) continuation.resume(false)
                }
        }
    }
}
