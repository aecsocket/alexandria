package io.gitlab.aecsocket.alexandria.core.physics

import io.gitlab.aecsocket.alexandria.core.extension.EPSILON
import io.gitlab.aecsocket.alexandria.core.extension.quaternion
import kotlin.math.*

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

    operator fun unaryMinus() = this * -1.0

    fun dot(q: Quaternion) = x*q.x + y*q.y + z*q.z + w*q.w

    fun asString(fmt: String = "%f") = "($fmt + ${fmt}i + ${fmt}j + ${fmt}k)".format(w, x, y, z)
    override fun toString() = asString(DECIMAL_FORMAT)

    override fun equals(other: Any?) = other is Quaternion &&
            x.compareTo(other.x) == 0 && y.compareTo(other.y) == 0 && z.compareTo(other.z) == 0 && w.compareTo(other.w) == 0

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

fun quaternionOfAxisAngle(axis: Vector3, angle: Double): Quaternion {
    val halfAngle = angle / 2.0
    val s = sin(halfAngle)
    return Quaternion(
        axis.x * s,
        axis.y * s,
        axis.z * s,
        cos(halfAngle),
    )
}

// must all be normalized
fun quaternionOfAxes(x: Vector3, y: Vector3, z: Vector3) = Matrix3(
    x.x, y.x, z.x,
    x.y, y.y, z.y,
    x.z, y.z, z.z
).quaternion()

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

    return if (v1.sqrLength < EPSILON) {
        // `dir` and `up` are collinear
        quaternionFromTo(Vector3.Forward, dir)
    } else {
        val v2 = dir.cross(v1).normalized
        quaternionOfAxes(v1, v2, dir)
    }
}
