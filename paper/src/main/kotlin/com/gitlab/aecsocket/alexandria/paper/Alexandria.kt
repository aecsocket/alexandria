package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.gitlab.aecsocket.alexandria.core.LogLevel
import com.gitlab.aecsocket.alexandria.core.LogList
import com.gitlab.aecsocket.alexandria.core.TableAlign
import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.extension.walkFile
import com.gitlab.aecsocket.alexandria.core.serializer.Serializers
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import com.gitlab.aecsocket.alexandria.paper.extension.scheduleRepeating
import com.gitlab.aecsocket.alexandria.paper.serializer.PaperSerializers
import com.gitlab.aecsocket.glossa.adventure.MiniMessageI18N
import com.gitlab.aecsocket.glossa.adventure.load
import com.gitlab.aecsocket.glossa.core.I18N
import com.gitlab.aecsocket.glossa.core.PATH_SEPARATOR
import com.gitlab.aecsocket.glossa.core.TranslationNode
import com.gitlab.aecsocket.glossa.core.visit
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.map.MapFont
import org.bukkit.map.MinecraftFont
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.dataClassFieldDiscoverer
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.objectmapping.meta.Required
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.util.NamingSchemes
import java.io.BufferedReader
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import kotlin.io.path.isDirectory
import kotlin.reflect.KClass

private const val LOCALE = "locale"
private const val PADDING = "padding"
private const val CHAR_WIDTHS = "char_widths"

private const val DATABASE_NAME = "alexandria.db"

private lateinit var instance: Alexandria
val AlexandriaAPI get() = instance

class Alexandria : BasePlugin() {
    @ConfigSerializable
    data class DatabaseSettings(
        @Required val jdbcUrl: String,
    )

    private data class Registration(
        val plugin: BasePlugin,
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

    lateinit var padding: String private set
    lateinit var charSizes: MapFont private set
    lateinit var i18n: I18N<Component> private set
    lateinit var configOptions: ConfigurationOptions private set
    internal var db: HikariDataSource? = null
        private set

    var paddingWidth: Int = -1
        private set

    private val _players = HashMap<Player, AlexandriaPlayer>()
    val players: Map<Player, AlexandriaPlayer> get() = _players

    val playerLocks = PlayerLocks(this)
    val playerActions = PlayerActions(this)
    val humanPersistence = HumanPersistence(this)
    val contextActions = ContextActions(this)
    val debugBoard = DebugBoard(this)
    val meshes = MeshManager(this)

    private val registrations = ArrayList<Registration>()

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
                playerOf(player).onPacketSend(event)
            }

