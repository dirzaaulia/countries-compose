package com.dirzaaulia.countries.platform

import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils

/** GL utility functions shared across EarthGLRenderer — extracted to keep renderer under 300 lines. */

internal fun loadGLTexture(bytes: ByteArray): Int {
    if (bytes.isEmpty()) return 0
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return 0
    val textureHandle = IntArray(1)
    GLES20.glGenTextures(1, textureHandle, 0)
    if (textureHandle[0] != 0) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
    }
    bitmap.recycle()
    return textureHandle[0]
}

internal fun compileGLShader(
    type: Int,
    shaderCode: String,
): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)
    return shader
}

internal fun createGLProgram(
    vertexShader: Int,
    fragmentShader: Int,
): Int {
    val program = GLES20.glCreateProgram()
    GLES20.glAttachShader(program, vertexShader)
    GLES20.glAttachShader(program, fragmentShader)
    GLES20.glLinkProgram(program)
    return program
}

internal fun getPlanetAxialTilt(
    planetType: Float,
    isMoonMode: Boolean,
): Float =
    when {
        isMoonMode || planetType in 0.5f..1.5f -> 1.54f // Moon
        planetType in 1.5f..2.5f -> 25.19f // Mars
        planetType in 2.5f..3.5f -> 0.03f // Mercury
        planetType in 3.5f..4.5f -> 177.36f // Venus
        planetType in 4.5f..5.5f -> 3.13f // Jupiter
        planetType in 5.5f..6.5f -> 26.73f // Saturn
        planetType in 6.5f..7.5f -> 97.77f // Uranus
        planetType in 7.5f..8.5f -> 28.32f // Neptune
        else -> 0f // Earth
    }

internal class SphereGLBuffers(
    sphereMesh: com.dirzaaulia.countries.domain.globe.SphereMesh,
) {
    val indexCount = sphereMesh.indices.size

    val vertexBuffer: java.nio.FloatBuffer =
        java.nio.ByteBuffer
            .allocateDirect(sphereMesh.vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereMesh.vertices)
                position(0)
            }

    val texCoordBuffer: java.nio.FloatBuffer =
        java.nio.ByteBuffer
            .allocateDirect(sphereMesh.texCoords.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereMesh.texCoords)
                position(0)
            }

    val normalBuffer: java.nio.FloatBuffer =
        java.nio.ByteBuffer
            .allocateDirect(sphereMesh.normals.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereMesh.normals)
                position(0)
            }

    val indexBuffer: java.nio.ShortBuffer =
        java.nio.ByteBuffer
            .allocateDirect(sphereMesh.indices.size * 2)
            .order(java.nio.ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
                put(sphereMesh.indices)
                position(0)
            }
}
