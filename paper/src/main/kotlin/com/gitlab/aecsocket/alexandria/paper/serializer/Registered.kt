package com.gitlab.aecsocket.alexandria.paper.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.paper.extension.bukkit
import net.kyori.adventure.key.Key
import net.minecraft.resources.ResourceLocation
import org.bukkit.Keyed
import org.bukkit.Particle
import org.bukkit.Registry
import org.bukkit.craftbukkit.v1_19_R1.CraftParticle
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

open class RegisteredSerializer<T : Keyed>(private val registry: Registry<T>, clazz: Class<T>) : TypeSerializer<T> {
    private val className = clazz.simpleName

    override fun serialize(type: Type, obj: T?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(obj.key)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): T {
        val key = node.force<Key>()
        return registry[key.bukkit()]
            ?: throw SerializationException(node, type, "Invalid $className $key")
    }
}

inline fun <reified T : Keyed> RegisteredSerializer(registry: Registry<T>) =
    RegisteredSerializer(registry, T::class.java)

val MaterialSerializer = RegisteredSerializer(Registry.MATERIAL)

val EntityTypeSerializer = RegisteredSerializer(Registry.ENTITY_TYPE)

val StatisticSerializer = RegisteredSerializer(Registry.STATISTIC)

object ParticleSerializer : TypeSerializer<Particle> {
    override fun serialize(type: Type, obj: Particle?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            val resource = net.minecraft.core.Registry.PARTICLE_TYPE.getKey(CraftParticle.toNMS(obj).type)
                ?: throw SerializationException(node, type, "Particle $obj has no key")
            node.set(Key.key(resource.namespace, resource.path))
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Particle {
        val key = node.force<Key>()
        val resource = ResourceLocation(key.namespace(), key.value())
        val particleType = net.minecraft.core.Registry.PARTICLE_TYPE.get(resource)
            ?: throw SerializationException(node, type, "Invalid Particle $key")
        return CraftParticle.toBukkit(particleType)
    }
}
