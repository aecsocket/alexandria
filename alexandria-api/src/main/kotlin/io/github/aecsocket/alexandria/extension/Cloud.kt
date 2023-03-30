package io.github.aecsocket.alexandria.extension

import cloud.commandframework.Command
import cloud.commandframework.context.CommandContext
import kotlin.reflect.KClass

fun <C : Any> Command.Builder<C>.senderType(type: KClass<out C>) = senderType(type.java)

fun <T : Any> CommandContext<*>.getOr(key: String): T? = getOptional<T>(key).orElse(null)

fun CommandContext<*>.hasFlag(key: String): Boolean = flags().hasFlag(key)

fun <T : Any> CommandContext<*>.flag(key: String): T? = flags().get(key)
