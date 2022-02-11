package com.github.aecsocket.minecommons.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * Convenience class for returning a list of results from a method.
 * @param <T> The type to store.
 */
public class Callback<T> implements Iterable<T> {
    private final Deque<T> queue = new ArrayDeque<>();

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

    /**
     * Adds another callback's result to this.
     * @param callback The next callback.
     * @return This callback.
     */
    public Callback<T> then(Callback<T> callback) {
        queue.addAll(callback.queue);
        return this;
    }

    /**
     * Adds another callback's result to this.
     * @param supplier The supplier to the next callback.
     * @return This callback.
     */
    public Callback<T> then(Supplier<Callback<T>> supplier) {
        queue.addAll(supplier.get().queue);
        return this;
    }

    @Override public Iterator<T> iterator() { return queue.iterator(); }
}
