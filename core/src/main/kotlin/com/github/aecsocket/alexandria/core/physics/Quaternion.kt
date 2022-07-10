package com.github.aecsocket.alexandria.core.physics

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EPSILON = 0.000001

// https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/math/Quaternion.java
data class Quaternion(val x: Double, val y: Double, val z: Double, val w: Double) {
    constructor(v: Vector3, w: Double) : this(v.x, v.y, v.z, w)

    val conjugate: Quaternion get() = Quaternion(-x, -y, -z, w)

    val norm: Double get() = x*x + y*y + z*z + w*w

    val length: Double get() = sqrt(norm)

    val normalized: Quaternion
        get() {
            val length = length
            return if (length == 0.0) Zero
            else Quaternion(x/length, y/length, z/length, w/length)
        }

    // TODO we can just assume that this quaternion is a unit quaternion
    // so we don't need to normalize or anything, just get conjugate
    // https://github.com/mrdoob/three.js/blob/dev/src/math/Quaternion.js #invert
    val inverse: Quaternion
        get() {
            val norm = norm
            return if (norm > 0.0) {
                val invNorm = 1 / norm
                Quaternion(-x*invNorm, -y*invNorm, -z*invNorm, w*invNorm)
            } else throw ArithmeticException("Cannot invert quaternion with norm $norm: $this")
        }

    operator fun times(q: Quaternion) = Quaternion(
        x*q.w + y*q.z - z*q.y + w*q.x,
        -x*q.z + y*q.w + z*q.x + w*q.y,
        x*q.y - y*q.x + z*q.w + w*q.z,
        -x*q.x - y*q.y - z*q.z + w*q.w,
    )

    operator fun times(s: Double) = Quaternion(
        x*s, y*s, z*s, w*s
    )

    operator fun times(v: Vector3): Vector3 {
        val u = Vector3(x, y, z)
        val s = w
        return (u * 2.0 * u.dot(v)) +
                (v * (s*s - u.dot(u))) +
                (u.cross(v) * 2.0 * s)
    }

    fun dot(q: Quaternion) = x*q.x + y*q.y + z*q.z + w*q.w

    fun asString(fmt: String = "%f") = "($fmt + ${fmt}i + ${fmt}j + ${fmt}k)".format(w, x, y, z)
    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Identity = Quaternion(0.0, 0.0, 0.0, 1.0)
        val Zero = Quaternion(0.0, 0.0, 0.0, 0.0)
    }
}

fun slerp(q1: Quaternion, q2: Quaternion, t: Double): Quaternion {
    var (q2x, q2y, q2z, q2w) = q2
    var result = q1.x*q2x + q1.y*q2y + q1.z*q2z + q1.w*q2w
    if (result < 0.0) {
        q2x *= -1
        q2y *= -1
        q2z *= -1
        q2w *= -1
        result *= -1
    }

    var scale0 = 1 - t
    var scale1 = t
    if ((1 - result) > 0.1) {
        val theta = acos(result)
        val invSinTheta = 1.0 / sin(theta)

        scale0 = sin((1 - t) * theta) * invSinTheta
        scale1 = sin(t * theta) * invSinTheta
    }

    return Quaternion(
        scale0*q1.x + scale1*q2x,
        scale0*q1.y + scale1*q2y,
        scale0*q1.z + scale1*q2z,
        scale0*q1.w + scale1*q2w,
    )
}

// must all be normalized
fun quaternionOfAxes(x: Vector3, y: Vector3, z: Vector3): Quaternion {
    val t = x.x + y.y + z.z
    val (m00, m10, m20) = x
    val (m01, m11, m21) = y
    val (m02, m12, m22) = z
    return when {
        t >= 0 -> {
            val s = sqrt(t + 1)
            val s2 = 0.5 / s
            Quaternion(
                (m21 - m12) * s2,
                (m02 - m20) * s2,
                (m10 - m01) * s2,
                s * 0.5,
            )
        }
        m00 > m11 && m00 > m22 -> {
            val s = sqrt(1 + m00 - m11 - m22)
            val s2 = 0.5 / s
            Quaternion(
                s * 0.5,
                (m10 + m01) * s2,
                (m02 + m20) * s2,
                (m21 - m12) * s2,
            )
        }
        m11 > m22 -> {
            val s = sqrt(1 + m11 - m00 - m22)
            val s2 = 0.5 / s
            Quaternion(
                (m10 + m01) * s2,
                s * 0.5,
                (m21 + m12) * s,
                (m02 - m20) * s,
            )
        }
        else -> {
            val s = sqrt(1 + m22 - m00 - m11)
            val s2 = 0.5 / s
            Quaternion(
                (m02 + m20) * s2,
                (m21 + m12) * s2,
                s * 0.5,
                (m10 - m01) * s2,
            )
        }
    }
}

// https://github.com/mrdoob/three.js/blob/dev/src/math/Quaternion.js#L358
fun quaternionFromTo(from: Vector3, to: Vector3): Quaternion {
    var r = from.dot(to) + 1
    return (if (r < EPSILON) {
        // `from` and `to` point in opposite directions
        r = 0.0
        if (abs(from.x) > abs(from.z)) Quaternion(
            -from.y,
            from.x,
            0.0,
            r,
        ) else Quaternion(
            0.0,
            -from.z,
            from.y,
            r
        )
    } else Quaternion(
        from.y*to.z - from.z*to.y,
        from.z*to.x - from.x*to.z,
        from.x*to.y - from.y*to.x,
        r
    )).normalized
}

// same semantics as https://docs.unity3d.com/ScriptReference/Quaternion.LookRotation.html
fun quaternionLooking(dir: Vector3, up: Vector3): Quaternion {
    val v1 = up.cross(dir).normalized

    return if (v1 == Vector3.Zero) {
        // `dir` and `up` are collinear
        quaternionFromTo(Vector3.Z, dir)
    } else {
        val v2 = dir.cross(v1).normalized
        quaternionOfAxes(v1, v2, dir)
    }
}
