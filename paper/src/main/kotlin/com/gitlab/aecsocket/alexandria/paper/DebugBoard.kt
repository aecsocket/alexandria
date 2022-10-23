package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.util.*

private const val INTERNAL_NAME = "debug_board"

class DebugBoard internal constructor() : PlayerFeature<DebugBoard.PlayerData> {
    inner class PlayerData internal constructor() : PlayerFeature.PlayerData {
        var lastLines: List<String>? = null
            internal set
    }

    override fun createFor(player: AlexandriaPlayer) = PlayerData()

    fun clear(player: AlexandriaPlayer) {
        val data = player.featureData(this)
        data.lastLines?.forEach { line ->
            player.handle.sendPacket(WrapperPlayServerUpdateScore(
                line,
                WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                INTERNAL_NAME,
                Optional.empty(),
            ))
        }
        data.lastLines = emptyList()
    }

    fun show(player: AlexandriaPlayer, title: Component, lines: Iterable<Component>) {
        val data = player.featureData(this)
        player.handle.sendPacket(WrapperPlayServerScoreboardObjective(
            INTERNAL_NAME,
            if (data.lastLines == null)
                WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE
            else
                WrapperPlayServerScoreboardObjective.ObjectiveMode.UPDATE,
            title,
            WrapperPlayServerScoreboardObjective.RenderType.INTEGER,
        ))

        player.handle.sendPacket(WrapperPlayServerDisplayScoreboard(
            1,
            INTERNAL_NAME
        ))

        clear(player)

        val convertedLines = lines.map { LegacyComponentSerializer.legacySection().serialize(it) }
        data.lastLines = convertedLines

        convertedLines.reversed().forEachIndexed { idx, line ->
            player.handle.sendPacket(WrapperPlayServerUpdateScore(
                line,
                WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                INTERNAL_NAME,
                Optional.of(idx+1)
            ))
        }
    }
}

fun AlexandriaPlayer.clearDebugBoard() =
    AlexandriaAPI.debugBoard.clear(this)

fun AlexandriaPlayer.showDebugBoard(title: Component, lines: Iterable<Component>) =
    AlexandriaAPI.debugBoard.show(this, title, lines)
