package io.github.aecsocket.alexandria.desc

import net.kyori.adventure.key.Key
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

/**
 * Describes the base type of item created by an [ItemDesc].
 */
sealed interface ItemType {
    /**
     * An item type described by a namespaced key.
     */
    data class Keyed(val key: Key) : ItemType

    /**
     * Platform-specific type.
     */
    interface Raw : ItemType
}

/**
 * Descriptor for an item stack.
 * @param type The base type of item.
 * @param modelData The custom model data.
 * @param damage The damage value of the item, if it can be damaged.
 * @param isUnbreakable If the item has a visible damage bar and can be broken.
 */
@ConfigSerializable
data class ItemDesc(
    @Required val type: ItemType,
    val modelData: Int = 0,
    val damage: Int = 0,
    val isUnbreakable: Boolean = false,
) {
    init {
        require(damage >= 0) { "requires damage >= 0" }
    }
}
