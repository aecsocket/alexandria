package com.github.aecsocket.minecommons.paper.inputs;

import com.github.aecsocket.minecommons.core.InputType;
import com.github.aecsocket.minecommons.core.event.EventDispatcher;

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

    /**
     * Gets a scroll direction from a hotbar slot change.
     * @param cur The new slot.
     * @param prv The previous slot.
     * @return The scroll direction, either {@link InputType#SCROLL_UP} or {@link InputType#SCROLL_DOWN}.
     */
    public static InputType scrollDirection(int cur, int prv) {
        return (cur < prv || (cur == 8 && prv == 0)) && !(cur == 0 && prv == 8)
            ? InputType.SCROLL_UP : InputType.SCROLL_DOWN;
    }
}
