package com.gitlab.aecsocket.minecommons.core;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utilities for text.
 */
public final class Text {
    private Text() {}

    /**
     * Merges a throwable's messages, and its parents (if they have a message), into a single string,
     * split by the delimiter.
     * @param thrown The throwable.
     * @param delimiter The delimiter.
     * @return The joined message.
     */
    public static String mergeMessages(Throwable thrown, String delimiter) {
        Validation.notNull("thrown", (Object) thrown);
        Validation.notNull("delimiter", delimiter);

        String text = thrown.getClass().getSimpleName() + (thrown.getMessage() != null ? " - " + thrown.getMessage() : "");
        return thrown.getCause() == null ? text : text + delimiter + mergeMessages(thrown.getCause(), delimiter);
    }

    /**
     * Merges a throwable's messages, and its parents (if they have a message), into a single string,
     * split by the delimiter `{@code : }`.
     * @param thrown The throwable.
     * @return The joined message.
     */
    public static String mergeMessages(Throwable thrown) {
        return mergeMessages(thrown, " > ");
    }

    /**
     * Gets the individual lines of the stack trace of a throwable.
     * @param thrown The throwable.
     * @param indent The amount of spaces to use to replace {@code \t}'s.
     * @return The lines.
     */
    public static String[] stackTrace(Throwable thrown, int indent) {
        Validation.notNull("thrown", (Object) thrown);
        StringWriter writer = new StringWriter();
        thrown.printStackTrace(new PrintWriter(writer));
        return writer.toString().replace("\t", " ".repeat(indent)).split("\n");
    }

    /**
     * Prefixes an array of lines with a string.
     * @param prefix The prefix.
     * @param lines The lines.
     * @return The modified lines.
     */
    public static String[] prefix(String prefix, String... lines) {
        Validation.notNull("prefix", prefix);
        for (int i = 0; i < lines.length; i++) {
            lines[i] = prefix + lines[i];
        }
        return lines;
    }

    /**
     * Indents an array of lines with spaces.
     * @param indent The amount of spaces to indent with.
     * @param lines The lines.
     * @return The modified lines.
     */
    public static String[] indent(int indent, String... lines) {
        return prefix(" ".repeat(indent), lines);
    }
}
