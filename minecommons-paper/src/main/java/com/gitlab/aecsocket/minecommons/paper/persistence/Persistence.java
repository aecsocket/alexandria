package com.gitlab.aecsocket.minecommons.paper.persistence;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

/**
 * General utilities for using {@link PersistentDataContainer}s.
 */
public final class Persistence {
    private Persistence() {}

    /**
     * Sets an enum value on a data container.
     * @param data The container.
     * @param key The key to set under.
     * @param value The enum value.
     * @see #getEnum(PersistentDataContainer, NamespacedKey, Class)
     */
    public static void setEnum(PersistentDataContainer data, NamespacedKey key, Enum<?> value) {
        data.set(key, PersistentDataType.INTEGER, value.ordinal());
    }

    /**
     * Gets an enum value on a data container.
     * @param data The container.
     * @param key The key to get under.
     * @param type The enum type.
     * @param <E> The enum type.
     * @return An Optional of the result.
     */
    public static <E extends Enum<E>> Optional<E> getEnum(PersistentDataContainer data, NamespacedKey key, Class<E> type) {
        if (data.has(key, PersistentDataType.INTEGER))
            //noinspection ConstantConditions
            return Optional.of(type.getEnumConstants()[data.get(key, PersistentDataType.INTEGER)]);
        else
            return Optional.empty();
    }
}
