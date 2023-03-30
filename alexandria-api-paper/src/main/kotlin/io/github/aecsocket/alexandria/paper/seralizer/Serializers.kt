package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.alexandria.serializer.alexandriaSerializers
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaPaperSerializers = TypeSerializerCollection.builder()
    .registerAll(alexandriaSerializers)
    .registerExact(materialSerializer)
    .registerExact(entityTypeSerializer)
    .registerExact(statisticSerializer)
    .registerExact(ParticleSerializer)
    .build()
