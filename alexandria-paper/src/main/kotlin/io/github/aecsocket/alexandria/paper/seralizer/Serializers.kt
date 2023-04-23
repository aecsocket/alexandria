package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.extension.registerExact
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaPaperSerializers: TypeSerializerCollection = TypeSerializerCollection.builder()
    .registerExact(materialSerializer)
    .registerExact(entityTypeSerializer)
    .registerExact(statisticSerializer)
    .registerExact(ParticleSerializer)
    .build()
