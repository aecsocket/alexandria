package com.github.aecsocket.minecommons.paper.raycast;

import com.github.aecsocket.minecommons.core.Colls;
import com.github.aecsocket.minecommons.core.bounds.Bound;
import com.github.aecsocket.minecommons.core.bounds.Box;
import com.github.aecsocket.minecommons.core.bounds.OrientedBound;
import com.github.aecsocket.minecommons.core.raycast.Boundable;
import com.github.aecsocket.minecommons.core.raycast.Raycast;
import com.github.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.github.aecsocket.minecommons.paper.PaperBounds;
import com.github.aecsocket.minecommons.paper.PaperUtils;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A raycast provider for Paper blocks and entities.
 */
public class PaperRaycast extends Raycast<PaperRaycast.PaperBoundable> {
    /** The bound name for a waterlogged part. */
    public static final String WATERLOGGED = "__waterlogged";
    /** The bound for a waterlogged part. */
    public static final Box WATERLOGGED_BOUND = Box.box(Vector3.ZERO, Vector3.vec3(1));

    /**
     * The options for a raycast.
     * @param block The block options.
     * @param entity The entity options.
     */
    public record Options(
        OfBlock block,
        OfEntity entity
    ) {
        /**
         * The default options.
         */
        public static final Options DEFAULT = new Options(
            new OfBlock(true, true, true, null),
            new OfEntity(null)
        );

        /**
         * Options for raycasting blocks.
         * @param ignoreAir If bound checks with air should be skipped.
         * @param ignorePassable If bound checks with {@link Block#isPassable()} should be skipped.
         * @param doWaterlogging If waterlogged parts of blocks should also be collided with, under the
         *                       bound name {@link #WATERLOGGED}.
         * @param test The specific test for blocks.
         */
        public record OfBlock(
            boolean ignoreAir,
            boolean ignorePassable,
            boolean doWaterlogging,
            @Nullable Predicate<Block> test
        ) {}

        /**
         * Options for raycasting entities.
         * @param test The specific test for entities.
         */
        public record OfEntity(
            @Nullable Predicate<Entity> test
        ) {}
    }

    /**
     * A raycast provider builder.
     */
    public static final class Builder {
        private double entityLenience = 8;
        private final Map<Material, List<Bounds<BlockData>>> blockBounds = new EnumMap<>(Material.class);
        private final Map<EntityType, List<Bounds<Entity>>> entityBounds = new EnumMap<>(EntityType.class);

        private Builder() {}

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
        public Map<Material, List<Bounds<BlockData>>> blockBounds() { return blockBounds; }

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
        public Builder blockBound(Material material, Predicate<BlockData> test, Map<String, Bound> bounds) {
            blockBounds.computeIfAbsent(material, k -> new ArrayList<>()).add(new Bounds<>(test, bounds));
            return this;
        }

        /**
         * Adds block bounds.
         * @param material The type of block.
         * @param test The test for these bounds to apply.
         * @param boundsBuilder The builder for the bounds.
         * @return This instance.
         */
        public Builder blockBound(Material material, Predicate<BlockData> test, Consumer<Colls.OfMap<String, Bound>> boundsBuilder) {
            Map<String, Bound> bounds = new HashMap<>();
            boundsBuilder.accept(Colls.map(bounds));
            blockBound(material, test, bounds);
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
         * Adds entity bounds.
         * @param type The type of entity.
         * @param test The test for these bounds to apply.
         * @param boundsBuilder The builder for the bounds.
         * @return This instance.
         */
        public Builder entityBound(EntityType type, Predicate<Entity> test, Consumer<Colls.OfMap<String, Bound>> boundsBuilder) {
            Map<String, Bound> bounds = new HashMap<>();
            boundsBuilder.accept(Colls.map(bounds));
            entityBound(type, test, bounds);
            return this;
        }

        /**
         * Builds a Paper raycast provider.
         * @param options The per-cast options.
         * @param world The world to raycast from.
         * @return The raycast provider.
         */
        public PaperRaycast build(Options options, World world) {
            return new PaperRaycast(entityLenience, blockBounds, entityBounds, options, world);
        }
    }

