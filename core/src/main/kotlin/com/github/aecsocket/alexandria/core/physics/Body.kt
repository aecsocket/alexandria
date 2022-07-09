package com.github.aecsocket.alexandria.core.physics

interface Body {
    // object -> world space
    val transform: Transform
    val shape: Shape
}

data class SimpleBody(
    override val transform: Transform = Transform.Identity,
    override val shape: Shape
) : Body

interface ForwardingBody : Body {
    val backing: Body

    override val transform get() = backing.transform
    override val shape get() = backing.shape
}
