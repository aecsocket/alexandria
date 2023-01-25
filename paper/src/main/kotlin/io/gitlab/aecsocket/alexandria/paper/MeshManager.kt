package io.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.*
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.ScoreBoardTeamInfo
import io.gitlab.aecsocket.alexandria.core.extension.EulerOrder
import io.gitlab.aecsocket.alexandria.core.extension.degrees
import io.gitlab.aecsocket.alexandria.core.extension.euler
import io.gitlab.aecsocket.alexandria.core.physics.Transform
import io.gitlab.aecsocket.alexandria.paper.extension.bukkitAir
import io.gitlab.aecsocket.alexandria.paper.extension.bukkitNextEntityId
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*

private const val INTERP_HEIGHT = -1.4385 // Y change from model to location of armor stand
private const val SNAPPING_HEIGHT = -1.8135 // Y change from model to location of armor stand on AEC
private const val SMALL_HEIGHT = 0.7125 // Y change from normal to small stand to make the model position match
private const val TEXT_HEIGHT = 1.05 // Y change from model origin to nameplate origin

enum class MeshMode {
    ITEM,
    TEXT
}

sealed interface Mesh {
    val id: UUID
    var mode: MeshMode
    var transform: Transform
    var item: ItemStack
    var glowingColor: NamedTextColor

    fun trackedPlayers(): Iterable<Player>

    fun updatePlayerTracker(playerTracker: PlayerTracker)

    fun glowing(state: Boolean, players: Iterable<Player>)

    fun glowing(state: Boolean, player: Player) = glowing(state, setOf(player))

    fun name(name: Component?, players: Iterable<Player>)

    fun name(name: Component?, player: Player) = name(name, setOf(player))

    fun spawn(players: Iterable<Player>)

    fun spawn(player: Player) = spawn(setOf(player))

    fun remove(players: Iterable<Player>)

    fun remove(player: Player) = remove(setOf(player))
}

@ConfigSerializable
data class MeshSettings(
    val snapping: Boolean = false,
    val small: Boolean = false
)

fun interface PlayerTracker {
    fun players(): Iterable<Player>
}

class MeshManager internal constructor() : PacketListener {
    private val _meshes = HashMap<UUID, BaseMesh>()
    val meshes: Map<UUID, Mesh> get() = _meshes

    fun nextMeshId() = UUID.randomUUID()

    operator fun contains(id: UUID) = _meshes.contains(id)

    operator fun get(id: UUID): Mesh? = _meshes[id]

    fun create(
        transform: Transform,
        playerTracker: PlayerTracker,
        settings: MeshSettings,
        mode: MeshMode,
        item: ItemStack
    ): Mesh {
        val id = nextMeshId()
        return (
            if (settings.snapping) SnappingMesh(id, transform, playerTracker, mode, settings.small, item)
            else InterpMesh(id, transform, playerTracker, mode, settings.small, item)
        ).also {
            _meshes[id] = it
        }
    }

    fun createItem(
        transform: Transform,
        playerTracker: PlayerTracker,
        settings: MeshSettings,
        item: ItemStack
    ) = create(transform, playerTracker, settings, MeshMode.ITEM, item)

    fun createText(
        transform: Transform,
        playerTracker: PlayerTracker,
        settings: MeshSettings
    ) = create(transform, playerTracker, settings, MeshMode.TEXT, bukkitAir)

    fun remove(id: UUID, update: Boolean = true): Mesh? {
        return _meshes.remove(id)?.also {
            if (update) {
                it.remove(it.lastTrackedPlayers)
            }
        }
    }

    fun remove(mesh: Mesh, update: Boolean = true) = remove(mesh.id, update)

    internal fun update() {
        _meshes.forEach { (_, mesh) ->
            mesh.lastTrackedPlayers = mesh.trackedPlayers()
        }
    }

