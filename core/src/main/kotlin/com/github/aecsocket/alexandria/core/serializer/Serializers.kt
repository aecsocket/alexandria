package com.github.aecsocket.alexandria.core.serializer

import com.github.aecsocket.alexandria.core.extension.register
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.util.Locale

object Serializers {
    val ALL: TypeSerializerCollection = TypeSerializerCollection.builder()
        .register(Locale::class, LocaleSerializer)
        .build()
}
