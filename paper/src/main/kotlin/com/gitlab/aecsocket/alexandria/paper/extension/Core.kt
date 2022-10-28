package com.gitlab.aecsocket.alexandria.paper.extension

import com.gitlab.aecsocket.alexandria.core.input.Input
import com.gitlab.aecsocket.alexandria.core.input.InputMapper
import org.bukkit.entity.Player

private const val PLAYER_SNEAKING = "player_sneaking"
private const val PLAYER_NOT_SNEAKING = "player_not_sneaking"

private const val PLAYER_SPRINTING = "player_sprinting"
private const val PLAYER_NOT_SPRINTING = "player_not_sprinting"

private const val PLAYER_FLYING = "player_flying"
private const val PLAYER_NOT_FLYING = "player_not_flying"

fun <V> InputMapper<V>.getForPlayer(input: Input, player: Player, tags: Collection<String> = emptySet()): V? {
    return get(input, setOf(
        if (player.isSneaking) PLAYER_SNEAKING else PLAYER_NOT_SNEAKING,
        if (player.isSprinting) PLAYER_SPRINTING else PLAYER_NOT_SPRINTING,
        if (player.isFlying) PLAYER_FLYING else PLAYER_NOT_FLYING,
    ) + tags)
}
