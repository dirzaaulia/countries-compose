package com.dirzaaulia.countries.domain.globe

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Flat annular disc mesh for Saturn's ring system.
 *
 * Ring radii are normalized so that 1.0 = planet sphere radius.
 * Based on NASA Cassini mission data (km from Saturn centre, equatorial radius = 60,268 km):
 *   C Ring inner  : 74,510 km → 1.2361 × R
 *   A Ring outer  : 136,775 km → 2.2694 × R
 *
 * UV layout: u = radial position [0=inner C-Ring, 1=outer A-Ring], v = azimuth [0,1].
 * Both faces are visible — back-face culling must be DISABLED before drawing.
 */
class SaturnRingMesh(
    /** Inner edge radius relative to planet radius (C-Ring inner ≈ 1.236). */
    val innerRadius: Float = 1.236f,
    /** Outer edge radius relative to planet radius (A-Ring outer ≈ 2.269). */
    val outerRadius: Float = 2.269f,
    /** Number of angular segments around the disc (more = smoother). */
    val segments: Int = 256,
) {
    val vertices: FloatArray
    val texCoords: FloatArray
    val indices: ShortArray

    init {
        // Two vertices per segment (inner + outer) + 1 duplicated seam vertex
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

            // Inner vertex (u = 0.0 → C-Ring inner edge)
            vertices[vi++] = cosA * innerRadius
            vertices[vi++] = 0f
            vertices[vi++] = sinA * innerRadius
            texCoords[ti++] = 0f
            texCoords[ti++] = uCoord

            // Outer vertex (u = 1.0 → A-Ring outer edge)
            vertices[vi++] = cosA * outerRadius
            vertices[vi++] = 0f
            vertices[vi++] = sinA * outerRadius
            texCoords[ti++] = 1f
            texCoords[ti++] = uCoord
        }

        // Triangle strip → two triangles per segment quad
        val indexCount = segments * 6
        indices = ShortArray(indexCount)
        var ii = 0
        for (seg in 0 until segments) {
            val i0 = (seg * 2).toShort()
            val i1 = (seg * 2 + 1).toShort()
            val i2 = (seg * 2 + 2).toShort()
            val i3 = (seg * 2 + 3).toShort()
            // Upper face
            indices[ii++] = i0; indices[ii++] = i2; indices[ii++] = i1
            // Lower face (reversed winding — both sides visible)
            indices[ii++] = i1; indices[ii++] = i2; indices[ii++] = i3
        }
    }
}
