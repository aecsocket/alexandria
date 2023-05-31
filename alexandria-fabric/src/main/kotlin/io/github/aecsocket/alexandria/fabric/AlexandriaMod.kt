package io.github.aecsocket.alexandria.fabric

import io.github.aecsocket.alexandria.log.ListLog
import io.github.aecsocket.alexandria.hook.AlexandriaHook
import io.github.aecsocket.alexandria.log.Log
import io.github.aecsocket.alexandria.log.Slf4JLog
import io.github.aecsocket.glossa.Glossa
import io.github.aecsocket.glossa.GlossaStandard
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.text.Component
import org.slf4j.LoggerFactory
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import kotlin.jvm.optionals.getOrNull

abstract class AlexandriaMod<S : AlexandriaHook.Settings>(
    manifest: AlexandriaHook.Manifest,
    configOptions: ConfigurationOptions,
) : ModInitializer {
    val modId = manifest.id
    val log = Slf4JLog(LoggerFactory.getLogger(modId))

    protected abstract fun loadSettings(node: ConfigurationNode): S

    protected abstract fun onInit(log: Log)

    protected abstract fun onLoad(log: Log)

    protected abstract fun onReload(log: Log)

    protected val ax = object : AlexandriaHook<S>(
        manifest = manifest,
        log = log,
        settingsFile = FabricLoader.getInstance().configDir.resolve("$modId.toml").toFile(),
        configOptions = configOptions,
    ) {
        override val meta: Meta
            get() = this@AlexandriaMod.axMeta

        override fun loadSettings(node: ConfigurationNode) =
            this@AlexandriaMod.loadSettings(node)

        override fun onGlossaBuild(log: Log, model: GlossaStandard.Model) {}

        override fun onInit(log: Log) =
            this@AlexandriaMod.onInit(log)

        override fun onLoad(log: Log) =
            this@AlexandriaMod.onLoad(log)

        override fun onReload(log: Log) =
            this@AlexandriaMod.onReload(log)
    }

    val settings: S
        get() = ax.settings

    val glossa: Glossa
        get() = ax.glossa

    private lateinit var axMeta: AlexandriaHook.Meta

    final override fun onInitialize() {
        val meta = FabricLoader.getInstance().getModContainer(modId).getOrNull()?.metadata
            ?: throw IllegalStateException("No mod with ID $modId")
        axMeta = AlexandriaHook.Meta(
            name = meta.name,
            version = meta.version.friendlyString,
            authors = meta.authors.map { it.name },
        )

        ax.init()
    }

    fun reload(): ListLog {
        return ax.reload()
    }

    fun yamlConfigLoader() = ax.yamlConfigLoader()

    fun tomlConfigLoader() = ax.tomlConfigLoader()

    fun asChat(comp: Component) = ax.asChat(comp)
}
