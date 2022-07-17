package com.gitlab.aecsocket.alexandria.core

import com.gitlab.aecsocket.alexandria.core.extension.nextVector3
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.core.physics.quaternionOfAxes
import kotlin.random.Random
import kotlin.test.Test

// this class is for quickly testing different physics maths classes
class PhysicsTest {
    @Test
    fun testTransform() {
        val up = Vector3.Y
        val dir = Random.Default.nextVector3().normalized

        println("$up | ${dir.asString("%ff")}")
        val v1 = up.cross(dir).normalized
        val v2 = dir.cross(v1).normalized
        println("$v1 | $v2")
        println(quaternionOfAxes(v1, v2, dir))
    }
}
