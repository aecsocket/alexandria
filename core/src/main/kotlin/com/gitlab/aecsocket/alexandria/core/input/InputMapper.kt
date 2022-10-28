package com.gitlab.aecsocket.alexandria.core.input

class InputMapper<V> private constructor(
    private val values: Map<InputType, List<Trigger<V>>>
) {
    private data class Trigger<V>(
        val tags: Set<String>,
        val value: V
    ) {
        fun <R> map(mapper: (V) -> R): Trigger<R> {
            return Trigger(tags, mapper(value))
        }
    }

    fun get(input: Input, tags: Collection<String>): V? {
        val allTags = when (input) {
            is Input.Mouse -> listOf(input.button.key, input.state.key)
            is Input.SwapHands -> emptyList()
            is Input.Drop -> emptyList()
            is Input.HeldItem -> listOf(input.direction.key)
            is Input.Sneak -> listOf(input.now.toString())
            is Input.Sprint -> listOf(input.now.toString())
            is Input.Flight -> listOf(input.now.toString())
            is Input.HorseJump -> listOf(input.now.toString())
            is Input.ElytraFlight -> emptyList()
            is Input.Menu -> listOf(input.menu.key, input.open.toString())
            is Input.LeaveBed -> emptyList()
        } + tags

        values[input.type]?.let { triggers ->
            triggers.forEach { trigger ->
                if (allTags.containsAll(trigger.tags)) {
                    return trigger.value
                }
            }
        }
        return null
    }

    fun <R> map(mapper: (V) -> R): InputMapper<R> {
        return InputMapper(values
            .map { (type, triggers) -> type to triggers.map { trigger -> trigger.map(mapper) } }
            .associate { it })
    }

    class Builder<V> internal constructor() {
        private data class BuildTrigger<V>(
            val inputType: InputType,
            val tags: Iterable<String>,
            val value: V
        )

        private val triggers = ArrayList<BuildTrigger<V>>()

        fun trigger(inputType: InputType, tags: Iterable<String>, value: V): Builder<V> {
            triggers.add(BuildTrigger(inputType, tags, value))
            return this
        }

        fun build(): InputMapper<V> {
            val byInputType = HashMap<InputType, MutableList<Trigger<V>>>()
            triggers.forEach { (inputType, tags, value) ->
                byInputType.computeIfAbsent(inputType) { ArrayList() }
                    .add(Trigger(tags.toSet(), value))
            }
            return InputMapper(byInputType)
        }
    }

    companion object {
        fun <V> builder() = Builder<V>()
    }
}
