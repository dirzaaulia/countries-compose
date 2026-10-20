package com.dirzaaulia.countries.domain.globe

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PlanetaryRingMesh(
    val innerRadius: Float = 1.236f,
    val outerRadius: Float = 2.269f,
    val segments: Int = 256,
) {
    val vertices: FloatArray
    val texCoords: FloatArray
    val indices: ShortArray

    init {
        val vertexCount = (segments + 1) * 2
        vertices = FloatArray(vertexCount * 3)
        texCoords = FloatArray(vertexCount * 2)

        var vi = 0
        var ti = 0

        for (seg in 0..segments) {
            val angle = (seg.toFloat() / segments.toFloat()) * 2f * PI.toFloat()
            val cosA = cos(angle)
            val sinA = sin(angle)
            val uCoord = seg.toFloat() / segments.toFloat()

            vertices[vi++] = cosA * innerRadius
            vertices[vi++] = 0f
            vertices[vi++] = sinA * innerRadius
            texCoords[ti++] = 0f
            texCoords[ti++] = uCoord

            vertices[vi++] = cosA * outerRadius
            vertices[vi++] = 0f
            vertices[vi++] = sinA * outerRadius
            texCoords[ti++] = 1f
            texCoords[ti++] = uCoord
        }

        val indexCount = segments * 6
        indices = ShortArray(indexCount)
        var ii = 0
        for (seg in 0 until segments) {
            val i0 = (seg * 2).toShort()
            val i1 = (seg * 2 + 1).toShort()
            val i2 = (seg * 2 + 2).toShort()
            val i3 = (seg * 2 + 3).toShort()
            indices[ii++] = i0
            indices[ii++] = i2
            indices[ii++] = i1
            indices[ii++] = i1
            indices[ii++] = i2
            indices[ii++] = i3
        }
    }
}
