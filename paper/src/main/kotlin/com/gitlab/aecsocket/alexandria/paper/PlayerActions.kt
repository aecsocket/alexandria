package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.Spinner
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import com.gitlab.aecsocket.alexandria.paper.extension.scheduleRepeating
import com.gitlab.aecsocket.glossa.core.I18NArgs
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import kotlin.math.min

private const val CONFIG_PATH = "actions"

fun interface PlayerAction {
    fun start(player: Player): Data

    data class Data(
        val textSlot: TextSlot? = null,
        val duration: Long? = null,
        val onUpdate: (UpdateContext) -> Unit = {},
        val onStop: (Boolean) -> Unit = {},
    )

    interface UpdateContext {
        val elapsed: Long

        fun stop(success: Boolean = false)
    }
}

data class PlayerActionInstance(
    val action: PlayerAction,
    val data: PlayerAction.Data,
    val textSlot: TextSlot,
    val startAt: Long,
) {
    var bossBar: BossBar? = null
}

private fun indeterminateProgress(sec: Double) = 1 - (1 / (sec + 1))

private val TextSlot.key get() = when (this) {
    is TextSlot.InActionBar -> "in_action_bar"
    is TextSlot.InTitle -> "in_title"
    is TextSlot.InBossBar -> "in_boss_bar"
}

class PlayerActions internal constructor(
    private val alexandria: Alexandria,
    settings: Settings = Settings()
) {
    var settings: Settings = settings
        private set

    @ConfigSerializable
    data class Settings(
        val defaultTextSlot: TextSlot = TextSlot.InActionBar,
        val spinner: Spinner<String> = Spinner(),
    )

    private val _players = HashMap<Player, PlayerActionInstance>()
    val players: Map<Player, PlayerActionInstance> get() = _players

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun PlayerQuitEvent.on() {
                stop(player)
            }
        })

        alexandria.scheduleRepeating { update() }
    }

    internal fun load(settings: ConfigurationNode) {
        this.settings = settings.node(CONFIG_PATH).get { Settings() }
    }

    private fun makeActionText(
        player: Player,
        textSlot: TextSlot,
        elapsed: Long,
        duration: Long?,
        state: String,
    ): Component? {
        val i18n = alexandria.i18nFor(player)

        val spinner = settings.spinner.state(elapsed)
        val args = I18NArgs<Component>(spinner?.let {
            mapOf("spinner" to Component.text(spinner))
        } ?: emptyMap())

        val slotKey = textSlot.key

        // todo make bar

        return duration?.let {
            val sElapsed = min(elapsed, duration)
            val remaining = duration - sElapsed
            val progress = sElapsed.toDouble() / duration

            i18n.makeOne("action.$state.$slotKey.determinate") {
                add(args)
                icu("elapsed_ms", sElapsed)
                icu("elapsed_sec", sElapsed / 1000.0)

                icu("duration_ms", duration)
                icu("duration_sec", duration / 1000.0)

                icu("remaining_ms", remaining)
                icu("remaining_sec", remaining / 1000.0)

                icu("percent_complete", progress)
                icu("percent_remaining", 1 - progress)
            }
        } ?: i18n.makeOne("action.$state.$slotKey.indeterminate") {
            add(args)
            icu("elapsed_ms", elapsed)
            icu("elapsed_sec", elapsed / 1000.0)
        }
    }

    private fun update() {
        val time = System.currentTimeMillis()
        val iter = _players.iterator()
        while (iter.hasNext()) {
            val (player, instance) = iter.next()
            val (_, data, textSlot, startAt) = instance
            val (_, duration, onUpdate) = data

            val elapsed = time - startAt
            var stopSuccess: Boolean? = null

            val progress = duration?.let {
                if (elapsed >= duration) {
                    stopSuccess = true
                }

                min(elapsed, duration) / duration
            } ?: indeterminateProgress(elapsed / 1000.0)

            makeActionText(player, textSlot, elapsed, duration, "in_progress")?.let { text ->
                when (textSlot) {
                    is TextSlot.InActionBar -> textSlot.show(player, text)
                    is TextSlot.InTitle -> textSlot.show(player, text)
                    is TextSlot.InBossBar -> {
                        instance.bossBar?.let { bar ->
                            textSlot.apply(bar, text)
                            bar.progress(progress.toFloat())
                        } ?: textSlot.createBar(text, progress.toFloat()).also {
                            player.showBossBar(it)
                            instance.bossBar = it
                        }
                    }
                }
            }

            onUpdate(object : PlayerAction.UpdateContext {
                override val elapsed get() = elapsed

                override fun stop(success: Boolean) {
                    stopSuccess = success
                }
            })

            stopSuccess?.let { success ->
                stopAction(player, instance, success)
                iter.remove()
            }
        }
    }

    private fun stopAction(player: Player, instance: PlayerActionInstance, success: Boolean) {
        val (_, data, textSlot, startAt) = instance
        val (_, duration) = data

        data.onStop(success)

        val elapsed = System.currentTimeMillis() - startAt
        val text = makeActionText(player, textSlot, elapsed, duration, if (success) "complete" else "cancelled")

        when (textSlot) {
            is TextSlot.InActionBar -> text?.let { textSlot.show(player, it) }
            is TextSlot.InTitle -> text?.let { textSlot.show(player, it) }
            is TextSlot.InBossBar -> {
                // we don't write the text to boss bar here because we're hiding anyway
                instance.bossBar?.let {
                    player.hideBossBar(it)
                }
            }
        }
    }

    operator fun get(player: Player) = _players[player]

    fun start(player: Player, action: PlayerAction): PlayerActionInstance? {
        if (_players.contains(player)) return null
        val data = action.start(player)
        return PlayerActionInstance(
            action, data,
            data.textSlot ?: settings.defaultTextSlot,
            System.currentTimeMillis()
        ).also { _players[player] = it }
    }

    fun stop(player: Player, success: Boolean = false) {
        _players[player]?.let { action ->
            stopAction(player, action, success)
            _players.remove(player)
        }
    }

    fun stopAll(success: Boolean = false) {
        _players.forEach { (player, action) ->
            stopAction(player, action, success)
        }
        _players.clear()
    }
}

val Player.action get() = AlexandriaAPI.playerActions[this]

fun Player.startAction(action: PlayerAction) = AlexandriaAPI.playerActions.start(this, action)

fun Player.stopAction(success: Boolean = false) = AlexandriaAPI.playerActions.stop(this, success)
