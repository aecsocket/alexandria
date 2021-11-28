package com.gitlab.aecsocket.minecommons.paper;

import com.gitlab.aecsocket.minecommons.core.CollectionBuilder;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3.*;

/**
 * Utilities concerning the Paper platform.
 */
public final class PaperUtils {
    private PaperUtils() {}

    /**
     * A map of block faces to their normal vectors.
     */
    public static final Map<BlockFace, Vector3> NORMALS = CollectionBuilder.map(new HashMap<BlockFace, Vector3>())
            .put(BlockFace.UP, vec3(0, 1, 0)).put(BlockFace.DOWN, vec3(0, -1, 0))
            .put(BlockFace.NORTH, vec3(0, 0, -1)).put(BlockFace.SOUTH, vec3(0, 0, 1))
            .put(BlockFace.EAST, vec3(-1, 0, 0)).put(BlockFace.WEST, vec3(1, 0, 0))
            .build();

    private static final double rayTraceDistance = 4;

    /**
     * Checks if an item is null or is of type {@link Material#AIR}.
     * @param item The item.
     * @return The result.
     */
    public static boolean empty(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Checks if a block is null or is of type {@link Material#AIR}.
     * @param block The block.
     * @return The result.
     */
    public static boolean empty(@Nullable Block block) {
        return block == null || block.getType() == Material.AIR;
    }

    /**
     * Converts a null item to an air ItemStack, otherwise returns the item passed.
     * @param item The item.
     * @return The result.
     */
    public static ItemStack normalize(@Nullable ItemStack item) {
        return item == null ? new ItemStack(Material.AIR) : item;
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
     * Converts a commons vector to a Paper vector.
     * @param vector The original.
     * @return The result.
     */
    public static Vector toPaper(Vector3 vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    /**
     * Converts a commons vector to a Paper location.
     * @param vector The original.
     * @param world The world used for the location.
     * @return The result.
     */
    public static Location toPaper(Vector3 vector, World world) {
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

    /**
     * Modifies an item stack's metadata.
     * @param item The item to modify.
     * @param function The function to apply.
     * @return The passed (and modified) item.
     */
    public static ItemStack modify(ItemStack item, Consumer<ItemMeta> function) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
            function.accept(meta);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Adds lines of lore to a specified item meta, creating a lore list if it does not already exist,
     * otherwise appending to existing lore.
     * @param meta The meta.
     * @param add The lines to add.
     */
    public static void addLore(ItemMeta meta, Collection<Component> add) {
        List<Component> lore = meta.lore();
        lore = lore == null ? new ArrayList<>() : lore;
        lore.addAll(add);
        meta.lore(lore);
    }

    /**
     * Adds lines of lore to a specified item meta, creating a lore list if it does not already exist,
     * otherwise appending to existing lore.
     * @param meta The meta.
     * @param add The lines to add.
     */
    public static void addLore(ItemMeta meta, Component... add) {
        List<Component> lore = meta.lore();
        lore = lore == null ? new ArrayList<>() : lore;
        lore.addAll(Arrays.asList(add));
        meta.lore(lore);
    }

    /**
     * Gets a normal vector from a block face.
     * @param face The face.
     * @return The vector.
     */
    public static Vector3 normal(BlockFace face) {
        Vector3 result = NORMALS.get(face);
        if (result == null)
            throw new IllegalArgumentException("Invalid block face: accepts " + NORMALS.keySet());
        return result;
    }
}
