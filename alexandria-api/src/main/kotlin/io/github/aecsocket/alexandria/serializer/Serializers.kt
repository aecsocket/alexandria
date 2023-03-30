package io.github.aecsocket.alexandria.serializer

import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.glossa.configurate.LocaleSerializer
import io.github.aecsocket.klam.configurate.klamSerializers
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaSerializers = TypeSerializerCollection.builder()
    .registerAll(ConfigurateComponentSerializer.configurate().serializers())
    .registerAll(klamSerializers)
    .registerExact(LocaleSerializer)
    .build()
