package io.github.aecsocket.alexandria.desc

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

/**
 * Describes the base type of item created by an [ItemDesc]. Up to platforms to implement.
 */
interface RawItemType

/**
 * Descriptor for an item stack.
 * @param type The base type of item.
 * @param modelData The custom model data.
 * @param damage The damage value of the item, if it can be damaged.
 * @param isUnbreakable If the item has a visible damage bar and can be broken.
 */
@ConfigSerializable
data class ItemDesc(
    @Required val type: RawItemType,
    val modelData: Int = 0,
    val damage: Int = 0,
    val isUnbreakable: Boolean = false,
)
