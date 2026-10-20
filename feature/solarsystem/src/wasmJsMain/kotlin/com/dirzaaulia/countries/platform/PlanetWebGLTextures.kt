package com.dirzaaulia.countries.platform

import com.dirzaaulia.countries.data.solarsystem.PlanetTextureProvider
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLTexture
import org.khronos.webgl.set
import org.w3c.dom.Image
import kotlin.coroutines.resume
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(bytes) => URL.createObjectURL(new Blob([bytes], { type: 'image/jpeg' }))")
private external fun createTextureBlobUrl(bytes: Uint8Array): String

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(url) => URL.revokeObjectURL(url)")
private external fun revokeTextureBlobUrl(url: String)

@OptIn(ExperimentalWasmJsInterop::class)
internal class PlanetWebGLTextures(
    private val gl: WebGLRenderingContext,
) {
    private val planets = mutableMapOf<PlanetId, WebGLTexture>()
    private val loading = mutableSetOf<PlanetId>()
    val black: WebGLTexture? = createBlackTexture()
    var night: WebGLTexture? = null
        private set
    var clouds: WebGLTexture? = null
        private set

    fun day(planetId: PlanetId): WebGLTexture? = planets[planetId]

    fun loadPlanet(
        scope: CoroutineScope,
        planetId: PlanetId,
    ) {
        if (planetId in loading) return
        if (planetId in planets && (planetId != PlanetId.EARTH || (night != null && clouds != null))) return
        loading.add(planetId)
        val job =
            scope.launch {
                try {
                    if (planetId !in planets) {
                        PlanetTextureProvider.getOrFetchTexture(planetId)?.let { bytes ->
                            upload(bytes)?.let { planets[planetId] = it }
                        }
                    }
                    if (planetId == PlanetId.EARTH) loadEarthLayers()
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (error: Exception) {
                    println("PlanetWebGLTextures: ${error.message}")
                }
            }
        job.invokeOnCompletion { loading.remove(planetId) }
    }

    private suspend fun loadEarthLayers() =
        coroutineScope {
            if (night == null) {
                launch {
                    PlanetTextureProvider.getOrFetchEarthNightTexture()?.let { night = upload(it) }
                }
            }
            if (clouds == null) {
                launch {
                    PlanetTextureProvider.getOrFetchEarthCloudTexture()?.let { clouds = upload(it) }
                }
            }
        }

    private suspend fun upload(bytes: ByteArray): WebGLTexture? =
        suspendCancellableCoroutine { continuation ->
            val pixels = Uint8Array(bytes.size)
            for (i in bytes.indices) pixels[i] = bytes[i]
            val url = createTextureBlobUrl(pixels)
            val image = Image()
            image.onload = {
                if (continuation.isActive) continuation.resume(uploadImage(image))
                revokeTextureBlobUrl(url)
            }
            image.onerror = { _, _, _, _, _ ->
                if (continuation.isActive) continuation.resume(null)
                revokeTextureBlobUrl(url)
                null
            }
            continuation.invokeOnCancellation {
                image.onload = null
                image.onerror = null
                revokeTextureBlobUrl(url)
            }
            image.src = url
        }

    private fun uploadImage(image: Image): WebGLTexture? {
        val texture = gl.createTexture() ?: return null
        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture)
        gl.texImage2D(
            WebGLRenderingContext.TEXTURE_2D,
            0,
            WebGLRenderingContext.RGBA,
            WebGLRenderingContext.RGBA,
            WebGLRenderingContext.UNSIGNED_BYTE,
            image,
        )
        setParameters(WebGLRenderingContext.LINEAR_MIPMAP_LINEAR)
        gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D)
        return texture
    }

    private fun createBlackTexture(): WebGLTexture? {
        val texture = gl.createTexture() ?: return null
        val pixel = Uint8Array(4)
        pixel[3] = 255.toByte()
        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture)
        gl.texImage2D(
            WebGLRenderingContext.TEXTURE_2D,
            0,
            WebGLRenderingContext.RGBA,
            1,
            1,
            0,
            WebGLRenderingContext.RGBA,
            WebGLRenderingContext.UNSIGNED_BYTE,
            pixel,
        )
        setParameters(WebGLRenderingContext.LINEAR)
        return texture
    }

    private fun setParameters(minFilter: Int) {
        gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, minFilter)
        gl.texParameteri(
            WebGLRenderingContext.TEXTURE_2D,
            WebGLRenderingContext.TEXTURE_MAG_FILTER,
            WebGLRenderingContext.LINEAR,
        )
        gl.texParameteri(
            WebGLRenderingContext.TEXTURE_2D,
            WebGLRenderingContext.TEXTURE_WRAP_S,
            WebGLRenderingContext.REPEAT,
        )
        gl.texParameteri(
            WebGLRenderingContext.TEXTURE_2D,
            WebGLRenderingContext.TEXTURE_WRAP_T,
            WebGLRenderingContext.CLAMP_TO_EDGE,
        )
    }
}
