package com.dirzaaulia.countries

import kotlin.math.*

/**
 * Procedural UV Sphere generator for 3D Globe rendering.
 * Provides vertex positions, texture coordinates (UV), normal vectors, and indices.
 */
class SphereMesh(
    val stacks: Int = 36,
    val sectors: Int = 72,
    val radius: Float = 1.0f
) {
    val vertices: FloatArray
    val texCoords: FloatArray
    val normals: FloatArray
    val indices: ShortArray

    init {
        val numVertices = (stacks + 1) * (sectors + 1)
        vertices = FloatArray(numVertices * 3)
        texCoords = FloatArray(numVertices * 2)
        normals = FloatArray(numVertices * 3)

        var vIdx = 0
        var tIdx = 0
        var nIdx = 0

        for (i in 0..stacks) {
            val lat = (PI / 2.0 - i.toDouble() * PI / stacks).toFloat()
            val xy = cos(lat)
            val z = sin(lat)

            val v = i.toFloat() / stacks.toFloat()

            for (j in 0..sectors) {
                val u = j.toFloat() / sectors.toFloat()
                // Longitude ranges from -PI (-180 deg) at u=0 to +PI (+180 deg) at u=1
                // Longitude 0 (Prime Meridian) is at u=0.5
                val lng = ((u - 0.5f) * 2.0f * PI).toFloat()
                
                // Coordinates matching GlobeMath.latLngToCartesian
                val x = xy * sin(lng)
                val y = z
                val zCoord = xy * cos(lng)

                // Vertex position
                vertices[vIdx++] = x * radius
                vertices[vIdx++] = y * radius
                vertices[vIdx++] = zCoord * radius

                // Texture coordinates
                texCoords[tIdx++] = u
                texCoords[tIdx++] = v

                // Normal vector (unit sphere normal equals normalized position)
                normals[nIdx++] = x
                normals[nIdx++] = y
                normals[nIdx++] = zCoord
            }
        }

        val numIndices = stacks * sectors * 6
        indices = ShortArray(numIndices)
        var iIdx = 0

        for (i in 0 until stacks) {
            var k1 = i * (sectors + 1)
            var k2 = k1 + sectors + 1

            for (j in 0 until sectors) {
                if (i != 0) {
                    indices[iIdx++] = k1.toShort()
                    indices[iIdx++] = k2.toShort()
                    indices[iIdx++] = (k1 + 1).toShort()
                }

                if (i != (stacks - 1)) {
                    indices[iIdx++] = (k1 + 1).toShort()
                    indices[iIdx++] = k2.toShort()
                    indices[iIdx++] = (k2 + 1).toShort()
                }

                k1++
                k2++
            }
        }
    }
}
