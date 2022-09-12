package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.paper.extension.location
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import com.gitlab.aecsocket.alexandria.paper.human.HumanPlayer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Pig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*

private const val CONFIG_PATH = "persistence"
private const val TABLE_NAME = "player_persistence"

class PlayerPersistence internal constructor(
    private val alexandria: Alexandria,
) {
    lateinit var settings: Settings private set

    @ConfigSerializable
    data class Settings(
        val enabled: Boolean = false,
        val save: Boolean = true,
        val load: Boolean = true,
    )

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun PlayerQuitEvent.on() {
                if (!settings.enabled || !settings.save) return

                val human = HumanPlayer(player)
                alexandria.useDb { conn ->
                    conn.prepareStatement(
                        "REPLACE INTO $TABLE_NAME VALUES (?, ?, ?, ?, ?, ?)"
                    ).use { stmt ->
                        stmt.setString(1, human.id.toString())
                        stmt.setString(2, human.worldId.toString())
                        stmt  .setLong(3, player.chunk.chunkKey)
                        stmt.setDouble(4, human.position.x)
                        stmt.setDouble(5, human.position.y)
                        stmt.setDouble(6, human.position.z)
                        stmt.executeUpdate()
                    }
                }
            }

            @EventHandler
            fun PlayerJoinEvent.on() {
                if (!settings.enabled || !settings.save) return

                val playerId = player.uniqueId.toString()
                val world = player.world
                val expectedWorldId = world.uid.toString()

                alexandria.useDb { conn ->
                    conn.prepareStatement(
                        """SELECT world_id, position_x, position_y, position_z FROM $TABLE_NAME
                        WHERE player_id = ?""".trimIndent()
                    ).use { stmt ->
                        stmt.setString(1, playerId)
                        val results = stmt.executeQuery()

                        if (results.next()) {
                            val worldId = results.getString(1)

                            // make sure the world hasn't reset or something
                            // otherwise we can't trust our data
                            if (expectedWorldId == worldId) {
                                val position = Vector3(
                                    results.getDouble(2),
                                    results.getDouble(3),
                                    results.getDouble(4),
                                )

                                player.teleportAsync(position.location(world))
                            }
                        }
                    }
                }
            }

            @EventHandler
            fun EntitiesLoadEvent.on() {
                if (!settings.enabled || !settings.load) return

                val worldId = world.uid.toString()
                val chunkKey = chunk.chunkKey

                alexandria.useDb { conn ->
                    conn.prepareStatement(
                        """SELECT player_id, position_x, position_y, position_z FROM $TABLE_NAME
                        WHERE world_id = ? AND chunk_key = ?""".trimIndent()
                    ).use { stmt ->
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

                            println(" > spawned zomboid for $playerId @ $position")
                        }
                    }
                }
            }
        })
    }

    internal fun load(settings: ConfigurationNode) {
        this.settings = settings.node(CONFIG_PATH).get { Settings() }
        if (!this.settings.enabled) return

        alexandria.useDb { conn -> conn.createStatement().use { stmt ->
            stmt.executeUpdate(
                """CREATE TABLE IF NOT EXISTS player_persistence (
                player_id   VARCHAR(36) PRIMARY KEY  NOT NULL,
                world_id    VARCHAR(36)              NOT NULL,
                chunk_key   INTEGER                  NOT NULL,
                position_x  REAL                     NOT NULL,
                position_y  REAL                     NOT NULL,
                position_z  REAL                     NOT NULL)
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
    }
}
