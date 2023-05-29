package io.github.aecsocket.alexandria.fabric.extension

import io.github.aecsocket.alexandria.extension.DEFAULT
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.level.Level

fun <V> Map<String, V>.forLevel(level: Level) = get(level.dimension().location().toString()) ?: get(DEFAULT)

inline fun <reified E> createEvent(noinline invokerFactory: (Array<E>) -> E): Event<E> {
    return EventFactory.createArrayBacked(E::class.java, invokerFactory)
}
