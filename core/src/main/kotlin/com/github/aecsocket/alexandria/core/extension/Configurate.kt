package com.github.aecsocket.alexandria.core.extension

import io.leangen.geantyref.TypeToken
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.lang.reflect.Type
import kotlin.reflect.KClass

inline fun <reified T> typeToken() = object : TypeToken<T>() {}

fun <V> ConfigurationNode.force(type: TypeToken<V>) = get(type)
    ?: throw SerializationException(this, type.type, "A value is required for this field")

inline fun <reified V> ConfigurationNode.force() = force(typeToken<V>())

fun ConfigurationNode.forceList(type: Type) = if (isList) childrenList()
    else throw SerializationException(this, type, "Field must be expressed as list")

fun ConfigurationNode.forceList(type: Type, vararg args: String): List<ConfigurationNode> {
    if (isList) {
        val list = childrenList()
        if (list.size == args.size)
            return list
        throw SerializationException(this, type, "Field must be expressed as list of [${args.joinToString()}], found ${list.size}")
    }
    throw SerializationException(this, type, "Field must be expressed as list")
}

fun ConfigurationNode.forceMap(type: Type) = if (isMap) childrenMap()
    else throw SerializationException(this, type, "A map is required for this field")

fun <T : Any> TypeSerializerCollection.Builder.register(type: KClass<T>, serializer: TypeSerializer<T>) =
    register(type.java, serializer)
