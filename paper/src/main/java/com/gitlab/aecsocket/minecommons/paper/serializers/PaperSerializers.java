package com.gitlab.aecsocket.minecommons.paper.serializers;

import com.gitlab.aecsocket.minecommons.paper.ItemSlot;
import com.gitlab.aecsocket.minecommons.paper.display.Particles;
import com.gitlab.aecsocket.minecommons.paper.display.PreciseSound;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
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
            .register(ItemStack.class, ItemStackSerializer.INSTANCE)
            .register(BlockData.class, BlockDataSerializer.INSTANCE)
            .register(World.class, WorldSerializer.INSTANCE)
            .register(Location.class, LocationSerializer.INSTANCE)
            .register(Vector.class, VectorSerializer.INSTANCE)
            .register(ItemSlot.class, ItemSlotSerializer.INSTANCE)
            .register(PreciseSound.class, PreciseSoundSerializer.INSTANCE)
            .register(Particles.class, ParticlesSerializer.INSTANCE)
            .registerExact(NamespacedKey.class, NamespacedKeySerializer.INSTANCE)
            .register(PotionEffectType.class, PotionEffectTypeSerializer.INSTANCE)
            .register(PotionEffect.class, PotionEffectSerializer.INSTANCE)
            .build();
}
