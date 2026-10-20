package com.dirzaaulia.countries.domain.globe

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object Matrix4 {
    fun identity(): FloatArray = FloatArray(16).also { setIdentity(it) }

    fun setIdentity(m: FloatArray) {
        for (i in 0..15) {
            m[i] = if (i % 5 == 0) 1f else 0f
        }
    }

    fun multiply(
        result: FloatArray,
        lhs: FloatArray,
        rhs: FloatArray,
    ) {
        val temp = FloatArray(16)
        for (col in 0..3) {
            val c4 = col * 4
            for (row in 0..3) {
                temp[row + c4] =
                    lhs[row] * rhs[c4] +
                    lhs[row + 4] * rhs[c4 + 1] +
                    lhs[row + 8] * rhs[c4 + 2] +
                    lhs[row + 12] * rhs[c4 + 3]
            }
        }
        for (i in 0..15) {
            result[i] = temp[i]
        }
    }

    fun rotateX(
        m: FloatArray,
        angleDeg: Float,
    ) {
        val rad = angleDeg * (PI.toFloat() / 180f)
        val c = cos(rad)
        val s = sin(rad)
        val rot = FloatArray(16)
        setIdentity(rot)
        rot[5] = c
        rot[6] = s
        rot[9] = -s
        rot[10] = c
        multiply(m, m, rot)
    }

    fun rotateY(
        m: FloatArray,
        angleDeg: Float,
    ) {
        val rad = angleDeg * (PI.toFloat() / 180f)
        val c = cos(rad)
        val s = sin(rad)
        val rot = FloatArray(16)
        setIdentity(rot)
        rot[0] = c
        rot[2] = -s
        rot[8] = s
        rot[10] = c
        multiply(m, m, rot)
    }

    fun rotateZ(
        m: FloatArray,
        angleDeg: Float,
    ) {
        val rad = angleDeg * (PI.toFloat() / 180f)
        val c = cos(rad)
        val s = sin(rad)
        val rot = FloatArray(16)
        setIdentity(rot)
        rot[0] = c
        rot[1] = s
        rot[4] = -s
        rot[5] = c
        multiply(m, m, rot)
    }

    fun scale(
        m: FloatArray,
        sx: Float,
        sy: Float,
        sz: Float,
    ) {
        for (i in 0..3) {
            m[i] *= sx
            m[i + 4] *= sy
            m[i + 8] *= sz
        }
    }

    fun ortho(
        m: FloatArray,
        left: Float,
        right: Float,
        bottom: Float,
        top: Float,
        near: Float,
        far: Float,
    ) {
        setIdentity(m)
        val rl = right - left
        val tb = top - bottom
        val fn = far - near
        m[0] = 2.0f / rl
        m[5] = 2.0f / tb
        m[10] = -2.0f / fn
        m[12] = -(right + left) / rl
        m[13] = -(top + bottom) / tb
        m[14] = -(far + near) / fn
        m[15] = 1.0f
    }

    fun translate(
        m: FloatArray,
        tx: Float,
        ty: Float,
        tz: Float,
    ) {
        val trans = FloatArray(16)
        setIdentity(trans)
        trans[12] = tx
        trans[13] = ty
        trans[14] = tz
        multiply(m, m, trans)
    }

    fun perspective(
        m: FloatArray,
        fovyDeg: Float,
        aspect: Float,
        near: Float,
        far: Float,
    ) {
        for (i in 0..15) m[i] = 0f
        val f = 1.0f / kotlin.math.tan(fovyDeg * (PI.toFloat() / 360f))
        val rangeReciprocal = 1.0f / (near - far)
        m[0] = f / aspect
        m[5] = f
        m[10] = (far + near) * rangeReciprocal
        m[11] = -1.0f
        m[14] = 2.0f * far * near * rangeReciprocal
    }

    fun transformPoint(
        m: FloatArray,
        x: Double,
        y: Double,
        z: Double,
    ): Point3D {
        val xf = x.toFloat()
        val yf = y.toFloat()
        val zf = z.toFloat()
        val rx = m[0] * xf + m[4] * yf + m[8] * zf + m[12]
        val ry = m[1] * xf + m[5] * yf + m[9] * zf + m[13]
        val rz = m[2] * xf + m[6] * yf + m[10] * zf + m[14]
        return Point3D(rx.toDouble(), ry.toDouble(), rz.toDouble())
    }
}
