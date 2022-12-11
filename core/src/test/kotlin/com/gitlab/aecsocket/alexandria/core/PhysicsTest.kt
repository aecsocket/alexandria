package com.gitlab.aecsocket.alexandria.core

import com.gitlab.aecsocket.alexandria.core.extension.nextVector3
import com.gitlab.aecsocket.alexandria.core.physics.*
import org.junit.jupiter.api.Test
import kotlin.random.Random

// this class is for quickly testing different physics maths classes
class PhysicsTest {
    @Test
    fun testTransform() {
        val ray = Ray(
            Vector3(0.926141, 1.217214, 1.556720),
            Vector3(-0.237886, -0.502104, -0.831446)
        )
        val box = BoxShape(Vector3(0.5))
        testRayBox(ray, box)
    }
}
