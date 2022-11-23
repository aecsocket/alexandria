package com.gitlab.aecsocket.alexandria.core.input

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
class InputMapper<V>(
    @Setting(nodeFromParent = true) private val values: Map<InputType, List<Value<V>>>
) {
    interface Filter {
        fun matches(input: Input): Boolean
    }

    object TrueFilter : Filter {
        override fun matches(input: Input) = true
    }

    @ConfigSerializable
    data class MouseFilter(
        val button: Input.MouseButton? = null,
        val state: Input.MouseState? = null,
    ) : Filter {
        override fun matches(input: Input) = input is Input.Mouse &&
            (button == null || input.button == button) &&
            (state == null || input.state == state)
    }

    @ConfigSerializable
    data class HeldItemFilter(
        val direction: Input.ScrollDirection? = null,
    ) : Filter {
        override fun matches(input: Input) = input is Input.HeldItem &&
            (direction == null || input.direction == direction)
    }

    @ConfigSerializable
    data class ToggleableFilter(
        val now: Boolean? = null,
    ) : Filter {
        override fun matches(input: Input) = input is Input.Toggleable &&
            (now == null || input.now == now)
    }

    @ConfigSerializable
    data class MenuFilter(
        val menu: Input.MenuType? = null,
        val open: Boolean? = null,
    ) : Filter {
        override fun matches(input: Input) = input is Input.Menu &&
            (menu == null || input.menu == menu) &&
            (open == null || input.open == open)
    }

    data class Value<V>(
        val filter: Filter,
        val value: V
    ) {
        fun <R> map(transform: (V) -> R) = Value(filter, transform(value))
    }

    fun matches(input: Input): List<V> {
        return (values[input.type] ?: return emptyList())
            .filter { (filter) -> filter.matches(input) }
            .map { (_, value) -> value }
    }

    fun <R> map(transform: (V) -> R): InputMapper<R> {
        return InputMapper(values.map { (type, values) ->
            type to values.map { it.map(transform) }
        }.associate { it })
    }
}
