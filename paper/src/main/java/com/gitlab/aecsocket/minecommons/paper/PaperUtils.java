package com.gitlab.aecsocket.minecommons.paper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

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
}
