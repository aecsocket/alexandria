package com.github.aecsocket.minecommons.paper.inputs;

import com.github.aecsocket.minecommons.core.InputType;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import org.bukkit.entity.Player;

/**
 * Handles player inputs through a packet listener.
 * <p>
 * Uses:
 * <ul>
 *     <li>{@link PacketType.Play.Client#ANIMATION}: {@link InputType#MOUSE_LEFT}</li>
 *     <li>{@link PacketType.Play.Client#PLAYER_BLOCK_PLACEMENT}: {@link InputType#MOUSE_RIGHT}</li>
 *     <li>{@link PacketType.Play.Client#PLAYER_DIGGING}}: {@link InputType#OFFHAND}, {@link InputType#DROP}</li>
 *     <li>{@link PacketType.Play.Client#HELD_ITEM_CHANGE}: {@link InputType#SWAP}, {@link InputType#SCROLL_UP}, {@link InputType#SCROLL_DOWN}</li>
 *     <li>{@link PacketType.Play.Client#ENTITY_ACTION}: {@link InputType#SNEAK_START}, {@link InputType#SNEAK_STOP}, {@link InputType#SPRINT_START}, {@link InputType#SPRINT_STOP}</li>
 *     <li>{@link PacketType.Play.Client#PLAYER_ABILITIES}: {@link InputType#FLIGHT_START}, {@link InputType#FLIGHT_STOP}</li>
 *     <li>{@link PacketType.Play.Client#ADVANCEMENT_TAB}: {@link InputType#ADVANCEMENTS}</li>
 * </ul>
 * Calls events of type {@link Events.PacketInput}.
 */
public class PacketInputs extends AbstractInputs implements PacketListener {
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        var type = event.getPacketType();
        Player player = (Player) event.getPlayer();

        if (type == PacketType.Play.Client.ANIMATION) {
            var packet = new WrapperPlayClientAnimation(event);
            if (packet.getHand() == InteractionHand.MAIN_HAND && !hasDropped(player)) {
                handle(new Events.PacketInput(player, InputType.MOUSE_LEFT, event), event::setCancelled);
            }
        }

        if (type == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            var packet = new WrapperPlayClientPlayerBlockPlacement(event);
            if (packet.getHand() == InteractionHand.MAIN_HAND) {
                handle(new Events.PacketInput(player, InputType.MOUSE_RIGHT, event), event::setCancelled);
            }
        }

        if (type == PacketType.Play.Client.PLAYER_DIGGING) {
            var packet = new WrapperPlayClientPlayerDigging(event);
            switch (packet.getAction()) {
                case SWAP_ITEM_WITH_OFFHAND -> handle(new Events.PacketInput(player, InputType.OFFHAND, event), event::setCancelled);
                case DROP_ITEM, DROP_ITEM_STACK -> {
                    handle(new Events.PacketInput(player, InputType.DROP, event), event::setCancelled);
                    dropped(player);
                }
            }
        }

        if (type == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            var packet = new WrapperPlayClientHeldItemChange(event);
            int next = packet.getSlot();
            int last = player.getInventory().getHeldItemSlot();
            if (next == last)
                return;
            handle(new Events.PacketInput(player, InputType.SWAP, event), event::setCancelled);
            handle(new Events.PacketInput(player, scrollDirection(next, last), event), () -> {
                event.setCancelled(true);
                player.getInventory().setHeldItemSlot(last);
            });
        }

        if (type == PacketType.Play.Client.ENTITY_ACTION) {
            var packet = new WrapperPlayClientEntityAction(event);
            switch (packet.getAction()) {
                case START_SNEAKING ->  handle(new Events.PacketInput(player, InputType.SNEAK_START, event),  event::setCancelled);
                case STOP_SNEAKING ->   handle(new Events.PacketInput(player, InputType.SNEAK_STOP, event),   event::setCancelled);
                case START_SPRINTING -> handle(new Events.PacketInput(player, InputType.SPRINT_START, event), event::setCancelled);
                case STOP_SPRINTING ->  handle(new Events.PacketInput(player, InputType.SPRINT_STOP, event),  event::setCancelled);
            }
        }

        if (type == PacketType.Play.Client.PLAYER_ABILITIES) {
            var packet = new WrapperPlayClientPlayerAbilities(event);
            handle(new Events.PacketInput(
                player,
                Boolean.TRUE.equals(packet.isFlying()) ? InputType.FLIGHT_START : InputType.FLIGHT_STOP, event),
                event::setCancelled);
        }

        if (type == PacketType.Play.Client.ADVANCEMENT_TAB) {
            var packet = new WrapperPlayClientAdvancementTab(event);
            if (packet.getAction() == WrapperPlayClientAdvancementTab.Action.OPENED_TAB) {
                handle(new Events.PacketInput(player, InputType.ADVANCEMENTS, event), event::setCancelled);
            }
        }
    }

    /**
     * The events this class can call.
     */
    public static final class Events {
        private Events() {}

        /**
         * Runs when a player makes an input which has an underlying {@link PacketReceiveEvent}.
         */
        public static class PacketInput extends Inputs.Events.Input {
            private final PacketReceiveEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public PacketInput(Player player, InputType input, PacketReceiveEvent event) {
                super(player, input);
                this.event = event;
            }

            /**
             * Gets the underlying packet event.
             * @return The event.
             */
            public PacketReceiveEvent event() { return event; }
        }
    }
}
