package com.gitlab.aecsocket.alexandria.core.physics

import kotlin.math.abs

data class Matrix3(
    val n00: Double, val n01: Double, val n02: Double,
    val n10: Double, val n11: Double, val n12: Double,
    val n20: Double, val n21: Double, val n22: Double,
) {
    operator fun times(m: Matrix3) = Matrix3(
        n00*m.n00 + n01*m.n10 + n02*m.n20,  n00*m.n01 + n01*m.n11 + n02*m.n21,  n00*m.n02 + n01*m.n12 + n02*m.n22,
        n10*m.n00 + n11*m.n10 + n12*m.n20,  n10*m.n01 + n11*m.n11 + n12*m.n21,  n10*m.n02 + n11*m.n12 + n12*m.n22,
        n20*m.n00 + n21*m.n10 + n22*m.n20,  n20*m.n01 + n21*m.n11 + n22*m.n21,  n20*m.n02 + n21*m.n12 + n22*m.n22,
    )

    operator fun times(v: Vector3) = Vector3(
        n00*v.x + n01*v.y + n02*v.z,
        n10*v.x + n11*v.y + n12*v.z,
        n20*v.x + n21*v.y + n22*v.z,
    )

    operator fun times(s: Double) = Matrix3(
        n00*s, n01*s, n02*s,
        n10*s, n11*s, n12*s,
        n20*s, n21*s, n22*s,
    )

    // https://github.com/glslify/glsl-inverse/blob/master/index.glsl
    val inverse: Matrix3
        get() {
        val b00 = n11*n22 - n12*n21
        val b10 = n12*n20 - n10*n22
        val b20 = n10*n21 - n11*n20
        val det = n00*b00 + n01*b10 + n02*b20
        return if (abs(det) <= 0.0) Zero else Matrix3(
            n11*n22 - n12*n21, n02*n21 - n01*n22, n01*n12 - n02*n11,
            n12*n20 - n10*n22, n00*n22 - n02*n20, n02*n10 - n00*n12,
            n10*n21 - n11*n20, n01*n20 - n00*n21, n00*n11 - n01*n10,
        ) * (1 / det)
    }

    // get column
    operator fun get(i: Int) = when (i) {
        0 -> Vector3(n00, n10, n20)
        1 -> Vector3(n01, n11, n21)
        2 -> Vector3(n02, n12, n22)
        else -> throw IndexOutOfBoundsException()
    }

    fun asString(fmt: String = "%f") = """Matrix3 [
  $fmt $fmt $fmt
  $fmt $fmt $fmt
  $fmt $fmt $fmt
]""".format(
        n00, n01, n02,
        n10, n11, n12,
        n20, n21, n22,
    )

    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Identity = Matrix3(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0,
        )

        val Zero = Matrix3(
            0.0, 0.0, 0.0,
            0.0, 0.0, 0.0,
            0.0, 0.0, 0.0,
        )
    }
}

