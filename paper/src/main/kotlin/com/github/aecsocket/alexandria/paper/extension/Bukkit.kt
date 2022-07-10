package com.github.aecsocket.alexandria.paper.extension

import com.github.aecsocket.alexandria.core.extension.*
import com.github.aecsocket.alexandria.core.physics.Quaternion
import com.github.aecsocket.alexandria.core.physics.Transform
import org.bukkit.Bukkit
import org.bukkit.entity.Entity

val bukkitCurrentTick get() = Bukkit.getCurrentTick()
@Suppress("DEPRECATION")
val bukkitNextEntityId get() = Bukkit.getUnsafe().nextEntityId()
val bukkitPlayers get() = Bukkit.getOnlinePlayers()

var Entity.transform: Transform
    get() = Transform(
        // we ignore pitch, because entities can't really *rotate* up/down
        rot = Euler3(y = -location.yaw.radians.toDouble()).quaternion(EulerOrder.ZYX),
        tl = location.vector(),
    )
    set(value) {
        val (x, y, z) = value.tl
        val yaw = value.rot.euler(EulerOrder.XYZ).yaw.degrees.toFloat()
        val location = location.copy(x = x, y = y, z = z, yaw = yaw)
        teleport(location)
    }

var Entity.looking: Quaternion
    // here we DON'T ignore pitch, since entities can *look* up/down
    get() = Euler3(location.pitch.toDouble(), -location.yaw.toDouble()).radians.quaternion(EulerOrder.ZYX)
    set(value) {
        val (pitch, yaw) = value.euler(EulerOrder.XYZ).degrees
        setRotation(yaw.toFloat(), pitch.toFloat())
    }
