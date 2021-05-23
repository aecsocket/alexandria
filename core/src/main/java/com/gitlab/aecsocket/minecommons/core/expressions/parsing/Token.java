package com.gitlab.aecsocket.minecommons.core.expressions.parsing;

public record Token(int type, String sequence) {
    public static final Token EPSILON = new Token(0, "");

    @Override public String toString() { return "<%d> `%s`".formatted(type, sequence); }
}
