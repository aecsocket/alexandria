package com.gitlab.aecsocket.alexandria.core.input

enum class InputType(val key: String) {
    MOUSE           ("mouse"),
    SWAP_HANDS      ("swap_hands"),
    DROP            ("drop"),
    HELD_ITEM       ("held_item"),
    SNEAK           ("sneak"),
    SPRINT          ("sprint"),
    FLIGHT          ("flight"),
    HORSE_JUMP      ("horse_jump"),
    ELYTRA_FLIGHT   ("elytra_flight"),
    MENU            ("menu"),
    LEAVE_BED       ("leave_bed")
}

sealed interface Input {
    val type: InputType

    data class Mouse(val button: MouseButton, val state: MouseState) : Input {
        override val type get() = InputType.MOUSE
    }

    object SwapHands : Input {
        override val type get() = InputType.SWAP_HANDS
    }

    object Drop : Input {
        override val type get() = InputType.DROP
    }

    data class HeldItem(val direction: ScrollDirection) : Input {
        override val type get() = InputType.HELD_ITEM
    }

    interface Toggleable : Input {
        val now: Boolean
    }

    data class Sneak(override val now: Boolean) : Toggleable {
        override val type get() = InputType.SNEAK
    }

    data class Sprint(override val now: Boolean) : Toggleable {
        override val type get() = InputType.SPRINT
    }

    data class Flight(override val now: Boolean) : Toggleable {
        override val type get() = InputType.FLIGHT
    }

    data class HorseJump(override val now: Boolean) : Toggleable {
        override val type get() = InputType.HORSE_JUMP
    }

    object ElytraFlight : Input {
        override val type get() = InputType.ELYTRA_FLIGHT
    }

    data class Menu(val menu: MenuType, val open: Boolean) : Input {
        override val type get() = InputType.MENU
    }

    object LeaveBed : Input {
        override val type get() = InputType.LEAVE_BED
    }


    enum class MouseButton(val key: String) {
        LEFT    ("left"),
        RIGHT   ("right"),
    }

    enum class MouseState(val key: String) {
        UNDEFINED   ("undefined"),
        DOWN        ("down"),
        UP          ("up"),
    }

    enum class ScrollDirection(val key: String, val offset: Int) {
        DOWN    ("down", -1),
        UP      ("up", 1)
    }

    enum class MenuType(val key: String) {
        ADVANCEMENTS    ("advancements"),
        HORSE           ("horse"),
    }
}
