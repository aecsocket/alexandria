package com.github.aecsocket.minecommons.core.event;

/**
 * Represents an event which has a "cancelled" state, in which
 * the intended action does not run.
 */
public interface Cancellable {
    /**
     * Gets if this event is cancelled.
     * @return The state.
     */
    boolean cancelled();

    /**
     * Sets if this event is cancelled.
     * @param cancelled The state.
     */
    void cancelled(boolean cancelled);

    /**
     * Cancels this event. The consequence is left up to the event.
     */
    default void cancel() { cancelled(true); }
}
