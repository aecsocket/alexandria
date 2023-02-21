package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.core.extension.registerExact
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaPaperSerializers = TypeSerializerCollection.builder()
    .registerExact(materialSerializer)
    .registerExact(entityTypeSerializer)
    .registerExact(statisticSerializer)
    .registerExact(ParticleSerializer)
    .build()
