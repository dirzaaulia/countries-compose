package com.dirzaaulia.countries.platform

import com.dirzaaulia.countries.domain.globe.GlobeShaders
import com.dirzaaulia.countries.domain.globe.SphereMesh
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLShader
import org.khronos.webgl.WebGLTexture
import org.khronos.webgl.WebGLUniformLocation
import org.khronos.webgl.set

internal class PlanetWebGLScene(
    val gl: WebGLRenderingContext,
) {
    val program: WebGLProgram? = createProgram()
    private val uniforms = mutableMapOf<String, WebGLUniformLocation?>()
    private val mesh = SphereMesh(stacks = 36, sectors = 72, radius = 1f)
    private val positions = uploadArray(mesh.vertices)
    private val coordinates = uploadArray(mesh.texCoords)
    private val normals = uploadArray(mesh.normals)
    private val indices = uploadIndices(mesh.indices)
    private val positionLocation = program?.let { gl.getAttribLocation(it, "a_Position") } ?: 0
    private val coordinateLocation = program?.let { gl.getAttribLocation(it, "a_TexCoordinate") } ?: 0
    private val normalLocation = program?.let { gl.getAttribLocation(it, "a_Normal") } ?: 0

    init {
        gl.clearColor(0f, 0f, 0f, 0f)
        gl.enable(WebGLRenderingContext.DEPTH_TEST)
        gl.depthFunc(WebGLRenderingContext.LEQUAL)
        gl.enable(WebGLRenderingContext.CULL_FACE)
        gl.cullFace(WebGLRenderingContext.BACK)
    }

    fun uniform(name: String): WebGLUniformLocation? =
        uniforms.getOrPut(name) {
            program?.let { gl.getUniformLocation(it, name) }
        }

    fun bindTextures(
        day: WebGLTexture,
        night: WebGLTexture?,
        clouds: WebGLTexture?,
    ) {
        bindTexture("u_DayTexture", 0, day)
        bindTexture("u_NightTexture", 1, night)
        bindTexture("u_CloudTexture", 2, clouds)
    }

    private fun bindTexture(
        name: String,
        unit: Int,
        texture: WebGLTexture?,
    ) {
        gl.activeTexture(WebGLRenderingContext.TEXTURE0 + unit)
        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture)
        gl.uniform1i(uniform(name), unit)
    }

    fun draw() {
        bindAttribute(positions, positionLocation, 3)
        bindAttribute(coordinates, coordinateLocation, 2)
        bindAttribute(normals, normalLocation, 3)
        gl.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, indices)
        gl.drawElements(
            WebGLRenderingContext.TRIANGLES,
            mesh.indices.size,
            WebGLRenderingContext.UNSIGNED_SHORT,
            0,
        )
    }

    private fun bindAttribute(
        buffer: WebGLBuffer?,
        location: Int,
        size: Int,
    ) {
        gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, buffer)
        gl.enableVertexAttribArray(location)
        gl.vertexAttribPointer(location, size, WebGLRenderingContext.FLOAT, false, 0, 0)
    }

    private fun uploadArray(values: FloatArray): WebGLBuffer? {
        val array = Float32Array(values.size)
        for (i in values.indices) array[i] = values[i]
        val buffer = gl.createBuffer()
        gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, buffer)
        gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, array, WebGLRenderingContext.STATIC_DRAW)
        return buffer
    }

    private fun uploadIndices(values: ShortArray): WebGLBuffer? {
        val array = Uint16Array(values.size)
        for (i in values.indices) array[i] = values[i]
        val buffer = gl.createBuffer()
        gl.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, buffer)
        gl.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, array, WebGLRenderingContext.STATIC_DRAW)
        return buffer
    }

    private fun createProgram(): WebGLProgram? {
        val vertex = compileShader(WebGLRenderingContext.VERTEX_SHADER, GlobeShaders.VERTEX_SHADER)
        val fragment = compileShader(WebGLRenderingContext.FRAGMENT_SHADER, GlobeShaders.FRAGMENT_SHADER)
        if (vertex == null || fragment == null) return null
        val result = gl.createProgram() ?: return null
        gl.attachShader(result, vertex)
        gl.attachShader(result, fragment)
        gl.linkProgram(result)
        gl.deleteShader(vertex)
        gl.deleteShader(fragment)
        return result
    }

    private fun compileShader(
        type: Int,
        source: String,
    ): WebGLShader? {
        val shader = gl.createShader(type) ?: return null
        gl.shaderSource(shader, source)
        gl.compileShader(shader)
        return shader
    }
}
