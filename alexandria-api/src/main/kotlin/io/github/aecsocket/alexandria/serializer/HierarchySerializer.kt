package io.github.aecsocket.alexandria.serializer

import io.github.aecsocket.alexandria.extension.force
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

const val TYPE_KEY = "type"

class HierarchySerializer<T : Any>(
    private val baseType: Class<out T>,
    private val subTypes: Map<String, Class<out T>>,
    private val typeKey: String = TYPE_KEY,
) : TypeSerializer<T> {
    private val subTypeToKey = subTypes.map { (a, b) -> b to a }.associate { it }

    init {
        subTypes.forEach { (_, subType) ->
            if (!baseType.isAssignableFrom(subType))
                throw IllegalArgumentException("$subType is not a subtype of $baseType")
        }
    }

    override fun serialize(type: Type, obj: T?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            val subType = obj::class.java
            val key = subTypeToKey[subType]
                ?: throw SerializationException(node, type, "$subType does not have a registered key")
            node.set(obj)
            node.node(typeKey).set(key)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): T {
        val typeName = node.node(typeKey).force<String>()
        val targetType = subTypes[typeName]
            ?: throw SerializationException(node, type, "Invalid type name '$typeName'")
        // SAFETY: we've checked at construction time that all `targetType`s are subtypes of `T`
        @Suppress("UNCHECKED_CAST")
        return node.force(targetType) as T
    }

    interface Model<T : Any> {
        fun subType(key: String, subType: Class<out T>)
    }
}

inline fun <T : Any, reified U : T> HierarchySerializer.Model<T>.subType(key: String) =
    subType(key, U::class.java)

inline fun <reified T : Any> HierarchySerializer(
    subTypes: Map<String, Class<out T>>,
    typeKey: String = TYPE_KEY,
): HierarchySerializer<T> {
    return HierarchySerializer(
        baseType = T::class.java,
        subTypes = subTypes,
        typeKey = typeKey,
    )
}

inline fun <reified T : Any> HierarchySerializer(
    typeKey: String = TYPE_KEY,
    block: HierarchySerializer.Model<T>.() -> Unit,
): HierarchySerializer<T> {
    val subTypes = HashMap<String, Class<out T>>()
    val baseType = T::class.java
    block(object : HierarchySerializer.Model<T> {
        override fun subType(key: String, subType: Class<out T>) {
            if (subTypes.contains(key))
                throw IllegalArgumentException("Subtype already exists for key $key")
            subTypes[key] = subType
        }
    })
    return HierarchySerializer(
        baseType = baseType,
        subTypes = subTypes,
        typeKey = typeKey,
    )
}
