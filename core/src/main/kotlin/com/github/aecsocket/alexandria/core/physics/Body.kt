package com.github.aecsocket.alexandria.core.physics

interface Body {
    val shape: Shape

    // object -> world space
    val transform: Transform

    // world -> object space
    val invTransform: Transform
}

interface DynamicBody : Body {
    override var transform: Transform
}

class SimpleBody(
    override val shape: Shape,
    override val transform: Transform = Transform.Identity,
    override val invTransform: Transform = transform.inverse,
) : Body {
    override fun toString() = "SimpleBody($shape @ ${transform.tl})"
}

/*class DynamicSimpleBody(
    shape: Shape,
    override var transform: Transform = Transform.Identity,
    invTransform: Transform = transform.inverse,
) : SimpleBody(shape, transform)
*/