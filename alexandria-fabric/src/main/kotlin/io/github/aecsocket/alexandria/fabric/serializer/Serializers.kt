package io.github.aecsocket.alexandria.fabric.serializer

import io.github.aecsocket.alexandria.extension.register
import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.alexandria.serializer.apiSerializers
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import org.spongepowered.configurate.serialize.TypeSerializerCollection

private inline fun <reified T> TypeSerializerCollection.Builder.registerRegistry(
    registry: Registry<T>,
): TypeSerializerCollection.Builder {
  registerExact(RegisteredSerializer(registry))
  return this
}

val fabricSerializers: TypeSerializerCollection =
    TypeSerializerCollection.builder()
        .registerAll(apiSerializers)
        // we don't actually register all registries here, since that will cause serialization
        // problems
        .registerRegistry(BuiltInRegistries.ITEM)
        .registerRegistry(BuiltInRegistries.PARTICLE_TYPE)
        .register(ItemTypeSerializer)
        .build()
