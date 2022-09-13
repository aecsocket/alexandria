package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.gitlab.aecsocket.alexandria.core.input.INPUT_SWAP_HANDS
import com.gitlab.aecsocket.alexandria.core.input.InputMapper
import com.gitlab.aecsocket.alexandria.core.input.InputPredicate
import com.gitlab.aecsocket.alexandria.core.physics.*
import com.gitlab.aecsocket.alexandria.paper.extension.*
import com.gitlab.aecsocket.glossa.core.I18N
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*
import java.util.concurrent.atomic.AtomicLong

private const val CONFIG_PATH = "context_actions"

data class ContextAction(
    val getName: (I18N<Component>) -> Component,
    val useRadius: Double,
    val onUse: (UseContext) -> Unit,
) {
    interface UseContext {
        fun remove()
    }
}

data class ContextActionInstance(
    val id: Long,
    val action: ContextAction,
    val position: Vector3,
    val useShape: SphereShape,
    val displayEntityId: Int,
) {
    fun sendSpawn(player: Player, actionText: Component) {
        player.sendPacket(WrapperPlayServerSpawnEntity(
            displayEntityId, Optional.of(UUID.randomUUID()), EntityTypes.ARMOR_STAND,
            Vector3d(position.x, position.y - 0.4, position.z), 0f, 0f, 0f,
            0, Optional.empty(),
        ))

        player.sendPacket(WrapperPlayServerEntityMetadata(displayEntityId, listOf(
            EntityData(0, EntityDataTypes.BYTE, (0x20).toByte()), // invisible
            EntityData(2, EntityDataTypes.OPTIONAL_COMPONENT, Optional.of(actionText)), // custom name
            EntityData(3, EntityDataTypes.BOOLEAN, true), // custom name visible
            EntityData(4, EntityDataTypes.BOOLEAN, true), // silent
            EntityData(15, EntityDataTypes.BYTE, (0x10).toByte()), // marker
        )))
    }

    fun sendText(player: Player, actionText: Component) {
        player.sendPacket(WrapperPlayServerEntityMetadata(displayEntityId, listOf(
            EntityData(2, EntityDataTypes.OPTIONAL_COMPONENT, Optional.of(actionText)),
        )))
    }

    fun sendRemove(player: Player) {
        player.sendPacket(WrapperPlayServerDestroyEntities(displayEntityId))
    }
}

private const val NORMAL = "normal"
private const val SELECTED = "selected"
private const val UNAVAILABLE = "unavailable"

class ContextActions internal constructor(
    private val alexandria: Alexandria,
) {
    inner class ForPlayer(private val player: Player) {
        private val nextActionId = AtomicLong()

        internal val _actions = HashMap<Long, ContextActionInstance>()
        val actions: Map<Long, ContextActionInstance> get() = _actions

        var selected: Long? = null
            private set

        fun nextActionId() = nextActionId.getAndIncrement()

        fun selected() = _actions[selected]

        fun ContextActionInstance.text(state: String): Component {
            return alexandria.i18nFor(player)
                .safeOne("context_actions.display.$state") {
                    subst("action_name", action.getName(this))
                }
        }

        fun ContextActionInstance.isSelected() = id == selected

        fun ContextActionInstance.updateText() {
            sendText(player, text(
                if (player.hasLockByType(PlayerLock.UseAction)) UNAVAILABLE
                else if (isSelected()) SELECTED else NORMAL
            ))
        }

        fun create(action: ContextAction, position: Vector3): ContextActionInstance {
            val id = nextActionId()
            val entityId = bukkitNextEntityId
            return ContextActionInstance(id, action, position, SphereShape(action.useRadius), entityId).also {
                it.sendSpawn(player, it.text(NORMAL))
                _actions[id] = it
            }
        }

        fun deselect() {
            val selectedAction = selected()
            selected = null
            selectedAction?.updateText()
        }

        fun select(actionId: Long) {
            val action = _actions[actionId]
                ?: throw IllegalArgumentException("Action with ID $actionId does not exist on $player")
            if (selected != actionId) {
                deselect()
                selected = actionId
                action.updateText()
            }
        }

        fun remove(actionId: Long) = _actions.remove(actionId)?.also {
            it.sendRemove(player)
            if (it.id == selected) {
                selected = null
            }
        }

        fun remove(action: ContextActionInstance) = remove(action.id)
    }

    private val _players = HashMap<Player, ForPlayer>()
    val actions: Map<Player, ForPlayer> get() = _players

    lateinit var settings: Settings

    @ConfigSerializable
    data class Settings(
        val inputs: InputMapper = InputMapper(mapOf(
            INPUT_SWAP_HANDS to listOf(InputPredicate(listOf(
                ACTION_CANCEL, ACTION_USE, ACTION_OPEN_MENU)))
        )),
    )

    internal fun load(settings: ConfigurationNode) {
        this.settings = settings.node(CONFIG_PATH).get { Settings() }
    }

    internal fun enable() {
        val inputs = PacketInputListener(::handleInput)
        PacketEvents.getAPI().eventManager.registerListener(inputs, PacketListenerPriority.NORMAL)

        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun PlayerQuitEvent.on() {
                _players.remove(player)
            }
        })

        alexandria.scheduleRepeating {
            _players.forEach { (player, forPlayer) ->
                data class ActionBody(val instance: ContextActionInstance) : Body {
                    override val shape get() = instance.useShape
                    override val transform = Transform(instance.position)
                }

                // TODO raycast against other world stuff
                // depend on CraftBullet
                val bodies = forPlayer._actions.map { (_, instance) -> ActionBody(instance) }

                raycastOf(bodies).cast(player.eyeLocation.ray(), 4.0)?.let { cls ->
                    forPlayer.select(cls.hit.instance.id)
                } ?: forPlayer.deselect()
            }
        }

        fun onLockUpdate(player: Player, lock: PlayerLockInstance) {
            if (lock.type === PlayerLock.UseAction) {
                _players[player]?.run {
                    _actions.forEach { (_, instance) -> instance.updateText() }
                }
            }
        }

        alexandria.playerLocks.onLockAcquire { (player, lock) -> onLockUpdate(player, lock) }
        alexandria.playerLocks.onLockRelease { (player, lock) -> onLockUpdate(player, lock) }
    }

    operator fun get(player: Player) = _players.computeIfAbsent(player) { ForPlayer(player) }

    private fun handleInput(event: PacketInputListener.Event) {
        val player = event.player
        settings.inputs.actionOf(event.input, player).forEach { inputAction ->
            val processed = when (inputAction) {
                ACTION_CANCEL -> run {
                    if (player.action != null) {
                        player.stopAction()
                        return@run true
                    }
                    false
                }
                ACTION_USE -> run {
                    _players[player]?.let { forPlayer ->
                        forPlayer.selected()?.let { instance ->
                            if (!player.hasLockByType(PlayerLock.UseAction)) {
                                instance.action.onUse(object : ContextAction.UseContext {
                                    override fun remove() {
                                        forPlayer.remove(instance.id)
                                    }
                                })
                            }

                            return@run true
                        }
                    }
                    false
                }
                ACTION_OPEN_MENU -> {
                    true
                }
                else -> throw IllegalArgumentException("Unknown input action '$inputAction'")
            }
            if (processed) {
                event.cancel()
                return
            }
        }
    }

    companion object {
        const val ACTION_USE = "use"
        const val ACTION_CANCEL = "cancel"
        const val ACTION_OPEN_MENU = "open_menu"
    }
}
