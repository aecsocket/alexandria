package com.github.aecsocket.alexandria.core.extension

import cloud.commandframework.context.CommandContext
import cloud.commandframework.types.tuples.Triplet

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
