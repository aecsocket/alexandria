package io.github.aecsocket.alexandria.hook

import io.github.aecsocket.alexandria.extension.resource
import io.github.aecsocket.alexandria.extension.sanitizeText
import io.github.aecsocket.alexandria.kebabCasePattern
import io.github.aecsocket.alexandria.log.*
import io.github.aecsocket.alexandria.validateKey
import io.github.aecsocket.glossa.Glossa
import io.github.aecsocket.glossa.GlossaStandard
import io.github.aecsocket.glossa.InvalidMessageProvider
import io.github.aecsocket.glossa.configurate.fromConfigLoader
import io.github.aecsocket.glossa.glossaStandard
import me.lucko.configurate.toml.TOMLConfigurationLoader
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.util.Locale

private const val LANG_EXT = ".yml"
private val defaultLangResources = listOf(
    "io/github/aecsocket/alexandria/lang/root.yml",
    "io/github/aecsocket/alexandria/lang/en-US.yml",
)
val fallbackLocale: Locale = Locale.forLanguageTag("en-US")

abstract class AlexandriaHook<S : AlexandriaHook.Settings>(
    val manifest: Manifest,
    private val log: Log,
    private val settingsFile: File,
    private val configOptions: ConfigurationOptions,
) {
    data class Manifest(
        val id: String,
        val accentColor: TextColor,
        val langResources: List<String>,
    ) {
        init {
            validateKey(id, kebabCasePattern)
        }
    }

    data class Meta(
        val name: String,
        val version: String,
        val authors: List<String>,
    )

    interface Settings {
        val defaultLocale: Locale
    }

    private val chatPrefix = text("(${manifest.id}) ", manifest.accentColor)

    lateinit var settings: S
        private set
    lateinit var glossa: Glossa
        private set

    abstract val meta: Meta

    protected abstract fun loadSettings(node: ConfigurationNode): S

    protected abstract fun onGlossaBuild(log: Log, model: GlossaStandard.Model)

    protected abstract fun onInit(log: Log)

    protected abstract fun onLoad(log: Log)

    protected abstract fun onReload(log: Log)

    fun yamlConfigLoader(): YamlConfigurationLoader.Builder = YamlConfigurationLoader.builder()
        .defaultOptions(configOptions)

    fun tomlConfigLoader(): TOMLConfigurationLoader.Builder = TOMLConfigurationLoader.builder()
        .defaultOptions(configOptions)

    fun asChat(comp: Component) = text()
        .append(chatPrefix)
        .append(sanitizeText(comp))
        .build()

    fun init() {
        loadInternal(log)
        onLoad(log)
        onInit(log)
    }

    fun reload(): ListLog {
        val log = ListLog()

        loadInternal(log)
        onLoad(log)
        onReload(log)
        log.logTo(this.log)

        return log
    }

    private fun loadInternal(log: Log) {
        val settingsLoader = tomlConfigLoader()
            .file(settingsFile)
            .build()
        val settings = try {
            val settingsNode = settingsLoader.load()
            loadSettings(settingsNode).also {
                log.debug { "Loaded settings from $settingsFile" }
            }
        } catch (ex: Exception) {
            log.error(ex) { "Could not load settings from $settingsFile" }
            loadSettings(settingsLoader.createNode())
        }
        this.settings = settings

        val glossa = glossaStandard(
            defaultLocale = settings.defaultLocale,
            invalidMessageProvider = InvalidMessageProvider.Logging { log.warn { it } },
        ) {
            (defaultLangResources + manifest.langResources).forEach { path ->
                try {
                    fromConfigLoader(yamlConfigLoader().source { resource(path).bufferedReader() }.build())
                    log.debug { "Loaded language resource from jar:$path" }
                } catch (ex: Exception) {
                    log.warn(ex) { "Could not load language resource from jar:$path" }
                }
            }

            onGlossaBuild(log, this)
        }
        log.info { "Loaded ${glossa.countSubstitutions()} substitutions, ${glossa.countStyles()} styles, ${glossa.countMessages()} messages, ${glossa.countLocales()} locales" }
        this.glossa = glossa
    }

    protected fun GlossaStandard.Model.fromFiles(log: Log, root: File) {
        root.walkTopDown().forEach { file ->
            if (!file.endsWith(LANG_EXT)) return@forEach
            val path = file.relativeTo(root)

            try {
                fromConfigLoader(yamlConfigLoader().file(file).build())
                log.debug { "Loaded language resource from $path" }
            } catch (ex: Exception) {
                log.warn(ex) { "Could not load language resource from $path" }
            }
        }
    }
}
