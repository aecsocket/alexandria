package com.github.aecsocket.alexandria.core.serializer

import com.github.aecsocket.alexandria.core.extension.force
import com.github.aecsocket.alexandria.core.extension.forceMap
import com.github.aecsocket.alexandria.core.extension.typeToken
import com.github.aecsocket.alexandria.core.physics.*
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val TYPE = "type"
private const val ROT = "rot"
private const val TL = "tl"
private const val TRANSFORM = "transform"
private const val SHAPE = "shape"
private const val MIN = "min"
private const val MAX = "max"
private const val CENTER = "center"
private const val RADIUS = "radius"

object ShapeSerializer : TypeSerializer<Shape> {
    val TYPES = mapOf(
        "box" to typeToken<Box>(),
        "sphere" to typeToken<Sphere>(),
    )

    override fun serialize(type: Type, obj: Shape?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            when (obj) {
                is Empty -> node.appendListNode()
                else -> node.set(obj)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Shape {
        return if (node.isList) {
            Empty
        } else {
            val typeName = node.node(TYPE).force<String>()
            val boundType = TYPES[typeName]
                ?: throw SerializationException(node, type, "No shape type for key '$typeName' - available: [${TYPES.keys.joinToString()}]")
            node.force(boundType)
        }
    }
}

object TransformSerializer : TypeSerializer<Transform> {
    override fun serialize(type: Type, obj: Transform?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.node(ROT).set(obj.rot)
            node.node(TL).set(obj.tl)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Transform {
        return when {
            node.isMap -> Transform(
                node.node(ROT).get { Quaternion.Identity },
                node.node(TL).get { Vector3.Zero },
            )
            else -> Transform(
                tl = node.force()
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
                Sphere(node.node(RADIUS).force()),
                Transform(tl = node.node(CENTER).get { Vector3.Zero }),
            )
            node.hasChild(MIN) && node.hasChild(MAX) -> {
                val min = node.node(MIN).force<Vector3>()
                val max = node.node(MAX).force<Vector3>()
                SimpleBody(
                    Box((max - min) / 2.0),
                    Transform(
                        rot = node.node(ROT).get { Quaternion.Identity },
                        tl = min.midpoint(max)
                    ),
                )
            }
            else -> throw SerializationException(node, type, "Invalid body format")
        }
    }
}
