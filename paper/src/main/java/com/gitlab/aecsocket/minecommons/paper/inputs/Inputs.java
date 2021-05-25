package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.gitlab.aecsocket.minecommons.core.event.Cancellable;
import com.gitlab.aecsocket.minecommons.core.event.EventDispatcher;
import org.bukkit.entity.Player;

/**
 * Reads and stores the state of player inputs.
 */
public interface Inputs {
    /**
     * Types of inputs that can be listened to, such as mouse buttons.
     */
    enum Input {
        LEFT    (75),
        RIGHT   (225);

        private final int holdThreshold;

        Input(int holdThreshold) {
            this.holdThreshold = holdThreshold;
        }

        public int holdThreshold() { return holdThreshold; }
    }

    /**
     * The last timestamp of a player making an input, in milliseconds.
     * @param player The player.
     * @param input The input type.
     * @return The timestamp.
     */
    long last(Player player, Input input);

    /**
     * Gets if a player is holding an input.
     * @param player The player.
     * @param input The input type.
     * @return The result.
     */
    boolean holding(Player player, Input input);

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
        public static class Input implements Cancellable {
            private final Player player;
            private final Inputs.Input input;
            private boolean cancelled;

            public Input(Player player, Inputs.Input input) {
                this.player = player;
                this.input = input;
            }

            public Player player() { return player; }
            public Inputs.Input input() { return input; }

            @Override public boolean cancelled() { return cancelled; }
            @Override public void cancelled(boolean cancelled) { this.cancelled = cancelled; }
        }
    }
}
