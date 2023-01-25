package io.gitlab.aecsocket.alexandria.paper.serializer

import io.gitlab.aecsocket.alexandria.core.extension.register
import io.gitlab.aecsocket.alexandria.core.extension.registerExact
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection

object PaperSerializers {
    val All: TypeSerializerCollection = TypeSerializerCollection.builder()
        .registerExact(MaterialSerializer)
        .registerExact(EntityTypeSerializer)
        .registerExact(StatisticSerializer)
        .registerExact(ParticleSerializer)
        .registerExact(ParticleEffectSerializer)
        .registerExact(SoundEffectSerializer)
        .registerExact(ColorSerializer)
        .registerExact(DustOptionsSerializer)
        .registerExact(TitleTimesSerializer)
        .registerExact(TextSlotSerializer)
        .registerExact(PlayerInventorySlotSerializer)
        .register(BlockDataSerializer)
        .registerAll(ConfigurateComponentSerializer.configurate().serializers())
        .build()
}