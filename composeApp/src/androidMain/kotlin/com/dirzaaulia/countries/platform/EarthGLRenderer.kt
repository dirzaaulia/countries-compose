package com.dirzaaulia.countries.platform

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeShaders
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.SphereMesh
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toRadians
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class EarthGLRenderer(
    @Suppress("UNUSED_PARAMETER") context: Context,
) : GLSurfaceView.Renderer {
    private val sphereMesh = SphereMesh(stacks = 48, sectors = 96, radius = 1.0f)
    private val sphereBuffers = SphereGLBuffers(sphereMesh)
    private val ringState = RingGLState()

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
    private var uPlanetTypeLoc = 0

    private var aPositionLoc = 0
    private var aTexCoordinateLoc = 0
    private var aNormalLoc = 0

    private val textures = EarthGLTextureManager()

    @Volatile private var isMoonMode = false

    @Volatile private var isMarsMode = false
    private var planetType = 0f

    @Volatile private var currentRotationX = 0f

    @Volatile private var currentRotationY = 0f

    @Volatile private var currentZoom = 1.0f

    @Volatile private var sunPosition = AstronomyMath.calculateSunPosition()

    @Volatile private var moonPhaseAngle = 0.0

    @Volatile private var moonSubsolarLatitude = 0.0

    @Volatile private var moonLibrationLatitude = 0.0

    @Volatile private var moonLibrationLongitude = 0.0

    private var viewportWidth = 1
    private var viewportHeight = 1
    private var cloudOffset = 0f

    fun setPlanetType(type: Float) {
        planetType = type
    }

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
    ) = textures.setTextures(dayBytes, nightBytes, cloudBytes)

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

        val vertexShader = compileGLShader(GLES20.GL_VERTEX_SHADER, GlobeShaders.VERTEX_SHADER)
        val fragmentShader = compileGLShader(GLES20.GL_FRAGMENT_SHADER, GlobeShaders.FRAGMENT_SHADER)
        programId = createGLProgram(vertexShader, fragmentShader)

        uMVPMatrixLoc = GLES20.glGetUniformLocation(programId, "u_MVPMatrix")
        uMVMatrixLoc = GLES20.glGetUniformLocation(programId, "u_MVMatrix")
        uDayTextureLoc = GLES20.glGetUniformLocation(programId, "u_DayTexture")
        uNightTextureLoc = GLES20.glGetUniformLocation(programId, "u_NightTexture")
        uCloudTextureLoc = GLES20.glGetUniformLocation(programId, "u_CloudTexture")
        uSunDirectionLoc = GLES20.glGetUniformLocation(programId, "u_SunDirection")
        uCloudOffsetLoc = GLES20.glGetUniformLocation(programId, "u_CloudOffset")
        uIsMoonLoc = GLES20.glGetUniformLocation(programId, "u_IsMoon")
        uIsMarsLoc = GLES20.glGetUniformLocation(programId, "u_IsMars")
        uPlanetTypeLoc = GLES20.glGetUniformLocation(programId, "u_PlanetType")

        aPositionLoc = GLES20.glGetAttribLocation(programId, "a_Position")
        aTexCoordinateLoc = GLES20.glGetAttribLocation(programId, "a_TexCoordinate")
        aNormalLoc = GLES20.glGetAttribLocation(programId, "a_Normal")

        Matrix.setIdentityM(viewMatrix, 0)
        ringState.initRing()
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

        if (programId == 0 || textures.dayTextureId == 0) return

        GLES20.glUseProgram(programId)

        cloudOffset = 0f

        val baseRadius = minOf(viewportWidth, viewportHeight).toFloat() * 0.38f
        val currentRadius = baseRadius * currentZoom

        // NASA Eyes orbital camera: User controls orbital camera around the planet
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.rotateM(viewMatrix, 0, currentRotationX, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, currentRotationY, 0f, 1f, 0f)

        // Planet stays stationary in world space with its natural astronomical axial tilt
        Matrix.setIdentityM(modelMatrix, 0)
        val axialTilt = getPlanetAxialTilt(planetType, isMoonMode)
        if (axialTilt != 0f) {
            Matrix.rotateM(modelMatrix, 0, axialTilt, 1f, 0f, 0f)
        }
        if (isMoonMode) {
            Matrix.rotateM(modelMatrix, 0, moonLibrationLatitude.toFloat(), 1f, 0f, 0f)
            Matrix.rotateM(modelMatrix, 0, moonLibrationLongitude.toFloat(), 0f, 1f, 0f)
        }
        Matrix.scaleM(modelMatrix, 0, currentRadius, currentRadius, currentRadius)

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uMVMatrixLoc, 1, false, mvMatrix, 0)
        GLES20.glUniform1f(uCloudOffsetLoc, cloudOffset)
        GLES20.glUniform1f(uIsMoonLoc, if (isMoonMode) 1.0f else 0.0f)
        GLES20.glUniform1f(uIsMarsLoc, if (isMarsMode) 1.0f else 0.0f)
        GLES20.glUniform1f(uPlanetTypeLoc, planetType)

        // Sun direction in Eye Space: World-space Sun direction transformed by camera viewMatrix
        val sunWorld =
            if (isMoonMode) {
                val phaseRad = moonPhaseAngle.toRadians
                val latitudeRad = moonSubsolarLatitude.toRadians
                Point3D(
                    sin(phaseRad) * cos(latitudeRad),
                    sin(latitudeRad),
                    -cos(phaseRad) * cos(latitudeRad),
                )
            } else {
                sunPosition.vector
            }

        val radX = currentRotationX.toDouble().toRadians
        val radY = currentRotationY.toDouble().toRadians
        val sunEye = rotateX(rotateY(sunWorld, radY), radX)

        GLES20.glUniform3f(
            uSunDirectionLoc,
            sunEye.x.toFloat(),
            sunEye.y.toFloat(),
            sunEye.z.toFloat(),
        )

        // Bind Day Texture to Unit 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.dayTextureId)
        GLES20.glUniform1i(uDayTextureLoc, 0)

        // Bind Night Texture to Unit 1
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        val nightId = if (textures.nightTextureId != 0) textures.nightTextureId else textures.dayTextureId
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nightId)
        GLES20.glUniform1i(uNightTextureLoc, 1)

        // Bind Cloud Texture to Unit 2
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        val cloudId = if (textures.cloudTextureId != 0) textures.cloudTextureId else textures.dayTextureId
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, cloudId)
        GLES20.glUniform1i(uCloudTextureLoc, 2)

        // Vertex positions, texcoords, normals
        GLES20.glEnableVertexAttribArray(aPositionLoc)
        GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, 0, sphereBuffers.vertexBuffer)

        GLES20.glEnableVertexAttribArray(aTexCoordinateLoc)
        GLES20.glVertexAttribPointer(aTexCoordinateLoc, 2, GLES20.GL_FLOAT, false, 0, sphereBuffers.texCoordBuffer)

        GLES20.glEnableVertexAttribArray(aNormalLoc)
        GLES20.glVertexAttribPointer(aNormalLoc, 3, GLES20.GL_FLOAT, false, 0, sphereBuffers.normalBuffer)

        // Draw the 3D Sphere
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            sphereBuffers.indexCount,
            GLES20.GL_UNSIGNED_SHORT,
            sphereBuffers.indexBuffer,
        )

        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aTexCoordinateLoc)
        GLES20.glDisableVertexAttribArray(aNormalLoc)

        // Draw Saturn ring disc (second pass — only for Saturn, planetType == 6)
        if (planetType == 6f) {
            ringState.drawRings(
                projectionMatrix = projectionMatrix,
                viewMatrix = viewMatrix,
                currentRadius = currentRadius,
                sunEye = sunEye,
            )
        }
    }

    private fun loadTexturesIfPending() = textures.uploadPending()
}
