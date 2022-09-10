package com.gitlab.aecsocket.alexandria.paper.serializer

import com.gitlab.aecsocket.alexandria.core.effect.ParticleEffect
import com.gitlab.aecsocket.alexandria.core.extension.register
import com.gitlab.aecsocket.alexandria.core.extension.registerExact
import com.gitlab.aecsocket.alexandria.paper.TextSlot
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer
import net.kyori.adventure.title.Title
import org.bukkit.Color
import org.bukkit.Particle.DustOptions
import org.bukkit.block.data.BlockData
import org.spongepowered.configurate.serialize.TypeSerializerCollection

object PaperSerializers {
    val ALL: TypeSerializerCollection = TypeSerializerCollection.builder()
        .registerExact(ParticleEffect::class, ParticleEffectSerializer)
        .registerExact(Color::class, ColorSerializer)
        .register(BlockData::class, BlockDataSerializer)
        .registerExact(DustOptions::class, DustOptionsSerializer)
        .registerExact(Title.Times::class, TitleTimesSerializer)
        .registerExact(TextSlot::class, TextSlotSerializer)
        .registerAll(ConfigurateComponentSerializer.configurate().serializers())
        .build()
}
