package com.dirzaaulia.countries.core.dynamicapi

enum class FeatureInstallStatus {
    INSTALLED,
    DOWNLOADING,
    NOT_INSTALLED,
    FAILED,
}

data class FeatureInstallState(
    val featureId: String,
    val status: FeatureInstallStatus = FeatureInstallStatus.NOT_INSTALLED,
    val progressFraction: Float = 0f,
    val bytesDownloaded: Long = 0L,
    val totalBytesToDownload: Long = 0L,
    val errorMessage: String? = null,
)
