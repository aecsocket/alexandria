package com.github.aecsocket.minecommons.core;

import java.util.*;

/**
 * Utilities for manipulating collections, such as builders.
 */
public final class Colls {
    private Colls() {}

    /**
     * Gets the number of total elements in an array of collections.
     * @param cols The collections.
     * @return The total number of elements.
     */
    public static int size(Collection<?>... cols) {
        int size = 0;
        for (var col : cols) {
            size += col.size();
        }
        return size;
    }

    /**
     * Joins multiple collections into the target collection.
     * @param target The target collection which will be mutated.
     * @param cols The collections to get values from.
     * @param <E> The element type.
     * @param <C> The target collection type.
     * @return The passed, and mutated, target collection.
     */
    @SafeVarargs
    public static <E, C extends Collection<E>> C join(C target, Collection<? extends E>... cols) {
        for (var col : cols) {
            target.addAll(col);
        }
        return target;
    }

    /**
     * Joins multiple collections into a list.
     * @param cols The collections to get values from.
     * @param <E> The element type.
     * @return The new, resulting list.
     */
    @SafeVarargs
    public static <E> List<E> joinList(Collection<? extends E>... cols) {
        return join(new ArrayList<>(size(cols)), cols);
    }

    /**
     * Joins multiple collections into a set.
     * @param cols The collections to get values from.
     * @param <E> The element type.
     * @return The new, resulting set.
     */
    @SafeVarargs
    public static <E> Set<E> joinSet(Collection<? extends E>... cols) {
        return join(new HashSet<>(size(cols)), cols);
    }

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
            Validation.notNull("value", value);
            this.value = value;
        }

        /**
         * Adds a value.
         * @param object The value to add.
         * @return This instance.
         */
        public OfCollection<E> add(E object) { value.add(object); return this; }

        /**
         * Adds values.
         * @param collection The values to add.
         * @return This instance.
         */
        public OfCollection<E> add(Collection<E> collection) { value.addAll(collection); return this; }

        /**
         * Adds values.
         * @param collection The values to add.
         * @return This instance.
         */
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
        public Collection<E> build() { return java.util.Collections.unmodifiableCollection(value); }
    }

    /**
     * @see #set(Set)
     * @param <E> The element type.
     */
    public static final class OfSet<E> {
        private final Set<E> value;

        private OfSet(Set<E> value) {
            Validation.notNull("value", value);
            this.value = value;
        }

        /**
         * Adds a value.
         * @param object The value to add.
         * @return This instance.
         */
        public OfSet<E> add(E object) { value.add(object); return this; }

        /**
         * Adds values.
         * @param collection The values to add.
         * @return This instance.
         */
        public OfSet<E> add(Collection<E> collection) { value.addAll(collection); return this; }

        /**
         * Adds values.
         * @param collection The values to add.
         * @return This instance.
         */
        @SafeVarargs public final OfSet<E> add(E... collection) { value.addAll(Arrays.asList(collection)); return this; }

        /**
         * Gets the internal collection used in this builder (mutable).
         * @return The collection.
         */
        public Set<E> get() { return value; }

        /**
         * Gets an immutable version of the collection being built.
         * @return The collection.
         */
        public Set<E> build() { return java.util.Collections.unmodifiableSet(value); }
    }

    /**
     * @see #list(List)
     * @param <E> The element type.
     */
    public static final class OfList<E> {
        private final List<E> value;

        private OfList(List<E> value) {
            Validation.notNull("value", value);
            this.value = value;
        }

        /**
         * Adds a value.
         * @param object The value to add.
         * @return This instance.
         */
        public OfList<E> add(E object) { value.add(object); return this; }

        /**
         * Adds values.
         * @param collection The values to add.
         * @return This instance.
         */
        public OfList<E> add(Collection<E> collection) { value.addAll(collection); return this; }

        /**
         * Adds values.
         * @param collection The values to add.
         * @return This instance.
         */
        @SafeVarargs public final OfList<E> add(E... collection) { value.addAll(Arrays.asList(collection)); return this; }

        /**
         * Gets the internal collection used in this builder (mutable).
         * @return The collection.
         */
        public List<E> get() { return value; }

        /**
         * Gets an immutable version of the collection being built.
         * @return The collection.
         */
        public List<E> build() { return java.util.Collections.unmodifiableList(value); }
    }

    /**
     * @see #map(Map)
     * @param <K> The key type.
     * @param <V> The value type.
     */
    public static final class OfMap<K, V> {
        private final Map<K, V> value;

        private OfMap(Map<K, V> value) {
            Validation.notNull("value", value);
            this.value = value;
        }

        /**
         * Adds a key/value pair.
         * @param k The key.
         * @param v The value.
         * @return This instance.
         */
        public OfMap<K, V> put(K k, V v) { value.put(k, v); return this; }

        /**
         * Adds key/value pairs.
         * @param collection The values to add.
         * @return This instance.
         */
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
        public Map<K, V> build() { return java.util.Collections.unmodifiableMap(value); }
    }
}
