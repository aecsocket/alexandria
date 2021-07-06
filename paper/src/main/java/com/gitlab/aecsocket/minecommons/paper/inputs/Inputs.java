package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.gitlab.aecsocket.minecommons.core.event.Cancellable;
import com.gitlab.aecsocket.minecommons.core.event.EventDispatcher;
import org.bukkit.entity.Player;

/**
 * Reads and stores the state of player inputs.
 */
public interface Inputs {
    /** Input type for a left mouse button click. */
    int LEFT = 0;
    /** Input type for a right mouse button click. */
    int RIGHT = 1;

    /**
     * Gets the event dispatcher which is used to call {@link Events.Input} events.
     * @return The event dispatcher.
     */
    EventDispatcher<Events.Input> events();

    /**
     * The events this class can call.
     */
    final class Events {
        private Events() {}

        /**
         * Runs when a player makes an input.
         */
        public static final class Input implements Cancellable {
            private final Player player;
            private final int input;
            private boolean cancelled;

            public Input(Player player, int input) {
                this.player = player;
                this.input = input;
            }

            public Player player() { return player; }
            public int input() { return input; }

            @Override public boolean cancelled() { return cancelled; }
            @Override public void cancelled(boolean cancelled) { this.cancelled = cancelled; }
        }
    }
}