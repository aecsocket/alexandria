package com.gitlab.aecsocket.alexandria.core.extension

import io.leangen.geantyref.TypeToken
import net.kyori.adventure.extra.kotlin.join
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.Style.style
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.lang.reflect.Type
import kotlin.reflect.KClass

inline fun <reified T> typeToken() = object : TypeToken<T>() {}

fun <V> ConfigurationNode.getIfExists(type: TypeToken<V>): V? = if (virtual()) null else get(type)

inline fun <reified V> ConfigurationNode.getIfExists() = getIfExists(typeToken<V>())

fun <V> ConfigurationNode.force(type: TypeToken<V>) = get(type)
    ?: throw SerializationException(this, type.type, "A value is required for this field")

inline fun <reified V> ConfigurationNode.force() = force(typeToken<V>())

fun ConfigurationNode.forceList(type: Type) = if (isList) childrenList()
    else throw SerializationException(this, type, "Field must be expressed as list")

fun ConfigurationNode.forceList(type: Type, vararg args: String): List<ConfigurationNode> {
    if (isList) {
        val list = childrenList()
        if (list.size == args.size)
            return list
        throw SerializationException(this, type, "Field must be expressed as list of [${args.joinToString()}], found ${list.size}")
    }
    throw SerializationException(this, type, "Field must be expressed as list")
}

fun ConfigurationNode.forceMap(type: Type) = if (isMap) childrenMap()
    else throw SerializationException(this, type, "A map is required for this field")

fun <T : Any> TypeSerializerCollection.Builder.register(type: KClass<T>, serializer: TypeSerializer<T>) =
    register(type.java, serializer)

inline fun <reified T : Any> TypeSerializerCollection.Builder.register(serializer: TypeSerializer<T>) =
    register(T::class.java, serializer)

fun <T : Any> TypeSerializerCollection.Builder.registerExact(type: KClass<T>, serializer: TypeSerializer<T>) =
    registerExact(type.java, serializer)

inline fun <reified T : Any> TypeSerializerCollection.Builder.registerExact(serializer: TypeSerializer<T>) =
    registerExact(T::class.java, serializer)

data class NodeRenderOptions(
    val boolean: Style = style(GOLD),
    val number: Style = style(AQUA),
    val string: Style = style(WHITE),
    val scalar: Style = style(WHITE),

    val comment: Style = style(DARK_GREEN),
    val bracket: Style = style(GRAY),
    val key: Style = style(YELLOW),
    val listIndex: Style = style(GRAY),

    val keySeparator: Style = style(GRAY),
    val itemSeparator: Style = style(GRAY),
) {
    companion object {
        val DEFAULT = NodeRenderOptions()
    }
}

fun ConfigurationNode.render(
    options: NodeRenderOptions = NodeRenderOptions.DEFAULT,
    showComments: Boolean = true
): List<Component> {
    val margin = text("  ")

    val res = when {
        isMap -> childrenMap()
            .filter { (_, child) -> !child.empty() }
            .flatMap { (key, child) ->
            val start = text()
                .append(text(key.toString(), options.key))
                .append(text(": ", options.keySeparator))
                .build()

            val lines = child.render(options, showComments)
            if (lines.size == 1) {
                listOf(start.append(lines[0]))
            } else {
                listOf(start) + lines.map { text().append(margin).append(it).build() }
            }
        }
        isList -> {
            val parts = childrenList().map { it.render(options, showComments) }
            if (parts.any { it.size != 1 }) {
                parts.flatMapIndexed { idx, lines ->
                    val prefix = "${idx+1}. "
                    val padding = text(" ".repeat(prefix.length))
                    val start = text(prefix, options.listIndex)

                    if (lines.isEmpty())
                        listOf(start)
                    else lines.mapIndexed { cIdx, line ->
                        (if (cIdx == 0) start else padding).append(line)
                    }
                }
            } else {
                listOf(text()
                    .append(text("[", options.bracket))
                    .append(parts.map { it[0] }.join(JoinConfiguration.separator(text(", ", options.itemSeparator))))
                    .append(text("]", options.bracket))
                    .build())
            }
        }
        else -> when (val raw = raw()) {
            is Boolean -> listOf(text(raw.toString(), options.boolean))
            is Number -> listOf(text(raw.toString(), options.number))
            is String -> raw.split('\n').map { text(it, options.string) }
            else -> listOf(text(raw.toString(), options.scalar))
        }
    }


    return if (showComments && this is CommentedConfigurationNode) {
        comment()?.let { comment ->
            comment.lines().map { text("# $it", options.comment) } + res
        } ?: res
    } else res
}
