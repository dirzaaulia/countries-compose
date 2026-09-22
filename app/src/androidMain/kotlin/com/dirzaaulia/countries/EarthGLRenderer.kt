package com.dirzaaulia.countries

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*

class EarthGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val sphereMesh = SphereMesh(stacks = 48, sectors = 96, radius = 1.0f)
    
    private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(sphereMesh.vertices.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(sphereMesh.vertices)
            position(0)
        }

    private val texCoordBuffer: FloatBuffer = ByteBuffer.allocateDirect(sphereMesh.texCoords.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(sphereMesh.texCoords)
            position(0)
        }

    private val normalBuffer: FloatBuffer = ByteBuffer.allocateDirect(sphereMesh.normals.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(sphereMesh.normals)
            position(0)
        }

    private val indexBuffer: ShortBuffer = ByteBuffer.allocateDirect(sphereMesh.indices.size * 2)
        .order(ByteOrder.nativeOrder()).asShortBuffer().apply {
            put(sphereMesh.indices)
            position(0)
        }

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var programId = 0
    private var uMVPMatrixLoc = 0
    private var uMVMatrixLoc = 0
    private var uDayTextureLoc = 0
    private var uNightTextureLoc = 0
    private var uCloudTextureLoc = 0
    private var uSunDirectionLoc = 0
    private var uCloudOffsetLoc = 0

    private var aPositionLoc = 0
    private var aTexCoordinateLoc = 0
    private var aNormalLoc = 0

    private var dayTextureId = 0
    private var nightTextureId = 0
    private var cloudTextureId = 0

    @Volatile
    private var pendingDayBytes: ByteArray? = null
    @Volatile
    private var pendingNightBytes: ByteArray? = null
    @Volatile
    private var pendingCloudBytes: ByteArray? = null

    @Volatile
    private var currentRotationX = 0f
    @Volatile
    private var currentRotationY = 0f
    @Volatile
    private var currentZoom = 1.0f

    private var viewportWidth = 1
    private var viewportHeight = 1
    private var cloudOffset = 0f

    fun updateCamera(rotX: Float, rotY: Float, zoom: Float) {
        currentRotationX = rotX
        currentRotationY = rotY
        currentZoom = zoom
    }

    fun setTextures(dayBytes: ByteArray, nightBytes: ByteArray, cloudBytes: ByteArray) {
        pendingDayBytes = dayBytes
        pendingNightBytes = nightBytes
        pendingCloudBytes = cloudBytes
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Deep cosmic void background
        GLES20.glClearColor(0.003f, 0.005f, 0.010f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)

        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, GlobeShaders.VERTEX_SHADER)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, GlobeShaders.FRAGMENT_SHADER)
        programId = createProgram(vertexShader, fragmentShader)

        uMVPMatrixLoc = GLES20.glGetUniformLocation(programId, "u_MVPMatrix")
        uMVMatrixLoc = GLES20.glGetUniformLocation(programId, "u_MVMatrix")
        uDayTextureLoc = GLES20.glGetUniformLocation(programId, "u_DayTexture")
        uNightTextureLoc = GLES20.glGetUniformLocation(programId, "u_NightTexture")
        uCloudTextureLoc = GLES20.glGetUniformLocation(programId, "u_CloudTexture")
        uSunDirectionLoc = GLES20.glGetUniformLocation(programId, "u_SunDirection")
        uCloudOffsetLoc = GLES20.glGetUniformLocation(programId, "u_CloudOffset")

        aPositionLoc = GLES20.glGetAttribLocation(programId, "a_Position")
        aTexCoordinateLoc = GLES20.glGetAttribLocation(programId, "a_TexCoordinate")
        aNormalLoc = GLES20.glGetAttribLocation(programId, "a_Normal")

        Matrix.setIdentityM(viewMatrix, 0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
        GLES20.glViewport(0, 0, width, height)

        val halfW = width / 2.0f
        val halfH = height / 2.0f
        Matrix.orthoM(projectionMatrix, 0, -halfW, halfW, -halfH, halfH, -10000f, 10000f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        loadTexturesIfPending()

        if (programId == 0 || dayTextureId == 0 || nightTextureId == 0) return

        GLES20.glUseProgram(programId)

        // Realistic static cloud cover (locked to planet surface, no fast moving drift)
        cloudOffset = 0f

        // Model matrix with rotation and responsive zoom
        Matrix.setIdentityM(modelMatrix, 0)
        
        // Exact 1:1 pixel-perfect mathematical synchronization with GlobeView 2D Canvas:
        val baseRadius = minOf(viewportWidth, viewportHeight).toFloat() * 0.38f
        val currentRadius = baseRadius * currentZoom

        // Matching GlobeMath rotation order:
        // In GlobeMath: rotateX first, then rotateY!
        // In OpenGL: post-multiplying rotateY then rotateX applies rotateX first to vertices!
        Matrix.rotateM(modelMatrix, 0, currentRotationY, 0f, 1f, 0f)
        Matrix.rotateM(modelMatrix, 0, currentRotationX, 1f, 0f, 0f)
        Matrix.scaleM(modelMatrix, 0, currentRadius, currentRadius, currentRadius)

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uMVMatrixLoc, 1, false, mvMatrix, 0)
        GLES20.glUniform1f(uCloudOffsetLoc, cloudOffset)

        // Real-time astronomical Sun direction (matches current UTC subsolar point)
        val sunPos = AstronomyMath.calculateSunPosition()
        var sunEye = sunPos.vector
        val radX = currentRotationX.toDouble().toRadians
        val radY = currentRotationY.toDouble().toRadians
        val cosX = cos(radX); val sinX = sin(radX)
        val cosY = cos(radY); val sinY = sin(radY)
        sunEye = rotateX(sunEye, cosX, sinX)
        sunEye = rotateY(sunEye, cosY, sinY)

        GLES20.glUniform3f(
            uSunDirectionLoc,
            sunEye.x.toFloat(),
            sunEye.y.toFloat(),
            sunEye.z.toFloat()
        )

        // Bind Day Texture to Unit 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, dayTextureId)
        GLES20.glUniform1i(uDayTextureLoc, 0)

        // Bind Night Texture to Unit 1
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nightTextureId)
        GLES20.glUniform1i(uNightTextureLoc, 1)

        // Bind Cloud Texture to Unit 2
        if (cloudTextureId != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, cloudTextureId)
            GLES20.glUniform1i(uCloudTextureLoc, 2)
        }

        // Vertex positions
        GLES20.glEnableVertexAttribArray(aPositionLoc)
        GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // Texture coordinates
        GLES20.glEnableVertexAttribArray(aTexCoordinateLoc)
        GLES20.glVertexAttribPointer(aTexCoordinateLoc, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        // Normals
        GLES20.glEnableVertexAttribArray(aNormalLoc)
        GLES20.glVertexAttribPointer(aNormalLoc, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)

        // Draw the 3D Sphere
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            sphereMesh.indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aTexCoordinateLoc)
        GLES20.glDisableVertexAttribArray(aNormalLoc)
    }

    private fun loadTexturesIfPending() {
        val dBytes = pendingDayBytes
        if (dBytes != null) {
            pendingDayBytes = null
            dayTextureId = loadGLTexture(dBytes)
        }
        val nBytes = pendingNightBytes
        if (nBytes != null) {
            pendingNightBytes = null
            nightTextureId = loadGLTexture(nBytes)
        }
        val cBytes = pendingCloudBytes
        if (cBytes != null) {
            pendingCloudBytes = null
            cloudTextureId = loadGLTexture(cBytes)
        }
    }

    private fun loadGLTexture(bytes: ByteArray): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)
        if (textureHandle[0] != 0) {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }
        return textureHandle[0]
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }
}
