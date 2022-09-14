package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.gitlab.aecsocket.alexandria.core.input.InputMapper
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val CONFIG_PATH = "context_actions"

data class ContextAction(
    val getName: (I18N<Component>) -> Component,
    val onUse: (UseContext) -> Unit,
) {
    interface UseContext {
        val forPlayer: ContextActions.ForPlayer

        fun remove()
    }
}

data class ContextActionInstance(
    val id: Long,
    val action: ContextAction,
    var position: Vector3,
    val displayEntityId: Int,
) {
    private fun Vector3.corrected() = Vector3d(x, y - 0.4, z)

    fun sendSpawn(player: Player, actionText: Component) {
        player.sendPacket(WrapperPlayServerSpawnEntity(
            displayEntityId, Optional.of(UUID.randomUUID()), EntityTypes.ARMOR_STAND,
            position.corrected(), 0f, 0f, 0f,
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

    fun sendPosition(player: Player, position: Vector3) {
        player.sendPacket(WrapperPlayServerEntityTeleport(displayEntityId,
            position.corrected(), 0f, 0f, false))
    }

    fun sendRemove(player: Player) {
        player.sendPacket(WrapperPlayServerDestroyEntities(displayEntityId))
    }
}

private const val NORMAL = "normal"
private const val SELECTED = "selected"
private const val UNAVAILABLE = "unavailable"

@ConfigSerializable
data class ActionRadialSettings(
    val radius: Double = 0.0,
    val transform: Transform = Transform.Identity,
    val circleStart: Double = 0.0,
)

data class ActionRadialInstance(
    val settings: ActionRadialSettings,
    val origin: Transform,
    val items: List<Item>,
) {
    data class Item(
        val action: ContextActionInstance,
        val offset: Vector3,
    )
}

fun interface RadialPopulator {
    fun populate(player: Player): List<ContextAction>
}

class ContextActions internal constructor(
    private val alexandria: Alexandria,
) {
    inner class ForPlayer(private val player: Player) {
        private val nextActionId = AtomicLong()

        internal val _actions = HashMap<Long, ContextActionInstance>()
        val actions: Map<Long, ContextActionInstance> get() = _actions

        val radialPopulators: MutableList<RadialPopulator> = ArrayList<RadialPopulator>()

        var radial: ActionRadialInstance? = null
            private set

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

        fun ContextActionInstance.position(position: Vector3) {
            this.position = position
            sendPosition(player, position)
        }

        internal fun update() {
            radial?.let { radial ->
                // move to the player's new position
                // but keep the same relative rotations as when they opened the menu
                val transform = Transform(
                    player.eyeLocation.position(),
                    radial.origin.rotation,
                )
                radial.items.forEach { item ->
                    val position = transform + radial.settings.transform + Transform(item.offset)
                    item.action.position(position.translation)
                }
            }
        }

        fun create(action: ContextAction, position: Vector3): ContextActionInstance {
            val id = nextActionId()
            val entityId = bukkitNextEntityId
            return ContextActionInstance(id, action, position, entityId).also {
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

        fun createRadial(
            actions: List<ContextAction>,
            origin: Transform = player.eyeLocation.transform(),
            settings: ActionRadialSettings = this@ContextActions.settings.radial
        ): ActionRadialInstance {
            removeRadial()
            if (actions.isEmpty()) throw IllegalArgumentException("Items must be non-empty")

            return ActionRadialInstance(settings, origin, actions.mapIndexed { idx, action ->
                // arrange items in a circle in front of the player
                val progress = (idx.toDouble() / actions.size + settings.circleStart) * 2*PI
                val offset = Vector3(
                    cos(progress) * settings.radius,
                    sin(progress) * settings.radius,
                    0.0,
                )
                val position = (origin + settings.transform + Transform(offset)).translation

                ActionRadialInstance.Item(
                    // action auto-removes the radial when selected
                    create(action.copy(onUse = { ctx ->
                        ctx.forPlayer.removeRadial()
                        action.onUse(ctx)
                    }), position),
                    offset
                )
            }).also {
                radial = it
            }
        }

        fun removeRadial() {
            radial?.let { radial ->
                radial.items.forEach { remove(it.action) }
            }
            radial = null
        }

        fun radialPopulator(populator: RadialPopulator) {
            radialPopulators.add(populator)
        }
    }

    @ConfigSerializable
    data class Settings(
        val inputs: InputMapper = InputMapper.Empty,
        val useRadius: Double = 0.0,
        val radial: ActionRadialSettings = ActionRadialSettings(),
    )

    private val _players = HashMap<Player, ForPlayer>()
    val actions: Map<Player, ForPlayer> get() = _players

    lateinit var settings: Settings

    private lateinit var useShape: SphereShape

    internal fun load(settings: ConfigurationNode) {
        this.settings = settings.node(CONFIG_PATH).get { Settings() }
        useShape = SphereShape(this.settings.useRadius)
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
                forPlayer.update()

                data class ActionBody(val instance: ContextActionInstance) : Body {
                    override val shape get() = useShape
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
                                    override val forPlayer get() = forPlayer

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
                ACTION_CLOSE_RADIAL -> run {
                    _players[player]?.let { forPlayer ->
                        if (forPlayer.radial != null) {
                            forPlayer.removeRadial()
                            return@run true
                        }
                    }
                    false
                }
                ACTION_OPEN_RADIAL -> run {
                    val forPlayer = get(player)
                    val actions = ArrayList<ContextAction>()
                    forPlayer.radialPopulators.forEach {
                        actions.addAll(it.populate(player))
                    }
                    if (actions.isNotEmpty()) {
                        forPlayer.createRadial(actions)
                        return@run true
                    }
                    false
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
        const val ACTION_CLOSE_RADIAL = "close_radial"
        const val ACTION_OPEN_RADIAL = "open_radial"
    }
}
