package com.github.aecsocket.minecommons.paper.inputs;

import com.github.aecsocket.minecommons.core.InputType;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Handles player inputs through an event listener.
 * <p>
 * Uses:
 * <ul>
 *     <li>{@link PlayerAnimationEvent}: {@link InputType#MOUSE_LEFT}</li>
 *     <li>{@link PlayerInteractEvent}: {@link InputType#MOUSE_RIGHT}</li>
 *     <li>{@link PlayerSwapHandItemsEvent}: {@link InputType#OFFHAND}</li>
 *     <li>{@link PlayerDropItemEvent}: {@link InputType#DROP}</li>
 *     <li>{@link PlayerItemHeldEvent}: {@link InputType#SWAP}, {@link InputType#SCROLL_UP}, {@link InputType#SCROLL_DOWN}</li>
 *     <li>{@link PlayerToggleSneakEvent}: {@link InputType#SNEAK_START}, {@link InputType#SNEAK_STOP}</li>
 *     <li>{@link PlayerToggleSprintEvent}: {@link InputType#SPRINT_START}, {@link InputType#SPRINT_STOP}</li>
 *     <li>{@link PlayerToggleFlightEvent}: {@link InputType#FLIGHT_START}, {@link InputType#FLIGHT_STOP}</li>
 * </ul>
 */
public class ListenerInputs extends AbstractInputs implements Listener {
    @EventHandler
    private void onEvent(PlayerAnimationEvent event) {
        if (hasDropped(event.getPlayer()))
            return;
        handle(new Events.AnimationInput(event.getPlayer(), InputType.MOUSE_LEFT, event), () -> event.setCancelled(true));
    }

    @EventHandler
    private void onEvent(PlayerInteractEvent event) {
        if (
            event.getHand() == EquipmentSlot.HAND &&
            (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        ) {
            handle(new Events.InteractInput(event.getPlayer(), InputType.MOUSE_RIGHT, event), () -> event.setCancelled(true));
        }
    }

    @EventHandler
    private void onEvent(PlayerSwapHandItemsEvent event) {
        handle(new Events.SwapHandInput(event.getPlayer(), InputType.OFFHAND, event), () -> event.setCancelled(true));
    }

    @EventHandler
    private void onEvent(PlayerDropItemEvent event) {
        handle(new Events.DropInput(event.getPlayer(), InputType.DROP, event), () -> event.setCancelled(true));
        dropped(event.getPlayer());
    }

    @EventHandler
    private void onEvent(PlayerItemHeldEvent event) {
        handle(new Events.SwapInput(event.getPlayer(), InputType.SWAP, event), () -> event.setCancelled(true));
        handle(new Events.SwapInput(event.getPlayer(),
            scrollDirection(event.getNewSlot(), event.getPreviousSlot()),
            event), () -> event.setCancelled(true));
    }

    @EventHandler
    private void onEvent(PlayerToggleSneakEvent event) {
        handle(new Events.SneakInput(event.getPlayer(), event.isSneaking() ? InputType.SNEAK_START : InputType.SNEAK_STOP, event),
            () -> event.setCancelled(true));
    }

    @EventHandler
    private void onEvent(PlayerToggleSprintEvent event) {
        handle(new Events.SprintInput(event.getPlayer(), event.isSprinting() ? InputType.SPRINT_START : InputType.SPRINT_STOP, event),
            () -> event.setCancelled(true));
    }

    @EventHandler
    private void onEvent(PlayerToggleFlightEvent event) {
        handle(new Events.FlightInput(event.getPlayer(), event.isFlying() ? InputType.FLIGHT_START : InputType.FLIGHT_STOP, event),
            () -> event.setCancelled(true));
    }

    /**
     * The events this class can call.
     */
    public static final class Events {
        private Events() {}

        /**
         * Runs when a player makes an input which has an underlying {@link PlayerEvent}.
         */
        public abstract static class EventInput extends Inputs.Events.Input {
            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             */
            protected EventInput(Player player, InputType input) {
                super(player, input);
            }

            /**
             * Gets the underlying Paper event.
             * @return The event.
             */
            public abstract PlayerEvent event();
        }

        /**
         * Runs when a player makes an input with an underlying {@link PlayerAnimationEvent}.
         */
        public static class AnimationInput extends EventInput {
            private final PlayerAnimationEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public AnimationInput(Player player, InputType input, PlayerAnimationEvent event) {
                super(player, input);
                this.event = event;
            }

            @Override public PlayerAnimationEvent event() { return event; }
        }

        /**
         * Runs when a player makes an input with an underlying {@link PlayerInteractEvent}.
         */
        public static class InteractInput extends EventInput {
            private final PlayerInteractEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public InteractInput(Player player, InputType input, PlayerInteractEvent event) {
                super(player, input);
                this.event = event;
            }

            @Override public PlayerInteractEvent event() { return event; }
        }

        /**
         * Runs when a player makes an input with an underlying {@link PlayerSwapHandItemsEvent}.
         */
        public static class SwapHandInput extends EventInput {
            private final PlayerSwapHandItemsEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public SwapHandInput(Player player, InputType input, PlayerSwapHandItemsEvent event) {
                super(player, input);
                this.event = event;
            }

            @Override public PlayerSwapHandItemsEvent event() { return event; }
        }

        /**
         * Runs when a player makes an input with an underlying {@link PlayerDropItemEvent}.
         */
        public static class DropInput extends EventInput {
            private final PlayerDropItemEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public DropInput(Player player, InputType input, PlayerDropItemEvent event) {
                super(player, input);
                this.event = event;
            }

            @Override public PlayerDropItemEvent event() { return event; }
        }

        /**
         * Runs when a player makes an input with an underlying {@link PlayerItemHeldEvent}.
         */
        public static class SwapInput extends EventInput {
            private final PlayerItemHeldEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public SwapInput(Player player, InputType input, PlayerItemHeldEvent event) {
                super(player, input);
                this.event = event;
            }

            @Override public PlayerItemHeldEvent event() { return event; }
        }

        /**
         * Runs when a player makes an input with an underlying {@link PlayerToggleSneakEvent}.
         */
        public static class SneakInput extends EventInput {
            private final PlayerToggleSneakEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public SneakInput(Player player, InputType input, PlayerToggleSneakEvent event) {
                super(player, input);
                this.event = event;
            }

            @Override public PlayerToggleSneakEvent event() { return event; }
        }

        /**
         * Runs when a player makes an input with an underlying {@link PlayerToggleSprintEvent}.
         */
        public static class SprintInput extends EventInput {
            private final PlayerToggleSprintEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public SprintInput(Player player, InputType input, PlayerToggleSprintEvent event) {
                super(player, input);
                this.event = event;
            }

            @Override public PlayerToggleSprintEvent event() { return event; }
        }

        /**
         * Runs when a player makes an input with an underlying {@link PlayerToggleFlightEvent}.
         */
        public static class FlightInput extends EventInput {
            private final PlayerToggleFlightEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public FlightInput(Player player, InputType input, PlayerToggleFlightEvent event) {
                super(player, input);
                this.event = event;
            }

            @Override public PlayerToggleFlightEvent event() { return event; }
        }
    }
}
