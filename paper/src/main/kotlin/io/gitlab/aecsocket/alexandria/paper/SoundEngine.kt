package io.gitlab.aecsocket.alexandria.paper

import io.gitlab.aecsocket.alexandria.core.extension.clamp01
import io.gitlab.aecsocket.alexandria.paper.effect.SoundEffect
import io.gitlab.aecsocket.alexandria.paper.extension.scheduleDelayed
import io.gitlab.aecsocket.alexandria.paper.extension.position
import io.gitlab.aecsocket.alexandria.paper.extension.trackedPlayers
import org.bukkit.Location
import org.bukkit.entity.Player
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import kotlin.math.abs

const val SPEED_IN_AIR = 340.29

@ConfigSerializable
data class SoundEngineEffect(
    val all: List<SoundEffect> = emptyList(),
    val outdoors: List<SoundEffect> = emptyList(),
    val indoors: List<SoundEffect> = emptyList(),
    val mixed: List<SoundEffect> = emptyList(),
) {
    fun map(transform: (SoundEffect) -> SoundEffect) = SoundEngineEffect(
        all.map(transform),
        outdoors.map(transform),
        indoors.map(transform),
        mixed.map(transform),
    )

    companion object {
        val Empty = SoundEngineEffect()
    }
}

class SoundEngine internal constructor(
    private val alexandria: Alexandria
) {
    @ConfigSerializable
    data class Settings(
        val speedOfSound: Double = SPEED_IN_AIR,
    )

    lateinit var settings: Settings private set

    internal fun load() {
        this.settings = alexandria.settings.soundEngine
    }

    fun outdoors(location: Location): Float {
        return location.block.lightFromSky / 15f
    }

    fun play(effect: SoundEngineEffect, location: Location, targets: Iterable<Player>) {
        if (!alexandria.isEnabled) return

        val txOutdoors = outdoors(location)
        val position = location.position()
        targets.forEach { player ->
            val axPlayer = player.alexandria
            val distance = player.location.distance(location)
            val rxOutdoors = outdoors(player.location)

            // tx = 0, rx = 0 -> max indoors
            // tx = 1, rx = 0 -> max mixed
            // tx = 0, rx = 1 -> max mixed
            // rx = 1, rx = 1 -> max outdoors

            // tx = 0, rx = 0.5 ->
            //       mixed = 0.5 - 0.0 = 0.5
            //     outdoor = 0.0 - 0.5 = 0.0
            //      indoor = (1 - 0.0) - 0.5 = 0.5

            val volMixed = abs(rxOutdoors - txOutdoors)
            val volOutdoors = clamp01(txOutdoors - volMixed)
            val volIndoors = clamp01((1 - txOutdoors) - volMixed)

            fun playAll(volume: Float, effects: List<SoundEffect>) {
                effects.forEach { effect ->
                    if (distance <= effect.range) {
                        alexandria.scheduleDelayed((distance / settings.speedOfSound).toLong()) {
                            axPlayer.effector.playSound(
                                effect.copy(volume = effect.sound.volume() * volume),
                                position
                            )
                        }
                    }
                }
            }

            playAll(1f, effect.all)
            if (volOutdoors > 0f) playAll(volOutdoors, effect.outdoors)
            if (volIndoors > 0f) playAll(volIndoors, effect.indoors)
            if (volMixed > 0f) playAll(volMixed, effect.mixed)
        }
    }

    fun play(effect: SoundEngineEffect, location: Location, target: Player) {
        play(effect, location, setOf(target))
    }

    fun play(effect: SoundEngineEffect, location: Location) {
        if (!location.isChunkLoaded) return
        play(effect, location, location.chunk.trackedPlayers())
    }
}