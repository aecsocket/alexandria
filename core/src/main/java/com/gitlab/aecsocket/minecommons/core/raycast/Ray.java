package com.gitlab.aecsocket.minecommons.core.raycast;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

public record Ray(Vector3 pos, Vector3 dir, Vector3 invDir) {
    private static final Vector3 vectorOne = new Vector3(1, 1, 1);

    public static Ray ray(Vector3 pos, Vector3 dir) {
        Vector3 invDir = vectorOne.divide(dir);
        return new Ray(pos, dir, invDir);
    }

    public Ray pos(Vector3 pos) {
        return new Ray(pos, dir, invDir);
    }

    public Vector3 at(double d) {
        return pos.add(dir.multiply(d));
    }
}
