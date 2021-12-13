package com.gitlab.aecsocket.minecommons.core.i18n;

import au.com.bytecode.opencsv.CSVReader;
import com.gitlab.aecsocket.minecommons.core.Callback;
import com.gitlab.aecsocket.minecommons.core.Files;
import com.gitlab.aecsocket.minecommons.core.serializers.I18NFormatSerializer;
import com.gitlab.aecsocket.minecommons.core.serializers.Serializers;
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides utilities to load formats and translations into a {@link MutableI18N} from external sources.
 */
public final class I18NLoader {
    private I18NLoader() {}

    /** {@code locale}. */
    public static final String LOCALE = "locale";
    /** {@code entries}. */
    public static final String ENTRIES = "entries";
    /** The extension used to mark files as translation files. */
    public static final String EXTENSION = "csv";
    /** {@code formats.conf}. */
    public static final Path FORMATS = Path.of("formats.conf");

    private static final ConfigurationOptions configOptions = ConfigurationOptions.defaults()
            .serializers(serializers -> serializers
                    .registerExact(Format.class, I18NFormatSerializer.INSTANCE)
                    .registerAll(ConfigurateComponentSerializer.configurate().serializers()));

    /**
     * Loads translations into an i18n through a reader.
     * @param i18n The i18n service.
     * @param reader The reader.
     * @return The translation created.
     * @throws IOException If the translation could not be parsed.
     */
    public static Translation loadTranslations(MutableI18N i18n, Reader reader) throws IOException {
        CSVReader csv = new CSVReader(reader);
        Locale locale = null;
        Map<String, List<String>> translations = new HashMap<>();
        for (var record : csv.readAll()) {
            if (record.length == 0)
                continue;
            String key = record[0];
            if (key.equals(LOCALE)) {
                if (record.length == 2) {
                    if (locale == null)
                        locale = Locale.forLanguageTag(record[1]);
                    else
                        throw new IOException("Locale under key `" + LOCALE + "` is defined multiple times");
                } else
                    throw new IOException("Record under key `" + LOCALE + "` must have columns [`locale`,translation locale]");
            } else {
                List<String> value = new ArrayList<>(Arrays.asList(record).subList(1, record.length));
                translations.put(key, value);
            }
        }

        if (locale == null)
            throw new IOException("No locale defined under key `" + LOCALE + "`");
        Translation translation = new Translation(locale, translations);
        i18n.registerTranslation(translation);
        return translation;
    }

    private interface RecursiveConsumer {
        void accept(ConfigurationNode node, String path) throws ConfigurateException;
    }

    private static void recursiveLoad(ConfigurationNode root, RecursiveConsumer consumer) throws ConfigurateException {
        root.visit((ConfigurationVisitor.Stateless<ConfigurateException>) node -> {
            NodePath path = node.path();
            if (path.size() == 0)
                return;
            consumer.accept(node, Stream.of(path.array()).map(Object::toString).collect(Collectors.joining(".")));
        });
    }

    /**
     * A result of loading a format.
     * @param key The format key.
     * @param format The format.
     */
    public record FormatResult(String key, Format format) {}

    private static void loadFormats(MutableI18N i18n, List<FormatResult> result, ConfigurationNode root, String... path) throws ConfigurateException {
        for (var entry : root.childrenMap().entrySet()) {
            var node = entry.getValue();
            String[] newPath = Arrays.copyOfRange(path, 0, path.length + 1);
            newPath[path.length] = ""+entry.getKey();
            if (node.isMap()) {
                loadFormats(i18n, result, node, newPath);
            } else
                result.add(new FormatResult(String.join(".", newPath), Serializers.require(node, Format.class)));
        }
    }

    /**
     * Loads formats into an i18n service from a Configurate configuration loader.
     * @param i18n The i18n service.
     * @param loaderFactory The factory for the configuration loader.
     * @return The formats created.
     * @throws ConfigurateException If the formats could not be parsed.
     */
    public static List<FormatResult> loadFormats(MutableI18N i18n, Supplier<ConfigurationLoader<?>> loaderFactory) throws ConfigurateException {
        ConfigurationNode node = loaderFactory.get().load(configOptions).node(ENTRIES);
        if (!node.isMap())
            throw new ConfigurateException(node, "Entries must be a map");
        List<FormatResult> result = new ArrayList<>();
        loadFormats(i18n, result, node);
        for (var entry : result) {
            i18n.registerFormat(entry.key, entry.format);
        }
        return result;
    }

    /**
     * A result of a loading operation in {@link #load(MutableI18N, File, Function)}.
     */
    public sealed interface Result permits Result.Missing,
            Result.FormatParseException,
            Result.FileOpenException, Result.FileParseException,
            Result.Success {
        /**
         * Gets the path that the result took place at.
         * @return The path.
         */
        Path path();

        /**
         * An important - but not critical - file was missing.
         * @param path The file path.
         */
        record Missing(Path path) implements Result {}

        /**
         * A format file could not be parsed.
         * @param path The file path.
         * @param exception The exception cause.
         */
        record FormatParseException(Path path, ConfigurateException exception) implements Result {}

        /**
         * A translation file could not be opened.
         * @param path The file path.
         * @param exception The exception cause.
         */
        record FileOpenException(Path path, IOException exception) implements Result {}

        /**
         * A translation file could not be parsed.
         * @param path The file path.
         * @param exception The exception cause.
         */
        record FileParseException(Path path, IOException exception) implements Result {}

        /**
         * A translation file was successfully parsed and registered.
         * @param path The file path.
         * @param translation The parsed translation.
         */
        record Success(Path path, Translation translation) implements Result {}
    }

    /**
     * Recursively loads translations and formats from a file (intended for a data folder).
     * @param i18n The i18n service.
     * @param root The root file.
     * @param loaderFactory Maps a file to a configuration loader.
     * @return The results.
     */
    public static Callback<Result> load(MutableI18N i18n, File root, Function<File, ConfigurationLoader<?>> loaderFactory) {
        Callback<Result> callback = Callback.create();

        // Load formats
        File formats = root.toPath().resolve(FORMATS).toFile();
        if (formats.exists()) {
            try {
                loadFormats(i18n, () -> loaderFactory.apply(formats));
            } catch (ConfigurateException e) {
                callback.add(new Result.FormatParseException(FORMATS, e));
            }
        } else
            callback.add(new Result.Missing(FORMATS));

        // Load translations
        Files.recursively(root, (file, path) -> {
            if (file.getName().endsWith(EXTENSION)) {
                FileReader reader;
                try {
                    reader = new FileReader(file);
                } catch (IOException e) {
                    callback.add(new Result.FileOpenException(path, e));
                    return;
                }

                try {
                    loadTranslations(i18n, reader);
                } catch (IOException e) {
                    callback.add(new Result.FileParseException(path, e));
                }
            }
        });

        return callback;
    }
}
