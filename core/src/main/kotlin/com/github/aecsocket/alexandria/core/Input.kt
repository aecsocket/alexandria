package com.github.aecsocket.alexandria.core

interface Input {
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

    enum class MouseButton {
        LEFT, RIGHT
    }

    enum class MouseState {
        UNDEFINED, DOWN, UP
    }

    enum class ScrollDirection(val direction: Int) {
        DOWN    (-1),
        UP      (1)
    }

    enum class MenuType {
        ADVANCEMENTS, HORSE
    }
}
