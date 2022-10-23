package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.LogLevel
import com.gitlab.aecsocket.alexandria.core.LogList
import com.gitlab.aecsocket.alexandria.core.Logging
import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.keyed.Keyed
import com.gitlab.aecsocket.alexandria.paper.extension.disable
import com.gitlab.aecsocket.alexandria.paper.extension.scheduleDelayed
import com.gitlab.aecsocket.glossa.core.I18N
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextColor
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.dataClassFieldDiscoverer
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.util.NamingSchemes
import java.io.InputStream

const val PATH_MANIFEST = "manifest.conf"
const val PATH_SETTINGS = "settings.conf"
const val PATH_LANG = "lang"
private const val LOG_LEVEL = "log_level"

private const val THREAD_NAME_WIDTH = 10

private val TEXT_REPLACEMENT = TextReplacementConfig.builder()
    .matchLiteral("\u00a0") // non-breaking space, from ICU messages
    .replacement(" ")
    .build()

private val manifestConfigOptions = ConfigurationOptions.defaults()
    .serializers {
        val mapper = ObjectMapper.factoryBuilder()
            .addDiscoverer(dataClassFieldDiscoverer())
            .defaultNamingScheme(NamingSchemes.SNAKE_CASE)
        it.registerAll(ConfigurateComponentSerializer.configurate().serializers())
        it.registerAnnotatedObjects(mapper.build())
    }

fun chatPrefixOf(name: String, color: TextColor) =
    Component.text("{$name} ", color)

abstract class BasePlugin : JavaPlugin() {
    @ConfigSerializable
    data class Manifest(
        val name: String,
        val chatName: String,
        val accentColor: TextColor,
        val langPaths: List<String>,
        val savedPaths: List<String>,
    ) {
        init {
            Keyed.validate(name)
        }
    }

    val log = Logging({
        fun String.crop(target: Int, padder: String.(Int) -> String) = if (length > target) substring(0, target)
            else padder(this, target)

        val threadName = Thread.currentThread().name.crop(THREAD_NAME_WIDTH, String::padEnd)

        logger.info("$threadName $it")
    })

    val manifest = HoconConfigurationLoader.builder()
        .source { resource(PATH_MANIFEST).bufferedReader() }
        .build()
        .load(manifestConfigOptions)
        .force<Manifest>()
    val chatPrefix = chatPrefixOf(manifest.chatName, manifest.accentColor)

    override fun onEnable() {
        if (!dataFolder.exists()) {
            manifest.savedPaths.forEach { path ->
                saveResource(path, false)
            }
        }
        scheduleDelayed { init() }
    }

    private fun init() {
        if (initInternal()) {
            val (log, success) = load()
            log.forEach { this.log.record(it) }
            if (!success)
                disable()
        } else disable()
    }

    protected open fun initInternal(): Boolean {
        return true
    }

    data class LoadResult(val log: LogList, val success: Boolean)

    fun load(): LoadResult {
        val log = LogList()

        val settings = try {
            AlexandriaAPI.configLoader().file(dataFolder.resolve(PATH_SETTINGS)).build().load()
        } catch (ex: Exception) {
            log.line(LogLevel.Error, ex) { "Could not load settings from $PATH_SETTINGS" }
            return LoadResult(log, false)
        }

        try {
            if (!loadInternal(log, settings))
                return LoadResult(log, false)
        } catch (ex: Exception) {
            log.line(LogLevel.Error, ex) { "Could not load data" }
            return LoadResult(log, false)
        }

        return LoadResult(log, true)
    }

    protected open fun loadInternal(log: LogList, settings: ConfigurationNode): Boolean {
        val logLevel = LogLevel.valueOf(settings.node(LOG_LEVEL).get { LogLevel.Verbose.name })
        this.log.level = logLevel

        return true
    }

    fun resource(path: String): InputStream {
        val url = classLoader.getResource(path)
            ?: throw IllegalArgumentException("No URL found for $path")
        val conn = url.openConnection()
        conn.useCaches = false
        return conn.getInputStream()
    }

    fun i18n(value: String) = "${manifest.name}.$value"

    fun chatMessage(content: Component): Component {
        return Component.empty()
            .append(chatPrefix)
            .append(content)
    }

    fun chatMessages(content: Iterable<Component>) = content.map { chatMessage(it) }

    fun sendMessage(audience: Audience, content: Iterable<Component>) {
        // send individually so lines appear right in console
        chatMessages(content).forEach {
            audience.sendMessage(it.replaceText(TEXT_REPLACEMENT))
        }
    }

    fun sendMessage(audience: Audience, content: I18N<Component>.() -> List<Component>) {
        sendMessage(audience, content(AlexandriaAPI.i18nFor(audience)))
    }
}
