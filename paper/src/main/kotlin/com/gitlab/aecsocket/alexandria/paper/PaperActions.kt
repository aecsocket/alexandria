package com.gitlab.aecsocket.alexandria.paper

import org.bukkit.entity.Player

interface Action

class PaperActions internal constructor(private val alexandria: Alexandria) {
    private val _active = HashMap<Player, Action>()
}
