package com.github.aecsocket.minecommons.core;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Convenience class for returning a list of results from a method.
 * @param <T> The type to store.
 */
public class Callback<T> implements Iterable<T> {
    private final Queue<T> queue = new ArrayDeque<>();

    private Callback() {}

    /**
     * Creates an empty callback.
     * @param <E> The type to store.
     * @return The callback.
     */
    public static <E> Callback<E> create() {
        return new Callback<>();
    }

    /**
     * Adds a value.
     * @param value The value.
     * @return This instance.
     */
    public Callback<T> add(T value) {
        queue.add(value);
        return this;
    }

    @Override public Iterator<T> iterator() { return queue.iterator(); }
}
