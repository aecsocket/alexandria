package com.gitlab.aecsocket.alexandria.paper.human

import com.gitlab.aecsocket.alexandria.core.human.Human
import com.gitlab.aecsocket.alexandria.paper.extension.position
import org.bukkit.entity.Player

class HumanPlayer(
    val handle: Player
) : Human {
    override val id get() = handle.uniqueId

    override val worldId get() = handle.world.uid
    override val position get() = handle.location.position()

    override val health get() = handle.health
}
