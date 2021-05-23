package com.gitlab.aecsocket.minecommons.paper.plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.netty.WirePacket;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.gitlab.aecsocket.minecommons.core.Logging;
import com.gitlab.aecsocket.minecommons.core.serializers.Serializers;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Utility class for managing packets using a ProtocolLib {@link ProtocolManager}.
 */
public final class ProtocolLibAPI {
    private final BasePlugin<?> plugin;
    private final ProtocolManager manager;

    public ProtocolLibAPI(BasePlugin<?> plugin, ProtocolManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public static ProtocolLibAPI create(BasePlugin<?> plugin) {
        return new ProtocolLibAPI(plugin, ProtocolLibrary.getProtocolManager());
    }

    public BasePlugin<?> plugin() { return plugin; }
    public ProtocolManager manager() { return manager; }

    /**
     * Builds a packet using a function.
     * @param type The packet type.
     * @param builder The builder.
     * @return The packet.
     */
    public PacketContainer build(PacketType type, Consumer<PacketContainer> builder) {
        PacketContainer packet = manager.createPacket(type);
        builder.accept(packet);
        return packet;
    }

    /**
     * Gets the players to send a packet to, if modifying a target player's state.
     * @param target The target player.
     * @return The players.
     */
    public Set<Player> packetTargets(Player target) {
        Set<Player> result = target.getTrackedPlayers();
        result.add(target);
        return result;
    }

    /**
     * Sends a packet, catching exceptions and forwarding them to {@link BasePlugin#log(Logging.Level, Throwable, String, Object...)}.
     * @param player The player to send to.
     * @param packet The packet to send.
     * @param wire If the packet should be converted to a {@link WirePacket} and sent instantly (not waiting for the 50ms packet loop).
     */
    public void send(Player player, PacketContainer packet, boolean wire) {
        try {
            if (wire) {
                manager.sendWirePacket(player, WirePacket.fromPacket(packet));
            } else {
                manager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            plugin.log(Logging.Level.WARNING, e, "Could not send packet to %s (%s)", player.getName(), player.getUniqueId());
        }
    }

    /**
     * Sends a non-wire packet, catching exceptions and forwarding them to {@link BasePlugin#log(Logging.Level, Throwable, String, Object...)}.
     * @param player The player to send to.
     * @param packet The packet to send.
     */
    public void send(Player player, PacketContainer packet) {
        send(player, packet, false);
    }

    /**
     * Builds and sends a packet, catching exceptions and forwarding them to {@link BasePlugin#log(Logging.Level, Throwable, String, Object...)}.
     * @param player The player to send to.
     * @param type The packet type to create.
     * @param builder The packet builder.
     * @param wire If the packet should be converted to a {@link WirePacket} and sent instantly (not waiting for the 50ms packet loop).
     */
    public void send(Player player, PacketType type, Consumer<PacketContainer> builder, boolean wire) {
        send(player, build(type, builder), wire);
    }

    /**
     * Builds and sends a non-wire packet, catching exceptions and forwarding them to {@link BasePlugin#log(Logging.Level, Throwable, String, Object...)}.
     * @param player The player to send to.
     * @param type The packet type to create.
     * @param builder The packet builder.
     */
    public void send(Player player, PacketType type, Consumer<PacketContainer> builder) {
        send(player, type, builder, false);
    }

    /**
     * Builds a list of {@link WrappedWatchableObject}s in a builder pattern.
     */
    public static final class WatcherObjectsBuilder {
        private final List<WrappedWatchableObject> watchables = new ArrayList<>();

        /**
         * Adds a {@link WrappedWatchableObject} to the watchable objects.
         * @param index The watcher object index.
         * @param serializer The serializer for the value.
         * @param value The value.
         * @return This instance.
         */
        public WatcherObjectsBuilder add(int index, WrappedDataWatcher.Serializer serializer, Object value) {
            watchables.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(index, serializer), value));
            return this;
        }

        /**
         * Adds a {@link WrappedWatchableObject} to the watchable objects.
         * @param index The watcher object index.
         * @param type The type of serializer for the value.
         * @param value The value.
         * @return This instance.
         */
        public WatcherObjectsBuilder add(int index, Class<?> type, Object value) {
            return add(index, WrappedDataWatcher.Registry.get(type), value);
        }

        /**
         * Gets the list of watchables.
         * @return The watchables.
         */
        public List<WrappedWatchableObject> get() { return watchables; }
    }

    /**
     * Gets a {@link WrappedWatchableObject} list builder, which can be used in a {@link PacketContainer#getWatchableCollectionModifier()}.
     * @return The builder.
     */
    public static WatcherObjectsBuilder watcherObjects() { return new WatcherObjectsBuilder(); }
}
