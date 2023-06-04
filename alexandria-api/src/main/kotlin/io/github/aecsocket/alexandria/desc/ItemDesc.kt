package io.github.aecsocket.alexandria.desc

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

interface RawItemType

@ConfigSerializable
data class ItemDesc(
    @Required val type: RawItemType,
    val modelData: Int = 0,
    val damage: Int = 0,
    val isUnbreakable: Boolean = false,
)
