package com.gitlab.aecsocket.minecommons.core.translation;

import com.gitlab.aecsocket.minecommons.core.Callback;
import com.gitlab.aecsocket.minecommons.core.Files;
import com.gitlab.aecsocket.minecommons.core.serializers.LocaleSerializer;
import com.gitlab.aecsocket.minecommons.core.serializers.TranslationSerializer;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Function;

/**
 * Loads entries into a {@link RegistryLocalizer} from disk.
 */
public class LocalizerLoader {
    /** The file extension that a file must have to be loaded. */
    public static final String FILE_EXTENSION = "conf";
    private static final ConfigurationOptions configOptions = ConfigurationOptions.defaults()
            .serializers(builder -> builder
                    .register(Locale.class, LocaleSerializer.INSTANCE)
                    .register(Translation.class, TranslationSerializer.INSTANCE));

    /**
     * A load result, used in {@link Callback}s.
     */
    public record Result(Path path, Translation translation, ConfigurateException exception) {}

    /**
     * Loads {@link Translation}s into a registry localizer from disk, using {@link ConfigurationLoader}s.
     * @param root The root file.
     * @param localizer The localizer to load into.
     * @param loaderFactory The factory for the {@link ConfigurationLoader}s.
     * @return A callback of results.
     */
    public static Callback<Result> load(File root, RegistryLocalizer localizer, Function<File, ConfigurationLoader<?>> loaderFactory) {
        Callback<Result> callback = new Callback<>();
        Files.recursively(root, (file, path) -> {
            if (!FILE_EXTENSION.equals(Files.extension(file.getName()))) return;
            ConfigurationLoader<?> loader = loaderFactory.apply(file);
            try {
                Translation translation = loader.load().get(Translation.class);
                if (translation != null) {
                    localizer.register(translation);
                    callback.add(new Result(path, translation, null));
                }
            } catch (ConfigurateException e) {
                callback.add(new Result(path, null, e));
            }
        });
        return callback;
    }

    /**
     * Loads {@link Translation}s into a registry localizer from disk, using a {@link HoconConfigurationLoader}.
     * @param root The root file.
     * @param localizer The localizer to load into.
     * @return A callback of results.
     * @see #load(File, RegistryLocalizer, Function) 
     */
    public static Callback<Result> hocon(File root, RegistryLocalizer localizer) {
        return load(root, localizer, file -> HoconConfigurationLoader.builder()
                .file(file)
                .defaultOptions(configOptions)
                .build()
        );
    }
}
