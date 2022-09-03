package com.gitlab.aecsocket.alexandria.paper.effect

import com.gitlab.aecsocket.alexandria.core.effect.Effector
import com.gitlab.aecsocket.alexandria.core.effect.SoundEffect
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.paper.extension.scheduleDelayed
import com.gitlab.aecsocket.alexandria.paper.extension.position
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.math.sqrt

class SimulatedSounds(
    val plugin: Plugin,
    val effectorOf: (Player) -> Effector,
    var speed: Double = SPEED_IN_AIR // m/s,
) {
    fun play(world: World, position: Vector3, effect: SoundEffect) {
        world.players.forEach { player ->
            val sqrDistance = player.location.position().sqrDistance(position)
            if (sqrDistance <= effect.sqrRange) {
                val distance = sqrt(sqrDistance)
                plugin.scheduleDelayed((distance / speed).toLong()) {
                    effectorOf(player).playSound(effect, position)
                }
            }
        }
    }

    companion object {
        const val SPEED_IN_AIR = 340.29
    }
}
