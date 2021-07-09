package com.gitlab.aecsocket.minecommons.core.event;

import com.gitlab.aecsocket.minecommons.core.Validation;

import java.util.*;
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
     * @param <E> The event type.
     * @param eventType The event type.
     * @param specific If the listener only listens to the exact event type specified, or all subclasses too.
     * @param listener The function to run on event call.
     * @param priority The numerical order in which this listener will be ran.
     */
    public record Listener<E>(Class<E> eventType, boolean specific, Consumer<E> listener, int priority) {
        /**
         * Creates an instance.
         * @param eventType The event type.
         * @param specific If the listener only listens to the exact event type specified, or all subclasses too.
         * @param listener The function to run on event call.
         * @param priority The numerical order in which this listener will be ran.
         */
        public Listener {
            Validation.notNull("eventType", eventType);
            Validation.notNull("listener", listener);
        }

        /**
         * Checks if a type provided will be listened to by this listener.
         * @param checkType The type to check.
         * @return The result.
         */
        public boolean acceptsType(Class<? extends E> checkType) {
            return checkType == eventType || (!specific && eventType.isAssignableFrom(checkType));
        }

        @Override
        public String toString() {
            return "{" +
                    eventType.getSimpleName() +
                    (specific ? " specific" : "") +
                    " @ " + priority +
                    " (" + listener + ")" +
                    '}';
        }
    }

    private final TreeSet<Listener<E>> listeners = new TreeSet<>(Comparator.comparingInt(Listener::priority));

    /**
     * Gets all registered listeners.
     * @return The listeners.
     */
    public Collection<Listener<E>> listeners() { return listeners; }

    /**
     * Gets all registered listeners for a specific event type.
     * <p>
     * Uses {@link Listener#acceptsType(Class)} to check if a listener is applicable for this.
     * @param eventType The event type.
     * @param <F> The event type.
     * @return The listeners.
     */
    public <F extends E> List<Listener<F>> listenersOf(Class<F> eventType) {
        List<Listener<F>> result = new ArrayList<>();
        for (Listener<E> listener : listeners) {
            if (listener.acceptsType(eventType)) {
                @SuppressWarnings("unchecked")
                Listener<F> fListener = (Listener<F>) listener;
                result.add(fListener);
            }
        }
        return result;
    }

    /**
     * Registers a listener, receiving event calls.
     * @param eventType The event type.
     * @param specific If the specific event type should be listened for, or all subtypes as well.
     * @param listener The action to run for the event.
     * @param priority The event priority. Default is {@link #DEFAULT_PRIORITY}.
     * @param <F> The event type.
     * @return The listener created.
     */
    public <F extends E> Listener<E> register(Class<F> eventType, boolean specific, Consumer<F> listener, int priority) {
        @SuppressWarnings("unchecked")
        Listener<E> registered = (Listener<E>) new Listener<>(eventType, specific, listener, priority);
        listeners.add(registered);
        return registered;
    }

    /**
     * Registers a listener, receiving event calls.
     * @param eventType The event type.
     * @param listener The action to run for the event.
     * @param priority The event priority. Default is {@link #DEFAULT_PRIORITY}.
     * @param <F> The event type.
     * @return The listener created.
     */
    public <F extends E> Listener<E> register(Class<F> eventType, Consumer<F> listener, int priority) {
        return register(eventType, false, listener, priority);
    }

    /**
     * Registers a listener, receiving event calls.
     * @param eventType The event type.
     * @param specific If the specific event type should be listened for, or all subtypes as well.
     * @param listener The action to run for the event.
     * @param <F> The event type.
     * @return The listener created.
     */
    public <F extends E> Listener<E> register(Class<F> eventType, boolean specific, Consumer<F> listener) {
        return register(eventType, specific, listener, DEFAULT_PRIORITY);
    }

    /**
     * Registers a listener, receiving event calls.
     * @param eventType The event type.
     * @param listener The action to run for the event.
     * @param <F> The event type.
     * @return The listener created.
     */
    public <F extends E> Listener<E> register(Class<F> eventType, Consumer<F> listener) {
        return register(eventType, false, listener, DEFAULT_PRIORITY);
    }

    /**
     * Unregisters a listener, stopping it from receiving event calls.
     * @param listener The registered listener, obtained from a register call.
     * @return This instance.
     */
    public EventDispatcher<E> unregister(Listener<E> listener) {
        listeners.remove(listener);
        return this;
    }

    /**
     * Unregisters all listeners for a specific type.
     * <p>
     * Uses {@link Listener#acceptsType(Class)} to check if a listener is applicable for this.
     * @param eventType The event type.
     * @return This instance.
     */
    public EventDispatcher<E> unregister(Class<? extends E> eventType) {
        listeners.removeIf(listener -> listener.acceptsType(eventType));
        return this;
    }

    /**
     * Unregisters all listeners.
     * @return This instance.
     */
    public EventDispatcher<E> unregisterAll() {
        listeners.clear();
        return this;
    }

    /**
     * Calls an event for all registered listeners.
     * @param event The event to call.
     * @param <F> The event type.
     * @return The event called.
     */
    public <F extends E> F call(F event) {
        @SuppressWarnings("unchecked")
        Class<F> eventType = (Class<F>) event.getClass();
        for (Listener<F> listener : listenersOf(eventType)) {
            listener.listener.accept(event);
        }
        return event;
    }
}
