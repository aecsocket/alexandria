package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.log.ListLog
import io.github.aecsocket.alexandria.hook.AlexandriaHook
import io.github.aecsocket.alexandria.log.Log
import io.github.aecsocket.alexandria.log.Slf4JLog
import io.github.aecsocket.alexandria.paper.extension.isFolia
import io.github.aecsocket.alexandria.paper.scheduling.FoliaScheduling
import io.github.aecsocket.alexandria.paper.scheduling.PaperScheduling
import io.github.aecsocket.alexandria.paper.scheduling.Scheduling
import io.github.aecsocket.glossa.Glossa
import io.github.aecsocket.glossa.GlossaStandard
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions

private const val SETTINGS_PATH = "settings.toml"
private const val LANG_PATH = "lang"

abstract class AlexandriaPlugin<S : AlexandriaHook.Settings>(
    manifest: AlexandriaHook.Manifest,
    configOptions: ConfigurationOptions,
    private val savedResources: List<String>,
) : JavaPlugin() {
    val log = Slf4JLog(slF4JLogger)

    protected abstract fun loadSettings(node: ConfigurationNode): S

    protected open fun onPreInit(log: Log) {}

    protected open fun onInit(log: Log) {}

    protected open fun onLoad(log: Log) {}

    protected open fun onReload(log: Log) {}

    protected open fun onDestroy(log: Log) {}

    protected val ax = object : AlexandriaHook<S>(
        manifest = manifest,
        log = log,
        settingsFile = dataFolder.resolve(SETTINGS_PATH),
        configOptions = configOptions,
    ) {
        val langFile = dataFolder.resolve(LANG_PATH)

        override val meta: Meta
            get() = this@AlexandriaPlugin.axMeta

        override fun loadSettings(node: ConfigurationNode) =
            this@AlexandriaPlugin.loadSettings(node)

        override fun onGlossaBuild(log: Log, model: GlossaStandard.Model) {
            model.fromFiles(log, langFile)
        }

        override fun onPreInit(log: Log) =
            this@AlexandriaPlugin.onPreInit(log)

        override fun onInit(log: Log) =
            this@AlexandriaPlugin.onInit(log)

        override fun onLoad(log: Log) =
            this@AlexandriaPlugin.onLoad(log)

        override fun onReload(log: Log) =
            this@AlexandriaPlugin.onReload(log)
    }

    val settings: S
        get() = ax.settings

    val glossa: Glossa
        get() = ax.glossa

    lateinit var scheduling: Scheduling
        private set
    private lateinit var axMeta: AlexandriaHook.Meta

    final override fun onLoad() {
        scheduling = if (isFolia) FoliaScheduling(this) else PaperScheduling(this)
        @Suppress("UnstableApiUsage")
        axMeta = AlexandriaHook.Meta(
            name = pluginMeta.name,
            version = pluginMeta.version,
            authors = pluginMeta.authors,
        )

        if (!dataFolder.exists()) {
            savedResources.forEach { path ->
                saveResource(path, false)
            }
        }

        ax.init()
    }

    final override fun onDisable() {
        onDestroy(log)
    }

    fun reload(): ListLog {
        return ax.reload()
    }

    fun yamlConfigLoader() = ax.yamlConfigLoader()

    fun tomlConfigLoader() = ax.tomlConfigLoader()

    fun asChat(comp: Component) = ax.asChat(comp)
}
