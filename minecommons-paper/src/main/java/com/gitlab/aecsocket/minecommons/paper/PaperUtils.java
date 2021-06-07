package com.gitlab.aecsocket.minecommons.paper;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Utilities concerning the Paper platform.
 */
public final class PaperUtils {
    private PaperUtils() {}

    private static final double rayTraceDistance = 4;

    /**
     * Checks if an item is null or is of type {@link Material#AIR}.
     * @param item The item.
     * @return The result.
     */
    public static boolean empty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Checks if a block is null or is of type {@link Material#AIR}.
     * @param block The block.
     * @return The result.
     */
    public static boolean empty(Block block) {
        return block == null || block.getType() == Material.AIR;
    }

    /**
     * Gets the exact target point that a player is looking at.
     * @param player The player.
     * @return The location.
     */
    public static Location target(Player player) {
        RayTraceResult result = player.rayTraceBlocks(rayTraceDistance);
        if (result != null)
            return result.getHitPosition().toLocation(player.getWorld());
        Location location = player.getEyeLocation();
        return location.add(location.getDirection().multiply(rayTraceDistance));
    }

    /**
     * Converts a commons vector to a Bukkit vector.
     * @param vector The original.
     * @return The result.
     */
    public static Vector toBukkit(Vector3 vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    /**
     * Converts a commons vector to a Bukkit location.
     * @param vector The original.
     * @param world The world used for the location.
     * @return The result.
     */
    public static Location toBukkit(Vector3 vector, World world) {
        return new Location(world, vector.x(), vector.y(), vector.z());
    }

    /**
     * Converts a Bukkit vector to a commons vector.
     * @param vector The original.
     * @return The result.
     */
    public static Vector3 toCommons(Vector vector) {
        return new Vector3(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Converts a Bukkit location to a commons vector.
     * @param location The original.
     * @return The result.
     */
    public static Vector3 toCommons(Location location) {
        return new Vector3(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Gets all players within a radius of a location.
     * @param location The location/center of radius.
     * @param radius The radius.
     * @return The players.
     */
    public static Collection<Player> players(Location location, double radius) {
        double sqrRadius = radius * radius;
        World world = location.getWorld();
        Collection<Player> result = new HashSet<>();
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= sqrRadius) {
                result.add(player);
            }
        }
        return result;
    }
}
