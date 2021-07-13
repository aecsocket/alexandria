package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.gitlab.aecsocket.minecommons.core.InputType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handles player inputs through a packet listener.
 * <p>
 * Uses:
 * <ul>
 *     <li>{@link PacketType.Play.Client#ARM_ANIMATION}: {@link InputType#MOUSE_LEFT}</li>
 *     <li>{@link PacketType.Play.Client#BLOCK_PLACE}: {@link InputType#MOUSE_RIGHT}</li>
 *     <li>{@link PacketType.Play.Client#BLOCK_DIG} state {@link EnumWrappers.PlayerDigType#SWAP_HELD_ITEMS}: {@link InputType#OFFHAND}</li>
 *     <li>{@link PacketType.Play.Client#BLOCK_DIG} state {@link EnumWrappers.PlayerDigType#DROP_ITEM}: {@link InputType#DROP}</li>
 *     <li>{@link PacketType.Play.Client#ADVANCEMENTS}: {@link InputType#ADVANCEMENTS}</li>
 * </ul>
 */
public class PacketInputs extends AbstractInputs implements PacketListener {
    private static final ListeningWhitelist sendingWhitelist = ListeningWhitelist.EMPTY_WHITELIST;
    private static final ListeningWhitelist receivingWhitelist = ListeningWhitelist.newBuilder()
            .types(PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.BLOCK_PLACE,
                    PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.ADVANCEMENTS)
            .build();

    private final Plugin plugin;

    private enum AdvancementAction {
        OPENED_TAB,
        CLOSED_SCREEN
    }

    /**
     * Creates an instance.
     * @param plugin The plugin which the packet listener is registered under.
     */
    public PacketInputs(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override public Plugin getPlugin() { return plugin; }

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
                handle(player, InputType.MOUSE_LEFT, () -> event.setCancelled(true));
            }
        }
        if (type == PacketType.Play.Client.BLOCK_PLACE) {
            if (packet.getHands().read(0) == EnumWrappers.Hand.MAIN_HAND) {
                handle(player, InputType.MOUSE_RIGHT, () -> event.setCancelled(true));
            }
        }
        if (type == PacketType.Play.Client.BLOCK_DIG) {
            switch (packet.getPlayerDigTypes().read(0)) {
                case SWAP_HELD_ITEMS -> handle(player, InputType.OFFHAND, () -> event.setCancelled(true));
                case DROP_ITEM, DROP_ALL_ITEMS -> handle(player, InputType.DROP, () -> event.setCancelled(true));
            }
        }
        if (type == PacketType.Play.Client.ADVANCEMENTS) {
            if (packet.getEnumModifier(AdvancementAction.class, 0).read(0) == AdvancementAction.OPENED_TAB) {
                handle(player, InputType.ADVANCEMENTS, () -> event.setCancelled(true));
            }
        }
    }
}
