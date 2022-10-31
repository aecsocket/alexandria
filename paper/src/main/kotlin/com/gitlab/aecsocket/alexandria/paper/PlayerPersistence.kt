package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.LogLevel
import com.gitlab.aecsocket.alexandria.core.extension.Polar2
import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.extension.pitch
import com.gitlab.aecsocket.alexandria.core.extension.yaw
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.paper.Alexandria.Companion.namespaced
import com.gitlab.aecsocket.alexandria.paper.extension.isEmpty
import com.gitlab.aecsocket.alexandria.paper.extension.location
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import com.gitlab.aecsocket.alexandria.paper.human.HumanPlayer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Pig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.inventory.ItemStack
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.io.File
import java.util.*

private const val TABLE_NAME = "player_persistence"
private const val DATA_PATH = "player_persistence"

private val HEALTH = namespaced("health")
private val FOOD = namespaced("food")
private val SATURATION = namespaced("saturation")
private val INVENTORY = namespaced("inventory")

class PlayerPersistence internal constructor(
    private val alexandria: Alexandria,
) {
    @ConfigSerializable
    data class Settings(
        val enabled: Boolean = false,
        val save: Boolean = true,
        val load: Boolean = true,
    )

    data class OnSave(
        val player: Player,
        val configNode: ConfigurationNode,
    )

    data class OnLoad(
        val player: Player,
        val configNode: ConfigurationNode,
    )

    lateinit var settings: Settings private set
    private lateinit var dataFolder: File

    private val onSave = ArrayList<(OnSave) -> Unit>()
    private val onLoad = ArrayList<(OnLoad) -> Unit>()

    internal fun load() {
        this.settings = alexandria.settings.playerPersistence
        if (!this.settings.enabled) return

        alexandria.useDb { conn -> conn.createStatement().use { stmt ->
            stmt.executeUpdate(
                """CREATE TABLE IF NOT EXISTS player_persistence (
                player_id      VARCHAR(36)  PRIMARY KEY NOT NULL,
                world_id       VARCHAR(36)              NOT NULL,
                chunk_key      INTEGER                  NOT NULL,
                position_x     REAL                     NOT NULL,
                position_y     REAL                     NOT NULL,
                position_z     REAL                     NOT NULL,
                heading_pitch  REAL                     NOT NULL,
                heading_yaw    REAL                     NOT NULL)
            """.trimIndent())

            stmt.executeUpdate(
                """CREATE INDEX IF NOT EXISTS idx_persistence_world
                ON player_persistence (world_id)
            """.trimIndent())

            stmt.executeUpdate(
                """CREATE INDEX IF NOT EXISTS idx_persistence_chunk
                ON player_persistence (chunk_key)
            """.trimIndent())
        } }

        dataFolder = alexandria.dataFolder.resolve(DATA_PATH)
        dataFolder.mkdirs()
    }

    fun dataFileFor(id: UUID) = dataFolder.resolve("$id.conf")

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun on(event: PlayerQuitEvent) {
                if (!settings.enabled || !settings.save) return

                val player = event.player
                val human = HumanPlayer(player)
                val playerId = human.id
                alexandria.useIO {
                    alexandria.useDb { conn ->
                        conn.prepareStatement(
                            "REPLACE INTO $TABLE_NAME VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                        ).use { stmt ->
                            stmt.setString(1, human.id.toString())
                            stmt.setString(2, human.worldId.toString())
                            stmt  .setLong(3, player.chunk.chunkKey)
                            stmt.setDouble(4, human.position.x)
                            stmt.setDouble(5, human.position.y)
                            stmt.setDouble(6, human.position.z)
                            stmt.setDouble(7, human.heading.pitch)
                            stmt.setDouble(8, human.heading.yaw)
                            stmt.executeUpdate()
                        }
                    }

                    val loader = alexandria.configLoader().file(dataFileFor(playerId)).build()
                    val configNode = loader.createNode()

                    val persistEvent = OnSave(player, configNode)
                    onSave.forEach { it(persistEvent) }

                    loader.save(configNode)
                }
            }

            @EventHandler
            fun on(event: PlayerJoinEvent) {
                if (!settings.enabled || !settings.save) return

                val player = event.player
                val playerId = player.uniqueId
                val world = player.world
                val expectedWorldId = world.uid.toString()

                alexandria.useDb { conn ->
                    conn.prepareStatement("""
                        SELECT world_id, position_x, position_y, position_z, heading_pitch, heading_yaw FROM $TABLE_NAME
                        WHERE player_id = ?
                    """.trimIndent()).use { stmt ->
                        stmt.setString(1, playerId.toString())
                        val results = stmt.executeQuery()

                        if (results.next()) {
                            val worldId = results.getString(1)

                            // make sure the world hasn't reset or something
                            // otherwise we can't trust our data
                            if (expectedWorldId != worldId) {
                                alexandria.log.line(LogLevel.Verbose) { "Player $player ($playerId) spawned in $worldId (${world.name}) but expected $expectedWorldId" }
                                return@use
                            }

                            val position = Vector3(
                                results.getDouble(2),
                                results.getDouble(3),
                                results.getDouble(4),
                            )
                            val heading = Polar2(
                                results.getDouble(5),
                                results.getDouble(6),
                            )

                            player.teleportAsync(position.location(world, heading))

                            val loader = alexandria.configLoader().file(dataFileFor(playerId)).build()
                            val configNode = loader.load()

                            val persistEvent = OnLoad(player, configNode)
                            onLoad.forEach { it(persistEvent) }
                        }
                    }
                }
            }

            @EventHandler
            fun on(event: EntitiesLoadEvent) {
                if (!settings.enabled || !settings.load) return

                val world = event.world
                val chunk = event.chunk
                val worldId = world.uid.toString()
                val chunkKey = chunk.chunkKey

                alexandria.useDb { conn ->
                    conn.prepareStatement("""
                        SELECT player_id, position_x, position_y, position_z FROM $TABLE_NAME
                        WHERE world_id = ? AND chunk_key = ?
                    """.trimIndent()).use { stmt ->
                        stmt.setString(1, worldId)
                        stmt.  setLong(2, chunkKey)
                        val results = stmt.executeQuery()

                        while (results.next()) {
                            val playerId = UUID.fromString(results.getString(1))
                            val position = Vector3(
                                results.getDouble(2),
                                results.getDouble(3),
                                results.getDouble(4)
                            )

                            val offlinePlayer = Bukkit.getOfflinePlayer(playerId)

                            world.spawnEntity(position.location(world), EntityType.PIG, CreatureSpawnEvent.SpawnReason.CUSTOM) { entity ->
                                entity as Pig
                                entity.setAI(false)
                                entity.removeWhenFarAway = false
                                entity.isSilent = true
                                entity.isCustomNameVisible = true
                                offlinePlayer.name?.let { entity.customName(Component.text(it)) }
                            }
                        }
                    }
                }
            }
        })

        onSave { (player, configNode) ->
            configNode.node(HEALTH).set(player.health)
            configNode.node(FOOD).set(player.foodLevel)
            configNode.node(SATURATION).set(player.saturation)

            val inventory = configNode.node(INVENTORY)
            player.inventory.contents.forEach { item ->
                if (item.isEmpty()) {
                    inventory.appendListNode().set("")
                } else {
                    inventory.appendListNode().set(Base64.getEncoder().encodeToString(item!!.serializeAsBytes()))
                }
            }
        }

        onLoad { (player, configNode) ->
            configNode.node(HEALTH).get<Double>()?.let { player.health = it }
            configNode.node(FOOD).get<Int>()?.let { player.foodLevel = it }
            configNode.node(SATURATION).get<Float>()?.let { player.saturation = it }

            val inventory = player.inventory
            configNode.node(INVENTORY).childrenList().forEachIndexed { idx, child ->
                val rawValue = child.force<String>()
                val item = if (rawValue.isEmpty()) null
                    else ItemStack.deserializeBytes(Base64.getDecoder().decode(rawValue))
                inventory.setItem(idx, item)
            }
        }
    }

    fun onSave(listener: (OnSave) -> Unit) {
        onSave.add(listener)
    }

    fun onLoad(listener: (OnLoad) -> Unit) {
        onLoad.add(listener)
    }
}
