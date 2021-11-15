package com.gitlab.aecsocket.minecommons.paper.plugin;

import com.gitlab.aecsocket.minecommons.core.Logging;
import com.gitlab.aecsocket.minecommons.core.Settings;
import com.gitlab.aecsocket.minecommons.core.Text;
import com.gitlab.aecsocket.minecommons.core.serializers.Serializers;
import com.gitlab.aecsocket.minecommons.core.translation.LocalizerLoader;
import com.gitlab.aecsocket.minecommons.core.translation.MiniMessageLocalizer;
import com.gitlab.aecsocket.minecommons.paper.serializers.PaperSerializers;
import com.gitlab.aecsocket.minecommons.paper.serializers.protocol.ProtocolSerializers;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.NamingSchemes;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.util.*;

/**
 * Plugin utility class.
 */
public abstract class BasePlugin<S extends BasePlugin<S>> extends JavaPlugin {
    /** The path to the resources manifest file in the JAR. */
    public static final String PATH_RESOURCES = "resources.conf";

    /** How detailed stack trace logs will be. */
    public enum StackTraceLogging {
        /** The entire stack trace is logged. */
        FULL,
        /** A user-readable, per-line breakdown of the trace is logged. */
        SIMPLIFIED,
        /** A single-line, compact version of the trace is logged. */
        MINIMAL
    }

    /** The resources that this defines. */
    protected ResourceManifest resourceManifest;
    /** The namespaced keys this caches. */
    protected final Map<String, NamespacedKey> keys = new HashMap<>();
    /** The logging provider. */
    protected final Logging logging = new Logging(getLogger());
    /** The settings. */
    protected Settings settings = new Settings();
    /** The localizer. */
    protected final MiniMessageLocalizer localizer = MiniMessageLocalizer.builder().build();
    /** The configuration options. */
    protected ConfigurationOptions configOptions = ConfigurationOptions.defaults()
            .serializers(builder -> builder
            .registerAnnotatedObjects(ObjectMapper.factoryBuilder().defaultNamingScheme(NamingSchemes.SNAKE_CASE).build()));
    /** The ProtocolLib integration, if loaded. */
    protected ProtocolLibAPI protocol;
    /** The base command. */
    protected BaseCommand<S> command;

    @Override
    public void onEnable() {
        if (hasDependency(getDescription(), "ProtocolLib") && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
            protocol = new ProtocolLibAPI(this);

        loadResourceManifest();
        saveResources();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::serverLoad, 0);
        try {
            command = createCommand();
        } catch (Exception e) {
            log(Logging.Level.ERROR, e, "Could not initialize command manager - command functionality will be disabled");
        }
    }

    /**
     * Creates a {@link BaseCommand} from this plugin.
     * @return The base command.
     * @throws Exception If the base command could not be created.
     */
    protected abstract BaseCommand<S> createCommand() throws Exception;

    /**
     * Loads the resource manifest from the JAR.
     */
    protected void loadResourceManifest() {
        Reader reader = getTextResource(PATH_RESOURCES);
        if (reader == null)
            throw new IllegalStateException("No JAR resources manifest at [" + PATH_RESOURCES + "] found!");

        try {
            resourceManifest = loaderBuilder()
                    .source(() -> new BufferedReader(reader))
                    .build()
                    .load().get(ResourceManifest.class);
            if (resourceManifest == null)
                throw new ConfigurateException("null");
        } catch (ConfigurateException e) {
            throw new IllegalStateException("Could not load resource manifest at [" + PATH_RESOURCES + "]", e);
        }
    }

    /**
     * Saves all resources defined to be saved in the resources manifest,
     * into the {@link #getDataFolder()} folder, if the data folder does not exist yet.
     */
    protected void saveResources() {
        if (!getDataFolder().exists()) {
            for (String path : resourceManifest.saved()) {
                saveResource(path, false);
            }
        }
    }

    /**
     * Sets up config option serializers.
     * @see #configOptionsDefaults(TypeSerializerCollection.Builder, ObjectMapper.Factory.Builder)
     */
    protected void setupConfigOptions() {
        TypeSerializerCollection.Builder serializers = TypeSerializerCollection.defaults().childBuilder();

        ObjectMapper.Factory.Builder mapperFactory = ObjectMapper.factoryBuilder();
        configOptionsDefaults(serializers, mapperFactory);
        serializers.registerAnnotatedObjects(mapperFactory.build());

        configOptions = configOptions.serializers(serializers.build());
    }

