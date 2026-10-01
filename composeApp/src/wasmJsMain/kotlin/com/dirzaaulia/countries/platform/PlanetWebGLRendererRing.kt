package com.dirzaaulia.countries.platform

import com.dirzaaulia.countries.domain.globe.GlobeShaders
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.SaturnRingMesh
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLShader
import org.khronos.webgl.WebGLUniformLocation
import org.khronos.webgl.set
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Saturn ring rendering state for the WebGL renderer.
 * Lifecycle: init once via [initWebGLRing], draw each frame via [drawWebGLRings].
 */
internal class RingWebGLState {
    var program: WebGLProgram? = null
    var uRingMVPLoc: WebGLUniformLocation? = null
    var uRingSunLoc: WebGLUniformLocation? = null
    var aPositionLoc = 0
    var aTexCoordLoc = 0
    var positionBuffer: WebGLBuffer? = null
    var texCoordBuffer: WebGLBuffer? = null
    var indexBuffer: WebGLBuffer? = null
    var ringIndexCount = 0
}

internal fun RingWebGLState.initWebGLRing(gl: WebGLRenderingContext) {
    val mesh = SaturnRingMesh()
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

/** Draws the Saturn ring disc. Call immediately after the sphere drawElements. */
internal fun RingWebGLState.drawWebGLRings(
    gl: WebGLRenderingContext,
    projectionMatrix: FloatArray,
    modelPitch: Float,
    modelYaw: Float,
    currentRadius: Float,
    sunEye: Point3D,
) {
    val prog = program ?: return

    gl.enable(WebGLRenderingContext.BLEND)
    gl.blendFunc(WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.ONE_MINUS_SRC_ALPHA)
    gl.disable(WebGLRenderingContext.CULL_FACE)

    val ringModel = ringIdentity()
    ringRotateX(ringModel, modelPitch)
    ringRotateY(ringModel, modelYaw)
    ringRotateX(ringModel, 26.73f) // Saturn axial tilt
    ringScale(ringModel, currentRadius)

    val ringMVP = FloatArray(16)
    ringMultiply(ringMVP, projectionMatrix, ringModel)

    gl.useProgram(prog)
    val mvpArr = Float32Array(16).also { a -> for (i in 0..15) a[i] = ringMVP[i] }
    gl.uniformMatrix4fv(uRingMVPLoc, false, mvpArr)
    gl.uniform3f(uRingSunLoc, sunEye.x.toFloat(), sunEye.y.toFloat(), sunEye.z.toFloat())

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

// ---- Minimal column-major 4x4 matrix helpers (ring-local, no Android.Matrix) ----

private fun ringIdentity() =
    FloatArray(16).also { m ->
        m[0] = 1f
        m[5] = 1f
        m[10] = 1f
        m[15] = 1f
    }

private fun ringRotateX(
    m: FloatArray,
    angleDeg: Float,
) {
    val rad = angleDeg * PI.toFloat() / 180f
    val c = cos(rad)
    val s = sin(rad)
    val tmp = ringIdentity()
    tmp[5] = c
    tmp[9] = -s
    tmp[6] = s
    tmp[10] = c
    val r = FloatArray(16)
    ringMultiply(r, m, tmp)
    r.copyInto(m)
}

private fun ringRotateY(
    m: FloatArray,
    angleDeg: Float,
) {
    val rad = angleDeg * PI.toFloat() / 180f
    val c = cos(rad)
    val s = sin(rad)
    val tmp = ringIdentity()
    tmp[0] = c
    tmp[8] = s
    tmp[2] = -s
    tmp[10] = c
    val r = FloatArray(16)
    ringMultiply(r, m, tmp)
    r.copyInto(m)
}

private fun ringScale(
    m: FloatArray,
    s: Float,
) {
    for (i in 0..3) {
        m[i] *= s
        m[4 + i] *= s
        m[8 + i] *= s
    }
}

private fun ringMultiply(
    result: FloatArray,
    lhs: FloatArray,
    rhs: FloatArray,
) {
    for (i in 0..3) {
        val r0 = rhs[i * 4]
        val r1 = rhs[i * 4 + 1]
        val r2 = rhs[i * 4 + 2]
        val r3 = rhs[i * 4 + 3]
        for (j in 0..3) {
            result[i * 4 + j] = lhs[j] * r0 + lhs[4 + j] * r1 + lhs[8 + j] * r2 + lhs[12 + j] * r3
        }
    }
}
