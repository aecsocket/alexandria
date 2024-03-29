package io.github.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.PacketEvents
import io.github.aecsocket.alexandria.hook.AlexandriaHook
import io.github.aecsocket.alexandria.paper.extension.isFolia
import io.github.aecsocket.alexandria.paper.scheduling.FoliaScheduling
import io.github.aecsocket.alexandria.paper.scheduling.PaperScheduling
import io.github.aecsocket.alexandria.paper.scheduling.Scheduling
import io.github.aecsocket.glossa.Glossa
import io.github.aecsocket.glossa.GlossaStandard
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.slf4j.logger
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions

private const val SETTINGS_PATH = "settings.yml"
private const val LANG_PATH = "lang"

abstract class AlexandriaPlugin<S : AlexandriaHook.Settings>(
    manifest: AlexandriaHook.Manifest,
    configOptions: ConfigurationOptions,
    private val savedResources: List<String>,
) : JavaPlugin() {
  val log = KotlinLogging.logger(slF4JLogger)

  protected abstract fun loadSettings(node: ConfigurationNode): S

  protected open fun onPreInit() {}

  protected open fun onInit() {}

  protected open fun onPostEnable() {}

  protected open fun onLoadData() {}

  protected open fun onReloadData() {}

  protected open fun onDestroy() {}

  protected val ax =
      object :
          AlexandriaHook<S>(
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

        override fun onGlossaBuild(model: GlossaStandard.Model) {
          model.fromFiles(langFile)
        }

        override fun onPreInit() = this@AlexandriaPlugin.onPreInit()

        override fun onInit() = this@AlexandriaPlugin.onInit()

        override fun onLoad() = this@AlexandriaPlugin.onLoadData()

        override fun onReload() = this@AlexandriaPlugin.onReloadData()
      }

  private var loaded = true

  val settings: S
    get() = ax.settings

  val glossa: Glossa
    get() = ax.glossa

  private lateinit var axMeta: AlexandriaHook.Meta
  lateinit var scheduling: Scheduling
    private set

  final override fun onLoad() {
    try {
      @Suppress("UnstableApiUsage")
      axMeta =
          AlexandriaHook.Meta(
              name = pluginMeta.name,
              version = pluginMeta.version,
              authors = pluginMeta.authors,
          )
      scheduling = if (isFolia) FoliaScheduling(this) else PaperScheduling(this)

      PacketEvents.setAPI(
          SpigotPacketEventsBuilder.build(this).apply { settings.checkForUpdates(false) })
      PacketEvents.getAPI().load()

      if (!dataFolder.exists()) {
        savedResources.forEach { path -> saveResource(path, false) }
      }

      ax.init()
    } catch (ex: Throwable) {
      loaded = false
      throw ex
    }
  }

  final override fun onEnable() {
    if (!loaded) return
    PacketEvents.getAPI().init()
    ChunkTracking.init(this)
    EntityTracking.init(this)
    onPostEnable()
  }

  final override fun onDisable() {
    if (!loaded) return
    onDestroy()
  }

  fun reload() {
    if (!loaded) return
    ax.reload()
  }

  fun yamlConfigLoader() = ax.yamlConfigLoader()

  fun asChat(comp: Component) = ax.asChat(comp)
}
