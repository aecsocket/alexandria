package com.gitlab.aecsocket.minecommons.paper.serializers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
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
            .register(World.class, WorldSerializer.INSTANCE)
            .register(Location.class, LocationSerializer.INSTANCE)
            .build();
}
