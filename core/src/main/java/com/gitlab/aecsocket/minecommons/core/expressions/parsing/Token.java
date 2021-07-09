package com.gitlab.aecsocket.minecommons.core.expressions.parsing;

/**
 * A symbol in a text input.
 * @param type The type.
 * @param sequence The text input for this symbol.
 */
public record Token(int type, String sequence) {
    /** A singleton token representing no input. */
    public static final Token EPSILON = new Token(0, "");

    @Override public String toString() { return "<%d> `%s`".formatted(type, sequence); }
}
