package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.BarRenderer
import com.gitlab.aecsocket.alexandria.core.Spinner
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import com.gitlab.aecsocket.alexandria.paper.extension.scheduleRepeating
import com.gitlab.aecsocket.glossa.core.I18N
import com.gitlab.aecsocket.glossa.core.I18NArgs
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import kotlin.math.min

private const val CONFIG_PATH = "actions"

data class PlayerAction(
    val getName: (I18N<Component>) -> Component,
    val onUpdate: (UpdateContext) -> Unit = {},
    val onStop: (Boolean) -> Unit = {},
    val duration: Long? = null,
    val textSlot: TextSlot? = null,
) {
    interface UpdateContext {
        val elapsed: Long

        fun stop(success: Boolean = false)
    }
}

data class PlayerActionInstance(
    val action: PlayerAction,
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
) {
    lateinit var settings: Settings private set

    @ConfigSerializable
    data class Settings(
        val defaultTextSlot: TextSlot = TextSlot.InActionBar,
        val spinner: Spinner<String> = Spinner(),
        val bar: BarRenderer = BarRenderer(0, "")
    )

    private val _players = HashMap<Player, PlayerActionInstance>()
    val players: Map<Player, PlayerActionInstance> get() = _players

    internal fun load(settings: ConfigurationNode) {
        this.settings = settings.node(CONFIG_PATH).get { Settings() }
    }

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun PlayerQuitEvent.on() {
                stop(player)
            }
        })

        alexandria.scheduleRepeating { update() }
    }

    private fun makeActionText(
        i18n: I18N<Component>,
        getActionName: (I18N<Component>) -> Component,
        textSlot: TextSlot,
        elapsed: Long,
        duration: Long?,
        state: String,
    ): Component? {
        val spinner = settings.spinner.state(elapsed)
        val args = I18NArgs.Scope(i18n).apply {
            subst("action_name", getActionName(this))
            spinner?.let {
                subst("spinner", text(spinner))
            }
        }.build()

        val slotKey = textSlot.key

        return duration?.let {
            val sElapsed = min(elapsed, duration)
            val remaining = duration - sElapsed
            val progress = sElapsed.toDouble() / duration

            val (barComplete, barBackground) = settings.bar.renderOne(progress.toFloat())

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

                subst("bar_complete", barComplete)
                subst("bar_background", barBackground)
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
            val (action, textSlot, startAt) = instance
            val (getName, onUpdate, _, duration) = action
            val i18n = alexandria.i18nFor(player)

            val elapsed = time - startAt
            var stopSuccess: Boolean? = null

            val progress = duration?.let {
                if (elapsed >= duration) {
                    stopSuccess = true
                }

                min(elapsed.toDouble(), duration.toDouble()) / duration
            } ?: indeterminateProgress(elapsed / 1000.0)

            val text = makeActionText(
                i18n, getName, textSlot, elapsed, duration, "in_progress")

            when (textSlot) {
                is TextSlot.InActionBar -> text?.let { textSlot.show(player, it) }
                is TextSlot.InTitle -> text?.let { textSlot.show(player, it) }
                is TextSlot.InBossBar -> {
                    instance.bossBar?.let { bar ->
                        text?.let { textSlot.apply(bar, it) }
                        bar.progress(progress.toFloat())
                    } ?: textSlot.createBar(text ?: empty(), progress.toFloat()).also {
                        player.showBossBar(it)
                        instance.bossBar = it
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
        val (action, textSlot, startAt) = instance
        val (getName, _, _, duration) = action

        action.onStop(success)

        val elapsed = System.currentTimeMillis() - startAt
        val text = makeActionText(
            alexandria.i18nFor(player), getName, textSlot, elapsed, duration,
            if (success) "complete" else "cancelled")

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
        return PlayerActionInstance(
            action, action.textSlot ?: settings.defaultTextSlot,
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
