package io.github.aecsocket.alexandria.fabric.extension

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.level.Level

fun <V> Map<String, V>.forWorld(world: Level) = get(world.dimension().location().toString()) ?: get("default")

inline fun <reified E> createEvent(noinline invokerFactory: (Array<E>) -> E): Event<E> {
    return EventFactory.createArrayBacked(E::class.java, invokerFactory)
}
