package com.github.aecsocket.alexandria.core.serializer

import com.github.aecsocket.alexandria.core.extension.force
import com.github.aecsocket.alexandria.core.physics.Point2
import com.github.aecsocket.alexandria.core.physics.Point3
import com.github.aecsocket.alexandria.core.physics.Vector2
import com.github.aecsocket.alexandria.core.physics.Vector3
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

abstract class CartesianSerializer<N, T> : TypeSerializer<T> {
    protected abstract val numComponents: Int

    protected abstract fun forEachComponent(obj: T, action: (N) -> Unit)

    protected abstract fun component(node: ConfigurationNode): N

    protected abstract fun fromList(comps: List<N>): T

    protected abstract fun fromSingle(value: N): T

    override fun serialize(type: Type, obj: T?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            forEachComponent(obj) {
                node.appendListNode().set(it)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): T {
        return if (node.isList) {
            val list = node.childrenList()
            if (list.size != numComponents)
                throw SerializationException(node, type, "Vector must be defined as list of $numComponents components")
            fromList(list.map { component(it) })
        } else {
            fromSingle(component(node))
        }
    }
}

object Vector2Serializer : CartesianSerializer<Double, Vector2>() {
    override val numComponents get() = 2

    override fun forEachComponent(obj: Vector2, action: (Double) -> Unit) {
        action(obj.x)
        action(obj.y)
    }

    override fun component(node: ConfigurationNode) = node.force<Double>()

    override fun fromList(comps: List<Double>) = Vector2(comps[0], comps[1])

    override fun fromSingle(value: Double) = Vector2(value)
}

object Vector3Serializer : CartesianSerializer<Double, Vector3>() {
    override val numComponents get() = 3

    override fun forEachComponent(obj: Vector3, action: (Double) -> Unit) {
        action(obj.x)
        action(obj.y)
        action(obj.z)
    }

    override fun component(node: ConfigurationNode) = node.force<Double>()

    override fun fromList(comps: List<Double>) = Vector3(comps[0], comps[1], comps[2])

    override fun fromSingle(value: Double) = Vector3(value)
}

object Point2Serializer : CartesianSerializer<Int, Point2>() {
    override val numComponents get() = 2

    override fun forEachComponent(obj: Point2, action: (Int) -> Unit) {
        action(obj.x)
        action(obj.y)
    }

    override fun component(node: ConfigurationNode) = node.force<Int>()

    override fun fromList(comps: List<Int>) = Point2(comps[0], comps[1])

    override fun fromSingle(value: Int) = Point2(value)
}

object Point3Serializer : CartesianSerializer<Int, Point3>() {
    override val numComponents get() = 3

    override fun forEachComponent(obj: Point3, action: (Int) -> Unit) {
        action(obj.x)
        action(obj.y)
        action(obj.z)
    }

    override fun component(node: ConfigurationNode) = node.force<Int>()

    override fun fromList(comps: List<Int>) = Point3(comps[0], comps[1], comps[2])

    override fun fromSingle(value: Int) = Point3(value)
}
