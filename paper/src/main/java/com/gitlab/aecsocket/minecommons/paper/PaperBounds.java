package com.gitlab.aecsocket.minecommons.paper;

import com.gitlab.aecsocket.minecommons.core.bounds.Bound;
import com.gitlab.aecsocket.minecommons.core.bounds.Box;
import com.gitlab.aecsocket.minecommons.core.bounds.Compound;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import net.minecraft.server.v1_16_R3.AxisAlignedBB;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.VoxelShape;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public final class PaperBounds {
    private PaperBounds() {}

    public static Bound from(VoxelShape shape) {
        List<Bound> bounds = new ArrayList<>();
        for (AxisAlignedBB bb : shape.d()) {
            bounds.add(Box.box(
                    new Vector3(bb.minX, bb.minY, bb.minZ),
                    new Vector3(bb.maxX, bb.maxY, bb.maxZ)
            ));
        }
        return new Compound(bounds.toArray(new Bound[0]));
    }

    public static Bound from(Block block) {
        VoxelShape shape = ((CraftBlock) block).getNMS().getShape(
                ((CraftChunk) block.getChunk()).getHandle(),
                new BlockPosition(block.getX(), block.getY(), block.getZ())
        );
        return from(shape);
    }

    public static Bound from(Entity entity) {
        BoundingBox box = entity.getBoundingBox();
        Location location = entity.getLocation();
        double x = location.getX(), y = location.getY(), z = location.getZ();
        return Box.box(
                new Vector3(box.getMinX() - x, box.getMinY() - y, box.getMinZ() - z),
                new Vector3(box.getMaxX() - x, box.getMaxY() - y, box.getMaxZ() - z),
                location.getYaw()
        );
    }
}
