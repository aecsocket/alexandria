package com.gitlab.aecsocket.alexandria.core.extension

import com.gitlab.aecsocket.alexandria.core.physics.*
import kotlin.math.*

private const val ONE_EPSILON = 0.999999

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

fun Matrix4(
    m: Matrix3,
    n03: Double, n13: Double, n23: Double,
    n30: Double, n31: Double, n32: Double, n33: Double
) = Matrix4(
    m.n00, m.n01, m.n02, n03,
    m.n10, m.n11, m.n12, n13,
    m.n20, m.n21, m.n22, n23,
      n30,   n31,   n32, n33
)

fun Matrix3(m: Matrix4) = Matrix3(
    m.n00, m.n01, m.n02,
    m.n10, m.n11, m.n12,
    m.n20, m.n21, m.n22
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

// Eulers

typealias Euler3 = Vector3

val Euler3.pitch get() = x
val Euler3.yaw get() = y
val Euler3.roll get() = z

val Euler3.radians get() = map { it.radians }
val Euler3.degrees get() = map { it.degrees }

enum class EulerOrder {
    XYZ,
    YXZ,
    ZXY,
    ZYX,
    YZX,
    XZY
}

// https://github.com/mrdoob/three.js/blob/dev/src/math/Quaternion.js#L223
fun Euler3.quaternion(order: EulerOrder): Quaternion {
    val s1 = sin(x); val c1 = cos(x)
    val s2 = sin(y); val c2 = cos(y)
    val s3 = sin(z); val c3 = cos(z)

    /*
                    float f = sin(0.5F * x);
        float g = cos(0.5F * x);
        float h = sin(0.5F * y);
        float i = cos(0.5F * y);
        float j = sin(0.5F * z);
        float k = cos(0.5F * z);
        this.i = f * i * k + g * h * j;
        this.j = g * h * k - f * i * j;
        this.k = f * h * k + g * i * j;
        this.r = g * i * k - f * h * j;



     */

    return when (order) {
        EulerOrder.XYZ -> Quaternion(
            s1*c2*c3 + c1*s2*s3,
            c1*s2*c3 - s1*c2*s3,
            c1*c2*s3 + s1*s2*c3,
            c1*c2*c3 - s1*s2*s3
        )
        EulerOrder.YXZ -> Quaternion(
            s1*c2*c3 + c1*s2*s3,
            c1*s2*c3 - s1*c2*s3,
            c1*c2*s3 - s1*s2*c3,
            c1*c2*c3 + s1*s2*s3
        )
        EulerOrder.ZXY -> Quaternion(
            s1*c2*c3 - c1*s2*s3,
            c1*s2*c3 + s1*c2*s3,
            c1*c2*s3 + s1*s2*c3,
            c1*c2*c3 - s1*s2*s3
        )
        EulerOrder.ZYX -> Quaternion(
            s1*c2*c3 - c1*s2*s3,
            c1*s2*c3 + s1*c2*s3,
            c1*c2*s3 - s1*s2*c3,
            c1*c2*c3 + s1*s2*s3
        )
        EulerOrder.YZX -> Quaternion(
            s1*c2*c3 + c1*s2*s3,
            c1*s2*c3 + s1*c2*s3,
            c1*c2*s3 - s1*s2*c3,
            c1*c2*c3 - s1*s2*s3
        )
        EulerOrder.XZY -> Quaternion(
            s1*c2*c3 - c1*s2*s3,
            c1*s2*c3 - s1*c2*s3,
            c1*c2*s3 + s1*s2*c3,
            c1*c2*c3 + s1*s2*s3,
        )
    }
}

// Matrix -> ...

// https://github.com/mrdoob/three.js/blob/dev/src/math/Quaternion.js#L223
fun Matrix3.quaternion(): Quaternion {
    val trace = n00 + n11 + n22
    return when {
        trace >= 0 -> {
            val s = 0.5 / sqrt(trace + 1)
            Quaternion(
                (n21 - n12) * s,
                (n02 - n20) * s,
                (n10 - n01) * s,
                0.25 / s
            )
        }
        n00 > n11 && n00 > n22 -> {
            val s = 2.0 * sqrt(1.0 + n00 - n11 - n22)
            Quaternion(
                0.25 * s,
                (n01 + n10) / s,
                (n02 + n20) / s,
                (n21 - n12) / s
            )
        }
        n11 > n22 -> {
            val s = 2.0 * sqrt(1.0 + n11 - n00 - n22)
            Quaternion(
                (n01 + n10) / s,
                0.25 * s,
                (n12 + n21) / s,
                (n02 - n20) / s
            )
        }
        else -> {
            val s = 2.0 * sqrt(1.0 + n22 - n00 - n11)
            Quaternion(
                (n02 + n20) / s,
                (n12 + n21) / s,
                0.25 * s,
                (n10 - n01) / s
            )
        }
    }
}

// https://github.com/mrdoob/three.js/blob/dev/src/math/Euler.js#L105
fun Matrix3.euler(order: EulerOrder): Euler3 {
    return when (order) {
        EulerOrder.XYZ -> {
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
        EulerOrder.YXZ -> {
            val x = asin(-clamp(n12, -1.0, 1.0))
            if (abs(n12) < ONE_EPSILON) Euler3(
                x,
                atan2(n02, n22),
                atan2(n10, n11)
            ) else Euler3(
                x,
                atan2(-n20, n00),
                0.0
            )
        }
        EulerOrder.ZXY -> {
            val x = asin(clamp(n21, -1.0, 1.0))
            return if (abs(n21) < ONE_EPSILON) Euler3(
                x,
                atan2(-n20, n22),
                atan2(-n01, n11)
            ) else Euler3(
                x,
                0.0,
                atan2(n10, n00)
            )
        }
        EulerOrder.ZYX -> {
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
        EulerOrder.YZX -> {
            val z = asin(clamp(n10, -1.0, 1.0))
            if (abs(n10) < ONE_EPSILON) Euler3(
                atan2(-n12, n11),
                atan2(-n20, n00),
                z,
            ) else Euler3(
                0.0,
                atan2(n02, n22),
                z,
            )
        }
        EulerOrder.XZY -> {
            val z = asin(-clamp(n01, -1.0, 1.0))
            return if (abs(n01) < ONE_EPSILON) Euler3(
                atan2(n21, n11),
                atan2(n02, n00),
                z
            ) else Euler3(
                atan2(-n12, n22),
                0.0,
                z
            )
        }
    }
}

fun Matrix4.transform(): Transform {
    val translation = Vector3(n03, n13, n23)
    val rotation = Matrix3(this).quaternion()
    return Transform(translation, rotation)
}

// Quaternion -> ...

fun Quaternion.matrix(): Matrix3 {
    // https://github.com/mrdoob/three.js/blob/dev/src/math/Matrix4.js # makeRotationFromQuaternion (#compose(zero, q, one))
    // note that we strip out the position and scale code (position = (0,0,0), scale = (1,1,1))
    // our matrices are in the opposite col/row order to three.js

    val x2 = x+x; val y2 = y+y; val z2 = z+z
    val xx = x*x2; val xy = x*y2; val xz = x*z2;
    val yy = y*y2; val yz = y*z2; val zz = z*z2;
    val wx = w*x2; val wy = w*y2; val wz = w*z2;

    return Matrix3(
        1 - (yy+zz), xy-wz, xz+wy,
        xy+wz, 1 - (xx+zz), yz-wx,
        xz-wy, yz+wx, 1 - (xx+yy)
    )
}

fun Quaternion.euler(order: EulerOrder): Euler3 {
    return matrix().euler(order)
}

// Transform -> ...

fun Transform.matrix(): Matrix4 {
    return Matrix4(rotation.matrix(),
        position.x, position.y, position.z,
        0.0, 0.0, 0.0, 1.0
    )
}
