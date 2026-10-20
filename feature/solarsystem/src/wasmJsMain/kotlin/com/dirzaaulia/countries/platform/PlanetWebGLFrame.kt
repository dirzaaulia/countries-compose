package com.dirzaaulia.countries.platform

import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.Matrix4
import com.dirzaaulia.countries.domain.globe.OrbitCamera
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import kotlinx.browser.window
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.set
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal class PlanetWebGLFrame {
    var planetType = 0f
    var isMoonMode = false
    var isMarsMode = false
    var moonPhaseAngle = 0.0
    var moonSubsolarLatitude = 0.0
    var moonLibrationLatitude = 0.0
    var moonLibrationLongitude = 0.0
    var rotationX = 0f
    var rotationY = 0f
    var zoom = 1f
    var sunPosition: SunPosition = AstronomyMath.calculateSunPosition()
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private var currentRadius = 0f

    fun render(
        canvas: HTMLCanvasElement,
        scene: PlanetWebGLScene,
        textures: PlanetWebGLTextures,
        rings: RingWebGLState,
        width: Int,
        height: Int,
    ) {
        val gl = scene.gl
        resizeCanvas(canvas, width, height)
        gl.viewport(0, 0, canvas.width, canvas.height)
        gl.clear(WebGLRenderingContext.COLOR_BUFFER_BIT or WebGLRenderingContext.DEPTH_BUFFER_BIT)
        val day = textures.day(activePlanet()) ?: return
        gl.useProgram(scene.program)
        prepareMatrices(canvas.width, canvas.height)
        val sunEye = sunInEyeSpace()
        uploadUniforms(scene, sunEye)
        scene.bindTextures(day, textures.night ?: textures.black, textures.clouds ?: textures.black)
        scene.draw()
        if (planetType == 6f || planetType == 7f || planetType == 8f) {
            rings.drawWebGLRings(gl, projectionMatrix, viewMatrix, currentRadius, sunEye, planetType)
        }
    }

    private fun activePlanet(): PlanetId =
        when {
            planetType in 3.5f..4.5f -> PlanetId.VENUS
            planetType in 4.5f..5.5f -> PlanetId.JUPITER
            planetType in 5.5f..6.5f -> PlanetId.SATURN
            planetType in 6.5f..7.5f -> PlanetId.URANUS
            planetType in 7.5f..8.5f -> PlanetId.NEPTUNE
            planetType in 2.5f..3.5f -> PlanetId.MERCURY
            isMarsMode || planetType in 1.5f..2.5f -> PlanetId.MARS
            isMoonMode || planetType in 0.5f..1.5f -> PlanetId.MOON
            else -> PlanetId.EARTH
        }

    private fun resizeCanvas(
        canvas: HTMLCanvasElement,
        width: Int,
        height: Int,
    ) {
        val dpr = window.devicePixelRatio.coerceAtLeast(1.0)
        val targetWidth = (width * dpr).toInt()
        val targetHeight = (height * dpr).toInt()
        if (canvas.width != targetWidth || canvas.height != targetHeight) {
            canvas.width = targetWidth
            canvas.height = targetHeight
        }
    }

    private fun prepareMatrices(
        width: Int,
        height: Int,
    ) {
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        Matrix4.ortho(projectionMatrix, -halfWidth, halfWidth, -halfHeight, halfHeight, -50000f, 50000f)
        OrbitCamera.computeViewMatrix(viewMatrix, rotationX, rotationY)
        Matrix4.setIdentity(modelMatrix)
        currentRadius = minOf(width, height) * 0.38f * zoom
        val tilt = axialTilt()
        if (tilt != 0f) Matrix4.rotateZ(modelMatrix, tilt)
        if (isMoonMode) {
            Matrix4.rotateX(modelMatrix, moonLibrationLatitude.toFloat())
            Matrix4.rotateY(modelMatrix, moonLibrationLongitude.toFloat())
        }
        Matrix4.scale(modelMatrix, currentRadius, currentRadius, currentRadius)
        Matrix4.multiply(mvMatrix, viewMatrix, modelMatrix)
        Matrix4.multiply(mvpMatrix, projectionMatrix, mvMatrix)
    }

    private fun axialTilt(): Float =
        when {
            isMoonMode || planetType in 0.5f..1.5f -> 1.54f
            planetType in 1.5f..2.5f -> 25.19f
            planetType in 2.5f..3.5f -> 0.03f
            planetType in 3.5f..4.5f -> 177.36f
            planetType in 4.5f..5.5f -> 3.13f
            planetType in 5.5f..6.5f -> 26.73f
            planetType in 6.5f..7.5f -> 97.77f
            planetType in 7.5f..8.5f -> 28.32f
            else -> 0f
        }

    private fun sunInEyeSpace(): Point3D {
        val world =
            if (isMoonMode) {
                val phase = moonPhaseAngle * PI / 180.0
                val latitude = moonSubsolarLatitude * PI / 180.0
                Point3D(
                    sin(phase) * cos(latitude),
                    sin(latitude),
                    cos(phase) * cos(latitude),
                )
            } else {
                sunPosition.vector
            }
        return OrbitCamera.worldToEyeSpace(world, rotationX, rotationY)
    }

    private fun uploadUniforms(
        scene: PlanetWebGLScene,
        sunEye: Point3D,
    ) {
        val gl = scene.gl
        gl.uniformMatrix4fv(scene.uniform("u_MVPMatrix"), false, matrixArray(mvpMatrix))
        gl.uniformMatrix4fv(scene.uniform("u_MVMatrix"), false, matrixArray(mvMatrix))
        gl.uniform1f(scene.uniform("u_CloudOffset"), 0f)
        gl.uniform1f(scene.uniform("u_IsMoon"), if (isMoonMode) 1f else 0f)
        gl.uniform1f(scene.uniform("u_IsMars"), if (isMarsMode) 1f else 0f)
        gl.uniform1f(scene.uniform("u_PlanetType"), planetType)
        gl.uniform3f(
            scene.uniform("u_SunDirection"),
            sunEye.x.toFloat(),
            sunEye.y.toFloat(),
            sunEye.z.toFloat(),
        )
    }

    private fun matrixArray(matrix: FloatArray): Float32Array {
        val result = Float32Array(16)
        for (i in 0..15) result[i] = matrix[i]
        return result
    }
}
