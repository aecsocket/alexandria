package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.BarRenderer
import com.gitlab.aecsocket.alexandria.core.Spinner
import com.gitlab.aecsocket.glossa.core.I18N
import com.gitlab.aecsocket.glossa.core.I18NArgs
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import kotlin.math.min

private const val CONFIG_PATH = "player_actions"

data class PlayerAction(
    val getName: (I18N<Component>) -> Component,
    val onUpdate: (UpdateContext) -> Unit = {},
    val onStop: (StopContext) -> Unit = {},
    val duration: Long? = null,
    val textSlot: TextSlot? = null,
) {
    interface UpdateContext {
        val elapsed: Long

        fun stop(success: Boolean = false)
    }

    interface StopContext {
        val success: Boolean
        val elapsed: Long
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
) : PlayerFeature<PlayerActions.PlayerData> {
    inner class PlayerData internal constructor(
        private val player: AlexandriaPlayer
    ) : PlayerFeature.PlayerData {
        var action: PlayerActionInstance? = null

        override fun dispose() {
            stop(player)
        }

        override fun update() {
            val action = action ?: return

            val (actionType, textSlot, startAt) = action
            val (getName, onUpdate, _, duration) = actionType
            val i18n = alexandria.i18nFor(player.handle)

            val elapsed = System.currentTimeMillis() - startAt
            var stopSuccess: Boolean? = null

            val progress = duration?.let {
                if (elapsed >= duration) {
                    stopSuccess = true
                }

                min(elapsed.toDouble(), duration.toDouble()) / duration
            } ?: indeterminateProgress(elapsed / 1000.0)

            val text = makeActionText(i18n, getName, textSlot, elapsed, duration, "in_progress")

            when (textSlot) {
                is TextSlot.InActionBar -> text?.let { textSlot.show(player.handle, it) }
                is TextSlot.InTitle -> text?.let { textSlot.show(player.handle, it) }
                is TextSlot.InBossBar -> {
                    action.bossBar?.let { bar ->
                        text?.let { textSlot.apply(bar, it) }
                        bar.progress(progress.toFloat())
                    } ?: textSlot.createBar(text ?: empty(), progress.toFloat()).also {
                        player.handle.showBossBar(it)
                        action.bossBar = it
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
                stopAction(player, action, success)
            }
        }
    }

    @ConfigSerializable
    data class Settings(
        val defaultTextSlot: TextSlot = TextSlot.InActionBar,
        val spinner: Spinner<String> = Spinner(),
        val bar: BarRenderer = BarRenderer(0, "")
    )

    lateinit var settings: Settings private set

    override fun createFor(player: AlexandriaPlayer) = PlayerData(player)

    internal fun load(settings: ConfigurationNode) {
        this.settings = settings.node(CONFIG_PATH).get { Settings() }
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

    private fun stopAction(player: AlexandriaPlayer, instance: PlayerActionInstance, success: Boolean) {
        val (action, textSlot, startAt) = instance
        val (getName, _, _, duration) = action
        var elapsed = System.currentTimeMillis() - startAt
        duration?.let { elapsed = min(elapsed, it) }

        action.onStop(object : PlayerAction.StopContext {
            override val success get() = success
            override val elapsed get() = elapsed
        })

        val handle = player.handle
        val text = makeActionText(
            alexandria.i18nFor(handle), getName, textSlot, elapsed, duration,
            if (success) "complete" else "cancelled")

        when (textSlot) {
            is TextSlot.InActionBar -> text?.let { textSlot.show(handle, it) }
            is TextSlot.InTitle -> text?.let { textSlot.show(handle, it) }
            is TextSlot.InBossBar -> {
                // we don't write the text to boss bar here because we're hiding anyway
                instance.bossBar?.let {
                    handle.hideBossBar(it)
                }
            }
        }
    }

    fun actionOf(player: AlexandriaPlayer) = player.featureData(this).action

    fun start(player: AlexandriaPlayer, action: PlayerAction): PlayerActionInstance? {
        val data = player.featureData(this)
        if (data.action == null) return null

        return PlayerActionInstance(
            action, action.textSlot ?: settings.defaultTextSlot,
            System.currentTimeMillis()
        ).also { data.action = it }
    }

    fun stop(player: AlexandriaPlayer, success: Boolean = false) {
        val data = player.featureData(this)
        data.action?.let { action ->
            stopAction(player, action, success)
            data.action = null
        }
    }
}

val AlexandriaPlayer.action get() = AlexandriaAPI.playerActions.actionOf(this)

fun AlexandriaPlayer.startAction(action: PlayerAction) = AlexandriaAPI.playerActions.start(this, action)

fun AlexandriaPlayer.stopAction(success: Boolean = false) = AlexandriaAPI.playerActions.stop(this, success)
