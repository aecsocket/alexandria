package io.github.aecsocket.alexandria.extension

import io.github.aecsocket.glossa.configurate.LocaleSerializer
import io.leangen.geantyref.TypeToken
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.lang.reflect.Type
import kotlin.reflect.KClass

inline fun <reified T> typeToken() = object : TypeToken<T>() {}

fun ConfigurationNode.force(type: Type) = get(type)
    ?: throw SerializationException(this, type, "A value is required for this field")

fun <V : Any> ConfigurationNode.force(type: KClass<V>) = get(type.java)
    ?: throw SerializationException(this, type.java, "A value is required for this field")

fun <V : Any> ConfigurationNode.force(type: TypeToken<V>) = get(type)
    ?: throw SerializationException(this, type.type, "A value is required for this field")

inline fun <reified V : Any> ConfigurationNode.force() = force(typeToken<V>())

inline fun <reified T> TypeSerializerCollection.Builder.register(serializer: TypeSerializer<T>) =
    register(T::class.java, serializer)

inline fun <reified T> TypeSerializerCollection.Builder.registerExact(serializer: TypeSerializer<T>) =
    registerExact(T::class.java, serializer)
