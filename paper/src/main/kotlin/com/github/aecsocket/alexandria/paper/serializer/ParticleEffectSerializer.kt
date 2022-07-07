package com.github.aecsocket.alexandria.paper.serializer

import com.github.aecsocket.alexandria.core.effect.ParticleEffect
import com.github.aecsocket.alexandria.core.extension.force
import com.github.aecsocket.alexandria.core.spatial.Vector3
import com.github.aecsocket.alexandria.paper.effect.particleByKey
import net.kyori.adventure.key.Key
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val PARTICLE = "particle"
private const val COUNT = "count"
private const val SIZE = "size"
private const val SPEED = "speed"
private const val DATA = "data"

object ParticleEffectSerializer : TypeSerializer<ParticleEffect> {
    override fun serialize(type: Type, obj: ParticleEffect?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.node(PARTICLE).set(obj.particle)
            node.node(COUNT).set(obj.count)
            node.node(SIZE).set(obj.size)
            node.node(SPEED).set(obj.speed)
            obj.data?.let { node.node(DATA).set(it) }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): ParticleEffect {
        val particle = node.node(PARTICLE).force<Key>()
        val data = particleByKey(particle)?.let {
            when (val dataType = it.dataType) {
                Void::class.java -> null
                /* note: it's valid to have the data be null here
                   even if the particle is deserialized with no data,
                   and the particle requires data,
                   it can always be provided through code later */
                else -> node.node(DATA).get(dataType)
            }
        }
        return ParticleEffect(
            node.node(PARTICLE).force(),
            node.node(COUNT).get { 0.0 },
            node.node(SIZE).get { Vector3.Zero },
            node.node(SPEED).get { 0.0 },
            data
        )
    }
}
