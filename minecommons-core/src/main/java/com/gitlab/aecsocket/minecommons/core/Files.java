package com.gitlab.aecsocket.minecommons.core;

import java.io.File;
import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * Utilities for files.
 */
public final class Files {
    private Files() {}

    private static void recursively(File root, BiConsumer<File, Path> function, Path path) {
        File file = root.toPath().resolve(path).toFile();
        if (file.isDirectory()) {
            for (String child : file.list())
                recursively(root, function, path.resolve(child));
        } else {
            function.accept(file, path);
        }
    }

    /**
     * Applies a function to every file in a directory.
     * @param root The root file.
     * @param function The function to apply to each file (does not apply to directories).
     */
    public static void recursively(File root, BiConsumer<File, Path> function) {
        Validation.notNull("root", root);
        Validation.notNull("function", function);
        recursively(root, function, Path.of(""));
    }

    /**
     * Gets the extension of a file name.
     * <p>
     * Attempts to find a {@code .}. If there is no delimiter in the filename, will return an
     * empty string. Otherwise, returns the last text after the delimiter.
     * @param filename The filename.
     * @return The extension, or an empty string if there was none.
     */
    public static String extension(String filename) {
        Validation.notNull("filename", filename);
        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }
}
