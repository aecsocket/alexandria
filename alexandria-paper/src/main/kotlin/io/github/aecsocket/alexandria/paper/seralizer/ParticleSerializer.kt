package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.desc.ParticleType
import io.github.aecsocket.alexandria.extension.force
import io.github.aecsocket.alexandria.paper.PaperParticleType
import io.github.aecsocket.alexandria.paper.extension.toResourceLocation
import java.lang.reflect.Type
import net.kyori.adventure.key.Key
import net.minecraft.core.registries.BuiltInRegistries
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_19_R3.CraftParticle
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer

fun particleToKey(particle: Particle): Key {
  val loc =
      BuiltInRegistries.PARTICLE_TYPE.getKey(CraftParticle.toNMS(particle).type)
          ?: throw IllegalStateException("Particle $particle has no key")
  return Key.key(loc.namespace, loc.path)
}

fun particleFromKey(key: Key): Particle? {
  val type = BuiltInRegistries.PARTICLE_TYPE.get(key.toResourceLocation()) ?: return null
  return CraftParticle.toBukkit(type)
}

object ParticleSerializer : TypeSerializer<Particle> {
  override fun serialize(type: Type, obj: Particle?, node: ConfigurationNode) {
    if (obj == null) node.set(null)
    else {
      node.set(particleToKey(obj))
    }
  }

  override fun deserialize(type: Type, node: ConfigurationNode): Particle {
    val key = node.force<Key>()
    return particleFromKey(key) ?: throw SerializationException(node, type, "Invalid particle $key")
  }
}

private const val TYPE = "type"
private const val DATA = "data"

object ParticleTypeSerializer : TypeSerializer<ParticleType> {
  override fun serialize(type: Type, obj: ParticleType?, node: ConfigurationNode) {
    if (obj !is PaperParticleType) return
    if (obj.type.dataType == Unit::class.java) {
      node.set(obj.type)
    } else {
      node.node(TYPE).set(obj.type)
      node.node(DATA).set(obj.data)
    }
  }

  override fun deserialize(type: Type, node: ConfigurationNode): ParticleType {
    return if (node.isMap) {
      val particleType = node.node(TYPE).force<Particle>()
      val data =
          if (particleType.dataType != Unit::class.java) {
            node.node(DATA).force(particleType.dataType)
          } else null
      PaperParticleType(particleType, data)
    } else {
      val particleType = node.force<Particle>()
      if (particleType.dataType != Unit::class.java)
          throw SerializationException(
              node, type, "Particle type $particleType requires a data parameter")
      PaperParticleType(particleType, null)
    }
  }
}
