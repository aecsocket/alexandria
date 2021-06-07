package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.gitlab.aecsocket.minecommons.core.raycast.Ray;

public interface Bound {
    boolean intersects(Vector3 point);

    Bound shift(Vector3 vec);
}
