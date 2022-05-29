package com.github.aecsocket.alexandria.paper.effect

import com.github.aecsocket.alexandria.core.effect.Effector
import com.github.aecsocket.alexandria.core.effect.ForwardingEffector
import com.github.aecsocket.alexandria.core.effect.ParticleEffect
import com.github.aecsocket.alexandria.core.effect.SoundEffect
import com.github.aecsocket.alexandria.core.extension.clamp01
import com.github.aecsocket.alexandria.core.extension.copy
import com.github.aecsocket.alexandria.core.vector.Vector3
import com.github.aecsocket.alexandria.paper.extension.*
import net.kyori.adventure.key.Key
import net.minecraft.core.Registry
import org.bukkit.World
import org.bukkit.craftbukkit.v1_18_R2.CraftParticle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class PaperEffectors(
    plugin: Plugin
) {
    private val players = HashMap<Player, PlayerEffector>()

    init {
        plugin.registerEvents(object : Listener {
            @EventHandler
            fun onQuit(event: PlayerQuitEvent) {
                players.remove(event.player)
            }
        })
    }

    private inner class PlayerEffector(
        val player: Player
    ) : Effector {
        override fun playSound(effect: SoundEffect, position: Vector3) {
            val distance = player.location.vector().distance(position)
            val volume = (1 - clamp01((distance - effect.dropoff) / (effect.range - effect.dropoff))).toFloat()
            player.playSound(effect.sound.copy(volume = effect.sound.volume() * volume),
                position.x, position.y, position.z)
        }

        override fun showParticle(effect: ParticleEffect, position: Vector3) {
            val particle = particleByKey(effect.particle)
                ?: throw IllegalArgumentException("Invalid particle key ${effect.particle}")
            player.spawnParticle(particle, position.location(player.world), effect.count.toInt(),
                effect.size.x, effect.size.y, effect.size.z,
                effect.speed, effect.data)
        }
    }

    private inner class WorldEffector(
        val world: World
    ) : ForwardingEffector {
        override fun effectors() = world.players.map(::player)
    }

    fun player(player: Player): Effector = players.computeIfAbsent(player) { PlayerEffector(it) }

    fun world(world: World): Effector = WorldEffector(world)

    companion object {
        fun particleByKey(key: Key) = Registry.PARTICLE_TYPE.get(key.asLocation())?.let { CraftParticle.toBukkit(it) }
    }
}