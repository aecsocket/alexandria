package com.github.aecsocket.alexandria.core

import com.github.aecsocket.alexandria.core.extension.nextQuaternion
import com.github.aecsocket.alexandria.core.extension.nextVector3
import kotlin.random.Random
import kotlin.test.Test

// this class is for quickly testing different physics maths classes
class PhysicsTest {
    @Test
    fun testTransform() {
        val q = Random.Default.nextQuaternion()
        val v = Random.Default.nextVector3()

        println("q = $q")
        println("v = $v")
        println("q * v = ${q * v}")
    }
}
