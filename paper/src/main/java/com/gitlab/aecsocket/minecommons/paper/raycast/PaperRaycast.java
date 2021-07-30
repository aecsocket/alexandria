package com.gitlab.aecsocket.minecommons.paper.raycast;

import com.gitlab.aecsocket.minecommons.core.CollectionBuilder;
import com.gitlab.aecsocket.minecommons.core.bounds.Bound;
import com.gitlab.aecsocket.minecommons.core.bounds.OrientedBound;
import com.gitlab.aecsocket.minecommons.core.raycast.Boundable;
import com.gitlab.aecsocket.minecommons.core.raycast.Raycast;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Point3;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.gitlab.aecsocket.minecommons.paper.PaperBounds;
import com.gitlab.aecsocket.minecommons.paper.PaperUtils;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.phys.AxisAlignedBB;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;


/**
 * A raycast provider for Paper blocks and entities.
 */
public class PaperRaycast extends Raycast<PaperRaycast.PaperBoundable> {
    /**
     * A raycast provider builder.
     */
    public static final class Builder {
        private boolean ignorePassable = true;
        private double entityLenience = 8;
        private final Map<Material, List<Bounds<Block>>> blockBounds = new HashMap<>();
        private final Map<EntityType, List<Bounds<Entity>>> entityBounds = new HashMap<>();

        private Builder() {}

        /**
         * Gets if passable blocks should be ignored.
         * @return The value.
         */
        public boolean ignorePassable() { return ignorePassable; }

        /**
         * Sets if passable blocks should be ignored.
         * @param ignorePassable The value.
         * @return This instance.
         */
        public Builder ignorePassable(boolean ignorePassable) { this.ignorePassable = ignorePassable; return this; }

        /**
         * Gets the amount of blocks that the search hitbox for entities will be expanded, to account for
         * larger than vanilla hitboxes.
         * @return The lenience.
         */
        public double entityLenience() { return entityLenience; }

        /**
         * Sets the amount of blocks that the search hitbox for entities will be expanded, to account for
         * larger than vanilla hitboxes.
         * @param entityLenience The lenience.
         * @return This instance.
         */
        public Builder entityLenience(double entityLenience) { this.entityLenience = entityLenience; return this; }

        /**
         * Gets all registered bounds for block states.
         * @return The registrations.
         */
        public Map<Material, List<Bounds<Block>>> blockBounds() { return blockBounds; }

        /**
         * Gets all registered bounds for entity states.
         * @return The registrations.
         */
        public Map<EntityType, List<Bounds<Entity>>> entityBounds() { return entityBounds; }

        /**
         * Adds block bounds.
         * @param material The type of block.
         * @param test The test for these bounds to apply.
         * @param bounds The bounds.
         * @return This instance.
         */
        public Builder blockBound(Material material, Predicate<Block> test, Map<String, Bound> bounds) {
            blockBounds.computeIfAbsent(material, k -> new ArrayList<>()).add(new Bounds<>(test, bounds));
            return this;
        }

        /**
         * Adds entity bounds.
         * @param type The type of entity.
         * @param test The test for these bounds to apply.
         * @param bounds The bounds.
         * @return This instance.
         */
        public Builder entityBound(EntityType type, Predicate<Entity> test, Map<String, Bound> bounds) {
            entityBounds.computeIfAbsent(type, k -> new ArrayList<>()).add(new Bounds<>(test, bounds));
            return this;
        }

        /**
         * Creates a bounds builder.
         * @return The builder.
         */
        public CollectionBuilder.OfMap<String, Bound> boundsBuilder() {
            return CollectionBuilder.map(new HashMap<>());
        }

        /**
         * Builds a Paper raycast provider.
         * @param world The world to raycast pos.
         * @return The raycast provider.
         */
        public PaperRaycast build(World world) {
            return new PaperRaycast(ignorePassable, entityLenience, blockBounds, entityBounds, world);
        }
    }

    /**
     * Creates a new builder.
     * @return The builder.
     */
    public static Builder builder() { return new Builder(); }

    /**
     * A boundable which has a Paper handle.
     * @param block The underlying block.
     * @param entity The underlying entity.
     * @param origin The handle location.
     * @param name The name of the bound.
     * @param bound The bound.
     */
    public record PaperBoundable(@Nullable Block block, @Nullable Entity entity, Vector3 origin, String name, Bound bound) implements Boundable {
        /**
         * Creates a boundable of a block.
         * @param block The underlying block.
         * @param name The name of the bound.
         * @param bound The bound.
         * @return The boundable.
         */
        public static PaperBoundable of(Block block, String name, Bound bound) {
            return new PaperBoundable(block, null, PaperUtils.toCommons(block.getLocation()), name, bound);
        }

