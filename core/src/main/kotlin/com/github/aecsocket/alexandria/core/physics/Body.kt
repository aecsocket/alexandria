package com.github.aecsocket.alexandria.core.physics

interface Body {
    val shape: Shape

    // object -> world space
    val transform: Transform
}

class SimpleBody(
    override val shape: Shape,
    override val transform: Transform = Transform.Identity,
) : Body {
    override fun toString() = "SimpleBody($shape @ ${transform.tl})"
}
