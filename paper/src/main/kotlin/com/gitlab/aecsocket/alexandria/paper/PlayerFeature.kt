package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent

interface PlayerFeature<P : PlayerFeature.PlayerData> {
    fun createFor(player: AlexandriaPlayer): P

    interface PlayerData {
        fun dispose() {}

        fun update() {}

        fun onPacketSend(event: PacketSendEvent) {}

        fun onPacketReceive(event: PacketReceiveEvent) {}
    }
}
