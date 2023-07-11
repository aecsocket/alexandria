package io.github.aecsocket.alexandria.extension

import io.leangen.geantyref.TypeToken
import java.lang.reflect.Type
import kotlin.reflect.KClass
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection

inline fun <reified T> typeToken() = object : TypeToken<T>() {}

fun ConfigurationNode.force(type: Type): Any =
    get(type) ?: throw SerializationException(this, type, "A value is required for this field")

fun <V : Any> ConfigurationNode.force(type: Class<V>): V =
    get(type) ?: throw SerializationException(this, type, "A value is required for this field")

fun <V : Any> ConfigurationNode.force(type: KClass<V>): V =
    get(type.java)
        ?: throw SerializationException(this, type.java, "A value is required for this field")

fun <V : Any> ConfigurationNode.force(type: TypeToken<V>): V =
    get(type) ?: throw SerializationException(this, type.type, "A value is required for this field")

inline fun <reified V : Any> ConfigurationNode.force() = force(typeToken<V>())

inline fun <reified T> TypeSerializerCollection.Builder.register(
    serializer: TypeSerializer<T>,
): TypeSerializerCollection.Builder = register(T::class.java, serializer)

inline fun <reified T> TypeSerializerCollection.Builder.registerExact(
    serializer: TypeSerializer<T>,
): TypeSerializerCollection.Builder = registerExact(T::class.java, serializer)
