package com.github.aecsocket.alexandria.paper.effect

import com.github.aecsocket.alexandria.core.effect.Effector
import com.github.aecsocket.alexandria.core.effect.ForwardingEffector
import com.github.aecsocket.alexandria.core.effect.ParticleEffect
import com.github.aecsocket.alexandria.core.effect.SoundEffect
import com.github.aecsocket.alexandria.core.extension.clamp01
import com.github.aecsocket.alexandria.core.extension.copy
import com.github.aecsocket.alexandria.core.physics.Vector3
import com.github.aecsocket.alexandria.paper.extension.location
import com.github.aecsocket.alexandria.paper.extension.registerEvents
import com.github.aecsocket.alexandria.paper.extension.vector
import net.kyori.adventure.key.Key
import net.minecraft.core.Registry
import org.bukkit.World
import org.bukkit.craftbukkit.v1_18_R2.CraftParticle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class PaperEffectors {
    private val players = HashMap<Player, PlayerEffector>()

    fun init(plugin: Plugin) {
        plugin.registerEvents(object : Listener {
            @EventHandler
            fun PlayerQuitEvent.on() {
                players.remove(player)
            }
        })
    }

    private inner class PlayerEffector(
        val player: Player
    ) : Effector {
        override fun playSound(effect: SoundEffect, position: Vector3) {
            val playerPos =  player.location.vector()
            val delta = position - playerPos
            val distance = delta.length
            val (dx, dy, dz) = (delta / distance) * 8.0
            val volume = (1 - clamp01((distance - effect.dropoff) / (effect.range - effect.dropoff))).toFloat()
            player.playSound(effect.sound.copy(volume = effect.sound.volume() * volume),
                position.x + dx, position.y + dy, position.z + dz)
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
}

fun particleByKey(key: Key) = Registry.PARTICLE_TYPE.get(key.location())?.let { CraftParticle.toBukkit(it) }

fun Iterable<SoundEffect>.playGlobal(effectors: PaperEffectors, world: World, position: Vector3) =
    forEach { effectors.world(world).playSound(it, position) }

fun Iterable<ParticleEffect>.showGlobal(effectors: PaperEffectors, world: World, position: Vector3) =
    forEach { effectors.world(world).showParticle(it, position) }
