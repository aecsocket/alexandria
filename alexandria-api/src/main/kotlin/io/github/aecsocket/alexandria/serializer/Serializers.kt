package io.github.aecsocket.alexandria.serializer

import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.glossa.configurate.LocaleSerializer
import io.github.aecsocket.klam.configurate.klamSerializers
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val apiSerializers: TypeSerializerCollection = TypeSerializerCollection.defaults().childBuilder()
    .registerAll(klamSerializers)
    .registerAll(ConfigurateComponentSerializer.configurate().serializers())
    .registerExact(LocaleSerializer)
    .build()