    /**
     * Registers default serializers and object mapper factory builder parameters.
     * @param serializers The serializers.
     * @param mapperFactory The object mapper factory builder.
     */
    protected void configOptionsDefaults(TypeSerializerCollection.Builder serializers, ObjectMapper.Factory.Builder mapperFactory) {
        mapperFactory.defaultNamingScheme(NamingSchemes.SNAKE_CASE);
        serializers.registerAll(PaperSerializers.SERIALIZERS);
        serializers.registerAll(Serializers.SERIALIZERS);
        if (protocol != null)
            serializers.registerAll(ProtocolSerializers.SERIALIZERS);
    }

    /**
     * Gets the logging provider.
     * @return The logging.
     */
    public Logging logging() { return logging; }

    /**
     * Gets the settings.
     * @return The settings.
     */
    public Settings settings() { return settings; }

    /**
     * Gets the localizer.
     * @return The localizer.
     */
    public MiniMessageLocalizer lc() { return localizer; }

    /**
     * Gets the configuration options.
     * @return The configuration options.
     */
    public ConfigurationOptions configOptions() { return configOptions; }

    /**
     * Gets the ProtocolLib integration.
     * @return The integration.
     */
    public ProtocolLibAPI protocol() { return protocol; }

    /**
     * Gets the root command.
     * @return The root command.
     */
    public BaseCommand<S> command() { return command; }

    /**
     * Gets the default locale of the plugin.
     * @return The locale.
     */
    public Locale defaultLocale() { return setting(Locale.US, (n, d) -> n.get(Locale.class, d), "locale"); }

    /**
     * The method that runs on server load.
     */
    protected void serverLoad() {
        setupConfigOptions();
        load();
    }

    /**
     * Clears and loads all runtime plugin data, including settings and language files.
     */
    public void load() {
        // Settings
        try {
            settings = loadSettings(file(resourceManifest.settings()));
        } catch (ConfigurateException e) {
            settings = new Settings();
            log(Logging.Level.ERROR, e, "Could not load settings from `%s`", resourceManifest.settings());
            return;
        }

        logging.level(setting(Logging.Level.INFO, (n, d) -> n.get(Logging.Level.class, d), "log_level"));

        // Language
        localizer.clear();
        localizer.defaultLocale(setting(Locale.US, (n, d) -> n.get(Locale.class, d), "locale"));

        for (String path : resourceManifest.language().resources()) {
            Reader reader = getTextResource(path);
            if (reader == null) {
                log(Logging.Level.WARNING, "No JAR translation at %s found", path);
                continue;
            }

            try {
                LocalizerLoader.load(localizer, () -> HoconConfigurationLoader.builder()
                        .source(() -> new BufferedReader(reader))
                        .build());
            } catch (ConfigurateException e) {
                log(Logging.Level.WARNING, e, "Could not load JAR translation from %s", path);
            }
        }

        LocalizerLoader.hocon(file(resourceManifest.language().dataPath()), localizer)
                .forEach(result -> {
            if (result.translation() != null)
                log(Logging.Level.VERBOSE, "Loaded language %s from %s", result.translation().locale().toLanguageTag(), result.path());
            else if (result.exception() != null)
                log(Logging.Level.WARNING, result.exception(), "Could not load language file from %s", result.path());
        });
    }

    /**
     * Cleans up current state, and {@link #load()}s data.
     */
    public void reload() {
        load();
    }

    /**
     * Gets a sub-file of this plugin's {@link #getDataFolder()}.
     * @param path The path to the file.
     * @return The file.
     */
    public File file(String path) { return new File(getDataFolder(), path); }

    /**
     * Creates a builder for a HOCON loader, using this plugin's configuration options.
     * @return The loader builder.
     */
    public HoconConfigurationLoader.Builder loaderBuilder() {
        return HoconConfigurationLoader.builder()
                .defaultOptions(configOptions);
    }

