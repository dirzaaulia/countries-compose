package com.dirzaaulia.countries.platform

import com.dirzaaulia.countries.domain.globe.GlobeShaders
import com.dirzaaulia.countries.domain.globe.Matrix4
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.PlanetaryRingMesh
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLShader
import org.khronos.webgl.WebGLUniformLocation
import org.khronos.webgl.set

/**
 * Saturn ring rendering state for the WebGL renderer.
 * Lifecycle: init once via [initWebGLRing], draw each frame via [drawWebGLRings].
 */
internal class RingWebGLState {
    var program: WebGLProgram? = null
    var uRingMVPLoc: WebGLUniformLocation? = null
    var uRingSunLoc: WebGLUniformLocation? = null
    var uPlanetTypeLoc: WebGLUniformLocation? = null
    var aPositionLoc = 0
    var aTexCoordLoc = 0
    var positionBuffer: WebGLBuffer? = null
    var texCoordBuffer: WebGLBuffer? = null
    var indexBuffer: WebGLBuffer? = null
    var ringIndexCount = 0
}

internal fun RingWebGLState.initWebGLRing(gl: WebGLRenderingContext) {
    val mesh = PlanetaryRingMesh()
    ringIndexCount = mesh.indices.size

    val posArray = Float32Array(mesh.vertices.size)
    for (i in mesh.vertices.indices) posArray[i] = mesh.vertices[i]
    positionBuffer = gl.createBuffer()
    gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, positionBuffer)
    gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, posArray, WebGLRenderingContext.STATIC_DRAW)

    val texArray = Float32Array(mesh.texCoords.size)
    for (i in mesh.texCoords.indices) texArray[i] = mesh.texCoords[i]
    texCoordBuffer = gl.createBuffer()
    gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, texCoordBuffer)
    gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, texArray, WebGLRenderingContext.STATIC_DRAW)

    val idxArray = Uint16Array(mesh.indices.size)
    for (i in mesh.indices.indices) idxArray[i] = mesh.indices[i]
    indexBuffer = gl.createBuffer()
    gl.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, indexBuffer)
    gl.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, idxArray, WebGLRenderingContext.STATIC_DRAW)

    val vs =
        compileRingWebGLShader(gl, WebGLRenderingContext.VERTEX_SHADER, GlobeShaders.RING_VERTEX_SHADER)
            ?: return
    val fs =
        compileRingWebGLShader(gl, WebGLRenderingContext.FRAGMENT_SHADER, GlobeShaders.RING_FRAGMENT_SHADER)
            ?: return
    val prog = gl.createProgram() ?: return
    gl.attachShader(prog, vs)
    gl.attachShader(prog, fs)
    gl.linkProgram(prog)
    program = prog

    uRingMVPLoc = gl.getUniformLocation(prog, "u_RingMVP")
    uRingSunLoc = gl.getUniformLocation(prog, "u_RingSun")
    uPlanetTypeLoc = gl.getUniformLocation(prog, "u_PlanetType")
    aPositionLoc = gl.getAttribLocation(prog, "a_Position")
    aTexCoordLoc = gl.getAttribLocation(prog, "a_TexCoord")
}

private fun compileRingWebGLShader(
    gl: WebGLRenderingContext,
    type: Int,
    src: String,
): WebGLShader? {
    val shader = gl.createShader(type) ?: return null
    gl.shaderSource(shader, src)
    gl.compileShader(shader)
    return shader
}

/** Draws the 3D ring disc. Call immediately after the sphere drawElements. */
internal fun RingWebGLState.drawWebGLRings(
    gl: WebGLRenderingContext,
    projectionMatrix: FloatArray,
    viewMatrix: FloatArray,
    currentRadius: Float,
    sunEye: Point3D,
    planetType: Float = 6f,
) {
    val prog = program ?: return

    gl.enable(WebGLRenderingContext.BLEND)
    gl.blendFunc(WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.ONE_MINUS_SRC_ALPHA)
    gl.disable(WebGLRenderingContext.CULL_FACE)

    val ringModel = Matrix4.identity()
    // NASA Eyes model: Rings stay fixed in space at each planet's equatorial tilt
    val ringTilt =
        when (planetType.toInt()) {
            7 -> 97.77f // Uranus vertical axial tilt
            8 -> 28.32f // Neptune axial tilt
            else -> 26.73f // Saturn axial tilt
        }
    Matrix4.rotateZ(ringModel, ringTilt)
    Matrix4.scale(ringModel, currentRadius, currentRadius, currentRadius)

    val ringMV = FloatArray(16)
    Matrix4.multiply(ringMV, viewMatrix, ringModel)
    val ringMVP = FloatArray(16)
    Matrix4.multiply(ringMVP, projectionMatrix, ringMV)

    gl.useProgram(prog)
    val mvpArr = Float32Array(16).also { a -> for (i in 0..15) a[i] = ringMVP[i] }
    gl.uniformMatrix4fv(uRingMVPLoc, false, mvpArr)
    gl.uniform3f(uRingSunLoc, sunEye.x.toFloat(), sunEye.y.toFloat(), sunEye.z.toFloat())
    gl.uniform1f(uPlanetTypeLoc, planetType)

    gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, positionBuffer)
    gl.enableVertexAttribArray(aPositionLoc)
    gl.vertexAttribPointer(aPositionLoc, 3, WebGLRenderingContext.FLOAT, false, 0, 0)

    gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, texCoordBuffer)
    gl.enableVertexAttribArray(aTexCoordLoc)
    gl.vertexAttribPointer(aTexCoordLoc, 2, WebGLRenderingContext.FLOAT, false, 0, 0)

    gl.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, indexBuffer)
    gl.drawElements(WebGLRenderingContext.TRIANGLES, ringIndexCount, WebGLRenderingContext.UNSIGNED_SHORT, 0)

    gl.disableVertexAttribArray(aPositionLoc)
    gl.disableVertexAttribArray(aTexCoordLoc)
    gl.enable(WebGLRenderingContext.CULL_FACE)
    gl.disable(WebGLRenderingContext.BLEND)
}