    sealed class BaseMesh(
        override val id: UUID,
        transform: Transform,
        private var playerTracker: PlayerTracker,
        mode: MeshMode,
        private val small: Boolean,
        item: ItemStack,
        private val baseYOffset: Double
    ) : Mesh {
        val entityId: UUID = UUID.randomUUID()
        val protocolId = bukkitNextEntityId
        var lastTrackedPlayers: Iterable<Player> = emptySet()
        override var transform = transform
            set(value) {
                field = value
                update(transform())
            }
        override var mode = mode
            set(value) {
                field = value
                yOffset = computeYOffset()
                update(transform())
            }

        private var yOffset = computeYOffset()

        private fun computeYOffset() =
            baseYOffset + when (mode) {
                MeshMode.TEXT -> TEXT_HEIGHT
                MeshMode.ITEM -> if (small) SMALL_HEIGHT else 0.0
            }

        override var glowingColor: NamedTextColor = NamedTextColor.WHITE
            set(value) {
                // due to how we handle storing the last team applied,
                // we can't send separate glowing colors to separate players
                field = value
                val packets = glowingColor(value)
                update(packets)
            }

        override var item = item
            set(value) {
                field = value
                update(item())
            }

        override fun trackedPlayers() = playerTracker.players()

        override fun updatePlayerTracker(playerTracker: PlayerTracker) {
            this.playerTracker = playerTracker
        }

        protected abstract fun transform(): () -> List<PacketWrapper<*>>

        protected fun update(packets: () -> Iterable<PacketWrapper<*>>) {
            // trackedPlayers() instead of last* ensures that, if we've just spawned this mesh and want to change glow color,
            // we aren't using stale (empty) player list
            trackedPlayers().forEach { player ->
                packets().forEach { player.sendPacket(it) }
            }
        }

        protected fun position(transform: Transform) = transform.position.y { it + yOffset }.run {
            Vector3d(x, y, z)
        }

        private fun headRotation(transform: Transform) = transform.rotation.euler(EulerOrder.ZYX).degrees.run {
            Vector3f(x.toFloat(), -y.toFloat(), -z.toFloat())
        }


        private fun item(): () -> List<PacketWrapper<*>> = {
            val stack = SpigotConversionUtil.fromBukkitItemStack(item)
            listOf(
                WrapperPlayServerEntityEquipment(protocolId, listOf(Equipment(EquipmentSlot.HELMET, stack)))
            )
        }

        private fun glowingColor(color: NamedTextColor): () -> List<PacketWrapper<*>> = {
            val team = AlexandriaTeams.colorToTeam(color)
            val eid = entityId.toString()
            listOf(
                WrapperPlayServerTeams(team, WrapperPlayServerTeams.TeamMode.ADD_ENTITIES, null as ScoreBoardTeamInfo?, eid)
            )
        }

        protected fun transform(positionId: Int, rotationId: Int): () -> List<PacketWrapper<*>> = {
            val position = position(transform)
            val headRotation = headRotation(transform)
            listOf(
                WrapperPlayServerEntityTeleport(positionId, position, 0f, 0f, false),
                WrapperPlayServerEntityMetadata(rotationId, listOf(
                    EntityData(16, EntityDataTypes.ROTATION, headRotation)
                ))
            )
        }

        protected fun spawnStand(position: Vector3d): () -> List<PacketWrapper<*>> {
            val pItem = item()
            val pGlowingColor = glowingColor(glowingColor)
            return {
                val headRotation = headRotation(transform)
                listOf(
                    WrapperPlayServerSpawnEntity(protocolId,
                        Optional.of(entityId), EntityTypes.ARMOR_STAND,
                        position, 0f, 0f, 0f, 0, Optional.empty()
                    ),
                    WrapperPlayServerEntityMetadata(protocolId, listOf(
                        EntityData(0, EntityDataTypes.BYTE, (0x20).toByte()),
                        EntityData(15, EntityDataTypes.BYTE, ((if (small) 0x01 else 0) or 0x10).toByte()),
                        EntityData(16, EntityDataTypes.ROTATION, headRotation),
                    ))
                ) + pItem() + pGlowingColor()
            }
        }

        override fun glowing(state: Boolean, players: Iterable<Player>) {
            val flags = (0x20 or (if (state) 0x40 else 0)).toByte() // invisible + glowing?

            players.forEach { player ->
                player.sendPacket(WrapperPlayServerEntityMetadata(protocolId, listOf(
                    EntityData(0, EntityDataTypes.BYTE, flags) // invisible + glowing?
                )))
            }
        }

        override fun name(name: Component?, players: Iterable<Player>) {
            players.forEach { player ->
                player.sendPacket(WrapperPlayServerEntityMetadata(protocolId, listOf(
                    EntityData(2, EntityDataTypes.OPTIONAL_COMPONENT,
                        name?.let {
                            Optional.of(GsonComponentSerializer.gson().serialize(it))
                        } ?: Optional.empty<Component>()),
                    EntityData(3, EntityDataTypes.BOOLEAN, name != null)
                )))
            }
        }
    }

    class InterpMesh internal constructor(
        id: UUID,
        transform: Transform,
        playerTracker: PlayerTracker,
        mode: MeshMode,
        small: Boolean,
        item: ItemStack
    ) : BaseMesh(id, transform, playerTracker, mode, small, item, INTERP_HEIGHT) {
        override fun transform() = transform(protocolId, protocolId)

        override fun spawn(players: Iterable<Player>) {
            val packets = spawnStand(position(transform))
            players.forEach { player ->
                packets().forEach { player.sendPacket(it) }
            }
        }

        override fun remove(players: Iterable<Player>) {
            players.forEach { player ->
                player.sendPacket(WrapperPlayServerDestroyEntities(protocolId))
            }
        }
    }

    class SnappingMesh internal constructor(
        id: UUID,
        transform: Transform,
        playerTracker: PlayerTracker,
        mode: MeshMode,
        small: Boolean,
        item: ItemStack
    ) : BaseMesh(id, transform, playerTracker, mode, small, item, SNAPPING_HEIGHT) {
        val vehicleId = bukkitNextEntityId

        override fun transform() = transform(vehicleId, protocolId)

        override fun spawn(players: Iterable<Player>) {
            val position = position(transform)
            val pSpawnStand = spawnStand(position)
            val vehicleUuid = UUID.randomUUID()
            val passengersArray = intArrayOf(protocolId)

            players.forEach { player ->
                pSpawnStand().forEach { player.sendPacket(it) }
                player.sendPacket(WrapperPlayServerSpawnEntity(vehicleId,
                    Optional.of(vehicleUuid), EntityTypes.AREA_EFFECT_CLOUD,
                    position, 0f, 0f, 0f, 0, Optional.empty()
                ))
                player.sendPacket(WrapperPlayServerEntityMetadata(vehicleId, listOf(
                    EntityData(8, EntityDataTypes.FLOAT, 0f)
                )))
                player.sendPacket(WrapperPlayServerSetPassengers(vehicleId, passengersArray))
            }
        }

        override fun remove(players: Iterable<Player>) {
            players.forEach { player ->
                player.sendPacket(WrapperPlayServerDestroyEntities(protocolId, vehicleId))
            }
        }
    }
}
