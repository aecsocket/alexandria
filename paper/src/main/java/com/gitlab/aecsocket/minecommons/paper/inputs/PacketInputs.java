package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.gitlab.aecsocket.minecommons.core.InputType;
import com.gitlab.aecsocket.minecommons.paper.plugin.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Handles player inputs through a packet listener.
 * <p>
 * Uses:
 * <ul>
 *     <li>{@link PacketType.Play.Client#ARM_ANIMATION}: {@link InputType#MOUSE_LEFT}</li>
 *     <li>{@link PacketType.Play.Client#BLOCK_PLACE}: {@link InputType#MOUSE_RIGHT}</li>
 *     <li>{@link PacketType.Play.Client#BLOCK_DIG} state {@link EnumWrappers.PlayerDigType#SWAP_HELD_ITEMS}: {@link InputType#OFFHAND}</li>
 *     <li>{@link PacketType.Play.Client#BLOCK_DIG} state {@link EnumWrappers.PlayerDigType#DROP_ITEM}: {@link InputType#DROP}</li>
 *     <li>{@link PacketType.Play.Client#HELD_ITEM_SLOT}: {@link InputType#SWAP}, {@link InputType#SCROLL_UP}, {@link InputType#SCROLL_DOWN}</li>
 *     <li>{@link PacketType.Play.Client#ENTITY_ACTION}: {@link InputType#SNEAK_START}, {@link InputType#SNEAK_STOP},
 *              {@link InputType#SPRINT_START}, {@link InputType#SPRINT_STOP}</li>
 *     <li>{@link PacketType.Play.Client#ABILITIES}: {@link InputType#FLIGHT_START}, {@link InputType#FLIGHT_STOP}</li>
 *     <li>{@link PacketType.Play.Client#ADVANCEMENTS}: {@link InputType#ADVANCEMENTS}</li>
 * </ul>
 * Calls events of type {@link Events.PacketInput}.
 */
public class PacketInputs extends AbstractInputs implements PacketListener {
    private static final ListeningWhitelist sendingWhitelist = ListeningWhitelist.EMPTY_WHITELIST;
    private static final ListeningWhitelist receivingWhitelist = ListeningWhitelist.newBuilder()
            .types(PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.BLOCK_PLACE,
                    PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.HELD_ITEM_SLOT,
                    PacketType.Play.Client.ENTITY_ACTION, PacketType.Play.Client.ABILITIES,
                    PacketType.Play.Client.ADVANCEMENTS)
            .build();

    private final BasePlugin<?> plugin;
    private int lastScroll;

    private enum AdvancementAction {
        OPENED_TAB,
        CLOSED_SCREEN
    }

    /**
     * Creates an instance.
     * @param plugin The plugin which the packet listener is registered under.
     */
    public PacketInputs(BasePlugin<?> plugin) {
        this.plugin = plugin;
    }

    @Override public BasePlugin<?> getPlugin() { return plugin; }

    @Override public ListeningWhitelist getSendingWhitelist() { return sendingWhitelist; }
    @Override public ListeningWhitelist getReceivingWhitelist() { return receivingWhitelist; }

    @Override public void onPacketSending(PacketEvent event) {}

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        PacketType type = event.getPacketType();
        Player player = event.getPlayer();

        if (type == PacketType.Play.Client.ARM_ANIMATION) {
            if (packet.getHands().read(0) == EnumWrappers.Hand.MAIN_HAND) {
                handle(new Events.PacketInput(player, InputType.MOUSE_LEFT, event), () -> event.setCancelled(true));
            }
        }
        if (type == PacketType.Play.Client.BLOCK_PLACE) {
            if (packet.getHands().read(0) == EnumWrappers.Hand.MAIN_HAND) {
                handle(new Events.PacketInput(player, InputType.MOUSE_RIGHT, event), () -> event.setCancelled(true));
            }
        }
        if (type == PacketType.Play.Client.BLOCK_DIG) {
            switch (packet.getPlayerDigTypes().read(0)) {
                case SWAP_HELD_ITEMS -> handle(new Events.PacketInput(player, InputType.OFFHAND, event), () -> event.setCancelled(true));
                case DROP_ITEM, DROP_ALL_ITEMS -> handle(new Events.PacketInput(player, InputType.DROP, event), () -> event.setCancelled(true));
            }
        }
        if (type == PacketType.Play.Client.HELD_ITEM_SLOT) {
            if (Bukkit.getCurrentTick() <= lastScroll + 1)
                return;
            handle(new Events.PacketInput(player, InputType.SWAP, event), () -> event.setCancelled(true));
            int prv = player.getInventory().getHeldItemSlot();
            handle(new Events.PacketInput(event.getPlayer(),
                    scrollDirection(packet.getIntegers().read(0), prv),
                    event), () -> {
                event.setCancelled(true);
                PacketContainer cancelPacket = new PacketContainer(PacketType.Play.Server.HELD_ITEM_SLOT);
                cancelPacket.getIntegers().write(0, prv);
                plugin.protocol().send(player, cancelPacket, false, false);
                lastScroll = Bukkit.getCurrentTick();
            });
        }
        if (type == PacketType.Play.Client.ENTITY_ACTION) {
            switch (packet.getPlayerActions().read(0)) {
                case START_SNEAKING -> handle(new Events.PacketInput(player, InputType.SNEAK_START, event), () -> event.setCancelled(true));
                case STOP_SNEAKING -> handle(new Events.PacketInput(player, InputType.SNEAK_STOP, event), () -> event.setCancelled(true));
                case START_SPRINTING -> handle(new Events.PacketInput(player, InputType.SPRINT_START, event), () -> event.setCancelled(true));
                case STOP_SPRINTING -> handle(new Events.PacketInput(player, InputType.SPRINT_STOP, event), () -> event.setCancelled(true));
            }
        }
        if (type == PacketType.Play.Client.ABILITIES) {
            handle(new Events.PacketInput(player, packet.getBooleans().read(0) ? InputType.FLIGHT_START : InputType.FLIGHT_STOP, event),
                    () -> event.setCancelled(true));
        }
        if (type == PacketType.Play.Client.ADVANCEMENTS) {
            if (packet.getEnumModifier(AdvancementAction.class, 0).read(0) == AdvancementAction.OPENED_TAB) {
                handle(new Events.PacketInput(player, InputType.ADVANCEMENTS, event), () -> event.setCancelled(true));
            }
        }
    }

    /**
     * The events this class can call.
     */
    public static final class Events {
        private Events() {}

        /**
         * Runs when a player makes an input which has an underlying {@link PacketEvent}.
         */
        public static class PacketInput extends Inputs.Events.Input {
            private final PacketEvent event;

            /**
             * Creates an instance.
             * @param player The player.
             * @param input The input type.
             * @param event The underlying event.
             */
            public PacketInput(Player player, InputType input, PacketEvent event) {
                super(player, input);
                this.event = event;
            }

            /**
             * Gets the underlying packet event.
             * @return The event.
             */
            public PacketEvent event() { return event; }
        }
    }
}
