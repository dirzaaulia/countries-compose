package com.dirzaaulia.countries.platform

import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.ExperimentalWasmJsInterop

/** Hardware sphere behind the Compose overlay; texture loading and frame work have separate owners. */
@OptIn(ExperimentalWasmJsInterop::class)
object PlanetWebGLRenderer {
    private var canvas: HTMLCanvasElement? = null
    private var scene: PlanetWebGLScene? = null
    private var textures: PlanetWebGLTextures? = null
    private val frame = PlanetWebGLFrame()
    private val rings = RingWebGLState()

    fun setVisible(visible: Boolean) {
        canvas?.style?.display = if (visible) "block" else "none"
        if (!visible) {
            scene?.gl?.clear(
                WebGLRenderingContext.COLOR_BUFFER_BIT or WebGLRenderingContext.DEPTH_BUFFER_BIT,
            )
        }
    }

    fun ensureInitialized(scope: CoroutineScope) {
        if (scene != null) {
            loadCoreTextures(scope)
            return
        }
        val target = findOrCreateCanvas()
        val gl =
            target.getContext("webgl") as? WebGLRenderingContext
                ?: target.getContext("experimental-webgl") as? WebGLRenderingContext
                ?: return
        val created = PlanetWebGLScene(gl)
        if (created.program == null) return
        canvas = target
        scene = created
        rings.initWebGLRing(gl)
        textures = PlanetWebGLTextures(gl)
        loadCoreTextures(scope)
    }

    private fun loadCoreTextures(scope: CoroutineScope) {
        textures?.loadPlanet(scope, PlanetId.EARTH)
        textures?.loadPlanet(scope, PlanetId.MOON)
    }

    private fun findOrCreateCanvas(): HTMLCanvasElement {
        val existing = document.getElementById("PlanetCanvas") as? HTMLCanvasElement
        if (existing != null) return existing
        val target = document.createElement("canvas") as HTMLCanvasElement
        target.id = "PlanetCanvas"
        target.style.position = "absolute"
        target.style.top = "0px"
        target.style.left = "0px"
        target.style.width = "100%"
        target.style.height = "100%"
        target.style.zIndex = "0"
        target.style.display = "block"
        target.style.setProperty("pointer-events", "none")
        val composeTarget = document.getElementById("ComposeTarget")
        if (composeTarget != null) {
            document.body?.insertBefore(target, composeTarget)
        } else {
            document.body?.appendChild(target)
        }
        return target
    }

    fun loadPlanetTexture(
        scope: CoroutineScope,
        planetId: PlanetId,
    ) {
        textures?.loadPlanet(scope, planetId)
    }

    fun setPlanetMode(
        planetType: Float,
        isMoon: Boolean = false,
        isMars: Boolean = false,
        phaseAngle: Double = 0.0,
        subsolarLatitude: Double = 0.0,
        librationLatitude: Double = 0.0,
        librationLongitude: Double = 0.0,
    ) {
        frame.planetType = planetType
        frame.isMoonMode = isMoon
        frame.isMarsMode = isMars
        frame.moonPhaseAngle = phaseAngle
        frame.moonSubsolarLatitude = subsolarLatitude
        frame.moonLibrationLatitude = librationLatitude
        frame.moonLibrationLongitude = librationLongitude
    }

    fun updateCamera(
        rotX: Float,
        rotY: Float,
        zoom: Float,
    ) {
        frame.rotationX = rotX
        frame.rotationY = rotY
        frame.zoom = zoom
    }

    fun setSunPosition(position: SunPosition) {
        frame.sunPosition = position
    }

    fun render(
        viewportWidth: Int,
        viewportHeight: Int,
    ) {
        frame.render(
            canvas = canvas ?: return,
            scene = scene ?: return,
            textures = textures ?: return,
            rings = rings,
            width = viewportWidth,
            height = viewportHeight,
        )
    }
}
