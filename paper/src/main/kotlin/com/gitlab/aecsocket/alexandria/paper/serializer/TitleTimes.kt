package com.gitlab.aecsocket.alexandria.paper.serializer

import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.Times.times
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type
import java.time.Duration

private const val FADE_IN = "fade_in"
private const val STAY = "stay"
private const val FADE_OUT = "fade_out"

private val KEEP = Duration.ofSeconds(-1)

object TitleTimesSerializer : TypeSerializer<Title.Times> {
    override fun serialize(type: Type, obj: Title.Times?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.node(FADE_IN).set(obj.fadeIn())
            node.node(STAY).set(obj.stay())
            node.node(FADE_OUT).set(obj.fadeOut())
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode) = times(
        node.node(FADE_IN).get { KEEP },
        node.node(STAY).get { KEEP },
        node.node(FADE_OUT).get { KEEP },
    )
}