            override fun onPacketReceive(event: PacketReceiveEvent) {
                val player = event.player as? Player ?: return
                playerOf(player).onPacketReceive(event)
            }
        })
        registerEvents(object : Listener {
            @EventHandler
            fun on(event: PlayerQuitEvent) {
                _players.remove(event.player)?.dispose()
            }
        })
        registerConsumer(this,
            onLoad = {
                addDefaultI18N()
            }
        )

        scheduleRepeating {
            _players.forEach { (_, player) -> player.update() }
        }
    }

    override fun initInternal(): Boolean {
        if (super.initInternal()) {
            val serializers = TypeSerializerCollection.defaults().childBuilder()
                .registerAll(Serializers.ALL)
                .registerAll(PaperSerializers.ALL)

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
            playerActions.enable()
            contextActions.enable()
            meshes.enable()
            humanPersistence.enable()

            return true
        }
        return false
    }

    override fun loadInternal(log: LogList, settings: ConfigurationNode): Boolean {
        if (super.loadInternal(log, settings)) {
            val locale = settings.node(LOCALE).get<Locale> { Locale.ROOT }
            i18n = MiniMessageI18N.Builder().apply {
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
                            style("accent_$regName") { color(reg.plugin.manifest.accentColor) }
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

                log.line(LogLevel.Info) { "Loaded translations for ${locales.size} locales, ${keys.size} keys, ${styles.size} styles, ${formats.sizeOfAll()} formats" }
            }.build(locale, MiniMessage.miniMessage())
            log.line(LogLevel.Info) { "Initialized translations, default locale: ${locale.toLanguageTag()}" }

            db?.close()
            val db = HikariDataSource(HikariConfig().apply {
                jdbcUrl = "jdbc:sqlite:${dataFolder.resolve(DATABASE_NAME).absolutePath}"
            }).also { this.db = it }
            try {
                db.connection.close()
                log.line(LogLevel.Verbose) { "Connected to database" }
            } catch (ex: SQLException) {
                log.line(LogLevel.Error, ex) { "Could not establish connection to local database" }
                return false
            }

            padding = settings.node(PADDING).get { " " }
            charSizes = MinecraftFont()
            settings.node(CHAR_WIDTHS).childrenMap().forEach { (char, width) ->
                charSizes.setChar(
                    char.toString()[0],
                    MapFont.CharacterSprite(width.force(), 0, booleanArrayOf())
                )
            }

            paddingWidth = widthOf(padding)

            fun tryLoad(featureType: KClass<*>, action: () -> Unit): Boolean? {
                try {
                    action()
                } catch (ex: Exception) {
                    log.line(LogLevel.Error, ex) { "Could not load ${featureType.simpleName} settings" }
                    return false
                }
                return null
            }

            tryLoad(PlayerActions::class) { playerActions.load(settings) }?.let { return it }
            tryLoad(HumanPersistence::class) { humanPersistence.load(settings) }?.let { return it }
            tryLoad(ContextActions::class) { contextActions.load(settings) }?.let { return it }

            return true
        }
        return false
    }

    override fun onDisable() {
        _players.forEach { (_, player) -> player.dispose() }
        PacketEvents.getAPI().terminate()
    }

    fun registerConsumer(
        plugin: BasePlugin,
        onInit: InitContext.() -> Unit = {},
        onLoad: LoadContext.() -> Unit = {},
    ) {
        registrations.add(Registration(plugin, onInit, onLoad))
    }

    fun configLoader() = HoconConfigurationLoader.builder().defaultOptions(configOptions)

    fun playerOf(player: Player) = _players.computeIfAbsent(player) { AlexandriaPlayer(this, it) }

    fun i18nFor(aud: Audience) = if (aud is Player) i18n.withLocale(aud.locale()) else i18n

    internal fun useDb(action: (Connection) -> Unit) {
        try {
            db?.connection?.use(action)
        } catch (ex: Exception) {
            log.line(LogLevel.Warning, ex) { "An error occurred performing a database action" }
        }
    }

    internal fun useIO(block: CoroutineScope.() -> Unit) {
        runBlocking(Dispatchers.IO) {
            try {
                block(this)
            } catch (ex: Exception) {
                log.line(LogLevel.Warning, ex) { "An error occurred performing an IO action" }
            }
        }
    }

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

    fun paddingOf(width: Int) =
        padding.repeat(width / (paddingWidth + 1))

    fun paddingOfWidth(component: Component) =
        paddingOf(widthOf(component))

    inner class StringTableRenderer(
        align: (Int) -> TableAlign = { TableAlign.START },
        justify: (Int) -> TableAlign = { TableAlign.START },
        colSeparator: String = "",
        rowSeparator: (List<Int>) -> Iterable<String> = { emptySet() },
    ) : com.gitlab.aecsocket.alexandria.core.StringTableRenderer(align, justify, colSeparator, rowSeparator) {
        override fun widthOf(value: String) =
            this@Alexandria.widthOf(value)

        override fun paddingOf(width: Int) =
            this@Alexandria.paddingOf(width)
    }

    inner class ComponentTableRenderer(
        align: (Int) -> TableAlign = { TableAlign.START },
        justify: (Int) -> TableAlign = { TableAlign.START },
        colSeparator: Component = empty(),
        rowSeparator: (List<Int>) -> Iterable<Component> = { emptySet() },
    ) : com.gitlab.aecsocket.alexandria.core.ComponentTableRenderer(align, justify, colSeparator, rowSeparator) {
        override fun widthOf(value: Component) =
            this@Alexandria.widthOf(value)

        override fun paddingOf(width: Int) =
            text(this@Alexandria.paddingOf(width))
    }

    companion object {
        const val Namespace = "alexandria"

        fun namespaced(value: String) = "$Namespace:$value"
    }
}

fun Player.sendPacket(packet: PacketWrapper<*>) {
    PacketEvents.getAPI().playerManager.sendPacket(this, packet)
}
