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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.function.Predicate;

public class PaperRaycast extends Raycast<PaperRaycast.PaperBoundable> {
    public static final class Builder {
        private double epsilon = EPSILON;
        private final Map<Material, List<Bounds<Block>>> blockBounds = new HashMap<>();
        private final Map<EntityType, List<Bounds<Entity>>> entityBounds = new HashMap<>();

        private Builder() {}

        public double epsilon() { return epsilon; }
        public Builder epsilon(double epsilon) { this.epsilon = epsilon; return this; }

        public Map<Material, List<Bounds<Block>>> blockBounds() { return blockBounds; }
        public Map<EntityType, List<Bounds<Entity>>> entityBounds() { return entityBounds; }

        public Builder blockBound(Material material, Predicate<Block> test, Map<String, Bound> bounds) {
            blockBounds.computeIfAbsent(material, k -> new ArrayList<>()).add(new Bounds<>(test, bounds));
            return this;
        }

        public Builder entityBound(EntityType type, Predicate<Entity> test, Map<String, Bound> bounds) {
            entityBounds.computeIfAbsent(type, k -> new ArrayList<>()).add(new Bounds<>(test, bounds));
            return this;
        }

        public CollectionBuilder.OfMap<String, Bound> boundsBuilder() {
            return CollectionBuilder.map(new HashMap<>());
        }

        public PaperRaycast build() {
            Validation.greaterThan("epsilon", epsilon, 0);
            return new PaperRaycast(epsilon, blockBounds, entityBounds);
        }
    }

    public static Builder builder() { return new Builder(); }

    public record PaperBoundable(Block block, Entity entity, Vector3 origin, String name, Bound bound) implements Boundable {
        public static PaperBoundable of(Block block, String name, Bound bound) {
            return new PaperBoundable(block, null, PaperUtils.toCommons(block.getLocation()), name, bound);
        }
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

    public record Bounds<T>(Predicate<T> test, Map<String, Bound> bounds) {}

    private final Map<Material, List<Bounds<Block>>> blockBounds;
    private final Map<EntityType, List<Bounds<Entity>>> entityBounds;
    private World world;

    public PaperRaycast(double epsilon, Map<Material, List<Bounds<Block>>> blockBounds, Map<EntityType, List<Bounds<Entity>>> entityBounds) {
        super(epsilon);
        this.blockBounds = blockBounds;
        this.entityBounds = entityBounds;
    }

    public PaperRaycast(Map<Material, List<Bounds<Block>>> blockBounds, Map<EntityType, List<Bounds<Entity>>> entityBounds) {
        this.blockBounds = blockBounds;
        this.entityBounds = entityBounds;
    }

    public Map<Material, List<Bounds<Block>>> blockBounds() { return blockBounds; }
    public Map<EntityType, List<Bounds<Entity>>> entityBounds() { return entityBounds; }

    public World world() { return world; }
    public PaperRaycast world(World world) { this.world = world; return this; }

    public List<PaperBoundable> boundables(Block block) {
        if (block.getType() == Material.AIR)
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
        return null;
    }

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
        return null;
    }

    @Override
    protected List<? extends PaperBoundable> baseObjects(Vector3 origin, Vector3 direction, double maxDistance) {
        return Collections.emptyList();
    }

    @Override
    protected List<? extends PaperBoundable> currentObjects(Vector3 point) {
        List<PaperBoundable> boundables = new ArrayList<>(boundables(world.getBlockAt((int) point.x(), (int) point.y(), (int) point.z())));
        for (Entity entity : world.getChunkAt((int) point.x() / 16, (int) point.z() / 16).getEntities()) {
            boundables.addAll(boundables(entity));
        }
        return boundables;
    }
}
