package com.github.aecsocket.alexandria.core.physics

data class RayCollision<B : Body>(
    val ray: Ray,
    val hit: B,
    val tIn: Double,
    val tOut: Double,
    val normal: Vector3
) {
    val penetration = tOut - tIn
    val posIn = ray.point(tIn)
    val posOut = ray.point(tOut)
}

fun <T : RayCollision<*>> Iterable<T?>.closest() = filterNotNull().minByOrNull { it.tIn }

abstract class Raycast<B : Body> {
    abstract fun cast(ray: Ray, maxDistance: Double, test: (B) -> Boolean = { true }): RayCollision<out B>?

    protected fun <T : B> collides(ray: Ray, bodies: Iterable<T>, maxDistance: Double): RayCollision<T>? {
        var closest: Pair<T, Collision>? = null
        bodies.forEach { body ->
            body.shape.collides(body.transform.invert(ray))?.let { collision ->
                if (collision.tIn <= maxDistance) {
                    closest = closest?.let {
                        if (collision.tIn < it.second.tIn) body to collision
                        else it
                    } ?: (body to collision)
                }
            }
        }
        return closest?.let { (body, collision) -> RayCollision(ray, body,
            collision.tIn, collision.tOut, body.transform.rot * collision.normal
        ) }
    }
}

fun <B : Body> raycastOf(bodies: Iterable<B>) = object : Raycast<B>() {
    override fun cast(ray: Ray, maxDistance: Double, test: (B) -> Boolean): RayCollision<out B>? {
        return collides(ray, bodies.filter(test), maxDistance)
    }
}
