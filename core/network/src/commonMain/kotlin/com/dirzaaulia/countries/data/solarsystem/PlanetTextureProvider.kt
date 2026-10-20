package com.dirzaaulia.countries.data.solarsystem

import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.platform.platformHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Universal CDN Texture Provider for 3D Planetary Visualization.
 * Fetches renderer-ready NASA Eyes maps from the versioned Oracle CDN release.
 */
object PlanetTextureProvider {
    private val memoryCache = mutableMapOf<PlanetId, ByteArray>()
    private var cachedNightTexture: ByteArray? = null
    private var cachedCloudTexture: ByteArray? = null

    private val httpClient by lazy {
        HttpClient(platformHttpClientEngine()) {
            expectSuccess = true
        }
    }

    private val cdnMirrors =
        arrayOf(
            "https://files.fc.dirzaaulia.com/countries/textures/eyes-v1/",
            "https://files.168.110.196.30.sslip.io/countries/textures/eyes-v1/",
        )

    suspend fun getOrFetchTexture(planetId: PlanetId): ByteArray? =
        withContext(Dispatchers.Default) {
            val cached = memoryCache[planetId]
            if (cached != null) return@withContext cached

            val filename = resolveTextureFilename(planetId)
            val bytes = fetchFromCdn(filename)
            if (bytes != null && isValidImageBytes(bytes)) {
                memoryCache[planetId] = bytes
            }
            bytes
        }

    suspend fun getOrFetchEarthNightTexture(): ByteArray? =
        withContext(Dispatchers.Default) {
            val cached = cachedNightTexture
            if (cached != null) return@withContext cached

            val bytes = fetchFromCdn("earth_night.jpg")
            if (bytes != null && isValidImageBytes(bytes)) {
                cachedNightTexture = bytes
            }
            bytes
        }

    suspend fun getOrFetchEarthCloudTexture(): ByteArray? =
        withContext(Dispatchers.Default) {
            val cached = cachedCloudTexture
            if (cached != null) return@withContext cached

            val bytes = fetchFromCdn("earth_clouds.jpg")
            if (bytes != null && isValidImageBytes(bytes)) {
                cachedCloudTexture = bytes
            }
            bytes
        }

    fun getCachedTexture(planetId: PlanetId): ByteArray? = memoryCache[planetId]

    fun clearCache() {
        memoryCache.clear()
        cachedNightTexture = null
        cachedCloudTexture = null
    }

    private fun resolveTextureFilename(planetId: PlanetId): String =
        when (planetId) {
            PlanetId.EARTH -> "earth_day.jpg"
            PlanetId.MOON -> "moon.jpg"
            PlanetId.MARS -> "mars.jpg"
            PlanetId.MERCURY -> "mercury.jpg"
            PlanetId.VENUS -> "venus.jpg"
            PlanetId.JUPITER -> "jupiter.jpg"
            PlanetId.SATURN -> "saturn.jpg"
            PlanetId.URANUS -> "uranus.jpg"
            PlanetId.NEPTUNE -> "neptune.jpg"
            PlanetId.SUN -> "sun.jpg"
        }

    private fun isValidImageBytes(bytes: ByteArray): Boolean {
        if (bytes.size < 1024) return false
        val isJpeg = bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte()
        val isPng =
            bytes.size >= 4 &&
                bytes[0] == 0x89.toByte() &&
                bytes[1] == 0x50.toByte() &&
                bytes[2] == 0x4E.toByte() &&
                bytes[3] == 0x47.toByte()
        return isJpeg || isPng
    }

    private suspend fun fetchFromCdn(filename: String): ByteArray? {
        for (baseUrl in cdnMirrors) {
            try {
                val bytes = httpClient.get("$baseUrl$filename").body<ByteArray>()
                if (isValidImageBytes(bytes)) return bytes
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // Both mirrors serve the same converted release; never substitute a globe portrait.
            }
        }
        return null
    }
}
