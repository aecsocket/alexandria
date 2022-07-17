package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.*
import com.gitlab.aecsocket.alexandria.core.physics.Quaternion
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

class QuaternionSerializer(
    var format: Format = Format.EULER,
    var order: EulerOrder = EulerOrder.XYZ,
) : TypeSerializer<Quaternion> {
    enum class Format {
        EULER, QUATERNION
    }

    override fun serialize(type: Type, obj: Quaternion?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else when (format) {
            Format.EULER -> {
                val euler = obj.euler(order).degrees
                node.appendListNode().set(euler.pitch)
                node.appendListNode().set(euler.yaw)
                node.appendListNode().set(euler.roll)
            }
            Format.QUATERNION -> {
                node.appendListNode().set(obj.x)
                node.appendListNode().set(obj.y)
                node.appendListNode().set(obj.z)
                node.appendListNode().set(obj.w)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Quaternion {
        val list = node.forceList(type)
        return when (list.size) {
            3 -> Euler3(
                list[0].force(), list[1].force(), list[2].force()
            ).radians.quaternion(order)
            4 -> Quaternion(
                list[0].force(), list[1].force(), list[2].force(), list[3].force()
            )
            else -> throw SerializationException(node, type,
                "Rotation must be represented as: list of 3 Euler angles; list of 4 quaternion components")
        }
    }
}
