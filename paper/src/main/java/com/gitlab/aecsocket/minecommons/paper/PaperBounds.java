package com.gitlab.aecsocket.minecommons.paper;

import com.gitlab.aecsocket.minecommons.core.bounds.Bound;
import com.gitlab.aecsocket.minecommons.core.bounds.Compound;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import static com.gitlab.aecsocket.minecommons.core.bounds.Box.*;

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
        for (AxisAlignedBB bb : shape.toList()) {
            bounds.add(box(
                    new Vector3(bb.a, bb.b, bb.c),
                    new Vector3(bb.d, bb.e, bb.f)
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
                new BlockPosition(block.getX(), block.getY(), block.getZ())
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
                new Vector3(box.getMinX() - x, box.getMinY() - y, box.getMinZ() - z),
                new Vector3(box.getMaxX() - x, box.getMaxY() - y, box.getMaxZ() - z),
                location.getYaw()
        );
    }
}
