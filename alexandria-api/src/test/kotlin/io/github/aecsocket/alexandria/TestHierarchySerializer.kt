package io.github.aecsocket.alexandria

import io.github.aecsocket.alexandria.serializer.HierarchySerializer
import io.github.aecsocket.alexandria.serializer.subType
import io.github.aecsocket.klam.configurate.registerExact
import org.junit.jupiter.api.assertThrows
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.kotlin.dataClassFieldDiscoverer
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.objectmapping.meta.Required
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import kotlin.test.Test
import kotlin.test.assertEquals

class TestHierarchySerializer {
    sealed interface MyValue {
        @ConfigSerializable
        data class Alpha(
            @Required val a: Int,
        ) : MyValue

        @ConfigSerializable
        data class Beta(
            @Required val b: Int,
        ) : MyValue
    }

    private val configOptions: ConfigurationOptions = ConfigurationOptions.defaults()
        .serializers { it
            // IMPORTANT: use the -Exact method
            .registerExact(HierarchySerializer<MyValue> {
                subType<_, MyValue.Alpha>("alpha")
                subType<_, MyValue.Beta>("beta")
            })
            .registerAnnotatedObjects(ObjectMapper.factoryBuilder()
                .addDiscoverer(dataClassFieldDiscoverer())
                .build()
            )
        }

    private fun loadNode(text: String): ConfigurationNode{
        return YamlConfigurationLoader
            .builder()
            .defaultOptions(configOptions)
            .buildAndLoadString(text)
    }

    @Test
    fun testDeserializeAlpha() {
        val node = loadNode("""
            type: "alpha"
            a: 3
        """.trimIndent())
        val value = node.get<MyValue>()

        assertEquals(MyValue.Alpha(a = 3), value)
    }

    @Test
    fun testDeserializeBeta() {
        val node = loadNode("""
            type: "beta"
            b: 5
        """.trimIndent())
        val value = node.get<MyValue>()

        assertEquals(MyValue.Beta(b = 5), value)
    }

    @Test
    fun testDeserializeUnknown() {
        val node = loadNode("""
            type: "unknown"
            a: 4
            b: 6
        """.trimIndent())

        assertThrows<SerializationException> {
            node.get<MyValue>()
        }
    }
}
