package com.gitlab.aecsocket.minecommons.core;

import java.util.*;

/**
 * Allows quickly adding values to different types of
 * collections using a builder pattern.
 */
public final class CollectionBuilder {
    private CollectionBuilder() {}

    /**
     * Creates a collection builder which initializes {@link Collection}s.
     * @param value The initial collection.
     * @param <E> The element type.
     * @return The collection builder.
     */
    public static <E> OfCollection<E> collection(Collection<E> value) { return new OfCollection<>(value); }

    /**
     * Creates a collection builder which initializes {@link Set}s.
     * @param value The initial collection.
     * @param <E> The element type.
     * @return The collection builder.
     */
    public static <E> OfSet<E> set(Set<E> value) { return new OfSet<>(value); }

    /**
     * Creates a collection builder which initializes {@link List}s.
     * @param value The initial collection.
     * @param <E> The element type.
     * @return The collection builder.
     */
    public static <E> OfList<E> list(List<E> value) { return new OfList<>(value); }

    /**
     * Creates a collection builder which initializes {@link Map}s.
     * @param value The initial collection.
     * @param <K> The key type.
     * @param <V> The value type.
     * @return The collection builder.
     */
    public static <K, V> OfMap<K, V> map(Map<K, V> value) { return new OfMap<>(value); }

    /**
     * @see #collection(Collection)
     * @param <E> The element type.
     */
    public static final class OfCollection<E> {
        private final Collection<E> value;

        private OfCollection(Collection<E> value) {
            Validation.notNull(value, "value");
            this.value = value;
        }

        public OfCollection<E> add(E object) { value.add(object); return this; }
        public OfCollection<E> add(Collection<E> collection) { value.addAll(collection); return this; }
        @SafeVarargs public final OfCollection<E> add(E... collection) { value.addAll(Arrays.asList(collection)); return this; }

        /**
         * Gets the internal collection used in this builder (mutable).
         * @return The collection.
         */
        public Collection<E> get() { return value; }

        /**
         * Gets an immutable version of the collection being built.
         * @return The collection.
         */
        public Collection<E> build() { return Collections.unmodifiableCollection(value); }
    }

    /**
     * @see #set(Set)
     * @param <E> The element type.
     */
    public static final class OfSet<E> {
        private final Set<E> value;

        private OfSet(Set<E> value) {
            Validation.notNull(value, "value");
            this.value = value;
        }

        public OfSet<E> add(E object) { value.add(object); return this; }
        public OfSet<E> add(Collection<E> collection) { value.addAll(collection); return this; }
        public OfSet<E> add(E... collection) { value.addAll(Arrays.asList(collection)); return this; }

        /**
         * Gets the internal collection used in this builder (mutable).
         * @return The collection.
         */
        public Set<E> get() { return value; }

        /**
         * Gets an immutable version of the collection being built.
         * @return The collection.
         */
        public Set<E> build() { return Collections.unmodifiableSet(value); }
    }

    /**
     * @see #list(List)
     * @param <E> The element type.
     */
    public static final class OfList<E> {
        private final List<E> value;

        private OfList(List<E> value) {
            Validation.notNull(value, "value");
            this.value = value;
        }

        public OfList<E> add(E object) { value.add(object); return this; }
        public OfList<E> add(Collection<E> collection) { value.addAll(collection); return this; }
        public OfList<E> add(E... collection) { value.addAll(Arrays.asList(collection)); return this; }

        /**
         * Gets the internal collection used in this builder (mutable).
         * @return The collection.
         */
        public List<E> get() { return value; }

        /**
         * Gets an immutable version of the collection being built.
         * @return The collection.
         */
        public List<E> build() { return Collections.unmodifiableList(value); }
    }

    /**
     * @see #map(Map)
     * @param <K> The key type.
     * @param <V> The value type.
     */
    public static final class OfMap<K, V> {
        private final Map<K, V> value;

        private OfMap(Map<K, V> value) {
            Validation.notNull(value, "value");
            this.value = value;
        }

        public OfMap<K, V> put(K k, V v) { value.put(k, v); return this; }
        public OfMap<K, V> put(Map<K, V> collection) { value.putAll(collection); return this; }

        /**
         * Gets the internal collection used in this builder (mutable).
         * @return The collection.
         */
        public Map<K, V> get() { return value; }

        /**
         * Gets an immutable version of the collection being built.
         * @return The collection.
         */
        public Map<K, V> build() { return Collections.unmodifiableMap(value); }
    }
}
