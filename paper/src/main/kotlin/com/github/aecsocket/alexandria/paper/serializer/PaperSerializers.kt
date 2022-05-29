package com.github.aecsocket.alexandria.paper.serializer

import com.github.aecsocket.alexandria.core.effect.ParticleEffect
import com.github.aecsocket.alexandria.core.extension.register
import com.github.aecsocket.glossa.configurate.I18NSerializers
import org.bukkit.Color
import org.bukkit.Particle.DustOptions
import org.bukkit.block.data.BlockData
import org.spongepowered.configurate.serialize.TypeSerializerCollection

object PaperSerializers {
    val ALL: TypeSerializerCollection = TypeSerializerCollection.builder()
        .register(ParticleEffect::class, ParticleEffectSerializer)
        .register(Color::class, ColorSerializer)
        .register(BlockData::class, BlockDataSerializer)
        .register(DustOptions::class, DustOptionsSerializer)
        .registerAll(I18NSerializers.ALL)
        .build()
}
