package com.github.aecsocket.minecommons.paper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import static com.github.aecsocket.minecommons.core.bounds.Box.*;
import static com.github.aecsocket.minecommons.core.vector.cartesian.Vector3.*;

import java.util.ArrayList;
import java.util.List;

import com.github.aecsocket.minecommons.core.bounds.Bound;
import com.github.aecsocket.minecommons.core.bounds.Compound;

/**
 * Utility for getting {@link Bound}s from Paper entities and blocks.
 */
public final class PaperBounds {
    private PaperBounds() {}

    /**
     * Gets a bound from a Minecraft voxel shape.
     * @param shape The shape.
     * @return The bound.
     */
    public static Bound from(VoxelShape shape) {
        List<Bound> bounds = new ArrayList<>();
        for (AABB bb : shape.toAabbs()) {
            bounds.add(box(
                vec3(bb.minX, bb.minY, bb.minZ),
                vec3(bb.maxX, bb.maxY, bb.maxZ)
            ));
        }
        return new Compound(bounds.toArray(new Bound[0]));
    }

    /**
     * Gets a bound from a block.
     * @param block The block.
     * @return The bound.
     */
    public static Bound from(Block block) {
        VoxelShape shape = ((CraftBlock) block).getNMS().getShape(
            ((CraftChunk) block.getChunk()).getHandle(),
            new BlockPos(block.getX(), block.getY(), block.getZ())
        );
        return from(shape);
    }

    /**
     * Gets a bound from an entity. Handles rotation.
     * @param entity The entity.
     * @return The bound.
     */
    public static Bound from(Entity entity) {
        BoundingBox box = entity.getBoundingBox();
        Location location = entity.getLocation();
        double x = location.getX(), y = location.getY(), z = location.getZ();
        return box(
            vec3(box.getMinX() - x, box.getMinY() - y, box.getMinZ() - z),
            vec3(box.getMaxX() - x, box.getMaxY() - y, box.getMaxZ() - z),
            location.getYaw()
        );
    }
}