data class Matrix4(
    val n00: Double, val n01: Double, val n02: Double, val n03: Double,
    val n10: Double, val n11: Double, val n12: Double, val n13: Double,
    val n20: Double, val n21: Double, val n22: Double, val n23: Double,
    val n30: Double, val n31: Double, val n32: Double, val n33: Double,
) {
    operator fun times(m: Matrix4) = Matrix4(
        n00*m.n00 + n01*m.n10 + n02*m.n20 + n03*m.n30,  n00*m.n01 + n01*m.n11 + n02*m.n21 + n03*m.n31,  n00*m.n02 + n01*m.n12 + n02*m.n22 + n03*m.n32,  n00*m.n03 + n01*m.n13 + n02*m.n23 + n03*m.n33,
        n10*m.n00 + n11*m.n10 + n12*m.n20 + n13*m.n30,  n10*m.n01 + n11*m.n11 + n12*m.n21 + n13*m.n31,  n10*m.n02 + n11*m.n12 + n12*m.n22 + n13*m.n32,  n10*m.n03 + n11*m.n13 + n12*m.n23 + n13*m.n33,
        n20*m.n00 + n21*m.n10 + n22*m.n20 + n23*m.n30,  n20*m.n01 + n21*m.n11 + n22*m.n21 + n23*m.n31,  n20*m.n02 + n21*m.n12 + n22*m.n22 + n23*m.n32,  n20*m.n03 + n21*m.n13 + n22*m.n23 + n33*m.n33,
        n30*m.n00 + n31*m.n10 + n32*m.n20 + n33*m.n30,  n30*m.n01 + n31*m.n11 + n32*m.n21 + n33*m.n31,  n30*m.n02 + n31*m.n12 + n32*m.n22 + n33*m.n32,  n30*m.n03 + n31*m.n13 + n32*m.n23 + n33*m.n33,
    )

    operator fun times(v: Vector4) = Vector4(
        n00*v.x + n01*v.y + n02*v.z + n03*v.w,
        n10*v.x + n11*v.y + n12*v.z + n13*v.w,
        n20*v.x + n21*v.y + n22*v.z + n23*v.w,
        n30*v.x + n31*v.y + n32*v.z + n33*v.w,
    )

    operator fun times(s: Double) = Matrix4(
        n00*s, n01*s, n02*s, n03*s,
        n10*s, n11*s, n12*s, n13*s,
        n20*s, n21*s, n22*s, n23*s,
        n30*s, n31*s, n32*s, n33*s,
    )

    val inverse: Matrix4
        get() {
        val fA0 = n00*n11 - n01*n10
        val fA1 = n00*n12 - n02*n10
        val fA2 = n00*n13 - n03*n10
        val fA3 = n01*n12 - n02*n11
        val fA4 = n01*n13 - n03*n11
        val fA5 = n02*n13 - n03*n12
        val fB0 = n20*n31 - n21*n30
        val fB1 = n20*n32 - n22*n30
        val fB2 = n20*n33 - n23*n30
        val fB3 = n21*n32 - n22*n31
        val fB4 = n21*n33 - n23*n31
        val fB5 = n22*n33 - n23*n32
        val det = fA0*fB5 - fA1*fB4 + fA2*fB3 + fA3*fB2 - fA4*fB1 + fA5*fB0

        return if (abs(det) <= 0.0) Zero else Matrix4(
            +n11 * fB5 - n12 * fB4 + n13 * fB3,  -n01 * fB5 + n02 * fB4 - n03 * fB3,  +n31 * fA5 - n32 * fA4 + n33 * fA3,  -n21 * fA5 + n22 * fA4 - n23 * fA3,
            -n10 * fB5 + n12 * fB2 - n13 * fB1,  +n00 * fB5 - n02 * fB2 + n03 * fB1,  -n30 * fA5 + n32 * fA2 - n33 * fA1,  +n20 * fA5 - n22 * fA2 + n23 * fA1,
            +n10 * fB4 - n11 * fB2 + n13 * fB0,  -n00 * fB4 + n01 * fB2 - n03 * fB0,  +n30 * fA4 - n31 * fA2 + n33 * fA0,  -n20 * fA4 + n21 * fA2 - n23 * fA0,
            -n10 * fB3 + n11 * fB1 - n12 * fB0,  +n00 * fB3 - n01 * fB1 + n02 * fB0,  -n30 * fA3 + n31 * fA1 - n32 * fA0,  +n20 * fA3 - n21 * fA1 + n22 * fA0,
        ) * (1 / det)
    }

    // get column
    operator fun get(i: Int) = when (i) {
        0 -> Vector4(n00, n10, n20, n30)
        1 -> Vector4(n01, n11, n21, n31)
        2 -> Vector4(n02, n12, n22, n32)
        3 -> Vector4(n03, n13, n23, n33)
        else -> throw IndexOutOfBoundsException()
    }

    fun asString(fmt: String = "%f") = """Matrix4 [
  $fmt $fmt $fmt $fmt
  $fmt $fmt $fmt $fmt
  $fmt $fmt $fmt $fmt
  $fmt $fmt $fmt $fmt
]""".format(
        n00, n01, n02, n03,
        n10, n11, n12, n13,
        n20, n21, n22, n23,
        n30, n31, n32, n33,
    )

    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Identity = Matrix4(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0,
        )

        val Zero = Matrix4(
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
        )
    }
}
