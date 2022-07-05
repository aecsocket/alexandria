package com.github.aecsocket.alexandria.core.bound

import com.github.aecsocket.alexandria.core.vector.Vector3

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
            val position by lazy { ray.point(travelled) }
        }

        data class Hit<B>(
            override val ray: Ray,
            val hit: B,
            val tIn: Double,
            val tOut: Double,
            val normal: Vector3
        ) : Result<B> {
            constructor(ray: Ray, hit: B, intersection: Bound.Intersection)
                : this(ray, hit, intersection.tIn, intersection.tOut, intersection.normal)

            override val travelled = tIn

            val posIn by lazy { ray.point(tIn) }

            val posOut by lazy { ray.point(tOut) }

            val penetration by lazy { tOut - tIn }
        }
    }

    abstract fun cast(ray: Ray, maxDistance: Double, test: (B) -> Boolean = { true }): Result<B>

    protected fun <C : B> intersects(ray: Ray, obj: C): Result.Hit<C>? {
        return obj.bound.intersects(ray)?.let {
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
