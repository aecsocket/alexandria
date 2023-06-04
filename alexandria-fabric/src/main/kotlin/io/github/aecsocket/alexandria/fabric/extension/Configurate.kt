package io.github.aecsocket.alexandria.fabric.extension

import io.github.aecsocket.alexandria.extension.apiSerializers
import io.github.aecsocket.alexandria.extension.register
import io.github.aecsocket.alexandria.extension.registerExact
import io.github.aecsocket.alexandria.fabric.serializer.RawItemTypeSerializer
import io.github.aecsocket.alexandria.fabric.serializer.RegisteredSerializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import org.spongepowered.configurate.serialize.TypeSerializerCollection

private inline fun <reified T> TypeSerializerCollection.Builder.registerRegistry(
    registry: Registry<T>,
): TypeSerializerCollection.Builder {
    registerExact(RegisteredSerializer(registry))
    return this
}

val fabricSerializers: TypeSerializerCollection = TypeSerializerCollection.builder()
    .registerAll(apiSerializers)
    // have to do this manually instead of a forEach loop over .REGISTRY because of reified type params
    .registerRegistry(BuiltInRegistries.GAME_EVENT)
    .registerRegistry(BuiltInRegistries.SOUND_EVENT)
    .registerRegistry(BuiltInRegistries.FLUID)
    .registerRegistry(BuiltInRegistries.MOB_EFFECT)
    .registerRegistry(BuiltInRegistries.BLOCK)
    .registerRegistry(BuiltInRegistries.ENCHANTMENT)
    .registerRegistry(BuiltInRegistries.ENTITY_TYPE)
    .registerRegistry(BuiltInRegistries.ITEM)
    .registerRegistry(BuiltInRegistries.POTION)
    .registerRegistry(BuiltInRegistries.PARTICLE_TYPE)
    .registerRegistry(BuiltInRegistries.BLOCK_ENTITY_TYPE)
    .registerRegistry(BuiltInRegistries.PAINTING_VARIANT)
    .registerRegistry(BuiltInRegistries.CUSTOM_STAT)
    .registerRegistry(BuiltInRegistries.CHUNK_STATUS)
    .registerRegistry(BuiltInRegistries.RULE_TEST)
    .registerRegistry(BuiltInRegistries.POS_RULE_TEST)
    .registerRegistry(BuiltInRegistries.MENU)
    .registerRegistry(BuiltInRegistries.RECIPE_TYPE)
    .registerRegistry(BuiltInRegistries.RECIPE_SERIALIZER)
    .registerRegistry(BuiltInRegistries.ATTRIBUTE)
    .registerRegistry(BuiltInRegistries.POSITION_SOURCE_TYPE)
    .registerRegistry(BuiltInRegistries.COMMAND_ARGUMENT_TYPE)
    .registerRegistry(BuiltInRegistries.STAT_TYPE)
    .registerRegistry(BuiltInRegistries.VILLAGER_TYPE)
    .registerRegistry(BuiltInRegistries.VILLAGER_PROFESSION)
    .registerRegistry(BuiltInRegistries.POINT_OF_INTEREST_TYPE)
    .registerRegistry(BuiltInRegistries.MEMORY_MODULE_TYPE)
    .registerRegistry(BuiltInRegistries.SENSOR_TYPE)
    .registerRegistry(BuiltInRegistries.SCHEDULE)
    .registerRegistry(BuiltInRegistries.ACTIVITY)
    .registerRegistry(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE)
    .registerRegistry(BuiltInRegistries.LOOT_FUNCTION_TYPE)
    .registerRegistry(BuiltInRegistries.LOOT_CONDITION_TYPE)
    .registerRegistry(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE)
    .registerRegistry(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE)
    .registerRegistry(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE)
    .registerRegistry(BuiltInRegistries.FLOAT_PROVIDER_TYPE)
    .registerRegistry(BuiltInRegistries.INT_PROVIDER_TYPE)
    .registerRegistry(BuiltInRegistries.HEIGHT_PROVIDER_TYPE)
    .registerRegistry(BuiltInRegistries.BLOCK_PREDICATE_TYPE)
    .registerRegistry(BuiltInRegistries.CARVER)
    .registerRegistry(BuiltInRegistries.FEATURE)
    .registerRegistry(BuiltInRegistries.STRUCTURE_PLACEMENT)
    .registerRegistry(BuiltInRegistries.STRUCTURE_PIECE)
    .registerRegistry(BuiltInRegistries.STRUCTURE_TYPE)
    .registerRegistry(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE)
    .registerRegistry(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE)
    .registerRegistry(BuiltInRegistries.FOLIAGE_PLACER_TYPE)
    .registerRegistry(BuiltInRegistries.TRUNK_PLACER_TYPE)
    .registerRegistry(BuiltInRegistries.ROOT_PLACER_TYPE)
    .registerRegistry(BuiltInRegistries.TREE_DECORATOR_TYPE)
    .registerRegistry(BuiltInRegistries.FEATURE_SIZE_TYPE)
    .registerRegistry(BuiltInRegistries.BIOME_SOURCE)
    .registerRegistry(BuiltInRegistries.CHUNK_GENERATOR)
    .registerRegistry(BuiltInRegistries.MATERIAL_CONDITION)
    .registerRegistry(BuiltInRegistries.MATERIAL_RULE)
    .registerRegistry(BuiltInRegistries.DENSITY_FUNCTION_TYPE)
    .registerRegistry(BuiltInRegistries.STRUCTURE_PROCESSOR)
    .registerRegistry(BuiltInRegistries.STRUCTURE_POOL_ELEMENT)
    .registerRegistry(BuiltInRegistries.CAT_VARIANT)
    .registerRegistry(BuiltInRegistries.FROG_VARIANT)
    .registerRegistry(BuiltInRegistries.BANNER_PATTERN)
    .registerRegistry(BuiltInRegistries.INSTRUMENT)
    .registerRegistry(BuiltInRegistries.DECORATED_POT_PATTERNS)
    .register(RawItemTypeSerializer)
    .build()
