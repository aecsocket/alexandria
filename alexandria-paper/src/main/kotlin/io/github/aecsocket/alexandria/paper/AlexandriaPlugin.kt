package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.log.ListLog
import io.github.aecsocket.alexandria.hook.AlexandriaHook
import io.github.aecsocket.alexandria.hook.AlexandriaManifest
import io.github.aecsocket.alexandria.hook.AlexandriaSettings
import io.github.aecsocket.alexandria.log.Slf4JLog
import io.github.aecsocket.alexandria.paper.extension.isFolia
import io.github.aecsocket.alexandria.paper.scheduling.FoliaScheduling
import io.github.aecsocket.alexandria.paper.scheduling.PaperScheduling
import io.github.aecsocket.alexandria.paper.scheduling.Scheduling
import io.github.aecsocket.glossa.Glossa
import io.github.aecsocket.glossa.GlossaStandard
import io.github.aecsocket.klam.DVec3
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurationNode

const val SETTINGS_PATH = "settings.toml"
const val LANG_PATH = "lang"

abstract class AlexandriaPlugin<S : AlexandriaSettings>(final override val manifest: AlexandriaManifest) : JavaPlugin(), AlexandriaHook {
    final override val log = Slf4JLog(slF4JLogger)
    private val settingsFile = dataFolder.resolve(SETTINGS_PATH)
    private val langFile = dataFolder.resolve(LANG_PATH)
    private val chatPrefix = AlexandriaHook.chatPrefix(manifest)

    lateinit var scheduling: Scheduling
        private set
    final override lateinit var settings: S
        private set
    final override lateinit var glossa: Glossa
        private set

    override val hookName get() = name
    @Suppress("UnstableApiUsage")
    override val version get() = pluginMeta.version
    @Suppress("UnstableApiUsage")
    override val authors: List<String> get() = pluginMeta.authors

    protected abstract val savedResources: List<String>

    override fun onLoad() {
        scheduling = if (isFolia) FoliaScheduling(this) else PaperScheduling(this)

        if (!dataFolder.exists()) {
            savedResources.forEach { path ->
                saveResource(path, false)
            }
        }

        AlexandriaHook.load(
            hook = this,
            settingsFile = settingsFile,
            loadSettings = ::loadSettings,
            onGlossaBuild = ::onGlossaBuild,
            setFields = ::setFields
        )
    }

    override fun reload(): ListLog {
        return AlexandriaHook.reload(
            hook = this,
            settingsFile = settingsFile,
            loadSettings = ::loadSettings,
            onGlossaBuild = ::onGlossaBuild,
            setFields = ::setFields,
        )
    }

    protected abstract fun loadSettings(node: ConfigurationNode): S

    private fun onGlossaBuild(model: GlossaStandard.Model) {
        AlexandriaHook.loadGlossaFromFiles(this@AlexandriaPlugin, model, log, langFile)
    }

    private fun setFields(settings: S, glossa: Glossa) {
        this.settings = settings
        this.glossa = glossa
    }

    override fun asChat(comp: Component) = AlexandriaHook.asChat(chatPrefix, comp)
}
