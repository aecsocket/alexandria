package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.klam.configurate.klamSerializers
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaPaperSerializers: TypeSerializerCollection = TypeSerializerCollection.builder()
    .registerAll(klamSerializers)
    .registerExact(materialSerializer)
    .registerExact(entityTypeSerializer)
    .registerExact(statisticSerializer)
    .registerExact(ParticleSerializer)
    .build()
