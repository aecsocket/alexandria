package com.gitlab.aecsocket.minecommons.core.bounds;

public interface OrientedBound extends Bound {
    double angle();
    OrientedBound angle(double angle);
}
