package com.gitlab.aecsocket.minecommons.core.raycast;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Provides raycasting in a 3D space, colliding with defined boundables.
 * @param <B> The boundable type.
 */
public abstract class Raycast<B extends Boundable> {
    /** The distance that is travelled by a ray after a successful collision, to penetrate the surface. */
    public static final double EPSILON = 0.01;

    /**
     * A collision result.
     * @param <B> The boundable type.
     * @param position The hit position.
     * @param out The position that the ray exited.
     * @param distance The distance travelled in total, including through the surface.
     * @param distanceThrough The distance travelled through the hit surface.
     * @param hit The hit boundable.
     */
    public record Result<B extends Boundable>(
            Vector3 position, @Nullable Vector3 out,
            double distance, double distanceThrough,
            @Nullable B hit
    ) {}

    private double epsilon;

    /**
     * Creates an instance.
     * @param epsilon The collision epsilon.
     */
    public Raycast(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Creates an instance.
     */
    public Raycast() {
        this(EPSILON);
    }

    /**
     * Gets the collision epsilon.
     * @return The value.
     */
    public double epsilon() { return epsilon; }

    /**
     * Sets the collision epsilon.
     * @param epsilon The value.
     */
    public void epsilon(double epsilon) { this.epsilon = epsilon; }

    /**
     * Gets all boundables defined at the start of a raycast.
     * @param origin The start location.
     * @param direction The ray direction.
     * @param maxDistance The max distance the ray will travel.
     * @return The boundables.
     */
    protected abstract List<? extends B> baseObjects(Vector3 origin, Vector3 direction, double maxDistance);

    /**
     * Gets all boundables at the current position of a raycast.
     * @param point The position of the raycast.
     * @return The boundables.
     */
    protected abstract List<? extends B> currentObjects(Vector3 point);

    /**
     * Casts a ray.
     * @param origin The start location.
     * @param direction The ray direction.
     * @param maxDistance The max distance the ray will travel.
     * @param test The test which determines if an object is eligible to be intersected.
     * @return The cast result.
     */
    public Result<B> cast(Vector3 origin, Vector3 direction, double maxDistance, @Nullable Predicate<B> test) {
        List<? extends B> objects = baseObjects(origin, direction, maxDistance);
        Vector3 current = origin;
        Vector3 step = direction.multiply(epsilon);

        Vector3 in = null;
        B hit = null;
        double hitTravelled = 0;
        for (double travelled = 0; travelled < maxDistance; travelled += epsilon) {
            B intersected = null;
            List<B> currentObjects = new ArrayList<>(objects);
            currentObjects.addAll(currentObjects(current));

            for (B object : currentObjects) {
                if ((test == null || test.test(object)) && object.bound().intersects(current.subtract(object.origin()))) {
                    if (hit == null) {
                        in = current;
                        hit = object;
                        hitTravelled = travelled;
                    }
                    intersected = object;
                    break;
                }
            }
            if (hit != null && !hit.equals(intersected))
                return new Result<>(in, current, travelled, travelled - hitTravelled, hit);

            current = current.add(step);
        }
        return new Result<>(current, null, -1, -1, null);
    }
}
