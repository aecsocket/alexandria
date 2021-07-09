package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.gitlab.aecsocket.minecommons.core.event.EventDispatcher;
import org.bukkit.entity.Player;

/**
 * An abstract implementation of an input manager.
 */
public abstract class AbstractInputs implements Inputs {
    /** The event dispatcher for input events. */
    protected final EventDispatcher<Events.Input> events = new EventDispatcher<>();

    @Override public EventDispatcher<Events.Input> events() { return events; }

    /**
     * Handles an input event.
     * @param player The player.
     * @param input The input type.
     * @param ifCancelled The code to run if cancelled.
     */
    protected void handle(Player player, int input, Runnable ifCancelled) {
        if (events.call(new Events.Input(player, input)).cancelled()) {
            ifCancelled.run();
        }
    }
}
