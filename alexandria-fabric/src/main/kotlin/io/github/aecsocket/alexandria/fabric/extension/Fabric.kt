package io.github.aecsocket.alexandria.fabric.extension

import io.github.aecsocket.alexandria.fabric.Persistable
import io.github.aecsocket.alexandria.fabric.mixin.EntityCounterAccess
import io.github.aecsocket.klam.DVec3
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.entity.Display.ItemDisplay
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

fun <V> Map<String, V>.forWorld(world: Level) =
    get(world.dimension().location().toString()) ?: get("default")

inline fun <reified E> createEvent(noinline invokerFactory: (Array<E>) -> E): Event<E> {
  return EventFactory.createArrayBacked(E::class.java, invokerFactory)
}

fun createTrackerEntity(world: Level, position: DVec3): Entity {
  val entity = ItemDisplay(EntityType.ITEM_DISPLAY, world)
  (entity as Persistable).setPersistent(false)
  entity.moveTo(position.toVec3())
  return entity
}

fun nextEntityId() = EntityCounterAccess.getEntityCounter().incrementAndGet()
