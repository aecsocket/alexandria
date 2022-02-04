package com.github.aecsocket.minecommons.paper.serializers;

import com.github.aecsocket.minecommons.paper.ItemSlot;
import com.github.aecsocket.minecommons.paper.effect.PaperParticleEffect;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * Utilities for serializers.
 */
public final class PaperSerializers {
    private PaperSerializers() {}

    /**
     * A {@link TypeSerializerCollection} with the default serializers defined in this package.
     */
    public static final TypeSerializerCollection SERIALIZERS = TypeSerializerCollection.builder()
        .registerExact(BlockData.class, BlockDataSerializer.INSTANCE)
        .registerExact(World.class, WorldSerializer.INSTANCE)
        .registerExact(Location.class, LocationSerializer.INSTANCE)
        .registerExact(Vector.class, VectorSerializer.INSTANCE)
        .registerExact(ItemSlot.class, ItemSlotSerializer.INSTANCE)
        .registerExact(PaperParticleEffect.class, ParticleEffectSerializer.INSTANCE)
        .registerExact(NamespacedKey.class, NamespacedKeySerializer.INSTANCE)
        .registerExact(PotionEffectType.class, PotionEffectTypeSerializer.INSTANCE)
        .registerExact(PotionEffect.class, PotionEffectSerializer.INSTANCE)
        .register(PaperParticleEffect.class, ParticleEffectSerializer.INSTANCE)
        .build();
}
