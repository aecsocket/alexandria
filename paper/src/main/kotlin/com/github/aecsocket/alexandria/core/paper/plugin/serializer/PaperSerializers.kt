package com.github.aecsocket.alexandria.core.paper.plugin.serializer

import com.github.aecsocket.glossa.configurate.I18NSerializers
import org.spongepowered.configurate.serialize.TypeSerializerCollection

object PaperSerializers {
    val ALL: TypeSerializerCollection = TypeSerializerCollection.builder()
        .registerAll(I18NSerializers.ALL)
        .build()
}
