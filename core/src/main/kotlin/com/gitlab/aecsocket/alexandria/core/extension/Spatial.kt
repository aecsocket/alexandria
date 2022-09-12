package com.gitlab.aecsocket.alexandria.core.extension

import com.gitlab.aecsocket.alexandria.core.physics.*
import kotlin.math.*

// Conversion
val Vector3.xy get() = Vector2(x, y)
val Vector4.xy get() = Vector2(x, y)
val Vector4.xyz get() = Vector3(x, y, z)

fun Vector3(v: Vector2, z: Double) = Vector3(v.x, v.y, z)

fun Vector4(v: Vector2, z: Double, w: Double) = Vector4(v.x, v.y, z, w)
fun Vector4(v: Vector3, w: Double) = Vector4(v.x, v.y, v.z, w)

fun Vector2.point() = Point2(x.toInt(), y.toInt())
fun Vector3.point() = Point3(x.toInt(), y.toInt(), z.toInt())

fun Point2.vector() = Vector2(x.toDouble(), y.toDouble())
fun Point3.vector() = Vector3(x.toDouble(), y.toDouble(), z.toDouble())

fun Matrix4(m: Matrix3, def: Double = 0.0, n33: Double = 1.0) = Matrix4(
    m.n00, m.n01, m.n02, def,
    m.n10, m.n11, m.n12, def,
    m.n20, m.n21, m.n22, def,
    def, def, def, n33,
)

// Kotlin random
typealias KRandom = kotlin.random.Random

fun KRandom.nextVector2() = Vector2(nextDouble(), nextDouble())
fun KRandom.nextVector3() = Vector3(nextDouble(), nextDouble(), nextDouble())
fun KRandom.nextVector4() = Vector4(nextDouble(), nextDouble(), nextDouble(), nextDouble())

fun KRandom.nextMatrix3() = Matrix3(
    nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble()
)
fun KRandom.nextMatrix4() = Matrix4(
    nextDouble(), nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble(), nextDouble()
)

fun KRandom.nextQuaternion() = Quaternion(
    nextDouble(), nextDouble(), nextDouble(), nextDouble()
).normalized

// Java random
typealias JRandom = java.util.Random

fun JRandom.nextVector2() = Vector2(nextDouble(), nextDouble())
fun JRandom.nextVector3() = Vector3(nextDouble(), nextDouble(), nextDouble())
fun JRandom.nextVector4() = Vector4(nextDouble(), nextDouble(), nextDouble(), nextDouble())

fun JRandom.nextMatrix3() = Matrix3(
    nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble()
)
fun JRandom.nextMatrix4() = Matrix4(
    nextDouble(), nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble(), nextDouble(),
    nextDouble(), nextDouble(), nextDouble(), nextDouble()
)

private fun trig(ang: Double) = sin(ang / 2) to cos(ang / 2)

// Eulers

typealias Euler3 = Vector3

val Euler3.pitch get() = x
val Euler3.yaw get() = y
val Euler3.roll get() = z

val Euler3.radians get() = map { it.radians }
val Euler3.degrees get() = map { it.degrees }

enum class EulerOrder {
    XYZ,
    ZYX,
    YZX,
}

// https://github.com/mrdoob/three.js/blob/dev/src/math/Quaternion.js#L223
// JME's impl seems to not work properly for our use-case
fun Euler3.quaternion(order: EulerOrder): Quaternion {
    val (s1, c1) = trig(x)
    val (s2, c2) = trig(y)
    val (s3, c3) = trig(z)

    return when (order) {
        EulerOrder.XYZ -> Quaternion(
            s1*c2*c3 + c1*s2*s3,
            c1*s2*c3 - s1*c2*s3,
            c1*c2*s3 + s1*s2*c3,
            c1*c2*c3 - s1*s2*s3,
        )
        EulerOrder.ZYX -> Quaternion(
            s1*c2*c3 - c1*s2*s3,
            c1*s2*c3 + s1*c2*s3,
            c1*c2*s3 - s1*s2*c3,
            c1*c2*c3 + s1*s2*s3,
        )
        EulerOrder.YZX -> Quaternion(
            s1*c2*c3 + c1*s2*s3,
            c1*s2*c3 + s1*c2*s3,
            c1*c2*s3 - s1*s2*c3,
            c1*c2*c3 - s1*s2*s3,
        )
    }
}

// Polar

typealias Polar2 = Vector2

val Polar2.pitch get() = x
val Polar2.yaw get() = y

val Polar2.radians get() = map { it.radians }
val Polar2.degrees get() = map { it.degrees }

// copied from Bukkit's vector (I think?)
fun Polar2.cartesian(): Vector3 {
    val xz = cos(pitch)
    return Vector3(
        -xz * sin(yaw),
        -sin(pitch),
        xz * cos(yaw),
    )
}

fun Polar2.euler() = Euler3(pitch, yaw, 0.0)

fun Vector3.polar(): Polar2 {
    val pitch = atan(-y / sqrt(x*x + z*z))
    val yaw = atan2(-x, z) + PI*2
    return Polar2(pitch, yaw)
}

fun Vector3.euler() = polar().euler()

// Quaternion -> ...

fun Quaternion.matrix(): Matrix3 {
    // https://github.com/mrdoob/three.js/blob/dev/src/math/Matrix4.js # makeRotationFromQuaternion (#compose(zero, q, one))
    // note that we strip out the position and scale code
    // (position = (0,0,0), scale = (1,1,1))
    val (x2, y2, z2) = Triple(x+x, y+y, z+z)
    val (xx, xy, xz) = Triple(x*x2, x*y2, x*z2)
    val (yy, yz, zz) = Triple(y*y2, y*z2, z*z2)
    val (wx, wy, wz) = Triple(w*x2, w*y2, w*z2)

    return Matrix3(
        1 - (yy+zz), xy+wz, xz-wy,
        xy-wz, 1 - (xx+zz), yz+wx,
        xz+wy, yz-wx, 1 - (xx+yy),
    )
}

fun Quaternion.euler(order: EulerOrder): Euler3 {
    return matrix().euler(order)
}

// Matrix -> ...

private const val ONE_EPSILON = 0.999999

fun Matrix3.euler(order: EulerOrder): Euler3 {
    // https://github.com/mrdoob/three.js/blob/dev/src/math/Euler.js#L105
    // I'm aware that XYZ and ZYX are switched. I don't understand why, but that's how it works
    return when (order) {
        EulerOrder.XYZ -> {
            val y = asin(-clamp(n20, -1.0, 1.0))
            if (abs(n20) < ONE_EPSILON) Euler3(
                atan2(n21, n22),
                y,
                atan2(n10, n00),
            ) else Euler3(
                0.0,
                y,
                atan2(-n01, n11),
            )
        }
        EulerOrder.ZYX -> {
            val y = asin(clamp(n02, -1.0, 1.0))
            if (abs(n02) < ONE_EPSILON) Euler3(
                atan2(-n12, n22),
                y,
                atan2(-n01, n00),
            ) else Euler3(
                atan2(n21, n11),
                y,
                0.0,
            )
        }
        EulerOrder.YZX -> {
            val z = asin(clamp(n10, -1.0, 1.0))
            if (abs(n10) < ONE_EPSILON) Euler3(
                atan2(-n12, n11),
                atan2(-n20, n00),
                0.0
            ) else Euler3(
                0.0,
                atan2(n02, n22),
                0.0
            )
        }
    }
}
