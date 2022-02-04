package com.github.aecsocket.minecommons.paper.serializers.protocol;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * Utilities for ProtocolLib serializers.
 */
public final class ProtocolSerializers {
    private ProtocolSerializers() {}

    /**
     * A {@link TypeSerializerCollection} with the default serializers defined in this package.
     */
    public static final TypeSerializerCollection SERIALIZERS = TypeSerializerCollection.builder()
        .register(WrappedSignedProperty.class, SignedPropertySerializer.INSTANCE)
        .register(WrappedGameProfile.class, GameProfileSerializer.INSTANCE)
        .build();
}
