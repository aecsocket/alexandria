package io.github.aecsocket.alexandria.fabric

import io.github.aecsocket.alexandria.log.ListLog
import io.github.aecsocket.alexandria.hook.AlexandriaHook
import io.github.aecsocket.alexandria.hook.AlexandriaManifest
import io.github.aecsocket.alexandria.hook.AlexandriaSettings
import io.github.aecsocket.alexandria.log.Log
import io.github.aecsocket.alexandria.log.Slf4JLog
import io.github.aecsocket.glossa.Glossa
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.text.Component
import org.slf4j.LoggerFactory
import org.spongepowered.configurate.ConfigurationNode
import kotlin.jvm.optionals.getOrNull

abstract class AlexandriaMod<S : AlexandriaSettings>(final override val manifest: AlexandriaManifest) : ModInitializer, AlexandriaHook {
    val modId = manifest.id

    override val log: Log = Slf4JLog(LoggerFactory.getLogger(modId))
    private val settingsFile = FabricLoader.getInstance().configDir.resolve("$modId.toml").toFile()
    private val chatPrefix = AlexandriaHook.chatPrefix(manifest)

    final override lateinit var settings: S
        private set
    final override lateinit var glossa: Glossa
        private set

    final override lateinit var hookName: String
        private set
    final override lateinit var version: String
        private set
    final override lateinit var authors: List<String>
        private set

    override fun onInitialize() {
        val meta = FabricLoader.getInstance().getModContainer(modId).getOrNull()?.metadata
            ?: throw IllegalStateException("No mod with ID $modId")

        hookName = meta.name
        version = meta.version.friendlyString
        authors = meta.authors.map { it.name }

        AlexandriaHook.load(
            hook = this,
            settingsFile = settingsFile,
            loadSettings = ::loadSettings,
            onGlossaBuild = {},
            setFields = ::setFields
        )
    }

    override fun reload(): ListLog {
        return AlexandriaHook.reload(
            hook = this,
            settingsFile = settingsFile,
            loadSettings = ::loadSettings,
            onGlossaBuild = {},
            setFields = ::setFields,
        )
    }

    protected abstract fun loadSettings(node: ConfigurationNode): S

    private fun setFields(settings: S, glossa: Glossa) {
        this.settings = settings
        this.glossa = glossa
    }

    override fun asChat(comp: Component) = AlexandriaHook.asChat(chatPrefix, comp)
}
