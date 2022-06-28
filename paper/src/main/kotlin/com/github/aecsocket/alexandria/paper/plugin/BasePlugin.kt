package com.github.aecsocket.alexandria.paper.plugin

import com.github.aecsocket.alexandria.core.ExceptionLogStrategy
import com.github.aecsocket.alexandria.core.LogLevel
import com.github.aecsocket.alexandria.core.LogList
import com.github.aecsocket.alexandria.core.Logging
import com.github.aecsocket.alexandria.core.extension.force
import com.github.aecsocket.alexandria.core.extension.register
import com.github.aecsocket.alexandria.core.serializer.Serializers
import com.github.aecsocket.alexandria.paper.extension.disable
import com.github.aecsocket.alexandria.paper.extension.scheduleDelayed
import com.github.aecsocket.alexandria.paper.serializer.PaperSerializers
import com.github.aecsocket.glossa.adventure.*
import com.github.aecsocket.glossa.configurate.I18NLoader
import com.github.aecsocket.glossa.core.I18N
import com.github.aecsocket.glossa.core.Translation
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.extra.kotlin.join
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.Style
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.dataClassFieldDiscoverer
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.util.NamingSchemes
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

private const val PATH_MANIFEST = "manifest.conf"
private const val PATH_SETTINGS = "settings.conf"
private const val PATH_LANG = "lang"
private const val LOG_LEVEL = "log_level"
private const val LOCALE = "locale"
private const val EXCEPTION_LOGGING = "exception_logging"

typealias ConfigOptionsAction = (TypeSerializerCollection.Builder, ObjectMapper.Factory.Builder) -> Unit

@ConfigSerializable
data class PluginManifest(
    val savedPaths: Map<String, ResourceEntry> = emptyMap(),
    val langPaths: List<String> = emptyList()
) {
    data class ResourceEntry(val asName: List<String>) {
        object Serializer : TypeSerializer<ResourceEntry> {
            override fun serialize(type: Type, obj: ResourceEntry?, node: ConfigurationNode) = throw UnsupportedOperationException()
            override fun deserialize(type: Type, node: ConfigurationNode): ResourceEntry {
                if (!node.isList)
                    throw SerializationException(node, type, "Expected list")
                val list = node.childrenList()
                return if (list.isEmpty()) ResourceEntry(listOf(node.key().toString())) else ResourceEntry(list.map { it.force() })
            }
        }
    }
}

abstract class BasePlugin<S : BasePlugin.LoadScope> : JavaPlugin() {
    interface LoadScope {
        fun onConfigOptionsSetup(action: ConfigOptionsAction)
    }

    protected lateinit var manifest: PluginManifest
    lateinit var i18n: I18N<Component>
    val log = Logging(logger)
    var configOptions: ConfigurationOptions = ConfigurationOptions.defaults()
        .serializers {
            it.register(PluginManifest.ResourceEntry::class, PluginManifest.ResourceEntry.Serializer)
            it.registerAnnotatedObjects(ObjectMapper.factoryBuilder()
                .addDiscoverer(dataClassFieldDiscoverer())
                .defaultNamingScheme(NamingSchemes.SNAKE_CASE)
                .build())
        }
    var chatPrefix: Component = Component.empty()

    protected abstract fun createLoadScope(configOptionActions: MutableList<ConfigOptionsAction>): S

    private val onLoad = ArrayList<S.() -> Unit>()
    private var loaded = false

    override fun onEnable() {
        super.onEnable()
        manifest = loaderBuilder()
            .source { resource(PATH_MANIFEST).bufferedReader() }
            .build()
            .load().force()
        if (!dataFolder.exists()) {
            saveResource(PATH_SETTINGS, false)
            for ((path, saveAs) in manifest.savedPaths) {
                val resource = resource(path).readAllBytes()
                saveAs.asName.forEach {
                    val file = dataFolder.resolve(it)
                    file.parentFile.mkdirs()
                    Files.write(file.toPath(), resource)
                }
            }
        }
        scheduleDelayed {
            serverLoad()
        }
    }

    fun defaultLocale() = i18n.locale

    fun locale(sender: CommandSender) = if (sender is Player) sender.locale() else defaultLocale()

    fun asChat(lines: List<Component>) = lines.map { text().append(chatPrefix).append(it) }

    fun send(audience: Audience, lines: List<Component>) =
        asChat(lines).forEach { audience.sendMessage(it) }

    fun send(audience: Audience, content: I18N<Component>.() -> List<Component>) =
        send(audience, content(i18n))

