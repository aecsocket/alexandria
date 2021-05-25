package com.gitlab.aecsocket.minecommons.paper;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Utilities concerning the Paper platform.
 */
public final class PaperUtils {
    private PaperUtils() {}

    /**
     * Checks if an item is null or is of type {@link Material#AIR}.
     * @param item The item.
     * @return The result.
     */
    public static boolean empty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
