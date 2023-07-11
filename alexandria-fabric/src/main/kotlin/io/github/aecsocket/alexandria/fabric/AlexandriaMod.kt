package io.github.aecsocket.alexandria.fabric

import io.github.aecsocket.alexandria.hook.AlexandriaHook
import io.github.aecsocket.glossa.Glossa
import io.github.aecsocket.glossa.GlossaStandard
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.jvm.optionals.getOrNull
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.text.Component
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions

abstract class AlexandriaMod<S : AlexandriaHook.Settings>(
    manifest: AlexandriaHook.Manifest,
    configOptions: ConfigurationOptions,
) : ModInitializer {
  val modId = manifest.id
  val log = KotlinLogging.logger(modId)

  protected abstract fun loadSettings(node: ConfigurationNode): S

  protected open fun onPreInit() {}

  protected open fun onInit() {}

  protected open fun onLoad() {}

  protected open fun onReload() {}

  protected val ax =
      object :
          AlexandriaHook<S>(
              manifest = manifest,
              log = log,
              settingsFile = FabricLoader.getInstance().configDir.resolve("$modId.yml").toFile(),
              configOptions = configOptions,
          ) {
        override val meta: Meta
          get() = this@AlexandriaMod.axMeta

        override fun loadSettings(node: ConfigurationNode) = this@AlexandriaMod.loadSettings(node)

        override fun onGlossaBuild(model: GlossaStandard.Model) {}

        override fun onPreInit() = this@AlexandriaMod.onPreInit()

        override fun onInit() = this@AlexandriaMod.onInit()

        override fun onLoad() = this@AlexandriaMod.onLoad()

        override fun onReload() = this@AlexandriaMod.onReload()
      }

  val settings: S
    get() = ax.settings

  val glossa: Glossa
    get() = ax.glossa

  private lateinit var axMeta: AlexandriaHook.Meta

  final override fun onInitialize() {
    val meta =
        FabricLoader.getInstance().getModContainer(modId).getOrNull()?.metadata
            ?: throw IllegalStateException("No mod with ID $modId")
    axMeta =
        AlexandriaHook.Meta(
            name = meta.name,
            version = meta.version.friendlyString,
            authors = meta.authors.map { it.name },
        )

    ax.init()
  }

  fun reload() {
    ax.reload()
  }

  fun yamlConfigLoader() = ax.yamlConfigLoader()

  fun asChat(comp: Component) = ax.asChat(comp)
}
