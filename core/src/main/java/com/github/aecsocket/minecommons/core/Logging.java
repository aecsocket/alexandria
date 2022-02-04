package com.github.aecsocket.minecommons.core;

import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;

/**
 * Convenience class for pretty logging, regardless of {@link Logger} level.
 */
public final class Logging {
    /** ANSI color escape sequence start. */
    private static final String START = "\033[";
    /** ANSI color escape sequence split. */
    private static final String SPLIT = ";";
    /** ANSI color escape sequence end. */
    private static final String END = "m";

    /**
     * A logging level, determining the style and priority of a logging message.
     * @param name The full name of the level.
     * @param level The numerical logging level. Higher is quieter.
     * @param prefix The short prefix of the level.
     * @param bgColor The ANSI color code for the prefix background color.
     * @param fgColor The ANSI color code for the prefix foreground color.
     * @param textColor The ANSI color code for the message text color.
     */
    public record Level(String name, int level, String prefix, String bgColor, String fgColor, String textColor) {
        /** Very fine detail, for debugging. */
        public static final Level DEBUG = new Level("debug", -2, "DBG", "100", "37", "90");
        /** Fine detail, to get extra detail on operations. */
        public static final Level VERBOSE = new Level("verbose", -1, "VRB", "44", "39", "37");
        /** Standard, important user-facing info. */
        public static final Level INFO = new Level("info", 0, "INF", "42", "30", "39");
        /** Notices which are not fatal but should be addressed by a user. */
        public static final Level WARNING = new Level("warning", 1, "WRN", "43", "30", "33");
        /** Notices which may be fatal to execution. */
        public static final Level ERROR = new Level("error", 2, "ERR", "41", "39", "31");

        /**
         * Creates an instance.
         * @param name The name.
         * @param level The numerical level.
         * @param prefix The prefix.
         * @param bgColor The background color code.
         * @param fgColor The foreground color code.
         * @param textColor The text color code.
         */
        public Level {
            Validation.notNull("name", name);
            Validation.notNull("prefix", prefix);
        }

        /**
         * Generates a prefix for logged messages.
         * @return The prefix.
         */
        private String genPrefix() {
            return START + bgColor + SPLIT + fgColor + END + // Color
                " " + prefix + " " + // Prefix
                START + "0" + SPLIT + textColor + END; // Text color
        }

        /**
         * The default levels, mapped to their {@link #name} field.
         */
        public static final Map<String, Level> DEFAULTS = ImmutableMap.<String, Level>builder()
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

    /**
     * Creates an instance.
     * @param logger The underlying logger to log to.
     * @param level The level to log at.
     */
    public Logging(Logger logger, Level level) {
        Validation.notNull("logger", logger);
        Validation.notNull("level", level);
        this.logger = logger;
        this.level = level;
    }

    /**
     * Creates an instance.
     * @param logger The underlying logger to log to.
     */
    public Logging(Logger logger) {
        Validation.notNull("logger", logger);
        this.logger = logger;
    }

    /**
     * Gets the underlying logger.
     * @return The logger.
     */
    public Logger logger() { return logger; }

    /**
     * Gets the level to log at.
     * @return The level.
     */
    public Level level() { return level; }

    /**
     * Sets the level to log at.
     * @param level The level.
     */
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
        logger.log(java.util.logging.Level.INFO, () -> level.genPrefix() + " " + text.formatted(args) + START + "0" + END);
    }
}
