package com.gitlab.aecsocket.alexandria.core.extension

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.Command
import cloud.commandframework.context.CommandContext
import cloud.commandframework.types.tuples.Triplet
import com.gitlab.aecsocket.alexandria.core.physics.Vector3

operator fun <U, V> cloud.commandframework.types.tuples.Pair<U, V>.component1(): U = first
operator fun <U, V> cloud.commandframework.types.tuples.Pair<U, V>.component2(): V = second

operator fun <U, V, W> Triplet<U, V, W>.component1(): U = first
operator fun <U, V, W> Triplet<U, V, W>.component2(): V = second
operator fun <U, V, W> Triplet<U, V, W>.component3(): W = third

@Suppress("UNCHECKED_CAST") // cloud is stupid in this regard anyway
fun <V> CommandContext<*>.get(key: String, default: () -> V): V =
    getOrDefault<V>(key, null) ?: default()

fun CommandContext<*>.flagged(name: String) = flags().hasFlag(name)

fun <V> CommandContext<*>.flag(name: String): V? = flags().get<V>(name)

fun <C> Command.Builder<C>.argumentVector3(
    name: String,
    desc: ArgumentDescription
): Command.Builder<C> = argumentTriplet(name,
    typeToken<Vector3>(),
    Triplet.of("x", "y", "z"),
    Triplet.of(Double::class.java, Double::class.java, Double::class.java),
    { _, (x, y, z) -> Vector3(x, y, z) },
    desc)

fun <C> Command.Builder<C>.argumentEuler3(
    name: String,
    desc: ArgumentDescription
): Command.Builder<C> = argumentTriplet(name,
    typeToken<Euler3>(),
    Triplet.of("pitch", "yaw", "roll"),
    Triplet.of(Double::class.java, Double::class.java, Double::class.java),
    { _, (x, y, z) -> Euler3(x, y, z) },
    desc)
