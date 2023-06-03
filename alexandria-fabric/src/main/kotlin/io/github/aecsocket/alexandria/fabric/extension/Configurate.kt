package io.github.aecsocket.alexandria.fabric.extension

import io.github.aecsocket.alexandria.extension.apiSerializers
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val FABRIC_SERIALIZERS: TypeSerializerCollection = TypeSerializerCollection.builder()
    .registerAll(apiSerializers)
    .build()
