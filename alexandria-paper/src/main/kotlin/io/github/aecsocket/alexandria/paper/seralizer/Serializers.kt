package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.extension.register
import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.alexandria.serializer.apiSerializers
import org.bukkit.Keyed
import org.bukkit.Registry
import org.spongepowered.configurate.serialize.TypeSerializerCollection

private inline fun <reified T : Keyed> TypeSerializerCollection.Builder.registerRegistry(
    registry: Registry<T>,
): TypeSerializerCollection.Builder {
  registerExact(RegisteredSerializer(registry))
  return this
}

val paperSerializers: TypeSerializerCollection =
    TypeSerializerCollection.builder()
        .registerAll(apiSerializers)
        .registerRegistry(Registry.ADVANCEMENT)
        .registerRegistry(Registry.ART)
        .registerRegistry(Registry.ATTRIBUTE)
        .registerRegistry(Registry.BIOME)
        .registerRegistry(Registry.BOSS_BARS)
        .registerRegistry(Registry.ENCHANTMENT)
        .registerRegistry(Registry.ENTITY_TYPE)
        .registerRegistry(Registry.LOOT_TABLES)
        .registerRegistry(Registry.MATERIAL)
        .registerRegistry(Registry.STATISTIC)
        .registerRegistry(Registry.STRUCTURE)
        .registerRegistry(Registry.STRUCTURE_TYPE)
        .registerRegistry(Registry.SOUNDS)
        .registerRegistry(Registry.VILLAGER_PROFESSION)
        .registerRegistry(Registry.VILLAGER_TYPE)
        .registerRegistry(Registry.MEMORY_MODULE_TYPE)
        .registerRegistry(Registry.FLUID)
        .registerRegistry(Registry.FROG_VARIANT)
        .registerRegistry(Registry.GAME_EVENT)
        .registerRegistry(Registry.POTION_EFFECT_TYPE)
        .registerExact(ParticleSerializer)
        .registerExact(BlockDataSerializer)
        .registerExact(DustOptionsSerializer)
        .register(ParticleTypeSerializer)
        .register(ItemTypeSerializer)
        .build()
