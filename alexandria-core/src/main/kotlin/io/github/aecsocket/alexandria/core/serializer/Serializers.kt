package io.github.aecsocket.alexandria.core.serializer

import io.github.aecsocket.alexandria.core.extension.registerExact
import io.github.aecsocket.glossa.configurate.LocaleSerializer
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaCoreSerializers = TypeSerializerCollection.builder()
    .registerAll(ConfigurateComponentSerializer.configurate().serializers())
    .registerExact(LocaleSerializer)
    .registerExact(Vec3fSerializer)
    .registerExact(Vec3dSerializer)
    .registerExact(QuatSerializer)
    .build()
