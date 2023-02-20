package io.github.aecsocket.alexandria.core.extension

import cloud.commandframework.context.CommandContext

fun <T : Any> CommandContext<*>.getOr(key: String): T? = getOptional<T>(key).orElse(null)

fun <T : Any> CommandContext<*>.hasFlag(key: String): Boolean = flags().hasFlag(key)

fun <T : Any> CommandContext<*>.flag(key: String): T? = flags().get(key)
