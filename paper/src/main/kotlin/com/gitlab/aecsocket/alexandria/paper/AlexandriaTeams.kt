package com.gitlab.aecsocket.alexandria.paper

import net.kyori.adventure.text.format.NamedTextColor

object AlexandriaTeams {
    val ColorToTeam = NamedTextColor.NAMES.keyToValue().map { (key, color) ->
        color to "ax_$key"
    }.associate { it }

    val TeamToColor = ColorToTeam.map { (a, b) -> b to a }.associate { it }

    fun colorToTeam(color: NamedTextColor) = ColorToTeam[color]
        ?: throw IllegalArgumentException("Invalid color $color")

    fun teamToColor(name: String) = TeamToColor[name]
        ?: throw IllegalArgumentException("Invalid team name '$name' to get color of")
}
