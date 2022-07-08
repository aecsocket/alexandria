package com.github.aecsocket.alexandria.core.extension

import com.github.aecsocket.alexandria.core.spatial.*
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

// Angles

typealias Euler3 = Vector3

val Euler3.pitch get() = x
val Euler3.yaw get() = y
val Euler3.roll get() = z

val Euler3.radians get() = map { it.radians }
val Euler3.degrees get() = map { it.degrees }

private fun trig(ang: Double) = sin(ang / 2) to cos(ang / 2)

// https://github.com/mrdoob/three.js/blob/dev/src/math/Quaternion.js#L223
// JME's impl seems to not work properly for our use-case
fun Euler3.quaternion(): Quaternion {
    val (s1, c1) = trig(x)
    val (s2, c2) = trig(y)
    val (s3, c3) = trig(z)
    return Quaternion(
        s1*c2*c3 + c1*s2*s3,
        c1*s2*c3 - s1*c2*s3,
        c1*c2*s3 + s1*s2*c3,
        c1*c2*c3 - s1*s2*s3,
    )
}

// https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/math/Quaternion.java l328
fun Quaternion.euler(): Euler3 {
    val ww = w*w
    val xx = x*x
    val yy = y*y
    val zz = z*z
    val unit = xx + yy + zz + ww
    val test = x*y + z*w
    return when {
        test > 0.499 * unit -> Euler3(
            0.0, 2 * atan2(x, w), PI / 2
        )
        test < -0.499 * unit -> Euler3(
            0.0, -2 * atan2(x, w), -PI / 2
        )
        else -> Euler3(
            atan2(2*x*w - 2*y*z, -xx + yy - zz + ww), // pitch
            atan2(2*y*w - 2*x*z, xx - yy - zz + ww), // yaw
            asin(2 * test / unit), // roll
        )
    }
}

fun Quaternion.matrix(): Matrix3 {
    val norm = norm
    val s = if (norm == 1.0) 2.0 else if (norm > 0.0) 2.0 / norm else 0.0

    val (xs, ys, zs) = Triple(x*s, y*s, z*s)
    val (xx, xy, xz) = Triple(x*xs, x*ys, x*zs)
    val (xw, yy, yz) = Triple(w*xs, y*ys, y*zs)
    val (yw, zz, zw) = Triple(w*ys, z*zs, w*zs)

    return Matrix3(
        1-(yy+zz), (xy-zw), (zw+yw),
        (xy+zw), 1-(xx+zz), (yz-xw),
        (xz-yw), (yz+xw), 1-(xx+yy),
    )
}
