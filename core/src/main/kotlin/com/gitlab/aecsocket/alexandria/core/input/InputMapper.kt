package com.gitlab.aecsocket.alexandria.core.input

const val INPUT_MOUSE = "mouse"
const val INPUT_SWAP_HANDS = "swap_hands"
const val INPUT_DROP = "drop"
const val INPUT_HELD_ITEM = "held_item"
const val INPUT_SNEAK = "sneak"
const val INPUT_SPRINT = "sprint"
const val INPUT_FLIGHT = "flight"
const val INPUT_HORSE_JUMP = "horse_jump"
const val INPUT_ELYTRA_FLIGHT = "elytra_flight"
const val INPUT_MENU = "menu"
const val INPUT_LEAVE_BED = "leave_bed"
val INPUT_TYPES = setOf(
    INPUT_MOUSE,
    INPUT_SWAP_HANDS,
    INPUT_DROP,
    INPUT_HELD_ITEM,
    INPUT_SNEAK,
    INPUT_SPRINT,
    INPUT_FLIGHT,
    INPUT_HORSE_JUMP,
    INPUT_ELYTRA_FLIGHT,
    INPUT_MENU,
    INPUT_LEAVE_BED,
)

data class InputPredicate(
    val actions: List<String>,
    val tags: Set<String> = emptySet(),
)

class InputMapper(
    val actions: Map<String, List<InputPredicate>>
) {
    fun actionOf(input: Input, tags: Collection<String>): List<String> {
        val (inputType, inputTags) = when (input) {
            is Input.Mouse -> INPUT_MOUSE to listOf(input.button.key, input.state.key)
            is Input.SwapHands -> INPUT_SWAP_HANDS to emptyList()
            is Input.Drop -> INPUT_DROP to emptyList()
            is Input.HeldItem -> INPUT_HELD_ITEM to listOf(input.direction.key)
            is Input.Sneak -> INPUT_SNEAK to listOf(input.now.toString())
            is Input.Sprint -> INPUT_SPRINT to listOf(input.now.toString())
            is Input.Flight -> INPUT_FLIGHT to listOf(input.now.toString())
            is Input.HorseJump -> INPUT_HORSE_JUMP to listOf(input.now.toString())
            is Input.ElytraFlight -> INPUT_ELYTRA_FLIGHT to emptyList()
            is Input.Menu -> INPUT_MENU to listOf(input.menu.key, input.open.toString())
            is Input.LeaveBed -> INPUT_LEAVE_BED to emptyList()
        }

        val allTags = inputTags + tags

        return actions.getOrDefault(inputType, emptyList())
            // make sure that the predicate matches all the event tags
            .filter { (_, tags) -> allTags.containsAll(tags) }
            // get the most specific predicate
            .maxByOrNull { (_, tags) -> tags.size }?.let { (action) -> action }
            ?: emptyList()
    }

    companion object {
        val Empty = InputMapper(emptyMap())
    }
}
