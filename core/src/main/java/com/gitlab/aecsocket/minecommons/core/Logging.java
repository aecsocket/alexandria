package com.gitlab.aecsocket.minecommons.core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Convenience class for pretty logging, regardless of {@link Logger} level.
 */
public final class Logging {
    private static final String start = "\033[";
    private static final String split = ";";
    private static final String end = "m";

    /**
     * A logging level, determining the style and priority of a logging message.
     */
    public record Level(String name, int level, String prefix, String bgColor, String fgColor, String textColor) {
        public static final Level DEBUG = new Level("debug", -2, "DBG", "100", "37", "90");
        public static final Level VERBOSE = new Level("verbose", -1, "VRB", "44", "39", "37");
        public static final Level INFO = new Level("info", 0, "INF", "42", "30", "39");
        public static final Level WARNING = new Level("warning", 1, "WRN", "43", "30", "33");
        public static final Level ERROR = new Level("error", 2, "ERR", "41", "39", "31");

        public Level {
            Validation.notNull(name, "name");
            Validation.notNull(prefix, "prefix");
        }

        private String genPrefix() {
            return start + bgColor + split + fgColor + end + // Color
                    " " + prefix + " " + // Prefix
                    start + "0" + split + textColor + end; // Text color
        }

        /**
         * The default levels, mapped to their {@link #name} field.
         */
        public static final Map<String, Level> DEFAULTS = CollectionBuilder.map(new HashMap<String, Level>())
                .put(DEBUG.name, DEBUG)
                .put(VERBOSE.name, VERBOSE)
                .put(INFO.name, INFO)
                .put(WARNING.name, WARNING)
                .put(ERROR.name, ERROR)
                .build();

        /**
         * Gets a default level by its {@link #name} field.
         * @param name The name.
         * @return The level.
         * @throws IllegalArgumentException If the level name could not be found in the defaults.
         */
        public static Level valueOf(String name) throws IllegalArgumentException {
            Level result = DEFAULTS.get(name);
            if (result == null)
                throw new IllegalArgumentException("Bad level `%s`, valid types: %s".formatted(name, DEFAULTS.keySet()));
            return result;
        }
    }

    private final Logger logger;
    private Level level = Level.INFO;

    public Logging(Logger logger, Level level) {
        Validation.notNull(logger, "logger");
        Validation.notNull(level, "level");
        this.logger = logger;
        this.level = level;
    }

    public Logging(Logger logger) {
        Validation.notNull(logger, "logger");
        this.logger = logger;
    }

    public Logger logger() { return logger; }

    public Level level() { return level; }
    public void level(Level level) { this.level = level; }

    /**
     * Logs an entry, if this instance's level is high enough.
     * <p>
     * If the level provided has a lower {@link #level} field than this instance's
     * level's {@link #level} field, the message will not be logged.
     * <p>
     * Is always logged to the {@link Logger} as {@link java.util.logging.Level#INFO}.
     * @param level The logging level.
     * @param text The text to log.
     * @param args The arguments to format the text with, using {@link String#formatted(Object...)}.
     */
    public void log(Level level, String text, Object... args) {
        if (level.level < this.level.level)
            return;
        logger.log(java.util.logging.Level.INFO, level.genPrefix() + " " + text.formatted(args) + start + "0" + end);
    }
}