    fun loaderBuilder(): AbstractConfigurationLoader.Builder<*, *> = HoconConfigurationLoader.builder()
        .defaultOptions(configOptions)

    fun resource(path: String): InputStream {
        val url = classLoader.getResource(path)
            ?: throw IllegalArgumentException("No URL found for $path")
        val conn = url.openConnection()
        conn.useCaches = false
        return conn.getInputStream()
    }

    fun onLoad(action: S.() -> Unit) {
        if (loaded)
            throw IllegalStateException("Plugin has already been loaded")
        onLoad.add(action)
    }

    protected open fun serverLoad(): Boolean {
        val configOptionsActions = ArrayList<ConfigOptionsAction>()
        onLoad.forEach { it(createLoadScope(configOptionsActions)) }
        loaded = true
        configOptions = ConfigurationOptions.defaults().serializers {
            val mapper = ObjectMapper.factoryBuilder().addDiscoverer(dataClassFieldDiscoverer())
            setupConfigOptions(it, mapper)
            configOptionsActions.forEach { func -> func(it, mapper) }
            it.registerAnnotatedObjects(mapper.build())
        }
        val (loadLog, success) = load()
        loadLog.forEach { log.record(it) }
        if (!success) {
            disable()
            return false
        }
        return true
    }

    protected open fun setupConfigOptions(
        serializers: TypeSerializerCollection.Builder,
        mapper: ObjectMapper.Factory.Builder
    ) {
        mapper.defaultNamingScheme(NamingSchemes.SNAKE_CASE)
        serializers.registerAll(Serializers.ALL)
        serializers.registerAll(PaperSerializers.ALL)
    }

    data class LoadResult(
        val log: LogList,
        val success: Boolean
    )

    fun load(): LoadResult {
        val log = LogList()
        try {
            val settings = loaderBuilder().file(dataFolder.resolve(PATH_SETTINGS)).build().load()
            if (!loadInternal(log, settings))
                return LoadResult(log, false)
        } catch (ex: Exception) {
            log.line(LogLevel.ERROR, ex) { "Could not load settings from $PATH_SETTINGS" }
            return LoadResult(log, false)
        }
        return LoadResult(log, true)
    }

    protected open fun loadInternal(log: LogList, settings: ConfigurationNode): Boolean {
        this.log.level = LogLevel.valueOf(settings.node(LOG_LEVEL).force())
        this.log.exceptionStrategy = ExceptionLogStrategy.valueOf(settings.node(EXCEPTION_LOGGING).force())

        i18n = AdventureI18NBuilder(settings.node(LOCALE).force()).apply {
            val translations = ArrayList<Translation.Root>()
            val styles = HashMap<String, Style>()
            val formats = HashMap<List<String>, I18NFormat>()

            fun loadI18N(level: LogLevel, path: String, source: () -> BufferedReader) {
                val (newTls, newStyles, newFormats) = try {
                    val node = loaderBuilder().source(source).build().load()
                    I18NLoader.loadAll(node)
                } catch (ex: ConfigurateException) {
                    log.line(level, ex) { "Could not parse language resource $path" }
                    return
                }
                translations.addAll(newTls)
                styles.putAll(newStyles)
                formats.putAll(newFormats)
            }

            fun loadLang(root: Path) {
                Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {
                    override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                        loadI18N(LogLevel.WARNING, path.toString()) {
                            Files.newBufferedReader(path, StandardCharsets.UTF_8)
                        }
                        return FileVisitResult.CONTINUE
                    }

                    override fun visitFileFailed(path: Path, ex: IOException): FileVisitResult {
                        log.line(LogLevel.WARNING, ex) { "Could not view language resource $path" }
                        return FileVisitResult.CONTINUE
                    }
                })
            }

            // Load from jar
            for (path in manifest.langPaths) {
                loadI18N(LogLevel.ERROR, "(jar) $path") { resource(path).bufferedReader() }
            }

            // Load from fs
            loadLang(dataFolder.resolve(PATH_LANG).toPath())

            // Apply loaded
            translations.forEach(::register)
            styles.forEach(::registerStyle)
            formats.forEach(::registerFormat)

            log.line(LogLevel.INFO) { "Loaded ${translations.size} translation(s), ${styles.size} style(s), ${formats.size} format(s)" }
        }.build()

        chatPrefix = i18n.safe("chat_prefix").join(JoinConfiguration.noSeparators())
        return true
    }
}
