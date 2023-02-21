package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.core.extension.force
import net.kyori.adventure.key.Key
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_19_R2.CraftParticle
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

object ParticleSerializer : TypeSerializer<Particle> {
    override fun serialize(type: Type, obj: Particle?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            val resource = BuiltInRegistries.PARTICLE_TYPE.getKey(CraftParticle.toNMS(obj).type)
                ?: throw SerializationException(node, type, "Particle $obj has no key")
            node.set(Key.key(resource.namespace, resource.path))
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Particle {
        val key = node.force<Key>()
        val resource = ResourceLocation(key.namespace(), key.value())
        val particleType = BuiltInRegistries.PARTICLE_TYPE.get(resource)
            ?: throw SerializationException(node, type, "Invalid particle $key")
        return CraftParticle.toBukkit(particleType)
    }
}
