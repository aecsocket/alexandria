package com.gitlab.aecsocket.alexandria.paper

interface PlayerFeature<P : PlayerFeature.PlayerData> {
    fun createFor(player: AlexandriaPlayer): P

    interface PlayerData {
        fun dispose() {}
    }
}
