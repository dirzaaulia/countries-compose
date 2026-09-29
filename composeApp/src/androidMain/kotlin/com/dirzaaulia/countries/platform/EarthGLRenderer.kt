package com.dirzaaulia.countries.platform

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeShaders
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.SphereMesh
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toRadians
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class EarthGLRenderer(
    @Suppress("UNUSED_PARAMETER") context: Context,
) : GLSurfaceView.Renderer {
    private val sphereMesh = SphereMesh(stacks = 48, sectors = 96, radius = 1.0f)

    private val vertexBuffer: FloatBuffer =
        ByteBuffer
            .allocateDirect(sphereMesh.vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereMesh.vertices)
                position(0)
            }

    private val texCoordBuffer: FloatBuffer =
        ByteBuffer
            .allocateDirect(sphereMesh.texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereMesh.texCoords)
                position(0)
            }

    private val normalBuffer: FloatBuffer =
        ByteBuffer
            .allocateDirect(sphereMesh.normals.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereMesh.normals)
                position(0)
            }

    private val indexBuffer: ShortBuffer =
        ByteBuffer
            .allocateDirect(sphereMesh.indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
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
    private var uIsMoonLoc = 0
    private var uIsMarsLoc = 0

    private var aPositionLoc = 0
    private var aTexCoordinateLoc = 0
    private var aNormalLoc = 0

    private var dayTextureId = 0
    private var nightTextureId = 0
    private var cloudTextureId = 0

    @Volatile
    private var isMoonMode = false

    @Volatile
    private var isMarsMode = false

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

    @Volatile
    private var sunPosition = AstronomyMath.calculateSunPosition()

    private var viewportWidth = 1
    private var viewportHeight = 1
    private var cloudOffset = 0f

    fun updateCamera(
        rotX: Float,
        rotY: Float,
        zoom: Float,
    ) {
        currentRotationX = rotX
        currentRotationY = rotY
        currentZoom = zoom
    }

    fun setSunPosition(position: SunPosition) {
        sunPosition = position
    }

    fun setIsMoon(isMoon: Boolean) {
        isMoonMode = isMoon
        if (isMoon) isMarsMode = false
    }

    fun setIsMars(isMars: Boolean) {
        isMarsMode = isMars
        if (isMars) isMoonMode = false
    }

    @Volatile
    private var moonPhaseAngle = 0.0

    @Volatile
    private var moonSubsolarLatitude = 0.0

    @Volatile
    private var moonLibrationLatitude = 0.0

    @Volatile
    private var moonLibrationLongitude = 0.0

    fun setMoonOrientation(
        phaseAngle: Double,
        subsolarLatitude: Double,
        librationLatitude: Double,
        librationLongitude: Double,
    ) {
        moonPhaseAngle = phaseAngle
        moonSubsolarLatitude = subsolarLatitude
        moonLibrationLatitude = librationLatitude
        moonLibrationLongitude = librationLongitude
    }

    fun setTextures(
        dayBytes: ByteArray,
        nightBytes: ByteArray? = null,
        cloudBytes: ByteArray? = null,
    ) {
        pendingDayBytes = dayBytes
        pendingNightBytes = nightBytes
        pendingCloudBytes = cloudBytes
    }

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?,
    ) {
        // Transparent OpenGL background so Compose deep space starfield shines through
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
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
        uIsMoonLoc = GLES20.glGetUniformLocation(programId, "u_IsMoon")
        uIsMarsLoc = GLES20.glGetUniformLocation(programId, "u_IsMars")

        aPositionLoc = GLES20.glGetAttribLocation(programId, "a_Position")
        aTexCoordinateLoc = GLES20.glGetAttribLocation(programId, "a_TexCoordinate")
        aNormalLoc = GLES20.glGetAttribLocation(programId, "a_Normal")

        Matrix.setIdentityM(viewMatrix, 0)
    }

    override fun onSurfaceChanged(
        gl: GL10?,
        width: Int,
        height: Int,
    ) {
        viewportWidth = width
        viewportHeight = height
        GLES20.glViewport(0, 0, width, height)

        val halfW = width / 2.0f
        val halfH = height / 2.0f
        Matrix.orthoM(projectionMatrix, 0, -halfW, halfW, -halfH, halfH, -50000f, 50000f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        loadTexturesIfPending()

        if (programId == 0 || dayTextureId == 0) return

        GLES20.glUseProgram(programId)

        // Realistic static cloud cover
        cloudOffset = 0f

        // Model matrix with rotation and responsive zoom
        Matrix.setIdentityM(modelMatrix, 0)

        // Exact 1:1 pixel-perfect mathematical synchronization with GlobeView 2D Canvas:
        val baseRadius = minOf(viewportWidth, viewportHeight).toFloat() * 0.38f
        val currentRadius = baseRadius * currentZoom

        val modelPitch = currentRotationX + if (isMoonMode) moonLibrationLatitude.toFloat() else 0f
        val modelYaw = currentRotationY + if (isMoonMode) moonLibrationLongitude.toFloat() else 0f
        Matrix.rotateM(modelMatrix, 0, modelPitch, 1f, 0f, 0f)
        Matrix.rotateM(modelMatrix, 0, modelYaw, 0f, 1f, 0f)
        Matrix.scaleM(modelMatrix, 0, currentRadius, currentRadius, currentRadius)

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uMVMatrixLoc, 1, false, mvMatrix, 0)
        GLES20.glUniform1f(uCloudOffsetLoc, cloudOffset)
        GLES20.glUniform1f(uIsMoonLoc, if (isMoonMode) 1.0f else 0.0f)
        GLES20.glUniform1f(uIsMarsLoc, if (isMarsMode) 1.0f else 0.0f)

        // Real-time astronomical Sun direction
        val radX = modelPitch.toDouble().toRadians
        val radY = modelYaw.toDouble().toRadians
        val cosX = cos(radX)
        val sinX = sin(radX)
        val cosY = cos(radY)
        val sinY = sin(radY)

        val sunEye =
            if (isMoonMode) {
                val phaseRad = moonPhaseAngle.toRadians
                // At phase 180 (Full Moon), Sun is at +Z (facing front)
                // At phase 0 (New Moon), Sun is at -Z (behind Moon)
                // At phase 90 (First Quarter), Sun is at +X (right)
                // At phase 270 (Last Quarter), Sun is at -X (left)
                val latitudeRad = moonSubsolarLatitude.toRadians
                var p =
                    Point3D(
                        sin(phaseRad) * cos(latitudeRad),
                        sin(latitudeRad),
                        -cos(phaseRad) * cos(latitudeRad),
                    )
                p = rotateY(p, cosY, sinY)
                p = rotateX(p, cosX, sinX)
                p
            } else {
                var p = sunPosition.vector
                p = rotateY(p, cosY, sinY)
                p = rotateX(p, cosX, sinX)
                p
            }

        GLES20.glUniform3f(
            uSunDirectionLoc,
            sunEye.x.toFloat(),
            sunEye.y.toFloat(),
            sunEye.z.toFloat(),
        )

        // Bind Day Texture to Unit 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, dayTextureId)
        GLES20.glUniform1i(uDayTextureLoc, 0)

        // Bind Night Texture to Unit 1 (fallback to dayTextureId if not present)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, if (nightTextureId != 0) nightTextureId else dayTextureId)
        GLES20.glUniform1i(uNightTextureLoc, 1)

        // Bind Cloud Texture to Unit 2 (fallback to dayTextureId if not present)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, if (cloudTextureId != 0) cloudTextureId else dayTextureId)
        GLES20.glUniform1i(uCloudTextureLoc, 2)

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
            indexBuffer,
        )

        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aTexCoordinateLoc)
        GLES20.glDisableVertexAttribArray(aNormalLoc)
    }

    private fun loadTexturesIfPending() {
        val dBytes = pendingDayBytes
        if (dBytes != null) {
            pendingDayBytes = null
            if (dayTextureId != 0) {
                GLES20.glDeleteTextures(1, intArrayOf(dayTextureId), 0)
            }
            dayTextureId = loadGLTexture(dBytes)
        }
        val nBytes = pendingNightBytes
        if (nBytes != null) {
            pendingNightBytes = null
            if (nightTextureId != 0) {
                GLES20.glDeleteTextures(1, intArrayOf(nightTextureId), 0)
            }
            nightTextureId = loadGLTexture(nBytes)
        }
        val cBytes = pendingCloudBytes
        if (cBytes != null) {
            pendingCloudBytes = null
            if (cloudTextureId != 0) {
                GLES20.glDeleteTextures(1, intArrayOf(cloudTextureId), 0)
            }
            cloudTextureId = loadGLTexture(cBytes)
        }
    }

    private fun loadGLTexture(bytes: ByteArray): Int {
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

    private fun compileShader(
        type: Int,
        shaderCode: String,
    ): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun createProgram(
        vertexShader: Int,
        fragmentShader: Int,
    ): Int {
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }
}
