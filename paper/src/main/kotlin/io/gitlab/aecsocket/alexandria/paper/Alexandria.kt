package io.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import io.gitlab.aecsocket.alexandria.core.LogLevel
import io.gitlab.aecsocket.alexandria.core.LogList
import io.gitlab.aecsocket.alexandria.core.ReplacingI18N
import io.gitlab.aecsocket.alexandria.core.TableAlign
import io.gitlab.aecsocket.alexandria.core.extension.force
import io.gitlab.aecsocket.alexandria.core.extension.walkFile
import io.gitlab.aecsocket.alexandria.core.serializer.Serializers
import io.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import io.gitlab.aecsocket.alexandria.paper.extension.scheduleRepeating
import io.gitlab.aecsocket.alexandria.paper.input.EventInputListener
import io.gitlab.aecsocket.alexandria.paper.input.InputEvent
import io.gitlab.aecsocket.alexandria.paper.serializer.PaperSerializers
import io.gitlab.aecsocket.glossa.adventure.MiniMessageI18N
import io.gitlab.aecsocket.glossa.adventure.TextReplacerI18N
import io.gitlab.aecsocket.glossa.adventure.load
import io.gitlab.aecsocket.glossa.core.*
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bstats.bukkit.Metrics
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.map.MapFont
import org.bukkit.map.MinecraftFont
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.dataClassFieldDiscoverer
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.util.NamingSchemes
import java.io.BufferedReader
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.isDirectory

private const val BSTATS_ID = 16725

private val textReplacements = listOf(
    TextReplacementConfig.builder()
        .matchLiteral("\u00a0") // non-breaking space (NBSP), from ICU messages
        .replacement(" ")
        .build(),
    TextReplacementConfig.builder()
        .matchLiteral("\u202f") // narrow non-breaking space (NNBSP)
        .replacement(" ")
        .build()
)

private lateinit var instance: Alexandria
val AlexandriaAPI get() = instance

typealias InputHandler = (event: InputEvent) -> Unit

