package io.github.aecsocket.alexandria.api.paper

import io.github.aecsocket.alexandria.api.paper.extension.resource
import io.github.aecsocket.alexandria.core.*
import io.github.aecsocket.glossa.configurate.fromConfigLoader
import io.github.aecsocket.glossa.core.Glossa
import io.github.aecsocket.glossa.core.InvalidMessageProvider
import io.github.aecsocket.glossa.core.glossaStandard
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.util.Locale

private const val PATH_SETTINGS = "settings.yml"
private val defaultLanguageResources = listOf(
    "lang/alexandria/root.yml",
    "lang/alexandria/en-US.yml",
)

abstract class AlexandriaApiPlugin(
    val manifest: Manifest,
) : JavaPlugin() {
    data class Manifest(
        val id: String,
        val accentColor: TextColor,
        val languageResources: List<String> = emptyList(),
        val savedResources: List<String> = emptyList(),
    ) {
        init {
            validateKey(id, kebabCasePattern)
        }
    }

    interface Settings {
        val defaultLocale: Locale
    }

    private val chatPrefix = text("(${manifest.id}) ", manifest.accentColor)
    abstract val settings: Settings
    lateinit var glossa: Glossa

    fun asChat(component: Component) = text()
        .append(chatPrefix)
        .append(component)
        .build()

    protected abstract fun configOptions(): ConfigurationOptions

    protected fun configLoaderBuilder(): YamlConfigurationLoader.Builder =
        YamlConfigurationLoader.builder()
            .defaultOptions(configOptions())

    protected abstract fun loadSettings(node: ConfigurationNode?)

    private fun defaultLoad(log: LoggingList) {
        try {
            val settingsNode = configLoaderBuilder()
                .file(dataFolder.resolve(PATH_SETTINGS))
                .build().load()
            loadSettings(settingsNode)
        } catch (ex: Exception) {
            log.error("Could not load settings from $PATH_SETTINGS", ex)
            loadSettings(null)
        }

        glossa = glossaStandard(
            defaultLocale = settings.defaultLocale,
            invalidMessageProvider = InvalidMessageProvider.DefaultLogging(logger)
        ) {
            (defaultLanguageResources + manifest.languageResources).forEach { path ->
                try {
                    fromConfigLoader(configLoaderBuilder().source { resource(path).bufferedReader() }.build())
                } catch (ex: Exception) {
                    log.warn("Could not load language resource from $path", ex)
                }
            }
        }
    }

    protected open fun load(log: LoggingList) {}

    protected open fun reload(log: Logging) {}

    override fun onLoad() {
        if (!dataFolder.exists()) {
            manifest.savedResources.forEach { path ->
                saveResource(path, false)
            }
        }

        val log = LoggingList()
        defaultLoad(log)
        load(log)
        log.logTo(logger)
    }

    fun reload(): LoggingList {
        val log = LoggingList()
        defaultLoad(log)
        load(log)
        reload(log)
        log.logTo(logger)
        return log
    }
}
