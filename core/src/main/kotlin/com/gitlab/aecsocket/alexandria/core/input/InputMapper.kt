package com.gitlab.aecsocket.alexandria.core.input

data class InputPredicate(
    val actions: List<String>,
    val tags: Set<String> = emptySet(),
)

class InputMapper<V> private constructor(
    private val values: Map<InputType, List<Trigger<V>>>
) {
    private data class Trigger<V>(
        val tags: Set<String>,
        val value: V
    )

    fun map(input: Input, tags: Collection<String>): V? {
        values[input.type]?.let { triggers ->
            triggers.forEach { trigger ->
                if (tags.containsAll(trigger.tags)) {
                    return trigger.value
                }
            }
        }
        return null
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
