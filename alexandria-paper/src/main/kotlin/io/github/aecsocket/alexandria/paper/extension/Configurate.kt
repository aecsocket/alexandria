package io.github.aecsocket.alexandria.paper.extension

import io.github.aecsocket.alexandria.extension.alexandriaApiSerializers
import io.github.aecsocket.alexandria.extension.register
import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.alexandria.paper.seralizer.*
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaPaperSerializers: TypeSerializerCollection = TypeSerializerCollection.builder()
    .registerAll(alexandriaApiSerializers)
    .registerExact(materialSerializer)
    .registerExact(entityTypeSerializer)
    .registerExact(statisticSerializer)
    .registerExact(ParticleSerializer)
    .register(RawParticleSerializer)
    .build()
