package com.gitlab.aecsocket.alexandria.paper

import org.bukkit.inventory.ItemStack

data class ItemBytes(
    val bytes: ByteArray
) {
    fun createItem() = ItemStack.deserializeBytes(bytes)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemBytes

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}
