package com.github.aecsocket.alexandria.core.bound

class FixedRaycast<B : Boundable>(
    val objects: Iterable<B>
) : Raycast<B>() {
    override fun cast(ray: Ray, maxDistance: Double, test: (B) -> Boolean): Result<B> {
        return intersectsAny(ray, objects) ?: Result.Miss(ray, maxDistance)
    }
}
