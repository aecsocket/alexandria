package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth
import org.bukkit.entity.Player

internal class AlexandriaPacketListener(
    private val alexandria: Alexandria
) : PacketListenerAbstract(PacketListenerPriority.HIGH) {
    override fun onPacketSend(event: PacketSendEvent) {
        val player = event.player as? Player ?: return
        when (event.packetType) {
            PacketType.Play.Server.UPDATE_HEALTH -> {
                val packet = WrapperPlayServerUpdateHealth(event)
                if (player.hasLockByType(PlayerLock.Sprint)) {
                    packet.food = NO_SPRINT_FOOD
                }
            }
        }
    }
}
