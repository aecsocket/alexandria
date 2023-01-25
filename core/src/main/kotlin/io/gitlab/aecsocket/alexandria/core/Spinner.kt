package io.gitlab.aecsocket.alexandria.core

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class Spinner<T>(
    val states: List<T> = emptyList(),
    val period: Long = 100
) {
    fun state(elapsed: Long): T? {
        if (states.isEmpty()) return null
        val index = (elapsed / period) % states.size
        return states[index.toInt()]
    }
}
