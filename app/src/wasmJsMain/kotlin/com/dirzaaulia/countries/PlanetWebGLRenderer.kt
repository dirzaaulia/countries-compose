package com.dirzaaulia.countries

import com.dirzaaulia.countries.generated.resources.Res
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.khronos.webgl.*
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image
import kotlin.math.*

@JsFun("(bytes, type) => URL.createObjectURL(new Blob([bytes], { type: type }))")
private external fun jsCreateBlobUrl(bytes: Uint8Array, type: String): String

@JsFun("(url) => URL.revokeObjectURL(url)")
private external fun jsRevokeBlobUrl(url: String)

/**
 * High-performance WebGL 1.0/2.0 Photorealistic 3D Planet Engine for WASM.
 * Direct architectural parity with Android's EarthGLRenderer.kt.
 */
object PlanetWebGLRenderer {

    private var gl: WebGLRenderingContext? = null
    private var canvas: HTMLCanvasElement? = null
    private var program: WebGLProgram? = null

    // Attribute & Uniform locations
    private var aPositionLoc = 0
    private var aTexCoordLoc = 0
    private var aNormalLoc = 0

    private var uMVPMatrixLoc: WebGLUniformLocation? = null
    private var uMVMatrixLoc: WebGLUniformLocation? = null
    private var uDayTextureLoc: WebGLUniformLocation? = null
    private var uNightTextureLoc: WebGLUniformLocation? = null
    private var uCloudTextureLoc: WebGLUniformLocation? = null
    private var uSunDirectionLoc: WebGLUniformLocation? = null
    private var uCloudOffsetLoc: WebGLUniformLocation? = null
    private var uIsMoonLoc: WebGLUniformLocation? = null

    // VBO & IBO buffers
    private var positionBuffer: WebGLBuffer? = null
    private var texCoordBuffer: WebGLBuffer? = null
    private var normalBuffer: WebGLBuffer? = null
    private var indexBuffer: WebGLBuffer? = null
    private var indexCount = 0

    // Textures
    private var dayTexture: WebGLTexture? = null
    private var nightTexture: WebGLTexture? = null
    private var cloudTexture: WebGLTexture? = null
    private var moonTexture: WebGLTexture? = null

    // Current state
    private var isMoonMode = false
    private var moonPhaseAngle = 0.0
    private var currentRotX = 0f
    private var currentRotY = 0f
    private var currentZoom = 1.0f

    private var isInitialized = false
    private var isLoadingTextures = false

    // Matrices (column-major FloatArray of length 16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    fun ensureInitialized(scope: CoroutineScope) {
        if (isInitialized) return
        isInitialized = true

        // 1. Get or create DOM canvas element positioned behind Compose canvas
        var c = document.getElementById("PlanetCanvas") as? HTMLCanvasElement
        if (c == null) {
            c = document.createElement("canvas") as HTMLCanvasElement
            c.id = "PlanetCanvas"
            c.style.position = "absolute"
            c.style.top = "0px"
            c.style.left = "0px"
            c.style.width = "100%"
            c.style.height = "100%"
            c.style.zIndex = "0"
            c.style.setProperty("pointer-events", "none")
            val composeTarget = document.getElementById("ComposeTarget")
            if (composeTarget != null) {
                document.body?.insertBefore(c, composeTarget)
            } else {
                document.body?.appendChild(c)
            }
        }
        canvas = c

        // 2. Initialize WebGL Context
        val context = c.getContext("webgl") as? WebGLRenderingContext
            ?: c.getContext("experimental-webgl") as? WebGLRenderingContext
        if (context == null) {
            println("PlanetWebGLRenderer: WebGL not supported in this browser.")
            return
        }
        gl = context

        // 3. Configure WebGL Pipeline
        context.clearColor(0.0f, 0.0f, 0.0f, 0.0f) // Transparent so cosmic space body background shows
        context.enable(WebGLRenderingContext.DEPTH_TEST)
        context.depthFunc(WebGLRenderingContext.LEQUAL)
        context.enable(WebGLRenderingContext.CULL_FACE)
        context.cullFace(WebGLRenderingContext.BACK)

        // 4. Compile Shaders
        val vs = compileShader(context, WebGLRenderingContext.VERTEX_SHADER, GlobeShaders.VERTEX_SHADER)
        val fs = compileShader(context, WebGLRenderingContext.FRAGMENT_SHADER, GlobeShaders.FRAGMENT_SHADER)
        if (vs == null || fs == null) return

