package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.gitlab.aecsocket.minecommons.core.event.EventDispatcher;

/**
 * An abstract implementation of an input manager.
 */
public abstract class AbstractInputs implements Inputs {
    /** The event dispatcher for input events. */
    protected final EventDispatcher<Events.Input> events = new EventDispatcher<>();

    @Override public EventDispatcher<Events.Input> events() { return events; }

    /**
     * Handles an input event.
     * @param event The incoming input event.
     * @param ifCancelled The code to run if cancelled.
     */
    protected void handle(Events.Input event, Runnable ifCancelled) {
        if (events.call(event).cancelled()) {
            ifCancelled.run();
        }
    }
}
