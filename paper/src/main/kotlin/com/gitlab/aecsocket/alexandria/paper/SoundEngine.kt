package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.paper.effect.SoundEffect
import com.gitlab.aecsocket.alexandria.paper.extension.scheduleDelayed
import com.gitlab.aecsocket.alexandria.paper.extension.position
import org.bukkit.World
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import kotlin.math.sqrt

private const val CONFIG_PATH = "sound_engine"
const val SPEED_IN_AIR = 340.29

class SoundEngine internal constructor(
    private val alexandria: Alexandria
) {
    @ConfigSerializable
    data class Settings(
        val speedOfSound: Double = SPEED_IN_AIR,
    )

    lateinit var settings: Settings private set

    internal fun load(settings: ConfigurationNode) {
        this.settings = settings.node(CONFIG_PATH).get { Settings() }
    }

    fun play(world: World, position: Vector3, effect: SoundEffect) {
        world.players.forEach { player ->
            val sqrDistance = player.location.position().sqrDistance(position)
            if (sqrDistance <= effect.sqrRange) {
                val distance = sqrt(sqrDistance)
                alexandria.scheduleDelayed((distance / settings.speedOfSound).toLong()) {
                    player.alexandria.effector.playSound(effect, position)
                }
            }
        }
    }
}
