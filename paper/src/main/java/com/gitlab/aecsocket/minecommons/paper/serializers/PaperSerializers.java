package com.gitlab.aecsocket.minecommons.paper.serializers;

import com.gitlab.aecsocket.minecommons.core.serializers.*;
import org.bukkit.Location;
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
            .registerAll(Serializers.SERIALIZERS)
            .register(ItemStack.class, ItemStackSerializer.INSTANCE)
            .register(Location.class, LocationSerializer.INSTANCE)
            .build();
}
