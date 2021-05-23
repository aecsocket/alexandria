package com.gitlab.aecsocket.minecommons.inputs;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handles player inputs through a packet listener.
 * <p>
 * Uses:
 * <ul>
 * <li>{@link PacketType.Play.Client#ARM_ANIMATION}: {@link Inputs.Input#LEFT}</li>
 * <li>{@link PacketType.Play.Client#BLOCK_PLACE}: {@link Inputs.Input#RIGHT}</li>
 * </ul>
 */
public class PacketInputs extends AbstractInputs implements PacketListener {
    private static final ListeningWhitelist sendingWhitelist = ListeningWhitelist.EMPTY_WHITELIST;
    private static final ListeningWhitelist receivingWhitelist = ListeningWhitelist.newBuilder()
            .types(PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.BLOCK_PLACE)
            .build();

    private final Plugin plugin;

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
                handle(player, Input.LEFT, () -> event.setCancelled(true));
            }
        }
        if (type == PacketType.Play.Client.BLOCK_PLACE) {
            if (packet.getHands().read(0) == EnumWrappers.Hand.MAIN_HAND) {
                handle(player, Input.RIGHT, () -> event.setCancelled(true));
            }
        }
    }
}