        /**
         * Creates a boundable of an entity.
         * @param entity The underlying entity.
         * @param name The name of the bound.
         * @param bound The bound.
         * @return The boundable.
         */
        public static PaperBoundable of(Entity entity, String name, Bound bound) {
            return new PaperBoundable(null, entity, PaperUtils.toCommons(entity.getLocation()), name, bound);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PaperBoundable that = (PaperBoundable) o;
            if ((entity == null) != (that.entity == null) || (block == null) != (that.block == null))
                return false;
            if (
                    (entity != null && entity.getEntityId() != that.entity.getEntityId())
                    || (block != null && !block.getLocation().equals(that.block.getLocation()))
            )
                return false;
            return origin.equals(that.origin) && name.equals(that.name) && bound.equals(that.bound);
        }

        @Override
        public int hashCode() {
            return Objects.hash(block, entity, origin, name, bound);
        }
    }

    /**
     * A collection of bounds, which are applied on a successful test.
     * @param <T> The type of object to test on.
     * @param test The test.
     * @param bounds The bounds.
     */
    public record Bounds<T>(Predicate<T> test, Map<String, Bound> bounds) {}

    private final boolean ignorePassable;
    private final double entityLenience;
    private final Map<Material, List<Bounds<Block>>> blockBounds;
    private final Map<EntityType, List<Bounds<Entity>>> entityBounds;
    private World world;

    /**
     * Creates an instance.
     * @param ignorePassable If passable blocks should be ignored.
     * @param entityLenience The amount of blocks that the search hitbox for entities will be expanded, to account for
     *                       larger than vanilla hitboxes.
     * @param blockBounds The registered block bounds.
     * @param entityBounds The registered entity bounds.
     * @param world The world this will cast pos.
     */
    public PaperRaycast(boolean ignorePassable, double entityLenience, Map<Material, List<Bounds<Block>>> blockBounds, Map<EntityType, List<Bounds<Entity>>> entityBounds, World world) {
        this.ignorePassable = ignorePassable;
        this.entityLenience = entityLenience;
        this.blockBounds = blockBounds;
        this.entityBounds = entityBounds;
        this.world = world;
    }

    /**
     * Gets if passable blocks should be ignored.
     * @return The value.
     */
    public boolean ignorePassable() { return ignorePassable; }

    /**
     * Gets the amount of blocks that the search hitbox for entities will be expanded, to account for
     * larger than vanilla hitboxes.
     * @return The lenience.
     */
    public double entityLenience() { return entityLenience; }

    /**
     * Gets the registered block bounds.
     * @return The registrations.
     */
    public Map<Material, List<Bounds<Block>>> blockBounds() { return blockBounds; }

    /**
     * Gets the registered entity bounds.
     * @return The registrations.
     */
    public Map<EntityType, List<Bounds<Entity>>> entityBounds() { return entityBounds; }

    /**
     * Gets the world this raycast provider operates pos.
     * @return The world.
     */
    public World world() { return world; }

    /**
     * Sets the world this raycast provider operates pos.
     * @param world The world.
     * @return This instance.
     */
    public PaperRaycast world(World world) { this.world = world; return this; }

