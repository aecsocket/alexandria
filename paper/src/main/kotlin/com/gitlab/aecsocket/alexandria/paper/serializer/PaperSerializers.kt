package com.gitlab.aecsocket.alexandria.paper.serializer

import com.gitlab.aecsocket.glossa.configurate.I18NSerializers
import com.gitlab.aecsocket.alexandria.core.effect.ParticleEffect
import com.gitlab.aecsocket.alexandria.core.extension.register
import com.gitlab.aecsocket.alexandria.core.extension.registerExact
import com.gitlab.aecsocket.alexandria.paper.input.InputMapper
import org.bukkit.Color
import org.bukkit.Particle.DustOptions
import org.bukkit.block.data.BlockData
import org.spongepowered.configurate.serialize.TypeSerializerCollection

object PaperSerializers {
    val ALL: TypeSerializerCollection = TypeSerializerCollection.builder()
        .registerExact(InputMapper::class, InputMapperSerializer)
        .registerExact(ParticleEffect::class, ParticleEffectSerializer)
        .registerExact(Color::class, ColorSerializer)
        .register(BlockData::class, BlockDataSerializer)
        .registerExact(DustOptions::class, DustOptionsSerializer)
        .registerAll(I18NSerializers.ALL)
        .build()
}
