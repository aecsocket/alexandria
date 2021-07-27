package com.gitlab.aecsocket.minecommons.paper.raycast;

import com.gitlab.aecsocket.minecommons.core.CollectionBuilder;
import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.bounds.Bound;
import com.gitlab.aecsocket.minecommons.core.bounds.OrientedBound;
import com.gitlab.aecsocket.minecommons.core.raycast.Boundable;
import com.gitlab.aecsocket.minecommons.core.raycast.Raycast;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.gitlab.aecsocket.minecommons.paper.PaperBounds;
import com.gitlab.aecsocket.minecommons.paper.PaperUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * A raycast provider for Paper blocks and entities.
 */
public class PaperRaycast extends Raycast<PaperRaycast.PaperBoundable> {
    /**
     * A raycast provider builder.
     */
    public static final class Builder {
        private double epsilon = EPSILON;
        private boolean ignorePassable = true;
        private final Map<Material, List<Bounds<Block>>> blockBounds = new HashMap<>();
        private final Map<EntityType, List<Bounds<Entity>>> entityBounds = new HashMap<>();

        private Builder() {}

        /**
         * Gets the collision epsilon.
         * @return The value.
         */
        public double epsilon() { return epsilon; }

        /**
         * Sets the collision epsilon.
         * @param epsilon The value.
         * @return This instance.
         */
        public Builder epsilon(double epsilon) {
            Validation.greaterThan("epsilon", epsilon, 0);
            this.epsilon = epsilon;
            return this;
        }

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
         * @param world The world to raycast in.
         * @return The raycast provider.
         */
        public PaperRaycast build(World world) {
            return new PaperRaycast(epsilon, ignorePassable, blockBounds, entityBounds, world);
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

    private record Offset(int x, int z) {}

    private static final List<Offset> offsets = Arrays.asList(new Offset(0, 0),
            new Offset(1, 0), new Offset(1, 1), new Offset(0, 1), new Offset(-1, 1),
            new Offset(-1, 0), new Offset(-1, -1), new Offset(0, -1), new Offset(1, -1));

    /**
     * A collection of bounds, which are applied on a successful test.
     * @param <T> The type of object to test on.
     * @param test The test.
     * @param bounds The bounds.
     */
    public record Bounds<T>(Predicate<T> test, Map<String, Bound> bounds) {}

    private boolean ignorePassable;
    private final Map<Material, List<Bounds<Block>>> blockBounds;
    private final Map<EntityType, List<Bounds<Entity>>> entityBounds;
    private World world;

    /**
     * Creates an instance.
     * @param epsilon The collision epsilon.
     * @param ignorePassable If passable blocks should be ignored.
     * @param blockBounds The registered block bounds.
     * @param entityBounds The registered entity bounds.
     * @param world The world this will cast in.
     */
    public PaperRaycast(double epsilon, boolean ignorePassable, Map<Material, List<Bounds<Block>>> blockBounds, Map<EntityType, List<Bounds<Entity>>> entityBounds, World world) {
        super(epsilon);
        this.ignorePassable = ignorePassable;
        this.blockBounds = blockBounds;
        this.entityBounds = entityBounds;
        this.world = world;
    }

    /**
     * Creates an instance.
     * @param ignorePassable If passable blocks should be ignored.
     * @param blockBounds The registered block bounds.
     * @param entityBounds The registered entity bounds.
     * @param world The world this will cast in.
     */
    public PaperRaycast(boolean ignorePassable, Map<Material, List<Bounds<Block>>> blockBounds, Map<EntityType, List<Bounds<Entity>>> entityBounds, World world) {
        this.ignorePassable = ignorePassable;
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
     * Gets the world this raycast provider operates in.
     * @return The world.
     */
    public World world() { return world; }

    /**
     * Sets the world this raycast provider operates in.
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
            return Collections.singletonList(PaperBoundable.of(block, "main", PaperBounds.from(block)));
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
            return Collections.singletonList(PaperBoundable.of(entity, "main", PaperBounds.from(entity)));
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

    @Override
    protected Collection<? extends PaperBoundable> objects(Vector3 origin, Vector3 direction, double maxDistance) {
        List<PaperBoundable> result = new ArrayList<>();
        List<Chunk> chunks = new ArrayList<>();
        Vector3 end = origin.add(direction.multiply(maxDistance));

        int x0 = correct(origin.x()), x1 = correct(end.x());
        int y0 = correct(origin.y()), y1 = correct(end.y());
        int z0 = correct(origin.z()), z1 = correct(end.z());

        int dx = Math.abs(x1-x0), sx = x0 < x1 ? 1 : -1;
        int dy = Math.abs(y1-y0), sy = y0 < y1 ? 1 : -1;
        int dz = Math.abs(z1-z0), sz = z0 < z1 ? 1 : -1;
        int dm = Math.max(dx, Math.max(dy, dz)), i = dm;
        x1 = y1 = z1 = dm/2;

        while (true) {
            if (!world.isChunkLoaded(x0 / 16, z0 / 16))
                break;
            Block block = world.getBlockAt(x0, y0, z0);
            result.addAll(boundables(block));
            chunks.add(block.getChunk());
            if (i-- == 0) break;
            x1 -= dx; if (x1 < 0) { x1 += dm; x0 += sx; }
            y1 -= dy; if (y1 < 0) { y1 += dm; y0 += sy; }
            z1 -= dz; if (z1 < 0) { z1 += dm; z0 += sz; }
        }

        for (Chunk chunk : chunks) {
            for (Entity entity : chunk.getEntities()) {
                result.addAll(boundables(entity));
            }
        }

        return result;
    }
}
