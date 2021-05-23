package com.gitlab.aecsocket.minecommons.core.event;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * Responsible for managing listeners: registration and sending events to listeners.
 * @param <E> The base event type.
 */
public final class EventDispatcher<E> {
    /** The default priority of listeners. */
    public static final int DEFAULT_PRIORITY = 0;

    /**
     * A registered listener.
     * @param <E> The specific event type.
     */
    public record Listener<E>(Consumer<E> listener, int priority) {}

    /**
     * A collection of listeners for a specific event type.
     * @param <E> The specific event type.
     */
    public static final class ListenerCollection<E> {
        private final TreeSet<Listener<E>> listeners = new TreeSet<>(Comparator.comparingInt(Listener::priority));

        /**
         * Gets all listeners registered, sorted by priority (lowest to highest).
         * @return The listeners.
         */
        public TreeSet<Listener<E>> listeners() { return listeners; }

        /**
         * Registers a listener.
         * @param listener The listener.
         * @return This instance.
         */
        public ListenerCollection<E> register(Listener<E> listener) {
            listeners.add(listener);
            return this;
        }

        /**
         * Registers a listener.
         * @param listener The listener function, consuming the event.
         * @param priority The priority of the event. Lower means the listener runs earlier.
         * @return This instance.
         */
        public ListenerCollection<E> register(Consumer<E> listener, int priority) {
            listeners.add(new Listener<>(listener, priority));
            return this;
        }

        /**
         * Registers a listener. Uses {@link #DEFAULT_PRIORITY} as the priority.
         * @param listener The listener function, consuming the event.
         * @return This instance.
         */
        public ListenerCollection<E> register(Consumer<E> listener) {
            listeners.add(new Listener<>(listener, DEFAULT_PRIORITY));
            return this;
        }

        /**
         * Calls an event to all registered listeners.
         * @param event The event.
         * @return The event.
         */
        public E call(E event) {
            for (Listener<E> listener : listeners) {
                listener.listener.accept(event);
            }
            return event;
        }
    }

    private final Map<Class<? extends E>, ListenerCollection<? extends E>> listeners = new HashMap<>();

    /**
     * Gets a listener collection for an event type.
     * @param eventType The event type.
     * @param <F> The event type.
     * @return The listener collection.
     */
    public <F extends E> ListenerCollection<F> event(Class<F> eventType) {
        @SuppressWarnings("unchecked")
        ListenerCollection<F> listeners = (ListenerCollection<F>) this.listeners.computeIfAbsent(eventType, t -> new ListenerCollection<>());
        return listeners;
    }
}
