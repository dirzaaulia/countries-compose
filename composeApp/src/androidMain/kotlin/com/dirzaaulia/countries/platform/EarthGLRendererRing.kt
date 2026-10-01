package com.dirzaaulia.countries.platform

import android.opengl.GLES20
import android.opengl.Matrix
import com.dirzaaulia.countries.domain.globe.GlobeShaders
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.SaturnRingMesh
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Saturn ring rendering state held by EarthGLRenderer.
 * Populated once in onSurfaceCreated; mutated only on the GL thread.
 */
internal class RingGLState {
    var programId = 0
    var uRingMVPLoc = 0
    var uRingSunLoc = 0
    var aPositionLoc = 0
    var aTexCoordLoc = 0
    var ringIndexCount = 0
    var vertexBuffer: FloatBuffer? = null
    var texCoordBuffer: FloatBuffer? = null
    var indexBuffer: ShortBuffer? = null
}

/** Compiles ring shader program and uploads VBOs from [SaturnRingMesh]. */
internal fun RingGLState.initRing() {
    val mesh = SaturnRingMesh()
    ringIndexCount = mesh.indices.size

    vertexBuffer =
        ByteBuffer
            .allocateDirect(mesh.vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(mesh.vertices)
                position(0)
            }

    texCoordBuffer =
        ByteBuffer
            .allocateDirect(mesh.texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(mesh.texCoords)
                position(0)
            }

    indexBuffer =
        ByteBuffer
            .allocateDirect(mesh.indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
                put(mesh.indices)
                position(0)
            }

    val vs = compileRingShader(GLES20.GL_VERTEX_SHADER, GlobeShaders.RING_VERTEX_SHADER)
    val fs = compileRingShader(GLES20.GL_FRAGMENT_SHADER, GlobeShaders.RING_FRAGMENT_SHADER)
    programId = GLES20.glCreateProgram()
    GLES20.glAttachShader(programId, vs)
    GLES20.glAttachShader(programId, fs)
    GLES20.glLinkProgram(programId)

    uRingMVPLoc = GLES20.glGetUniformLocation(programId, "u_RingMVP")
    uRingSunLoc = GLES20.glGetUniformLocation(programId, "u_RingSun")
    aPositionLoc = GLES20.glGetAttribLocation(programId, "a_Position")
    aTexCoordLoc = GLES20.glGetAttribLocation(programId, "a_TexCoord")
}

private fun compileRingShader(
    type: Int,
    src: String,
): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, src)
    GLES20.glCompileShader(shader)
    return shader
}

/**
 * Draws the Saturn ring disc as a second draw call.
 *
 * @param projectionMatrix  Orthographic projection (from EarthGLRenderer)
 * @param viewMatrix        Camera orbital view matrix (from EarthGLRenderer)
 * @param currentRadius     Pixel-space scaled planet radius
 * @param sunEye            Sun direction in eye space
 */
internal fun RingGLState.drawRings(
    projectionMatrix: FloatArray,
    viewMatrix: FloatArray,
    currentRadius: Float,
    sunEye: Point3D,
) {
    if (programId == 0 || vertexBuffer == null) return

    GLES20.glEnable(GLES20.GL_BLEND)
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    GLES20.glDisable(GLES20.GL_CULL_FACE)

    val ringModel = FloatArray(16)
    Matrix.setIdentityM(ringModel, 0)
    // NASA Eyes model: Saturn's rings stay fixed in space at Saturn's 26.73° equatorial tilt
    Matrix.rotateM(ringModel, 0, 26.73f, 1f, 0f, 0f)
    Matrix.scaleM(ringModel, 0, currentRadius, currentRadius, currentRadius)

    val ringMV = FloatArray(16)
    Matrix.multiplyMM(ringMV, 0, viewMatrix, 0, ringModel, 0)
    val ringMVP = FloatArray(16)
    Matrix.multiplyMM(ringMVP, 0, projectionMatrix, 0, ringMV, 0)

    GLES20.glUseProgram(programId)
    GLES20.glUniformMatrix4fv(uRingMVPLoc, 1, false, ringMVP, 0)
    GLES20.glUniform3f(uRingSunLoc, sunEye.x.toFloat(), sunEye.y.toFloat(), sunEye.z.toFloat())

    GLES20.glEnableVertexAttribArray(aPositionLoc)
    GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

    GLES20.glEnableVertexAttribArray(aTexCoordLoc)
    GLES20.glVertexAttribPointer(aTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

    GLES20.glDrawElements(
        GLES20.GL_TRIANGLES,
        ringIndexCount,
        GLES20.GL_UNSIGNED_SHORT,
        indexBuffer,
    )

    GLES20.glDisableVertexAttribArray(aPositionLoc)
    GLES20.glDisableVertexAttribArray(aTexCoordLoc)
    GLES20.glEnable(GLES20.GL_CULL_FACE)
    GLES20.glDisable(GLES20.GL_BLEND)
}
