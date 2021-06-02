package com.gitlab.aecsocket.minecommons.core.raycast;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class Raycast<B extends Boundable> {
    public static final double EPSILON = 0.01;

    public record Result<B extends Boundable>(
            Vector3 position, Vector3 out,
            double distance, double distanceThrough,
            B hit
    ) {}

    private double epsilon;

    public Raycast(double epsilon) {
        this.epsilon = epsilon;
    }

    public Raycast() {
        this(EPSILON);
    }

    public double epsilon() { return epsilon; }
    public void epsilon(double epsilon) { this.epsilon = epsilon; }

    protected abstract List<? extends B> baseObjects(Vector3 origin, Vector3 direction, double maxDistance);
    protected abstract List<? extends B> currentObjects(Vector3 point);

    public Result<B> cast(Vector3 origin, Vector3 direction, double maxDistance, Predicate<B> test) {
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
