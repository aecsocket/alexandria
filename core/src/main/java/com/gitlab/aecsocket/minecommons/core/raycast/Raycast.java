package com.gitlab.aecsocket.minecommons.core.raycast;

import com.gitlab.aecsocket.minecommons.core.bounds.Bound;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

import static com.gitlab.aecsocket.minecommons.core.vector.cartesian.Ray3.*;

/**
 * Provides raycasting in a 3D space, colliding with defined boundables.
 * @param <B> The boundable type.
 */
public abstract class Raycast<B extends Boundable> {
    /** The distance that is travelled by a ray to check for collisions. */
    public static final double EPSILON = 0.01;

    /**
     * A collision result.
     * @param <B> The boundable type.
     * @param ray The ray used to get this result.
     * @param distance The distance travelled from the origin to the in-position.
     * @param in Either the final position, or the position that the ray entered a bound.
     * @param out The position that the ray exited a bound, or null if no bound was hit.
     * @param penetration The distance travelled through the bound, or -1 if no bound was hit.
     * @param hit The bound that was hit, or null if no bound was hit.
     */
    public record Result<B extends Boundable>(
            Ray3 ray,
            double distance,
            Vector3 in,
            @Nullable Vector3 out,
            double penetration,
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
     * @return The collection of boundables.
     */
    protected abstract Collection<? extends B> objects(Vector3 origin, Vector3 direction, double maxDistance);

    /**
     * Casts a ray.
     * @param origin The start location.
     * @param direction The ray direction.
     * @param maxDistance The max distance the ray will travel.
     * @param test The test which determines if an object is eligible to be intersected.
     * @return The cast result.
     */
    public Result<B> cast(Vector3 origin, Vector3 direction, double maxDistance, @Nullable Predicate<B> test) {
        Collection<? extends B> objects = objects(origin, direction, maxDistance);
        Ray3 origRay = ray3(origin, direction);
        Result<B> nearestResult = null;
        for (B object : objects) {
            if (test != null && !test.test(object))
                continue;
            Vector3 objectOrig = object.origin();
            Ray3 ray = origRay.at(origin.subtract(objectOrig));
            Bound.Collision collision = object.bound().collision(ray);
            if (collision != null) {
                if (nearestResult == null || collision.in() < nearestResult.distance) {
                    nearestResult = new Result<>(origRay, collision.in(),
                            ray.point(collision.in()).add(objectOrig),
                            ray.point(collision.out()).add(objectOrig),
                            collision.out() - collision.in(),
                            object);
                }
            }
        }
        return nearestResult == null || nearestResult.distance > maxDistance
                ? new Result<>(origRay, maxDistance, origin.add(direction.multiply(maxDistance)), null, -1, null)
                : nearestResult;
    }
}
