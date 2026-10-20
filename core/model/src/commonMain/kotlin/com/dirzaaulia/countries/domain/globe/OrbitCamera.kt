package com.dirzaaulia.countries.domain.globe

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object OrbitCamera {
    const val MIN_PITCH_DEG = -89f
    const val MAX_PITCH_DEG = 89f
    const val DEFAULT_FOVY_DEG = 45f

    fun clampPitch(pitchDeg: Float): Float = pitchDeg.coerceIn(MIN_PITCH_DEG, MAX_PITCH_DEG)

    fun computeCameraDistance(
        viewportHeight: Float,
        zoom: Float,
        baseRadius: Float,
        fovyDeg: Float = DEFAULT_FOVY_DEG,
    ): Float {
        val fovyRad = fovyDeg * (PI.toFloat() / 360f)
        val focalLength = (viewportHeight / 2f) / kotlin.math.tan(fovyRad)
        return (focalLength / zoom.coerceAtLeast(0.1f)).coerceAtLeast(baseRadius * 1.05f)
    }

    fun computeViewMatrix(
        viewMatrix: FloatArray,
        pitchDeg: Float,
        yawDeg: Float,
        cameraDistance: Float = 0f,
    ) {
        Matrix4.setIdentity(viewMatrix)
        if (cameraDistance != 0f) {
            Matrix4.translate(viewMatrix, 0f, 0f, -cameraDistance)
        }
        Matrix4.rotateX(viewMatrix, clampPitch(pitchDeg))
        Matrix4.rotateY(viewMatrix, yawDeg)
    }

    fun worldToEyeSpace(
        world: Point3D,
        pitchDeg: Float,
        yawDeg: Float,
    ): Point3D {
        val clampedPitch = clampPitch(pitchDeg)
        val yawRad = yawDeg.toDouble() * PI / 180.0
        val pitchRad = clampedPitch.toDouble() * PI / 180.0
        val cosYaw = cos(yawRad)
        val sinYaw = sin(yawRad)
        val cosPitch = cos(pitchRad)
        val sinPitch = sin(pitchRad)

        val x1 = world.x * cosYaw + world.z * sinYaw
        val y1 = world.y
        val z1 = -world.x * sinYaw + world.z * cosYaw

        val xEye = x1
        val yEye = y1 * cosPitch - z1 * sinPitch
        val zEye = y1 * sinPitch + z1 * cosPitch

        return Point3D(xEye, yEye, zEye)
    }
}
