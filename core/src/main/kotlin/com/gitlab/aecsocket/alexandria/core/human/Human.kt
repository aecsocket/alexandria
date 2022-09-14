package com.gitlab.aecsocket.alexandria.core.human

import com.gitlab.aecsocket.alexandria.core.extension.Polar2
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import java.util.*

interface Human {
    val id: UUID

    val worldId: UUID
    val position: Vector3
    val heading: Polar2

    val health: Double
}
