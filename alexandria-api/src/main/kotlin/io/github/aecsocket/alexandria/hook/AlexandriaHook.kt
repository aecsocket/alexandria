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

private const val CONFIG_EXT = ".toml"
private val defaultLanguageResources = listOf(
    "io/github/aecsocket/alexandria/lang/root.yml",
    "io/github/aecsocket/alexandria/lang/en-US.yml",
)
val fallbackLocale: Locale = Locale.forLanguageTag("en-US")

data class AlexandriaManifest(
    val id: String,
    val accentColor: TextColor,
    val languageResources: List<String> = emptyList(),
) {
    init {
        validateKey(id, kebabCasePattern)
    }
}

interface AlexandriaSettings {
    val defaultLocale: Locale
}

interface AlexandriaHook {
    val manifest: AlexandriaManifest

    val log: Log
    val settings: AlexandriaSettings
    val glossa: Glossa
    val configOptions: ConfigurationOptions

    val hookName: String
    val version: String
    val authors: List<String>

    fun onLoad(log: Log) {}

    fun onReload(log: Log) {}

    fun reload(): ListLog

    fun asChat(comp: Component): Component

    companion object {
        private fun <S : AlexandriaSettings> loadInternal(
            hook: AlexandriaHook,
            settingsFile: File,
            loadSettings: (ConfigurationNode) -> S,
            onGlossaBuild: GlossaStandard.Model.() -> Unit,
            setFields: (S, Glossa) -> Unit,
            block: (Log) -> Unit,
        ): ListLog {
            val log = ListLog()

            val (settings, glossa) = baseLoad(hook, log, settingsFile, loadSettings, onGlossaBuild)
            setFields(settings, glossa)
            block(log)
            hook.onLoad(log)
            log.logTo(hook.log)

            return log
        }

        fun <S : AlexandriaSettings> load(
            hook: AlexandriaHook,
            settingsFile: File,
            loadSettings: (ConfigurationNode) -> S,
            onGlossaBuild: GlossaStandard.Model.() -> Unit,
            setFields: (S, Glossa) -> Unit,
        ): ListLog {
            return loadInternal(hook, settingsFile, loadSettings, onGlossaBuild, setFields) { log ->
                hook.onLoad(log)
            }
        }

        fun <S : AlexandriaSettings> reload(
            hook: AlexandriaHook,
            settingsFile: File,
            loadSettings: (ConfigurationNode) -> S,
            onGlossaBuild: GlossaStandard.Model.() -> Unit,
            setFields: (S, Glossa) -> Unit,
        ): ListLog {
            return loadInternal(hook, settingsFile, loadSettings, onGlossaBuild, setFields) { log ->
                hook.onReload(log)
            }
        }

        private fun <S : AlexandriaSettings> baseLoad(
            hook: AlexandriaHook,
            log: Log,
            settingsFile: File,
            loadSettings: (ConfigurationNode) -> S,
            onGlossaBuild: GlossaStandard.Model.() -> Unit,
        ): Pair<S, Glossa> {
            val settingsLoader = hook.tomlConfigLoader()
                .file(settingsFile)
                .build()
            val settings = try {
                val settingsNode = settingsLoader.load()
                loadSettings(settingsNode)
            } catch (ex: Exception) {
                log.error(ex) { "Could not load settings from $settingsFile" }
                loadSettings(settingsLoader.createNode())
            }

            val glossa = glossaStandard(
                defaultLocale = settings.defaultLocale,
                invalidMessageProvider = InvalidMessageProvider.Logging { hook.log.warn { it } },
            ) {
                (defaultLanguageResources + hook.manifest.languageResources).forEach { path ->
                    try {
                        fromConfigLoader(hook.yamlConfigLoader().source { resource(path).bufferedReader() }.build())
                        log.debug { "Loaded language resource from jar:$path" }
                    } catch (ex: Exception) {
                        log.warn(ex) { "Could not load language resource from jar:$path" }
                    }
                }

                onGlossaBuild(this)
            }
            log.info { "Loaded ${glossa.countSubstitutions()} substitutions, ${glossa.countStyles()} styles, ${glossa.countMessages()} messages, ${glossa.countLocales()} locales" }

            return settings to glossa
        }

        fun loadGlossaFromFiles(hook: AlexandriaHook, model: GlossaStandard.Model, log: Log, root: File) {
            root.walkTopDown().forEach { file ->
                if (!file.endsWith(CONFIG_EXT)) return@forEach
                val path = file.relativeTo(root)

                try {
                    model.fromConfigLoader(hook.yamlConfigLoader().file(file).build())
                    log.trace { "Loaded language resource from $path" }
                } catch (ex: Exception) {
                    log.warn(ex) { "Could not load language resource from $path" }
                }
            }
        }

        fun chatPrefix(manifest: AlexandriaManifest) = text("(${manifest.id}) ", manifest.accentColor)

        fun asChat(chatPrefix: Component, comp: Component) = text()
            .append(chatPrefix)
            .append(sanitizeText(comp))
            .build()
    }
}

fun AlexandriaHook.yamlConfigLoader(): YamlConfigurationLoader.Builder = YamlConfigurationLoader.builder()
    .defaultOptions(configOptions)

fun AlexandriaHook.tomlConfigLoader(): TOMLConfigurationLoader.Builder = TOMLConfigurationLoader.builder()
    .defaultOptions(configOptions)
