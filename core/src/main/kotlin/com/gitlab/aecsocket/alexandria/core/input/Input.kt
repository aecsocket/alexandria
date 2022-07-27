package com.gitlab.aecsocket.alexandria.core.input

sealed interface Input {
    data class Mouse(val button: MouseButton, val state: MouseState) : Input
    object SwapHands : Input
    object Drop : Input
    data class HeldItem(val direction: ScrollDirection) : Input
    data class Sneak(val now: Boolean) : Input
    data class Sprint(val now: Boolean) : Input
    data class Flight(val now: Boolean) : Input
    data class HorseJump(val now: Boolean) : Input
    object ElytraFlight : Input
    data class Menu(val menu: MenuType, val open: Boolean) : Input
    object LeaveBed : Input

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