    /**
     * Gets boundables for a block.
     * @param block The block.
     * @return The boundables.
     */
    public List<PaperBoundable> boundables(Block block) {
        if (block.getType() == Material.AIR || (ignorePassable && block.isPassable()))
            return Collections.emptyList();
        List<Bounds<Block>> boundsList = blockBounds.get(block.getType());
        if (boundsList == null) {
            return Collections.singletonList(PaperBoundable.of(block, block.getType().getKey().value(), PaperBounds.from(block)));
        }
        for (Bounds<Block> bounds : boundsList) {
            if (bounds.test.test(block)) {
                List<PaperBoundable> result = new ArrayList<>();
                for (var entry : bounds.bounds.entrySet()) {
                    Bound bound = entry.getValue();
                    result.add(PaperBoundable.of(block, entry.getKey(), bound));
                }
                return result;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Gets boundables for an entity
     * @param entity The entity.
     * @return The boundables.
     */
    public List<PaperBoundable> boundables(Entity entity) {
        List<Bounds<Entity>> boundsList = entityBounds.get(entity.getType());
        if (boundsList == null) {
            return Collections.singletonList(PaperBoundable.of(entity, entity.getType().getKey().value(), PaperBounds.from(entity)));
        }
        for (Bounds<Entity> bounds : boundsList) {
            if (bounds.test.test(entity)) {
                List<PaperBoundable> result = new ArrayList<>();
                double angle = Math.toRadians(entity.getLocation().getYaw());
                for (var entry : bounds.bounds.entrySet()) {
                    Bound bound = entry.getValue();
                    bound = bound instanceof OrientedBound oriented ? oriented.angle(oriented.angle() + angle) : bound;
                    result.add(PaperBoundable.of(entity, entry.getKey(), bound));
                }
                return result;
            }
        }
        return Collections.emptyList();
    }

    private record BoundablePair(Vector3 origin, List<PaperBoundable> boundables) {}

    private int correct(double v) {
        return (int) (v < 0 ? v - 1 : v);
    }

    private void add(Block block, List<PaperBoundable> result, Set<Point3> blocks, Set<Chunk> chunks) {
        if (block.getType() == Material.AIR)
            return;
        Point3 coord = Point3.point3(block.getX(), block.getY(), block.getZ());
        if (!blocks.contains(coord)) {
            result.addAll(boundables(block));
            blocks.add(coord);
            chunks.add(block.getChunk());
        }
    }

    private double frac(double v) {
        long l = (long) v;
        return v - (double) (v < l ? l - 1 : l);
    }

    private int floor(double v) {
        int i = (int) v;
        return v < i ? i - 1 : i;
    }

    /**
     * Tests for intersections between the provided ray, and the blocks in its path.
     * @param ray The ray.
     * @param maxDistance The max distance the ray will travel.
     * @param test The test which determines if an object is eligible to be intersected.
     * @return The cast result.
     */
    public Result<PaperBoundable> castBlocks(Ray3 ray, double maxDistance, @Nullable Predicate<PaperBoundable> test) {
        Vector3 orig = ray.orig();
        Vector3 end = ray.point(maxDistance);

        double x0 = orig.x(), x1 = end.x();
        double y0 = orig.y(), y1 = end.y();
        double z0 = orig.z(), z1 = end.z();

        int xi = floor(x0), yi = floor(y0), zi = floor(z0);

        double dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        int xs = (int) Math.signum(dx), ys = (int) Math.signum(dy), zs = (int) Math.signum(dz);
        double xa = xs == 0 ? Double.MAX_VALUE : (double) xs / dx;
        double ya = ys == 0 ? Double.MAX_VALUE : (double) ys / dy;
        double za = zs == 0 ? Double.MAX_VALUE : (double) zs / dz;

        double xb = xa * (xs > 0 ? 1 - frac(x0) : frac(x0));
        double yb = ya * (ys > 0 ? 1 - frac(y0) : frac(y0));
        double zb = za * (zs > 0 ? 1 - frac(z0) : frac(z0));

        Result<PaperBoundable> result;
        while ((result = intersects(ray, boundables(world.getBlockAt(xi, yi, zi)), test)) == null) {
            if (xb > 1 && yb > 1 && zb > 1)
                break;
            if (!new Location(world, xb, yb, zb).isChunkLoaded())
                break;

            if (xb < yb) {
                if (xb < zb) {
                    xi += xs;
                    xb += xa;
                } else {
                    zi += zs;
                    zb += za;
                }
            } else if (yb < zb) {
                yi += ys;
                yb += ya;
            } else {
                zi += zs;
                zb += za;
            }
        }

        return result == null
                ? miss(ray, maxDistance, ray.point(maxDistance))
                : result;
    }

    /**
     * Tests for intersections between the provided ray, and the entities in its path.
     * @param ray The ray.
     * @param maxDistance The max distance the ray will travel.
     * @param test The test which determines if an object is eligible to be intersected.
     * @return The cast result.
     */
    public Result<PaperBoundable> castEntities(Ray3 ray, double maxDistance, @Nullable Predicate<PaperBoundable> test) {
        Vector3 orig = ray.orig();
        Vector3 end = ray.point(maxDistance);
        WorldServer world = ((CraftWorld) this.world).getHandle();

        var nearestResult = new AtomicReference<Raycast.Result<PaperBoundable>>();
        var nearestDist = new AtomicDouble();
        Vector3 min = Vector3.min(orig, end);
        Vector3 max = Vector3.max(orig, end);
        world.getEntities().a(new AxisAlignedBB(
                min.x() - entityLenience, min.y() - entityLenience, min.z() - entityLenience,
                max.x() + entityLenience, max.y() + entityLenience, max.z() + entityLenience
        ), ent -> {
            Entity entity = ent.getBukkitEntity();
            var result = intersects(ray, boundables(entity), test);
            if (result != null && (nearestResult.get() == null || result.distance() < nearestDist.get())) {
                nearestResult.set(result);
                nearestDist.set(result.distance());
            }
        });
        return nearestResult.get() == null
                ? miss(ray, maxDistance, ray.point(maxDistance))
                : nearestResult.get();
    }

    @Override
    public Result<PaperBoundable> cast(Ray3 ray, double maxDistance, @Nullable Predicate<PaperBoundable> test) {
        var block = castBlocks(ray, maxDistance, test);
        var entity = castEntities(ray, maxDistance, test);
        return block.distance() < entity.distance() ? block : entity;
    }
}