    /**
     * Creates a new builder.
     * @return The builder.
     */
    public static Builder builder() { return new Builder(); }

    /**
     * A boundable which has a Paper handle.
     * @param block The underlying block data.
     * @param entity The underlying entity.
     * @param origin The handle location.
     * @param name The name of the bound.
     * @param bound The bound.
     * @param hasWater If the bound has water in it.
     */
    public record PaperBoundable(@Nullable Block block, @Nullable Entity entity, Vector3 origin, String name, Bound bound, boolean hasWater) implements Boundable {
        /**
         * Creates a boundable of a block.
         * @param block The underlying block.
         * @param name The name of the bound.
         * @param bound The bound.
         * @param hasWater If this bound has water.
         * @return The boundable.
         */
        public static PaperBoundable of(Block block, String name, Bound bound, boolean hasWater) {
            return new PaperBoundable(block, null, PaperUtils.toCommons(block.getLocation()), name, bound, hasWater);
        }

        /**
         * Creates a boundable of an entity.
         * @param entity The underlying entity.
         * @param name The name of the bound.
         * @param bound The bound.
         * @param hasWater If this bound has water.
         * @return The boundable.
         */
        public static PaperBoundable of(Entity entity, String name, Bound bound, boolean hasWater) {
            return new PaperBoundable(null, entity, PaperUtils.toCommons(entity.getLocation()), name, bound, hasWater);
        }

        /**
         * Runs a function depending on which of the two hit types are present.
         * @param ifBlock If the block is present.
         * @param ifEntity If the entity is present.
         */
        public void ifPresent(Consumer<Block> ifBlock, Consumer<Entity> ifEntity) {
            if (block != null)
                ifBlock.accept(block);
            else if (entity != null)
                ifEntity.accept(entity);
        }

        /**
         * Maps the hit to a value depending on which of the two hit types are present.
         * @param ifBlock If the block is present.
         * @param ifEntity If the entity is present.
         * @param <U> The function return type.
         * @return The mapped value.
         */
        public <U> U map(Function<Block, U> ifBlock, Function<Entity, U> ifEntity) {
            if (block != null)
                return ifBlock.apply(block);
            else if (entity != null)
                return ifEntity.apply(entity);
            throw new IllegalStateException("neither value present");
        }

