package com.github.aecsocket.minecommons.core.i18n;

import au.com.bytecode.opencsv.CSVReader;
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import net.kyori.adventure.text.format.Style;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.aecsocket.minecommons.core.Callback;
import com.github.aecsocket.minecommons.core.serializers.I18NFormatSerializer;
import com.github.aecsocket.minecommons.core.serializers.Serializers;

/**
 * Provides utilities to load formats and translations into a {@link MutableI18N} from external sources.
 */
public final class I18NLoader {
    private I18NLoader() {}

    /** The extension used to mark files as config files. */
    public static final String EXT_CONFIG = "conf";
    /** The extension used to mark files as translation files. */
    public static final String EXT_TRANSLATION = "csv";
    /** {@code locale}. */
    public static final String LOCALE = "locale";
    /** {@code entries}. */
    public static final String ENTRIES = "entries";
    /** {@code styles}. */
    public static final String STYLES = "styles";
    /** {@code formats}. */
    public static final String FORMATS = "formats";

    private static final ConfigurationOptions configOptions = ConfigurationOptions.defaults()
        .serializers(serializers -> serializers
            .registerExact(Format.class, I18NFormatSerializer.INSTANCE)
            .registerAll(ConfigurateComponentSerializer.configurate().serializers()));
    private static final Pattern parsePattern = Pattern.compile("\\\\u([0-9a-f]{4})");

    private static String parse(String line) {
        Matcher match = parsePattern.matcher(line);
        return match.replaceAll(result -> {
            int cp = Integer.parseInt(result.group(1), 16);
            return String.valueOf(Character.toChars(cp));
        });
    }

    /**
     * Loads a translation into an i18n through a reader.
     * @param i18n The i18n service.
     * @param reader The reader.
     * @return The translation created.
     * @throws IOException If the translation could not be parsed.
     */
    public static Translation loadTranslation(MutableI18N i18n, Reader reader) throws IOException {
        try (CSVReader csv = new CSVReader(reader)) {
            Locale locale = null;
            Map<String, List<String>> translations = new HashMap<>();
            // format:
            //   locale,[locale of translation]
            //   [key],[comments],[line 1],[line 2],...
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
                        throw new IOException("Record under key `" + LOCALE + "` must have columns [`locale`, <translation locale>]");
                } else {
                    List<String> value = new ArrayList<>();
                    for (int i = 2; i < record.length; i++) {
                        var line = record[i];
                        value.add(parse(line));
                    }
                    translations.put(key, value);
                }
            }

