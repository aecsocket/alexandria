package io.github.aecsocket.alexandria.fabric.extension

import io.github.aecsocket.alexandria.extension.alexandriaApiSerializers
import org.spongepowered.configurate.serialize.TypeSerializerCollection

val alexandriaFabricSerializers: TypeSerializerCollection = TypeSerializerCollection.builder()
    .registerAll(alexandriaApiSerializers)
    .build()
