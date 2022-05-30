package com.github.aecsocket.alexandria.core

interface Input {
    data class Mouse(val button: Int, val state: Int) : Input
    object SwapHands : Input
    object Drop : Input
    data class HeldItem(val direction: Int) : Input
    data class Sneak(val now: Boolean) : Input
    data class Sprint(val now: Boolean) : Input
    data class Flight(val now: Boolean) : Input
    data class HorseJump(val now: Boolean) : Input
    object ElytraFlight : Input
    data class Menu(val menu: Int, val open: Boolean) : Input
    object LeaveBed : Input

    companion object {
        const val MOUSE_LEFT = 0
        const val MOUSE_RIGHT = 1

        const val MOUSE_UNDEFINED = -1
        const val MOUSE_DOWN = 0
        const val MOUSE_UP = 1

        const val SCROLL_DOWN = -1
        const val SCROLL_UP = 1

        const val MENU_ADVANCEMENTS = 0
        const val MENU_HORSE = 1
    }
}
