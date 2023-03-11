package io.github.aecsocket.alexandria.core.math

// inclusive on both ends
// an AABB of [(0, 0, 0), (1, 1, 1)] will give points:
//   (0, 0, 0), (1, 0, 0), (0, 1, 0), (1, 1, 0),
//   (0, 0, 1), (1, 0, 1), (0, 1, 1), (1, 1, 1)
fun AABB.enclosingPoints(): Iterable<Point3> {
    fun floor(s: Double): Int {
        val i = s.toInt()
        return if (s < i) i - 1 else i
    }

    fun ceil(s: Double): Int {
        val i = s.toInt()
        return if (s > i) i + 1 else i
    }

    val pMin = Point3(floor(min.x), floor(min.y), floor(min.z))
    val pMax = Point3(ceil(max.x), ceil(max.y), ceil(max.z))
    val extent = pMax - pMin
    val size = extent.x * extent.y * extent.z
    return object : Iterable<Point3> {
        override fun iterator() = object : Iterator<Point3> {
            var i = 0
            var dx = 0
            var dy = 0
            var dz = 0

            override fun hasNext() = i < size

            override fun next(): Point3 {
                if (i >= size)
                    throw IndexOutOfBoundsException("($dx, $dy, $dz)")
                val point = pMin + Point3(dx, dy, dz)
                dx += 1
                if (dx >= extent.x) {
                    dx = 0
                    dy += 1
                    if (dy >= extent.y) {
                        dy = 0
                        dz += 1
                    }
                }
                i += 1
                return point
            }
        }
    }
}

