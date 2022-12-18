package com.gitlab.aecsocket.alexandria.core

import com.gitlab.aecsocket.alexandria.core.extension.*
import com.gitlab.aecsocket.alexandria.core.physics.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

// this class is for quickly testing different physics maths classes
class PhysicsTest {
    @Test
    fun testTransform() {
        val transform = Transform(
            Random.nextVector3(),
            Random.nextQuaternion()
        )

        //assertEquals(transform.inverse, transform.matrix().inverse.transform())
    }
}
