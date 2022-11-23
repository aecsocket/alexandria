package com.gitlab.aecsocket.alexandria.paper.input

import com.gitlab.aecsocket.alexandria.core.input.Input
import org.bukkit.entity.Player

data class InputEvent(
    val player: Player,
    val input: Input,
    val cancel: () -> Unit
)

fun scrollDirection(next: Int, last: Int): Input.ScrollDirection? {
    return if (next == 0 && last == 8) Input.ScrollDirection.DOWN
    else if (next == 8 && last == 0) Input.ScrollDirection.UP
    else if (next > last) Input.ScrollDirection.DOWN
    else if (last > next) Input.ScrollDirection.UP
    else null
}
