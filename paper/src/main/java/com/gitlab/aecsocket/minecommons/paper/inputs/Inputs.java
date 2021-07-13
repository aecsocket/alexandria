package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.gitlab.aecsocket.minecommons.core.InputType;
import com.gitlab.aecsocket.minecommons.core.event.Cancellable;
import com.gitlab.aecsocket.minecommons.core.event.EventDispatcher;
import org.bukkit.entity.Player;

/**
 * Reads and stores the state of player inputs.
 */
public interface Inputs {
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
            private final InputType input;
            private boolean cancelled;

            /**
             * Creates an instance.
             * @param player The player that performed the input.
             * @param input The input type.
             */
            public Input(Player player, InputType input) {
                this.player = player;
                this.input = input;
            }

            /**
             * Gets the player that performed the input.
             * @return The player that performed the input.
             */
            public Player player() { return player; }

            /**
             * Gets the input type.
             * @return The input type.
             */
            public InputType input() { return input; }

            @Override public boolean cancelled() { return cancelled; }
            @Override public void cancelled(boolean cancelled) { this.cancelled = cancelled; }
        }
    }
}
