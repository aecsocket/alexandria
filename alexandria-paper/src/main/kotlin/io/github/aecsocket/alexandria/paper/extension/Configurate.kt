package io.github.aecsocket.alexandria.paper.extension

import io.github.aecsocket.alexandria.extension.alexandriaApiSerializers
import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.alexandria.paper.seralizer.ParticleSerializer
import io.github.aecsocket.alexandria.paper.seralizer.entityTypeSerializer
import io.github.aecsocket.alexandria.paper.seralizer.materialSerializer
import io.github.aecsocket.alexandria.paper.seralizer.statisticSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaPaperSerializers: TypeSerializerCollection = TypeSerializerCollection.builder()
    .registerAll(alexandriaApiSerializers)
    .registerExact(materialSerializer)
    .registerExact(entityTypeSerializer)
    .registerExact(statisticSerializer)
    .registerExact(ParticleSerializer)
    .build()
