package io.github.aecsocket.alexandria.extension

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.Command
import cloud.commandframework.context.CommandContext
import cloud.commandframework.types.tuples.Triplet
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FVec3
import kotlin.reflect.KClass

fun <C : Any> Command.Builder<C>.senderType(type: KClass<out C>) = senderType(type.java)

fun <T : Any> CommandContext<*>.getOr(key: String): T? = getOptional<T>(key).orElse(null)

fun CommandContext<*>.hasFlag(key: String): Boolean = flags().hasFlag(key)

fun <T : Any> CommandContext<*>.flag(key: String): T? = flags().get(key)

fun <C> Command.Builder<C>.argumentFVec3(
    name: String,
    description: ArgumentDescription = ArgumentDescription.empty()
) =
    argumentTriplet(
        name,
        typeToken<FVec3>(),
        Triplet.of("x", "y", "z"),
        Triplet.of(Float::class.java, Float::class.java, Float::class.java),
        { _, t -> FVec3(t.first, t.second, t.third) },
        description,
    )

fun <C> Command.Builder<C>.argumentDVec3(
    name: String,
    description: ArgumentDescription = ArgumentDescription.empty()
) =
    argumentTriplet(
        name,
        typeToken<DVec3>(),
        Triplet.of("x", "y", "z"),
        Triplet.of(Double::class.java, Double::class.java, Double::class.java),
        { _, t -> DVec3(t.first, t.second, t.third) },
        description,
    )
