package com.gitlab.aecsocket.alexandria.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required
import kotlin.math.min

@ConfigSerializable
data class BarRenderer(
    @Required val width: Int,
    @Required val fill: String,
) {
    fun render(segments: Collection<Float>): List<Component> {
        val res = ArrayList<Component>(segments.size + 1)
        var remaining = width
        segments.forEach { segment ->
            val segWidth = min((segment * width).toInt(), remaining)
            res.add(text(fill.repeat(segWidth)))
            remaining -= segWidth
        }
        res.add(text(fill.repeat(remaining)))
        return res
    }

    fun render(vararg segments: Float) = render(segments.toList())

    data class One(
        val first: Component,
        val background: Component,
    )

    fun renderOne(first: Float): One {
        val res = render(first)
        return One(res[0], res[1])
    }
}
