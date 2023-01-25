package io.gitlab.aecsocket.alexandria.core.serializer

import io.gitlab.aecsocket.alexandria.core.extension.force
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type
import java.util.*

object LocaleSerializer : TypeSerializer<Locale> {
    private const val ROOT = "root"

    override fun serialize(type: Type, obj: Locale?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(if (obj == Locale.ROOT) ROOT else obj.toLanguageTag())
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Locale {
        val string = node.force<String>()
        return fromString(string)
    }

    fun fromString(string: String): Locale = if (string == ROOT) Locale.ROOT else Locale.forLanguageTag(string)
}