    /**
     * Builds a new configuration loader from a file, using this plugin's configuration options.
     * <p>
     * By default uses a {@link HoconConfigurationLoader}.
     * @param file The file.
     * @return The loader.
     */
    public ConfigurationLoader<?> loader(File file) {
        return loaderBuilder()
                .file(file)
                .build();
    }

    /**
     * Loads settings from a file, using {@link #loader(File)} as the loader.
     * @param file The file.
     * @return The settings.
     * @throws ConfigurateException If the settings could not be loaded.
     */
    public Settings loadSettings(File file) throws ConfigurateException {
        return Settings.loadFrom(loader(file));
    }

    /**
     * Logs a message formatted with arguments, formatted by {@link String#formatted(Object...)}.
     * @param level The logging level.
     * @param message The message.
     * @param args The arguments.
     */
    public void log(Logging.Level level, String message, Object... args) {
        message.formatted(args).lines()
                .forEach(line -> logging.log(level, line));
    }

    /**
     * Logs an exception and a message with arguments, formatted by {@link String#formatted(Object...)}.
     * @param level The logging level.
     * @param thrown The exception thrown.
     * @param message The message.
     * @param args The arguments.
     */
    public void log(Logging.Level level, Throwable thrown, String message, Object... args) {
        switch (setting(StackTraceLogging.FULL, (n, d) -> n.get(StackTraceLogging.class, d), "stack_trace_logging")) {
            case FULL -> {
                log(level, message, args);
                for (var line : Text.stackTrace(thrown, 4)) {
                    log(level, line);
                }
            }
            case SIMPLIFIED -> {
                log(level, message.formatted(args) + ":");
                for (Throwable cur = thrown; cur != null; cur = cur.getCause()) {
                    log(level, "    " + cur.getClass().getSimpleName() +
                            (cur.getMessage() == null ? "" : ": " + cur.getMessage()));
                }
            }
            case MINIMAL -> log(level, message.formatted(args) + ": " + Text.mergeMessages(thrown));
        }
    }

    /**
     * A functional interface which accepts a {@link ConfigurationNode} and a {@link T} default value.
     * @param <T> The value input.
     * @see #setting(Object, DefaultedNodeFunction, Object...)
     */
    public interface DefaultedNodeFunction<T> {
        /**
         * Maps a node to a T value.
         * @param node The node.
         * @param defaultValue The default value, as supplied in the method.
         * @return The value.
         * @throws SerializationException If the value could not be deserialized.
         */
        T apply(ConfigurationNode node, T defaultValue) throws SerializationException;
    }

    /**
     * Gets a setting from this plugin's settings instance, or a default if it could not be loaded.
     * @param mapper A function to map the {@link ConfigurationNode} and the default {@link T} to a resulting {@link T}, used for caching.
     * @param defaultValue The default value, if there was an exception when deserializing.
     * @param path The path to the value.
     * @param <T> The value input.
     * @return The value.
     * @see Settings#get(Settings.NodeFunction, Settings.Path)
     */
    public <T> T setting(T defaultValue, DefaultedNodeFunction<T> mapper, Object... path) {
        try {
            T value = settings.get(n -> mapper.apply(n, defaultValue), path);
            return value == null ? defaultValue : value;
        } catch (SerializationException e) {
            log(Logging.Level.WARNING, e, "Could not get setting for path %s, defaulting to %s", Arrays.toString(path), defaultValue);
            return defaultValue;
        }
    }

    /**
     * Gets the locale of a command sender.
     * <p>
     * If the sender is a player, returns {@link Player#locale()}, otherwise gets {@link #defaultLocale()}.
     * @param sender The sender.
     * @return The locale.
     */
    public Locale locale(CommandSender sender) {
        return sender instanceof Player ? ((Player) sender).locale() : defaultLocale();
    }

    /**
     * Gets or creates a new namespaced key using this plugin.
     * @param key The name of the key.
     * @return The key.
     */
    public NamespacedKey key(String key) {
        return keys.computeIfAbsent(key, k -> new NamespacedKey(this, k));
    }

    /**
     * Gets if a description file has a dependency or soft-dependency on another plugin.
     * @param desc The description file.
     * @param dependency The dependency.
     * @return The result.
     */
    public static boolean hasDependency(PluginDescriptionFile desc, String dependency) {
        return desc.getDepend().contains(dependency) || desc.getSoftDepend().contains(dependency);
    }
}
