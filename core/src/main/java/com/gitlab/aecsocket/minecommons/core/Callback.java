package com.gitlab.aecsocket.minecommons.core;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Convenience class for returning a list of results from a method.
 * @param <E> The type to store.
 */
public class Callback<E> implements Iterable<E> {
    private final Queue<E> queue;

    /**
     * Creates an instance.
     * @param queue The queue.
     */
    public Callback(Queue<E> queue) {
        Validation.notNull("queue", queue);
        this.queue = queue;
    }

    /**
     * Creates an instance.
     */
    public Callback() {
        this(new ArrayDeque<>());
    }

    /**
     * Gets the internal queue that this instance uses.
     * @return The queue.
     */
    public Queue<E> queue() { return queue; }

    /**
     * Adds a value.
     * @param value The value.
     * @return This instance.
     */
    public Callback<E> add(E value) {
        queue.add(value);
        return this;
    }

    @Override public Iterator<E> iterator() { return queue.iterator(); }
}
