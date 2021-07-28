package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A volume in 3D space.
 */
public interface Bound {
    /**
     * Information on a collision with a ray.
     * @param in The t-value at which the ray enters the bound.
     * @param out The t-value at which the ray exits the bound.
     * @param normal The normal of the surface hit.
     */
    record Collision(double in, double out, Vector3 normal) {}

    /**
     * Gets if a ray collides with this bound.
     * @param ray The ray.
     * @return The collision info.
     */
    @Nullable Collision collision(Ray3 ray);

    /**
     * Translates this bound in some direction.
     * @param vec The vector to translate by.
     * @return A bound reflecting the changed state.
     */
    Bound shift(Vector3 vec);
}
