package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

private const val INTERNAL_NAME = "debug_board"

class DebugBoard internal constructor(
    private val alexandria: Alexandria,
) {
    private val boards = HashMap<Player, List<String>>()

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun PlayerQuitEvent.on() {
                boards.remove(player)
            }
        })
    }

    fun clear(player: Player) {
        boards[player]?.let { lines ->
            lines.forEach { line ->
                player.sendPacket(WrapperPlayServerUpdateScore(
                    line,
                    WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                    INTERNAL_NAME,
                    Optional.empty(),
                ))
            }
        }
        boards[player] = emptyList()
    }

    fun show(player: Player, title: Component, lines: Iterable<Component>) {
        player.sendPacket(WrapperPlayServerScoreboardObjective(
            INTERNAL_NAME,
            if (boards.contains(player))
                WrapperPlayServerScoreboardObjective.ObjectiveMode.UPDATE
            else
                WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE,
            title,
            WrapperPlayServerScoreboardObjective.RenderType.INTEGER,
        ))

        player.sendPacket(WrapperPlayServerDisplayScoreboard(
            1,
            INTERNAL_NAME
        ))

        val convertedLines = lines.map { LegacyComponentSerializer.legacySection().serialize(it) }

        boards[player]?.let { old ->
            old.forEach { line ->
                if (!convertedLines.contains(line))
                    player.sendPacket(WrapperPlayServerUpdateScore(
                        line,
                        WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                        INTERNAL_NAME,
                        Optional.empty()
                    ))
            }
        }

        boards[player] = convertedLines

        convertedLines.reversed().forEachIndexed { idx, line ->
            player.sendPacket(WrapperPlayServerUpdateScore(
                line,
                WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                INTERNAL_NAME,
                Optional.of(idx+1)
            ))
        }
    }
}

fun Player.clearDebugBoard() =
    AlexandriaAPI.debugBoard.clear(this)

fun Player.showDebugBoard(title: Component, lines: Iterable<Component>) =
    AlexandriaAPI.debugBoard.show(this, title, lines)
