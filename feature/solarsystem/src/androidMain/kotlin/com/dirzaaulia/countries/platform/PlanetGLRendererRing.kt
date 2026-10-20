package com.dirzaaulia.countries.platform

import android.opengl.GLES20
import com.dirzaaulia.countries.domain.globe.GlobeShaders
import com.dirzaaulia.countries.domain.globe.Matrix4
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.PlanetaryRingMesh
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Saturn ring rendering state held by PlanetGLRenderer.
 * Populated once in onSurfaceCreated; mutated only on the GL thread.
 */
internal class RingGLState {
    var programId = 0
    var uRingMVPLoc = 0
    var uRingSunLoc = 0
    var uPlanetTypeLoc = 0
    var aPositionLoc = 0
    var aTexCoordLoc = 0
    var ringIndexCount = 0
    var vertexBuffer: FloatBuffer? = null
    var texCoordBuffer: FloatBuffer? = null
    var indexBuffer: ShortBuffer? = null
}

/** Compiles ring shader program and uploads VBOs from [PlanetaryRingMesh]. */
internal fun RingGLState.initRing() {
    val mesh = PlanetaryRingMesh()
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
    uPlanetTypeLoc = GLES20.glGetUniformLocation(programId, "u_PlanetType")
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
 * @param projectionMatrix  Orthographic projection (from PlanetGLRenderer)
 * @param viewMatrix        Camera orbital view matrix (from PlanetGLRenderer)
 * @param currentRadius     Pixel-space scaled planet radius
 * @param sunEye            Sun direction in eye space
 */
internal fun RingGLState.drawRings(
    projectionMatrix: FloatArray,
    viewMatrix: FloatArray,
    currentRadius: Float,
    sunEye: Point3D,
    planetType: Float = 6f,
) {
    if (programId == 0 || vertexBuffer == null) return

    GLES20.glEnable(GLES20.GL_BLEND)
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    GLES20.glDisable(GLES20.GL_CULL_FACE)

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

    GLES20.glUseProgram(programId)
    GLES20.glUniformMatrix4fv(uRingMVPLoc, 1, false, ringMVP, 0)
    GLES20.glUniform3f(uRingSunLoc, sunEye.x.toFloat(), sunEye.y.toFloat(), sunEye.z.toFloat())
    GLES20.glUniform1f(uPlanetTypeLoc, planetType)

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
