package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.gitlab.aecsocket.minecommons.core.event.EventDispatcher;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * An abstract implementation of an input manager.
 */
public abstract class AbstractInputs implements Inputs {
    protected final Map<UUID, Map<Input, Long>> last = new HashMap<>();
    protected final EventDispatcher<Events.Input> events = new EventDispatcher<>();

    public Map<UUID, Map<Input, Long>> last() { return last; }
    @Override public EventDispatcher<Events.Input> events() { return events; }

    @Override
    public long last(Player player, Input input) {
        return last.getOrDefault(player.getUniqueId(), Collections.emptyMap()).getOrDefault(input, -1L);
    }

    @Override
    public boolean holding(Player player, Input input) {
        long last =  last(player, input);
        if (last == -1)
            return false;
        return last + input.holdThreshold() > System.currentTimeMillis();
    }

    /**
     * Handles an input event.
     * @param player The player.
     * @param input The input type.
     */
    protected void handle(Player player, Input input, Runnable ifCancelled) {
        last.computeIfAbsent(player.getUniqueId(), uuid -> new EnumMap<>(Input.class)).put(input, System.currentTimeMillis());
        if (events.event(Events.Input.class).call(new Events.Input(player, input)).cancelled()) {
            ifCancelled.run();
        }
    }
}
