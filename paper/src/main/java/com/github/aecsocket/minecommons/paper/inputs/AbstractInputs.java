package com.github.aecsocket.minecommons.paper.inputs;

import com.github.aecsocket.minecommons.core.InputType;
import com.github.aecsocket.minecommons.core.event.EventDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An abstract implementation of an input manager.
 */
public abstract class AbstractInputs implements Inputs {
    /** The event dispatcher for input events. */
    protected final EventDispatcher<Events.Input> events = new EventDispatcher<>();
    // this is kind of a memory leak, but we don't care, since it uses basically zero memory
    private final Map<UUID, Integer> lastDropTick = new HashMap<>();

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

    protected void dropped(Player player) {
        lastDropTick.put(player.getUniqueId(), Bukkit.getCurrentTick());
    }

    protected boolean hasDropped(Player player) {
        return lastDropTick.getOrDefault(player.getUniqueId(), -1) == Bukkit.getCurrentTick();
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
