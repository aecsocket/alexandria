package com.github.aecsocket.minecommons.core.expressions.parsing;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses text input into {@link Token}s, created by {@link Definition}s.
 */
public final class Tokenizer {
    /**
     * A token definition, which creates a {@link Token} from a string sequence.
     */
    public static final class Definition {
        private final Pattern pattern;
        private final int tokenType;

        /**
         * Creates a definition.
         * @param pattern The pattern.
         * @param tokenType The type of token.
         */
        public Definition(Pattern pattern, int tokenType) {
            this.pattern = pattern;
            this.tokenType = tokenType;
        }

        /**
         * Creates a definition.
         * @param pattern The pattern.
         * @param tokenType The type of token.
         * @return The definition.
         */
        public static Definition of(String pattern, int tokenType) {
            return new Definition(Pattern.compile("^(" + pattern + ")"), tokenType);
        }

        /**
         * The pattern used to identify this definition.
         * @return The pattern.
         */
        public Pattern pattern() { return pattern; }

        /**
         * The input of token that this definition creates.
         * @return The input.
         */
        public int input() { return tokenType; }

        /**
         * Creates a token from a string sequence.
         * @param sequence The sequence.
         * @return The token.
         */
        public Token create(String sequence) { return new Token(tokenType, sequence); }

        @Override public String toString() { return "<%d> /%s/".formatted(tokenType, pattern.pattern()); }
    }

    /**
     * Creates an instance of a {@link Tokenizer}.
     */
    public static final class Builder {
        private final List<Definition> definitions = new ArrayList<>();

        private Builder() {}

        /**
         * Adds a collection of definitions to this.
         * @param definitions The definitions.
         * @return This instance.
         */
        public Builder add(Collection<Definition> definitions) {
            this.definitions.addAll(definitions);
            return this;
        }

        /**
         * Adds a definition to this.
         * @param definition The definition.
         * @return This instance.
         */
        public Builder add(Definition definition) {
            definitions.add(definition);
            return this;
        }

        /**
         * Creates and adds a definition to this.
         * @param pattern The pattern.
         * @param input The token input.
         * @return This instance.
         */
        public Builder add(String pattern, int input) {
            definitions.add(Definition.of(pattern, input));
            return this;
        }

        /**
         * Builds an immutable tokenizer from the definitions.
         * @return The tokenizer.
         */
        public Tokenizer build() {
            return new Tokenizer(Collections.unmodifiableList(definitions));
        }
    }

    /**
     * Creates a new tokenizer builder.
     * @return The builder.
     */
    public static Builder builder() { return new Builder(); }

    private final List<Definition> definitions;

    private Tokenizer(List<Definition> definitions) {
        this.definitions = definitions;
    }

    /**
     * Gets the list of definitions that this tokenizer uses.
     * @return The definitions.
     */
    public List<Definition> definitions() { return definitions; }

    /**
     * Creates a queue of tokens from the input.
     * @param text The text to tokenize.
     * @return The queue of tokens.
     * @throws TokenzingException If the input could not be parsed.
     */
    public Deque<Token> tokenize(String text) throws TokenzingException {
        Deque<Token> tokens = new LinkedList<>();
        while (text.length() > 0) {
            boolean found = false;
            for (Definition definition : definitions()) {
                Matcher match = definition.pattern().matcher(text);
                if (match.find()) {
                    found = true;

                    String sequence = match.group().strip();
                    tokens.add(definition.create(sequence));

                    text = match.replaceFirst("").strip();
                    break;
                }
            }
            if (!found)
                throw new TokenzingException("Unexpected sequence `%s`".formatted(text));
        }
        return tokens;
    }
}
