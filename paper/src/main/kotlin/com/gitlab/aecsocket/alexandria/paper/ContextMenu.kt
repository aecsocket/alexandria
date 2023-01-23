package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import com.gitlab.aecsocket.alexandria.paper.extension.transform
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent

class ContextMenu internal constructor(private val alexandria: Alexandria) {
    internal fun enable() {
//        alexandria.registerEvents(object : Listener {
//            @EventHandler
//            fun on(event: PlayerAnimationEvent) {
//                val player = event.player
//                val mesh = alexandria.meshes.createText(
//                    player.eyeLocation.transform(),
//                    { setOf(player) },
//                    MeshSettings(snapping = false, small = false)
//                )
//                mesh.spawn(player)
//                mesh.name(Component.text("Test"), player)
//            }
//        })
    }
}
