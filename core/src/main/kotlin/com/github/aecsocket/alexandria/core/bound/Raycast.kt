package com.github.aecsocket.alexandria.core.bound

import com.github.aecsocket.alexandria.core.spatial.Vector3

interface Boundable {
    val bound: Bound
}

abstract class Raycast<B : Boundable> {
    sealed interface Result<out B> {
        val ray: Ray
        val travelled: Double

        data class Miss<B>(
            override val ray: Ray,
            override val travelled: Double
        ) : Result<B> {
            val position = ray.point(travelled)
        }

        data class Hit<B>(
            override val ray: Ray,
            val hit: B,
            val tIn: Double,
            val tOut: Double,
            val normal: Vector3
        ) : Result<B> {
            constructor(ray: Ray, hit: B, collision: Collision)
                : this(ray, hit, collision.tIn, collision.tOut, collision.normal)

            override val travelled = tIn

            val posIn = ray.point(tIn)

            val posOut = ray.point(tOut)

            val penetration = tOut - tIn
        }
    }

    abstract fun cast(ray: Ray, maxDistance: Double, test: (B) -> Boolean = { true }): Result<B>

    protected fun <C : B> intersects(ray: Ray, obj: C): Result.Hit<C>? {
        return obj.bound.collides(ray)?.let {
            Result.Hit(ray, obj, it)
        }
    }

    protected fun <C : B> intersectsAny(ray: Ray, objects: Iterable<C>): Result.Hit<C>? {
        var closest: Result.Hit<C>? = null
        objects.forEach { obj ->
            intersects(ray, obj)?.let { hit ->
                closest = closest?.let {
                    if (hit.tIn < it.tIn) hit else it
                } ?: hit
            }
        }
        return closest
    }
}
