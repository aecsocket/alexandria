package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.*
import com.gitlab.aecsocket.alexandria.core.physics.*
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type


private const val RADIUS = "radius"
private const val HALF_EXTENT = "half_extent"
private const val NORMAL = "normal"

object EmptyShapeSerializer : TypeSerializer<EmptyShape> {
    override fun serialize(type: Type, obj: EmptyShape?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else node.appendListNode()
    }

    override fun deserialize(type: Type, node: ConfigurationNode) = EmptyShape
}

object ShapeSerializer : TypeSerializer<Shape> {
    override fun serialize(type: Type, obj: Shape?, node: ConfigurationNode) {
        node.set(obj)
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Shape {
        return if (node.isList) {
            val list = node.childrenList()
            if (list.isEmpty()) EmptyShape else node.force<CompoundShape>()
        } else when {
            node.hasChild(RADIUS) -> SphereShape(node.node(RADIUS).force())
            node.hasChild(HALF_EXTENT) -> BoxShape(node.node(HALF_EXTENT).force())
            node.hasChild(NORMAL) -> PlaneShape(node.node(NORMAL).force())
            else -> throw SerializationException(node, type, "Invalid shape format")
        }
    }
}

object QuaternionSerializer : TypeSerializer<Quaternion> {
    override fun serialize(type: Type, obj: Quaternion?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.appendListNode().set(obj.x)
            node.appendListNode().set(obj.y)
            node.appendListNode().set(obj.z)
            node.appendListNode().set(obj.w)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Quaternion {
        val list = node.forceList(type, "order", "x", "y", "z")
        return if (list[0].raw() is String) {
            Euler3(
                list[1].force(),
                list[2].force(),
                list[3].force(),
            ).radians.quaternion(list[0].force())
        } else Quaternion(
            list[0].force(),
            list[1].force(),
            list[2].force(),
            list[3].force(),
        )
    }
}
