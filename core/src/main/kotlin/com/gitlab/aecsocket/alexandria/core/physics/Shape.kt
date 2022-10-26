package com.gitlab.aecsocket.alexandria.core.physics

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required
import org.spongepowered.configurate.objectmapping.meta.Setting

sealed interface Shape

object EmptyShape : Shape

@ConfigSerializable
data class SphereShape(
    @Required val radius: Double
) : Shape {
    companion object {
        val Unit = SphereShape(1.0)
    }
}

@ConfigSerializable
data class BoxShape(
    @Required val halfExtent: Vector3
) : Shape {
    companion object {
        val Unit = BoxShape(Vector3.One)
        val Half = BoxShape(Vector3(0.5))
    }
}

@ConfigSerializable
data class PlaneShape(
    @Required val normal: Vector3
) : Shape {
    companion object {
        val X = PlaneShape(Vector3.X)
        val Y = PlaneShape(Vector3.Y)
        val Z = PlaneShape(Vector3.Z)
    }
}

@ConfigSerializable
data class CompoundShape(
    @Setting(nodeFromParent = true) val children: List<Child>
) : Shape {
    @ConfigSerializable
    data class Child(
        val shape: Shape,
        val transform: Transform
    )
}
