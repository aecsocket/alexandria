package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.*
import com.gitlab.aecsocket.alexandria.core.physics.*
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val TYPE = "type"
private const val ROTATION = "rotation"
private const val TRANSLATION = "translation"
private const val TRANSFORM = "transform"
private const val SHAPE = "shape"
private const val MIN = "min"
private const val MAX = "max"
private const val CENTER = "center"
private const val RADIUS = "radius"
private const val NORMAL = "normal"

object ShapeSerializer : TypeSerializer<Shape> {
    val TYPES = mapOf(
        "box" to typeToken<BoxShape>(),
        "sphere" to typeToken<SphereShape>(),
        "plane" to typeToken<PlaneShape>(),
    )

    override fun serialize(type: Type, obj: Shape?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            when (obj) {
                is EmptyShape -> node.appendListNode()
                else -> node.set(obj)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Shape {
        return if (node.isList) EmptyShape
        else {
            val typeName = node.node(TYPE).force<String>()
            val boundType = TYPES[typeName]
                ?: throw SerializationException(node, type, "No shape type for key '$typeName' - available: [${TYPES.keys.joinToString()}]")
            node.force(boundType)
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
            ).quaternion(list[0].force())
        } else Quaternion(
            list[0].force(),
            list[1].force(),
            list[2].force(),
            list[3].force(),
        )
    }
}

object TransformSerializer : TypeSerializer<Transform> {
    override fun serialize(type: Type, obj: Transform?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.node(ROTATION).set(obj.rotation)
            node.node(TRANSLATION).set(obj.translation)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Transform {
        return when {
            node.isMap -> Transform(
                node.node(TRANSLATION).get { Vector3.Zero },
                node.node(ROTATION).get { Quaternion.Identity },
            )
            else -> Transform(
                translation = node.force()
            )
        }
    }
}

object SimpleBodySerializer : TypeSerializer<SimpleBody> {
    override fun serialize(type: Type, obj: SimpleBody?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.node(TRANSFORM).set(obj.transform)
            node.node(SHAPE).set(obj.shape)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): SimpleBody {
        node.forceMap(type)
        return when {
            node.hasChild(SHAPE) -> SimpleBody(
                node.node(SHAPE).force(),
                node.node(TRANSFORM).get { Transform.Identity },
            )
            node.hasChild(RADIUS) -> SimpleBody(
                SphereShape(node.node(RADIUS).force()),
                Transform(node.node(CENTER).get { Vector3.Zero }),
            )
            node.hasChild(MIN) && node.hasChild(MAX) -> {
                val min = node.node(MIN).force<Vector3>()
                val max = node.node(MAX).force<Vector3>()
                SimpleBody(
                    BoxShape((max - min) / 2.0),
                    Transform(
                        min.midpoint(max),
                        node.node(ROTATION).get { Quaternion.Identity },
                    ),
                )
            }
            node.hasChild(NORMAL) -> {
                val normal = node.node(NORMAL).force<Vector3>()
                val center = node.node(CENTER).force<Vector3>()
                SimpleBody(
                    PlaneShape(normal),
                    Transform(translation = center)
                )
            }
            else -> throw SerializationException(node, type, "Invalid body format")
        }
    }
}
