package com.gitlab.aecsocket.minecommons.paper.persistence;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * A PersistentDataType for storing {@link UUID} arrays.
 */
public class UUIDType implements PersistentDataType<byte[], UUID> {
    /** A singleton instance of this data type. */
    public static final UUIDType INSTANCE = new UUIDType();

    @Override public @NotNull Class<byte[]> getPrimitiveType() { return byte[].class; }
    @Override public @NotNull Class<UUID> getComplexType() { return UUID.class; }

    @Override
    public byte @NotNull [] toPrimitive(UUID obj, PersistentDataAdapterContext ctx) {
        ByteBuffer bytes = ByteBuffer.wrap(new byte[16]);
        bytes.putLong(obj.getMostSignificantBits());
        bytes.putLong(obj.getLeastSignificantBits());
        return bytes.array();
    }

    @Override
    public @NotNull UUID fromPrimitive(byte[] obj, PersistentDataAdapterContext ctx) {
        ByteBuffer bytes = ByteBuffer.wrap(obj);
        long firstLong = bytes.getLong();
        long secondLong = bytes.getLong();
        return new UUID(firstLong, secondLong);
    }
}