        val prog = context.createProgram() ?: return
        context.attachShader(prog, vs)
        context.attachShader(prog, fs)
        context.linkProgram(prog)
        program = prog

        // 5. Get Attributes and Uniforms
        aPositionLoc = context.getAttribLocation(prog, "a_Position")
        aTexCoordLoc = context.getAttribLocation(prog, "a_TexCoordinate")
        aNormalLoc = context.getAttribLocation(prog, "a_Normal")

        uMVPMatrixLoc = context.getUniformLocation(prog, "u_MVPMatrix")
        uMVMatrixLoc = context.getUniformLocation(prog, "u_MVMatrix")
        uDayTextureLoc = context.getUniformLocation(prog, "u_DayTexture")
        uNightTextureLoc = context.getUniformLocation(prog, "u_NightTexture")
        uCloudTextureLoc = context.getUniformLocation(prog, "u_CloudTexture")
        uSunDirectionLoc = context.getUniformLocation(prog, "u_SunDirection")
        uCloudOffsetLoc = context.getUniformLocation(prog, "u_CloudOffset")
        uIsMoonLoc = context.getUniformLocation(prog, "u_IsMoon")

        setIdentity(viewMatrix)

        // 6. Build and upload SphereMesh
        val mesh = SphereMesh(stacks = 36, sectors = 72, radius = 1.0f)
        indexCount = mesh.indices.size

