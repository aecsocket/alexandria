package com.github.aecsocket.alexandria.core.serializer

import com.github.aecsocket.alexandria.core.bound.*
import com.github.aecsocket.alexandria.core.extension.force
import com.github.aecsocket.alexandria.core.extension.typeToken
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val TYPE = "type"

object BoundSerializer : TypeSerializer<Bound> {
    val TYPES = mapOf(
        "box" to typeToken<Box>(),
        "sphere" to typeToken<Sphere>(),
    )

    override fun serialize(type: Type, obj: Bound?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            when (obj) {
                is Empty -> node.appendListNode()
                is Compound -> obj.bounds.forEach {
                    node.appendListNode().set(it)
                }
                else -> node.set(obj)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Bound {
        return if (node.isList) {
            val list = node.childrenList()
            if (list.isEmpty()) Empty
            else Compound(list.map { it.force() })
        } else {
            val typeName = node.node(TYPE).force<String>()
            val boundType = TYPES[typeName]
                ?: throw SerializationException(node, type, "No bound type for key '$typeName' - available: [${TYPES.keys.joinToString()}]")
            node.force(boundType)
        }
    }
}
