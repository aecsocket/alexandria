package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.TableAlign
import com.gitlab.aecsocket.alexandria.core.TableFormat
import com.gitlab.aecsocket.alexandria.core.extension.force
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val ALIGN = "align"
private const val JUSTIFY = "justify"
private const val DEFAULT = "default"

object TableFormatSerializer : TypeSerializer<TableFormat> {
    override fun serialize(type: Type, obj: TableFormat?, node: ConfigurationNode) {}

    override fun deserialize(type: Type, node: ConfigurationNode): TableFormat {
        fun makeFunction(node: ConfigurationNode): (Int) -> TableAlign {
            return if (node.empty()) { _ -> TableAlign.START }
            else {
                val default = node.node(DEFAULT).force<TableAlign>()
                val map = node.copy().apply { removeChild(DEFAULT) }.force<Map<Int, TableAlign>>()
                return { idx -> map[idx] ?: default }
            }
        }

        return TableFormat(
            makeFunction(node.node(ALIGN)),
            makeFunction(node.node(JUSTIFY))
        )
    }
}