        val posArray = Float32Array(mesh.vertices.size)
        for (i in mesh.vertices.indices) posArray[i] = mesh.vertices[i]
        positionBuffer = context.createBuffer()
        context.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, positionBuffer)
        context.bufferData(WebGLRenderingContext.ARRAY_BUFFER, posArray, WebGLRenderingContext.STATIC_DRAW)

        val texArray = Float32Array(mesh.texCoords.size)
        for (i in mesh.texCoords.indices) texArray[i] = mesh.texCoords[i]
        texCoordBuffer = context.createBuffer()
        context.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, texCoordBuffer)
        context.bufferData(WebGLRenderingContext.ARRAY_BUFFER, texArray, WebGLRenderingContext.STATIC_DRAW)

        val normArray = Float32Array(mesh.normals.size)
        for (i in mesh.normals.indices) normArray[i] = mesh.normals[i]
        normalBuffer = context.createBuffer()
        context.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, normalBuffer)
        context.bufferData(WebGLRenderingContext.ARRAY_BUFFER, normArray, WebGLRenderingContext.STATIC_DRAW)

        val idxArray = Uint16Array(mesh.indices.size)
        for (i in mesh.indices.indices) idxArray[i] = mesh.indices[i]
        indexBuffer = context.createBuffer()
        context.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, indexBuffer)
        context.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, idxArray, WebGLRenderingContext.STATIC_DRAW)

        // 7. Asynchronously load all 2K high-definition planetary textures
        loadTextures(scope)
    }

    private fun loadTextures(scope: CoroutineScope) {
        if (isLoadingTextures) return
        isLoadingTextures = true

        scope.launch(Dispatchers.Default) {
            try {
                val dayBytes = Res.readBytes("files/earth_day.jpg")
                loadTextureFromBytes(dayBytes) { tex -> dayTexture = tex }

                val nightBytes = Res.readBytes("files/earth_night.jpg")
                loadTextureFromBytes(nightBytes) { tex -> nightTexture = tex }

                val cloudBytes = Res.readBytes("files/earth_clouds.jpg")
                loadTextureFromBytes(cloudBytes) { tex -> cloudTexture = tex }

                val moonBytes = Res.readBytes("files/moon.jpg")
                loadTextureFromBytes(moonBytes) { tex -> moonTexture = tex }
            } catch (e: Exception) {
                println("PlanetWebGLRenderer: Error loading textures: ${e.message}")
            }
        }
    }

    private fun loadTextureFromBytes(bytes: ByteArray, onLoaded: (WebGLTexture) -> Unit) {
        val context = gl ?: return
        val u8 = Uint8Array(bytes.size)
        for (i in bytes.indices) u8[i] = bytes[i]

        val blobUrl = jsCreateBlobUrl(u8, "image/jpeg")
        val img = Image()
        img.onload = {
            val texture = context.createTexture()
            if (texture != null) {
                context.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture)
                context.texImage2D(
                    WebGLRenderingContext.TEXTURE_2D,
                    0,
                    WebGLRenderingContext.RGB,
                    WebGLRenderingContext.RGB,
                    WebGLRenderingContext.UNSIGNED_BYTE,
                    img
                )
                context.generateMipmap(WebGLRenderingContext.TEXTURE_2D)
                context.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_LINEAR)
                context.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR)
                context.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_S, WebGLRenderingContext.REPEAT)
                context.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_T, WebGLRenderingContext.CLAMP_TO_EDGE)
                onLoaded(texture)
            }
            jsRevokeBlobUrl(blobUrl)
        }
        img.src = blobUrl
    }

    fun setPlanetMode(isMoon: Boolean, phaseAngle: Double = 0.0) {
        isMoonMode = isMoon
        moonPhaseAngle = phaseAngle
    }

    fun updateCamera(rotX: Float, rotY: Float, zoom: Float) {
        currentRotX = rotX
        currentRotY = rotY
        currentZoom = zoom
    }

    fun render(viewportWidth: Int, viewportHeight: Int) {
        val context = gl ?: return
        val prog = program ?: return
        val c = canvas ?: return

        // Retina display scaling
        val dpr = window.devicePixelRatio.coerceAtLeast(1.0)
        val targetWidth = (viewportWidth * dpr).toInt()
        val targetHeight = (viewportHeight * dpr).toInt()

        if (c.width != targetWidth || c.height != targetHeight) {
            c.width = targetWidth
            c.height = targetHeight
        }
        context.viewport(0, 0, targetWidth, targetHeight)

        context.clear(WebGLRenderingContext.COLOR_BUFFER_BIT or WebGLRenderingContext.DEPTH_BUFFER_BIT)

        val activeDayTex = if (isMoonMode) (moonTexture ?: dayTexture) else dayTexture
        if (activeDayTex == null) return // Texture still streaming

        context.useProgram(prog)

        // 1. Orthographic Projection Matrix matching GlobeView 2D Canvas coordinate space
        val halfW = targetWidth / 2.0f
        val halfH = targetHeight / 2.0f
        ortho(projectionMatrix, -halfW, halfW, -halfH, halfH, -50000f, 50000f)

        // 2. Model Matrix matching exact 1:1 pixel scale of GlobeView
        setIdentity(modelMatrix)
        val baseRadius = minOf(targetWidth, targetHeight) * 0.38f
        val currentRadius = baseRadius * currentZoom

        rotateX(modelMatrix, currentRotX)
        rotateY(modelMatrix, currentRotY)
        scale(modelMatrix, currentRadius, currentRadius, currentRadius)

        multiply(mvMatrix, viewMatrix, modelMatrix)
        multiply(mvpMatrix, projectionMatrix, mvMatrix)

        val mvpArr = Float32Array(16)
        for (i in 0..15) mvpArr[i] = mvpMatrix[i]
        context.uniformMatrix4fv(uMVPMatrixLoc, false, mvpArr)

        val mvArr = Float32Array(16)
        for (i in 0..15) mvArr[i] = mvMatrix[i]
        context.uniformMatrix4fv(uMVMatrixLoc, false, mvArr)

        context.uniform1f(uCloudOffsetLoc, 0f)
        context.uniform1f(uIsMoonLoc, if (isMoonMode) 1.0f else 0.0f)

        // 3. Sun Direction calculation
        val radX = (currentRotX.toDouble() * PI / 180.0)
        val radY = (currentRotY.toDouble() * PI / 180.0)
        val cosX = cos(radX); val sinX = sin(radX)
        val cosY = cos(radY); val sinY = sin(radY)

        val sunEye = if (isMoonMode) {
            val phaseRad = moonPhaseAngle * PI / 180.0
            var p = Point3D(sin(phaseRad), 0.0, -cos(phaseRad))
            p = rotateY(p, cosY, sinY)
            p = rotateX(p, cosX, sinX)
            p
        } else {
            val sunPos = AstronomyMath.calculateSunPosition()
            var p = sunPos.vector
            p = rotateY(p, cosY, sinY)
            p = rotateX(p, cosX, sinX)
            p
        }

        context.uniform3f(uSunDirectionLoc, sunEye.x.toFloat(), sunEye.y.toFloat(), sunEye.z.toFloat())

        // 4. Bind Texture Samplers
        context.activeTexture(WebGLRenderingContext.TEXTURE0)
        context.bindTexture(WebGLRenderingContext.TEXTURE_2D, activeDayTex)
        context.uniform1i(uDayTextureLoc, 0)

        context.activeTexture(WebGLRenderingContext.TEXTURE1)
        context.bindTexture(WebGLRenderingContext.TEXTURE_2D, nightTexture ?: activeDayTex)
        context.uniform1i(uNightTextureLoc, 1)

        context.activeTexture(WebGLRenderingContext.TEXTURE2)
        context.bindTexture(WebGLRenderingContext.TEXTURE_2D, cloudTexture ?: activeDayTex)
        context.uniform1i(uCloudTextureLoc, 2)

        // 5. Bind Geometry Buffers & Draw
        context.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, positionBuffer)
        context.enableVertexAttribArray(aPositionLoc)
        context.vertexAttribPointer(aPositionLoc, 3, WebGLRenderingContext.FLOAT, false, 0, 0)

        context.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, texCoordBuffer)
        context.enableVertexAttribArray(aTexCoordLoc)
        context.vertexAttribPointer(aTexCoordLoc, 2, WebGLRenderingContext.FLOAT, false, 0, 0)

        context.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, normalBuffer)
        context.enableVertexAttribArray(aNormalLoc)
        context.vertexAttribPointer(aNormalLoc, 3, WebGLRenderingContext.FLOAT, false, 0, 0)

        context.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, indexBuffer)
        context.drawElements(WebGLRenderingContext.TRIANGLES, indexCount, WebGLRenderingContext.UNSIGNED_SHORT, 0)
    }

    private fun compileShader(gl: WebGLRenderingContext, type: Int, source: String): WebGLShader? {
        val shader = gl.createShader(type) ?: return null
        gl.shaderSource(shader, source)
        gl.compileShader(shader)
        return shader
    }

    // --- High-Performance 4x4 Column-Major Matrix Math ---

    private fun setIdentity(m: FloatArray) {
        for (i in 0..15) m[i] = 0f
        m[0] = 1f; m[5] = 1f; m[10] = 1f; m[15] = 1f
    }

    private fun ortho(m: FloatArray, left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) {
        val rl = 1.0f / (right - left)
        val tb = 1.0f / (top - bottom)
        val fn = 1.0f / (far - near)
        for (i in 0..15) m[i] = 0f
        m[0] = 2.0f * rl
        m[5] = 2.0f * tb
        m[10] = -2.0f * fn
        m[12] = -(right + left) * rl
        m[13] = -(top + bottom) * tb
        m[14] = -(far + near) * fn
        m[15] = 1.0f
    }

    private fun scale(m: FloatArray, x: Float, y: Float, z: Float) {
        for (i in 0..3) {
            m[i] *= x
            m[4 + i] *= y
            m[8 + i] *= z
        }
    }

    private fun rotateX(m: FloatArray, angleDeg: Float) {
        val rad = angleDeg * PI.toFloat() / 180.0f
        val c = cos(rad)
        val s = sin(rad)
        val temp = FloatArray(16)
        setIdentity(temp)
        temp[5] = c;  temp[9] = -s
        temp[6] = s;  temp[10] = c
        val res = FloatArray(16)
        multiply(res, m, temp)
        for (i in 0..15) m[i] = res[i]
    }

    private fun rotateY(m: FloatArray, angleDeg: Float) {
        val rad = angleDeg * PI.toFloat() / 180.0f
        val c = cos(rad)
        val s = sin(rad)
        val temp = FloatArray(16)
        setIdentity(temp)
        temp[0] = c;   temp[8] = s
        temp[2] = -s;  temp[10] = c
        val res = FloatArray(16)
        multiply(res, m, temp)
        for (i in 0..15) m[i] = res[i]
    }

    private fun multiply(result: FloatArray, lhs: FloatArray, rhs: FloatArray) {
        for (i in 0..3) {
            val rhs0 = rhs[i * 4]
            val rhs1 = rhs[i * 4 + 1]
            val rhs2 = rhs[i * 4 + 2]
            val rhs3 = rhs[i * 4 + 3]
            for (j in 0..3) {
                result[i * 4 + j] = lhs[j] * rhs0 + lhs[4 + j] * rhs1 + lhs[8 + j] * rhs2 + lhs[12 + j] * rhs3
            }
        }
    }
}
