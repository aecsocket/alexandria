package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.*
import io.github.aecsocket.alexandria.paper.extension.isFolia
import io.github.aecsocket.alexandria.paper.extension.resource
import io.github.aecsocket.alexandria.paper.extension.sanitizeText
import io.github.aecsocket.alexandria.paper.scheduling.FoliaScheduling
import io.github.aecsocket.alexandria.paper.scheduling.PaperScheduling
import io.github.aecsocket.alexandria.paper.scheduling.Scheduling
import io.github.aecsocket.glossa.configurate.fromConfigLoader
import io.github.aecsocket.glossa.Glossa
import io.github.aecsocket.glossa.InvalidMessageProvider
import io.github.aecsocket.glossa.glossaStandard
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.util.Locale

private const val PATH_SETTINGS = "settings.yml"
private const val PATH_LANG = "lang"
private val defaultLanguageResources = listOf(
    "alexandria/lang/root.yml",
    "alexandria/lang/en-US.yml",
)
val fallbackLocale: Locale = Locale.forLanguageTag("en-US")

abstract class AlexandriaPlugin(
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
    protected abstract val configOptions: ConfigurationOptions
    lateinit var glossa: Glossa
    lateinit var scheduling: Scheduling

    fun asChat(component: Component) = text()
        .append(chatPrefix)
        .append(sanitizeText(component))
        .build()

    fun configLoaderBuilder(): YamlConfigurationLoader.Builder =
        YamlConfigurationLoader.builder()
            .defaultOptions(configOptions)

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

        val glossa = glossaStandard(
            defaultLocale = settings.defaultLocale,
            invalidMessageProvider = InvalidMessageProvider.DefaultLogging(logger)
        ) {
            (defaultLanguageResources + manifest.languageResources).forEach { path ->
                try {
                    fromConfigLoader(configLoaderBuilder().source { resource(path).bufferedReader() }.build())
                    log.debug("Loaded language resource from jar:$path")
                } catch (ex: Exception) {
                    log.warn("Could not load language resource from jar:$path", ex)
                }
            }

            dataFolder.resolve(PATH_LANG).walkTopDown()
                .onFail { file, ex ->
                    val path = file.relativeTo(dataFolder)
                    log.warn("Could not open language resource at $path", ex)
                }
                .forEach { file ->
                    val path = file.relativeTo(dataFolder)
                    try {
                        fromConfigLoader(configLoaderBuilder().file(file).build())
                        log.debug("Loaded language resource from $path")
                    } catch (ex: Exception) {
                        log.warn("Could not load language resource from $path", ex)
                    }
                }
        }
        log.info("Loaded ${glossa.countSubstitutions()} substitutions, ${glossa.countStyles()} styles, ${glossa.countMessages()} messages, ${glossa.countLocales()} locales")

        this.glossa = glossa
    }

    protected open fun load(log: LoggingList) {}

    protected open fun reload(log: Logging) {}

    override fun onLoad() {
        scheduling = if (isFolia) FoliaScheduling(this) else PaperScheduling(this)

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