            if (locale == null)
                throw new IOException("No locale defined under key `" + LOCALE + "`");
            Translation translation = new Translation(locale, translations);
            i18n.registerTranslation(translation);
            return translation;
        }
    }

    /**
     * A result of loading an object.
     */
    public interface LoadResult {
        /**
         * Gets the key under which the object was loaded.
         * @return The key.
         */
        String key();
    }

    private interface RecursiveLoader<R> {
        R load(String key, ConfigurationNode node) throws SerializationException;
    }

    /**
     * A result of loading a style.
     * @param key The style key.
     * @param style The style.
     */
    public record StyleResult(String key, Style style) implements LoadResult {}

    /**
     * Loads styles into an i18n service from a Configurate node.
     * @param i18n The i18n service.
     * @param node The configuration node.
     * @return The styles created.
     * @throws SerializationException If the styles could not be parsed.
     */
    public static List<StyleResult> loadStyles(MutableI18N i18n, ConfigurationNode node) throws SerializationException {
        if (!node.isMap())
            throw new SerializationException(node, Style.class, "Entries must be a map");
        List<StyleResult> result = new ArrayList<>();
        for (var entry : node.childrenMap().entrySet()) {
            String key = ""+entry.getKey();
            Style style = Serializers.require(entry.getValue(), Style.class);
            result.add(new StyleResult(key, style));
            i18n.registerStyle(key, style);
        }
        return result;
    }

    /**
     * A result of loading a format.
     * @param key The format key.
     * @param format The format.
     */
    public record FormatResult(String key, Format format) implements LoadResult {}

    /**
     * Loads formats into an i18n service from a Configurate node.
     * @param i18n The i18n service.
     * @param node The configuration node.
     * @return The formats created.
     * @throws SerializationException If the formats could not be parsed.
     */
    public static List<FormatResult> loadFormats(MutableI18N i18n, ConfigurationNode node) throws SerializationException {
        if (!node.isMap())
            throw new SerializationException(node, Format.class, "Entries must be a map");
        List<FormatResult> result = new ArrayList<>();
        loadRecursive(i18n, result, (k, n) -> new FormatResult(k, Serializers.require(n, Format.class)), node);
        for (var entry : result) {
            i18n.registerFormat(entry.key, entry.format);
        }
        return result;
    }

    private static <R> void loadRecursive(MutableI18N i18n, List<R> result, RecursiveLoader<R> loader, ConfigurationNode root, String... path) throws SerializationException {
        for (var entry : root.childrenMap().entrySet()) {
            var node = entry.getValue();
            String[] newPath = Arrays.copyOfRange(path, 0, path.length + 1);
            newPath[path.length] = ""+entry.getKey();
            if (node.isMap()) {
                loadRecursive(i18n, result, loader, node, newPath);
            } else
                result.add(loader.load(String.join(".", newPath), node));
        }
    }

    /**
     * A result of a loading operation in {@link #load(MutableI18N, Path, Function)}.
     */
    public sealed interface Result permits Result.Missing,
        Result.StyleParseException, Result.FormatParseException,
        Result.FileOpenException, Result.ConfigParseException, Result.TranslationParseException,
        Result.OfConfig, Result.OfTranslation {
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
         * A style file could not be parsed.
         * @param path The file path.
         * @param exception The exception cause.
         */
        record StyleParseException(Path path, ConfigurateException exception) implements Result {}

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
         * A config file could not be parsed.
         * @param path The file path.
         * @param exception The exception cause.
         */
        record ConfigParseException(Path path, IOException exception) implements Result {}

        /**
         * A translation file could not be parsed.
         * @param path The file path.
         * @param exception The exception cause.
         */
        record TranslationParseException(Path path, IOException exception) implements Result {}

        /**
         * A config file was successfully parsed and registered.
         * @param path The file path.
         * @param styles The parsed styles.
         * @param formats The parsed formats.
         */
        record OfConfig(Path path, List<StyleResult> styles, List<FormatResult> formats) implements Result {}

        /**
         * A translation file was successfully parsed and registered.
         * @param path The file path.
         * @param translation The parsed translation.
         */
        record OfTranslation(Path path, Translation translation) implements Result {}
    }

    private interface SingleLoader {
        void load(File file) throws ConfigurateException;
    }

    /**
     * Recursively loads translations and formats from a file (intended for a data folder).
     * @param i18n The i18n service.
     * @param root The root path.
     * @param loaderFactory Maps a reader to a configuration loader.
     * @return The results.
     */
    public static Callback<Result> load(MutableI18N i18n, Path root, Function<BufferedReader, ConfigurationLoader<?>> loaderFactory) {
        Callback<Result> callback = Callback.create();

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {
                    enum FileType {
                        CONFIG, TRANSLATION
                    }

                    String name = path.toString().toLowerCase(Locale.ROOT);
                    FileType type = name.endsWith(EXT_CONFIG) ? FileType.CONFIG
                            : name.endsWith(EXT_TRANSLATION) ? FileType.TRANSLATION
                            : null;

                    if (type != null) {
                        try (BufferedReader reader = Files.newBufferedReader(path)) {
                            switch (type) {
                                case CONFIG -> {
                                    try {
                                        ConfigurationNode node = loaderFactory.apply(reader).load(configOptions);
                                        callback.add(new Result.OfConfig(
                                            path,
                                            loadStyles(i18n, node.node(STYLES)),
                                            loadFormats(i18n, node.node(FORMATS))
                                        ));
                                    } catch (IOException e) {
                                        callback.add(new Result.ConfigParseException(path, e));
                                    }
                                }
                                case TRANSLATION -> {
                                    try {
                                        callback.add(new Result.OfTranslation(path, loadTranslation(i18n, reader)));
                                    } catch (IOException e) {
                                        callback.add(new Result.TranslationParseException(path, e));
                                    }
                                }
                            }
                        } catch (IOException e) {
                            callback.add(new Result.FileOpenException(path, e));
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path path, IOException e) {
                    callback.add(new Result.FileOpenException(path, e));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            callback.add(new Result.FileOpenException(root, e));
        }

        return callback;
    }
}
