package com.dirzaaulia.countries.platform

import android.opengl.GLES20

/**
 * Manages the 3 pending-texture slots for EarthGLRenderer.
 * Decouples texture upload from the main renderer to keep EarthGLRenderer under 300 lines.
 */
internal class EarthGLTextureManager {
    @Volatile var pendingDayBytes: ByteArray? = null
    @Volatile var pendingNightBytes: ByteArray? = null
    @Volatile var pendingCloudBytes: ByteArray? = null

    var dayTextureId = 0
        private set
    var nightTextureId = 0
        private set
    var cloudTextureId = 0
        private set

    fun setTextures(
        dayBytes: ByteArray,
        nightBytes: ByteArray? = null,
        cloudBytes: ByteArray? = null,
    ) {
        pendingDayBytes = dayBytes
        pendingNightBytes = nightBytes
        pendingCloudBytes = cloudBytes
    }

    /** Must be called on the GL thread (inside onDrawFrame). */
    fun uploadPending() {
        val dBytes = pendingDayBytes
        if (dBytes != null) {
            pendingDayBytes = null
            if (dayTextureId != 0) GLES20.glDeleteTextures(1, intArrayOf(dayTextureId), 0)
            dayTextureId = loadGLTexture(dBytes)
        }
        val nBytes = pendingNightBytes
        if (nBytes != null) {
            pendingNightBytes = null
            if (nightTextureId != 0) GLES20.glDeleteTextures(1, intArrayOf(nightTextureId), 0)
            nightTextureId = loadGLTexture(nBytes)
        }
        val cBytes = pendingCloudBytes
        if (cBytes != null) {
            pendingCloudBytes = null
            if (cloudTextureId != 0) GLES20.glDeleteTextures(1, intArrayOf(cloudTextureId), 0)
            cloudTextureId = loadGLTexture(cBytes)
        }
    }
}
