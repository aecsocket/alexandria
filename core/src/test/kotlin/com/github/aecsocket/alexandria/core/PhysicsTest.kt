package com.github.aecsocket.alexandria.core

import com.github.aecsocket.alexandria.core.physics.Vector3
import com.github.aecsocket.alexandria.core.physics.quaternionLooking
import kotlin.test.Test

// this class is for quickly testing different physics maths classes
class PhysicsTest {
    @Test
    fun testTransform() {
        val up = Vector3.Y
        val dir = Vector3.Y

        val v1 = up.cross(dir).normalized
        val v2 = dir.cross(v1).normalized

        println("$up | $dir")
        println("$v1 | $v2")

        println(quaternionLooking(dir, up))
    }
}