class Alexandria : BasePlugin<Alexandria.Settings>(PluginManifest("alexandria",
    accentColor = TextColor.color(0x745f86),
    langPaths = listOf(
        "lang/default_root.conf",
        "lang/default_en-US.conf",
        "lang/root.conf"
    ),
    savedPaths = listOf(
        "settings.conf",
        "lang/root.conf"
    )
), Settings::class, Settings()) {
    @ConfigSerializable
    data class Settings(
        override val logLevel: LogLevel = LogLevel.Verbose,
        val enableBstats: Boolean = true,
        val locale: Locale = Locale.ROOT,
        val text: Text = Text(),
        val soundEngine: SoundEngine.Settings = SoundEngine.Settings(),
        val playerActions: PlayerActions.Settings = PlayerActions.Settings()
    ) : BasePluginSettings

    @ConfigSerializable
    data class Text(
        val padding: String = " ",
        val charWidths: Map<Char, Int> = emptyMap(),
    )

    private data class Registration(
        val plugin: BasePlugin<*>,
        val onInit: InitContext.() -> Unit,
        val onLoad: LoadContext.() -> Unit,
    )

    interface InitContext {
        val serializers: TypeSerializerCollection.Builder
    }

    interface LoadContext {
        fun addI18NSource(name: String, path: String? = null, source: () -> BufferedReader)

        fun addI18NRoot(name: String? = null, path: Path)

        fun addDefaultI18N()
    }

    lateinit var configOptions: ConfigurationOptions private set
    lateinit var i18n: I18N<Component> private set
    var paddingWidth: Int = -1
        private set
    lateinit var charSizes: MapFont private set

    private val _players = HashMap<Player, AlexandriaPlayer>()
    val players: Map<Player, AlexandriaPlayer> get() = _players

    val playerLocks = PlayerLocks(this)
    val playerActions = PlayerActions(this)
    val debugBoard = DebugBoard()
    val sounds = SoundEngine(this)
    val particles = ParticleEngine(this)
    val meshes = MeshManager()
    val contextMenu = ContextMenu(this)

    private val registrations = ArrayList<Registration>()
    private val onInput = ArrayList<InputHandler>()

    init {
        instance = this
    }

    override fun onLoad() {
        super.onLoad()
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().settings
            .checkForUpdates(false)
            .bStats(true)
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        super.onEnable()
        AlexandriaCommand(this)

        PacketEvents.getAPI().init()
        PacketEvents.getAPI().eventManager.registerListener(object : PacketListenerAbstract(PacketListenerPriority.LOW) {
            override fun onPacketSend(event: PacketSendEvent) {
                val player = event.player as? Player ?: return
                playerFor(player).onPacketSend(event)
            }

            override fun onPacketReceive(event: PacketReceiveEvent) {
                val player = event.player as? Player ?: return
                playerFor(player).onPacketReceive(event)
            }
        })
        EventInputListener { event ->
            onInput.forEach { it(event) }
        }.enable(this)
        registerEvents(object : Listener {
            @EventHandler
            fun on(event: PlayerJoinEvent) {
                playerFor(event.player).join()
            }

            @EventHandler
            fun on(event: PlayerQuitEvent) {
                _players.remove(event.player)?.dispose()
            }
        })

        registerDefaultConsumer()
        scheduleRepeating {
            _players.forEach { (_, player) -> player.update() }
            meshes.update()
        }
    }

    override fun initInternal(): Boolean {
        if (!super.initInternal()) return false

        val serializers = TypeSerializerCollection.defaults().childBuilder()
            .registerAll(Serializers.All)
            .registerAll(PaperSerializers.All)

        val initCtx = object : InitContext {
            override val serializers get() = serializers
        }
        registrations.forEach { it.onInit(initCtx) }

        configOptions = ConfigurationOptions.defaults()
            .serializers(serializers.registerAnnotatedObjects(
                ObjectMapper.factoryBuilder()
                    .addDiscoverer(dataClassFieldDiscoverer())
                    .defaultNamingScheme(NamingSchemes.SNAKE_CASE)
                    .build())
                .build())

        playerLocks.enable()
        contextMenu.enable()

        return true
    }

    override fun loadInternal(log: LogList): Boolean {
        if (!super.loadInternal(log)) return false

        // bStats
        if (settings.enableBstats) {
            Metrics(this, BSTATS_ID)
        }

        // i18n
        val locale = settings.locale
        val mmI18NBuilder = MiniMessageI18N.Builder().apply {
            data class I18NSource(
                val name: String,
                val source: () -> BufferedReader,
            )

            fun loadFromSource(warnLevel: LogLevel, source: I18NSource) {
                try {
                    load(configLoader().source(source.source).build())
                    log.line(LogLevel.Verbose) { "Loaded language resource ${source.name}" }
                } catch (ex: ConfigurateException) {
                    log.line(warnLevel, ex) { "Could not parse language resource ${source.name}" }
                }
            }

            data class I18NRoot(
                val name: String,
                val path: Path,
            )

            fun loadFromRoot(root: I18NRoot) {
                walkFile(root.path,
                    onVisit = { path, _ ->
                        loadFromSource(LogLevel.Warning, I18NSource("${root.name} : $path") {
                            Files.newBufferedReader(path)
                        })
                        FileVisitResult.CONTINUE
                    },
                    onFail = { path, ex ->
                        log.line(LogLevel.Warning, ex) { "Could not access language resource ${root.name} : $path" }
                        FileVisitResult.CONTINUE
                    }
                )
            }

            // get data from registrations
            val sources = ArrayList<I18NSource>()
            val roots = ArrayList<I18NRoot>()

            registrations.forEach { reg ->
                val regName = reg.plugin.manifest.name
                reg.onLoad(object : LoadContext {
                    override fun addI18NSource(name: String, path: String?, source: () -> BufferedReader) {
                        val baseName = "$regName/$name"
                        sources.add(I18NSource(path?.let { "$baseName : $it" } ?: baseName, source))
                    }

                    override fun addI18NRoot(name: String?, path: Path) {
                        roots.add(I18NRoot(name?.let { "$regName/$it" } ?: regName, path))
                    }

                    override fun addDefaultI18N() {
                        // default sources
                        reg.plugin.manifest.langPaths.forEach { path ->
                            addI18NSource("jar", path) {
                                reg.plugin.resource(path).bufferedReader()
                            }
                        }
                        addI18NRoot(path = reg.plugin.dataFolder.resolve(PATH_LANG).toPath())

                        // default accent style
                        styles["accent_$regName"] = Style.style(reg.plugin.manifest.accentColor)
                    }
                })
            }

            // load from sources
            sources.forEach { loadFromSource(LogLevel.Error, it) }

            // load from file system
            roots.forEach {
                if (it.path.isDirectory()) {
                    loadFromRoot(it)
                }
            }

            // calculate num of unique locales and message keys
            val locales = HashSet<Locale>()
            val keys = HashSet<String>()
            translations.forEach { root ->
                locales.add(root.locale)
                root.visit { node, path ->
                    if (node is TranslationNode.Value)
                        keys.add(path.joinToString(PATH_SEPARATOR))
                }
            }

            log.line(LogLevel.Info) { "Loaded translations for ${locales.size} locales, ${keys.size} keys, ${substitutions.size} substitutions, ${styles.size} styles" }
        }

        i18n = try {
            val baseI18N = mmI18NBuilder.build(locale, MiniMessage.miniMessage())
            TextReplacerI18N(baseI18N, textReplacements)
        } catch (ex: I18NBuildException) {
            log.line(LogLevel.Error, ex) { "Could not load translations" }
            EmptyI18N(Component.empty())
        }

        log.line(LogLevel.Info) { "Initialized translations, default locale: ${locale.toLanguageTag()}" }

        // text
        charSizes = MinecraftFont()
        settings.text.charWidths.forEach { (char, width) ->
            charSizes.setChar(char, MapFont.CharacterSprite(width, 0, booleanArrayOf()))
        }
        paddingWidth = widthOf(settings.text.padding)

        sounds.load()
        playerActions.load()

        return true
    }

    override fun onDisable() {
        _players.forEach { (_, player) -> player.dispose() }
        PacketEvents.getAPI().terminate()
    }

    fun registerConsumer(
        plugin: BasePlugin<*>,
        onInit: (InitContext) -> Unit = {},
        onLoad: (LoadContext) -> Unit = {},
    ) {
        registrations.add(Registration(plugin, onInit, onLoad))
    }

    fun onInput(handler: InputHandler) {
        onInput.add(handler)
    }

    fun configLoader(): AbstractConfigurationLoader.Builder<*, *> =
        HoconConfigurationLoader.builder().defaultOptions(configOptions)

    fun playerFor(player: Player) = _players.computeIfAbsent(player) { AlexandriaPlayer(it) }

    fun i18nFor(aud: Audience) = if (aud is Player) i18n.withLocale(aud.locale()) else i18n

    fun widthOf(text: String): Int {
        // our own error message will be more useful for admins
        return try {
            charSizes.getWidth(text)
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Text '$text' contains characters not defined in 'char_sizes'", ex)
        }
    }

    fun widthOf(component: Component) =
        widthOf(PlainTextComponentSerializer.plainText().serialize(component))

    fun paddingFor(width: Int) =
        settings.text.padding.repeat(width / (paddingWidth + 1))

    fun paddingForWidth(component: Component) =
        paddingFor(widthOf(component))

    private fun registerDefaultConsumer() {
        registerConsumer(this,
            onLoad = { ctx ->
                ctx.addDefaultI18N()
            }
        )
    }

    inner class StringTableRenderer(
        align: (Int) -> TableAlign = { TableAlign.START },
        justify: (Int) -> TableAlign = { TableAlign.START },
        colSeparator: String = "",
        rowSeparator: (List<Int>) -> Iterable<String> = { emptySet() },
    ) : io.gitlab.aecsocket.alexandria.core.StringTableRenderer(align, justify, colSeparator, rowSeparator) {
        override fun widthOf(value: String) =
            this@Alexandria.widthOf(value)

        override fun paddingOf(width: Int) =
            this@Alexandria.paddingFor(width)
    }

    inner class ComponentTableRenderer(
        align: (Int) -> TableAlign = { TableAlign.START },
        justify: (Int) -> TableAlign = { TableAlign.START },
        colSeparator: Component = Component.empty(),
        rowSeparator: (List<Int>) -> Iterable<Component> = { emptySet() },
    ) : io.gitlab.aecsocket.alexandria.core.ComponentTableRenderer(align, justify, colSeparator, rowSeparator) {
        override fun widthOf(value: Component) =
            this@Alexandria.widthOf(value)

        override fun paddingOf(width: Int) =
            text(this@Alexandria.paddingFor(width))
    }

    companion object {
        const val Namespace = "alexandria"

        fun namespaced(value: String) = "$Namespace:$value"
    }
}

fun Player.sendPacket(packet: PacketWrapper<*>) {
    PacketEvents.getAPI().playerManager.sendPacket(this, packet)
}