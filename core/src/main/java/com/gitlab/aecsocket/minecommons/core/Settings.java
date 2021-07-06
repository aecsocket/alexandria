package com.gitlab.aecsocket.minecommons.core;

import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.CheckedFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * A configuration storage object using Configurate {@link ConfigurationNode}s.
 * <p>
 * Implements caching.
 */
public final class Settings {
    /**
     * A path to a child node, comprised of an Object varargs.
     */
    public record Path(Object... path) {}

    /**
     * A functional interface which maps a {@link ConfigurationNode} to a {@link T},
     * allowing it to throw a {@link SerializationException}.
     * @param <T> The resulting type.
     */
    @FunctionalInterface
    public interface NodeFunction<T> extends CheckedFunction<ConfigurationNode, T, SerializationException> {}

    private final ConfigurationNode root;
    private final Map<Path, Object> cache = new HashMap<>();

    public Settings(ConfigurationNode root) {
        this.root = root;
    }

    public Settings() {
        this(BasicConfigurationNode.root());
    }

    public ConfigurationNode root() { return root; }
    public Map<Path, Object> cache() { return cache; }

    /**
     * Gets a setting value, either from cache if it exists, or from a {@link ConfigurationNode}.
     * @param mapper A function to map the {@link ConfigurationNode} to a {@link T}, used for caching.
     * @param path The path to the value.
     * @param <T> The value type.
     * @return The value.
     */
    public <T> T get(NodeFunction<T> mapper, Path path) throws SerializationException {
        if (cache.containsKey(path)) {
            @SuppressWarnings("unchecked")
            T value = (T) cache.get(path);
            return value;
        }

        T value = mapper.apply(root.node(path.path));
        cache.put(path, value);
        return value;
    }

    /**
     * Gets a setting value, either from cache if it exists, or from a {@link ConfigurationNode}.
     * @param mapper A function to map the {@link ConfigurationNode} to a {@link T}, used for caching.
     * @param path The path to the value.
     * @param <T> The value type.
     * @return The value.
     */
    public <T> T get(NodeFunction<T> mapper, Object... path) throws SerializationException {
        return get(mapper, new Path(path));
    }

    /**
     * Forces a cache key to a specific value.
     * @param value The value to set to.
     * @param path The path to the value.
     * @return The previous value.
     */
    public Object set(Object value, Path path) {
        return cache.put(path, value);
    }

    /**
     * Forces a cache key to a specific value.
     * @param value The value to set to.
     * @param path The path to the value.
     * @return The previous value.
     */
    public Object set(Object value, Object... path) {
        return set(value, new Path(path));
    }

    /**
     * Loads a settings instance from a Configurate loader.
     * @param loader The loader.
     * @return The settings instance.
     * @throws ConfigurateException If there was an exception in {@link ConfigurationLoader#load()}.
     */
    public static Settings loadFrom(ConfigurationLoader<?> loader) throws ConfigurateException {
        return new Settings(loader.load());
    }
}
