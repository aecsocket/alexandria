package com.gitlab.aecsocket.alexandria.paper.serializer

import com.gitlab.aecsocket.alexandria.core.extension.register
import com.gitlab.aecsocket.alexandria.core.extension.registerExact
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection

object PaperSerializers {
    val ALL: TypeSerializerCollection = TypeSerializerCollection.builder()
        .registerExact(MaterialSerializer)
        .registerExact(EntityTypeSerializer)
        .registerExact(StatisticSerializer)
        .registerExact(ParticleSerializer)
        .registerExact(ParticleEffectSerializer)
        .registerExact(ColorSerializer)
        .registerExact(DustOptionsSerializer)
        .registerExact(TitleTimesSerializer)
        .registerExact(TextSlotSerializer)
        .register(BlockDataSerializer)
        .registerAll(ConfigurateComponentSerializer.configurate().serializers())
        .build()
}