        @Override
        public String toString() {
            StringJoiner res = new StringJoiner(":");
            if (block != null)
                res.add(""+block.getType());
            if (entity != null)
                res.add(""+entity.getName());
            return "[" + res + " = " + bound + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PaperBoundable that = (PaperBoundable) o;
            if ((entity == null) != (that.entity == null) || (entity != null && entity.getEntityId() != that.entity.getEntityId())) return false;
            if ((block == null) != (that.block == null) || (block != null && !block.equals(that.block))) return false;
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

    private final double entityLenience;
    private final Map<Material, List<Bounds<BlockData>>> blockBounds;
    private final Map<EntityType, List<Bounds<Entity>>> entityBounds;
    private final Options options;

    private final World world;

    /**
     * Creates an instance.
     * @param entityLenience The amount of blocks that the search hitbox for entities will be expanded, to account for
     *                       larger than vanilla hitboxes.
     * @param blockBounds The registered block bounds.
     * @param entityBounds The registered entity bounds.
     * @param options The per-cast options.
     * @param world The world this will cast pos.
     */
    public PaperRaycast(double entityLenience, Map<Material, List<Bounds<BlockData>>> blockBounds, Map<EntityType, List<Bounds<Entity>>> entityBounds, Options options, World world) {
        this.entityLenience = entityLenience;
        this.blockBounds = blockBounds;
        this.entityBounds = entityBounds;
        this.options = options;
        this.world = world;
    }

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
    public Map<Material, List<Bounds<BlockData>>> blockBounds() { return blockBounds; }

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
     * Gets boundables for a block.
     * @param block The block.
     * @return The boundables.
     */
    public List<PaperBoundable> boundables(Block block) {
        List<PaperBoundable> res = null;
        Vector3 origin = PaperUtils.toCommons(block.getLocation());
        BlockData data = block.getBlockData();
        boolean isWater = block.getType() == Material.WATER;
        var boundsList = blockBounds.get(block.getType());
        if (boundsList == null) {
            res = new ArrayList<>();
            res.add(PaperBoundable.of(block, block.getType().getKey().value(), PaperBounds.from(block), isWater));
        } else {
            for (var bounds : boundsList) {
                if (bounds.test.test(data)) {
                    List<PaperBoundable> thisRes = new ArrayList<>();
                    for (var entry : bounds.bounds.entrySet()) {
                        Bound bound = entry.getValue();
                        thisRes.add(PaperBoundable.of(block, entry.getKey(), bound, isWater));
                    }
                    res = thisRes;
                    break;
                }
            }
            if (res == null)
                res = new ArrayList<>();
        }
        if (options.block.doWaterlogging && data instanceof Waterlogged wl && wl.isWaterlogged()) {
            res.add(PaperBoundable.of(block, WATERLOGGED, WATERLOGGED_BOUND, true));
        }
        return res;
    }

    /**
     * Gets boundables for an entity
     * @param entity The entity.
     * @return The boundables.
     */
    public List<PaperBoundable> boundables(Entity entity) {
        var boundsList = entityBounds.get(entity.getType());
        if (boundsList == null) {
            return Collections.singletonList(PaperBoundable.of(entity, entity.getType().getKey().value(), PaperBounds.from(entity), false));
        }
        for (var bounds : boundsList) {
            if (bounds.test.test(entity)) {
                List<PaperBoundable> result = new ArrayList<>();
                double angle = Math.toRadians(entity.getLocation().getYaw());
                for (var entry : bounds.bounds.entrySet()) {
                    Bound bound = entry.getValue();
                    bound = bound instanceof OrientedBound oriented ? oriented.angle(oriented.angle() + angle) : bound;
                    result.add(PaperBoundable.of(entity, entry.getKey(), bound, false));
                }
                return result;
            }
        }
        return Collections.emptyList();
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

        Result<PaperBoundable> result = null;
        while (true) {
            Block block = world.getBlockAt(xi, yi, zi);
            if (
                (block.getType() != Material.AIR || !options.block.ignoreAir)
                && (!block.isPassable() || !options.block.ignorePassable)
                && (options.block.test == null || options.block.test.test(block))
            ) {
                result = intersects(ray, boundables(block), test);
                if (result != null)
                    break;
            }

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

            if (
                xb > 1 && yb > 1 && zb > 1
                || !new Location(world, xi, yi, zi).isChunkLoaded()
            ) break;
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
        ServerLevel world = ((CraftWorld) this.world).getHandle();

        var nearestResult = new AtomicReference<Raycast.Result<PaperBoundable>>();
        var nearestDist = new AtomicDouble();
        Vector3 min = Vector3.min(orig, end);
        Vector3 max = Vector3.max(orig, end);
        world.getEntities().get(new AABB(
            min.x() - entityLenience, min.y() - entityLenience, min.z() - entityLenience,
            max.x() + entityLenience, max.y() + entityLenience, max.z() + entityLenience
        ), ent -> {
            Entity entity = ent.getBukkitEntity();
            if (options.entity.test != null && !options.entity.test.test(entity))
                return;
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

    /**
     * Gets if some block data is either water, or is waterlogged.
     * @param block The block data.
     * @return The water state.
     */
    public static boolean hasWater(BlockData block) {
        return block.getMaterial() == Material.WATER
            || block instanceof Waterlogged wl && wl.isWaterlogged();
    }
}
