package com.github.aecsocket.alexandria.paper.input

import com.github.aecsocket.alexandria.core.Input
import com.github.aecsocket.alexandria.core.extension.force
import com.github.aecsocket.alexandria.core.extension.forceList
import com.github.aecsocket.alexandria.core.extension.forceMap
import org.bukkit.entity.Player
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException

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

const val PLAYER_SNEAKING = "sneaking"
const val PLAYER_NOT_SNEAKING = "not_sneaking"
const val PLAYER_SPRINTING = "sprinting"
const val PLAYER_NOT_SPRINTING = "not_sprinting"
const val PLAYER_FLYING = "flying"
const val PLAYER_NOT_FLYING = "not_flying"

data class InputPredicate(
    val tags: Set<String>,
    val action: String,
)

class InputMapper(
    val actions: Map<String, List<InputPredicate>>
) {
    fun actionOf(input: Input, player: Player): String? {
        val playerTags = listOf(
            if (player.isSneaking) PLAYER_SNEAKING else PLAYER_NOT_SNEAKING,
            if (player.isSprinting) PLAYER_SPRINTING else PLAYER_NOT_SPRINTING,
            if (player.isFlying) PLAYER_FLYING else PLAYER_NOT_FLYING,
        )

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

        val allTags = inputTags + playerTags

        return actions.getOrDefault(inputType, emptyList())
            // make sure that the predicate matches all the event tags
            .filter { (tags) -> allTags.containsAll(tags) }
            // get the most specific predicate
            .maxByOrNull { (tags) -> tags.size }?.let { (_, action) -> action }
    }

    companion object {
        val Empty = InputMapper(emptyMap())

        fun deserialize(node: ConfigurationNode, actions: Set<String>): InputMapper {
            val type = InputMapper::class.java
            node.forceMap(type)

            return InputMapper(node.childrenMap().map { (inputType, child) ->
                if (!INPUT_TYPES.contains(inputType))
                    throw SerializationException(child, type, "Invalid input type '$inputType'")
                inputType.toString() to child.forceList(type).map { predicateNode ->
                    predicateNode.forceList(type, "tags", "action").run { InputPredicate(
                        get(0).force<HashSet<String>>(),
                        get(1).run {
                            val actionKey = force<String>()
                            if (!actions.contains(actionKey))
                                throw SerializationException(this, type, "Invalid action '$actionKey'")
                            actionKey
                        },
                    ) }
                }
            }.associate { it })
        }
    }
}
