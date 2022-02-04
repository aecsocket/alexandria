package com.github.aecsocket.minecommons.paper.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

/**
 * Command argument which parses a {@link Key}.
 * @param <C> The command sender type.
 */
public final class KeyArgument<C> extends CommandArgument<C, Key> {
    /** When a key cannot be parsed. */
    public static final Caption ARGUMENT_PARSE_FAILURE_KEY = Caption.of("argument.parse.failure.key");

    private KeyArgument(
        final boolean required,
        final String name,
        final String defaultValue,
        final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
        final ArgumentDescription defaultDescription
    ) {
        super(required, name, new KeyParser<>(), defaultValue, Key.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new builder
     *
     * @param name   Name of the component
     * @param <C>    Command sender type
     * @return Created builder
     */
    public static <C> Builder<C> newBuilder(final String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required command component
     *
     * @param name   Component name
     * @param <C>    Command sender type
     * @return Created component
     */
    public static <C> CommandArgument<C, Key> of(final String name) {
        return KeyArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create a new optional command component
     *
     * @param name   Component name
     * @param <C>    Command sender type
     * @return Created component
     */
    public static <C> CommandArgument<C, Key> optional(final String name) {
        return KeyArgument.<C>newBuilder(name).asOptional().build();
    }

    /**
     * Create a new required command component with a default value
     *
     * @param name         Component name
     * @param defaultValue Default value
     * @param <C>          Command sender type
     * @return Created component
     */
    public static <C> CommandArgument<C, Key> optional(
        final String name,
        final Key defaultValue
    ) {
        return KeyArgument.<C>newBuilder(name).asOptionalWithDefault(defaultValue.toString()).build();
    }

    /**
     * Builder class.
     * @param <C> The command sender type.
     */
    public static final class Builder<C> extends CommandArgument.Builder<C, Key> {
        private Builder(final String name) {
            super(Key.class, name);
        }

        /**
         * Builder a new example component
         *
         * @return Constructed component
         */
        @Override
        public KeyArgument<C> build() {
            return new KeyArgument<>(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }

    }

    /**
     * Parser class.
     * @param <C> The command sender type.
     */
    public static final class KeyParser<C> implements ArgumentParser<C, Key> {
        @Override
        public ArgumentParseResult<Key> parse(
            final CommandContext<C> ctx,
            final Queue<String> inputQueue
        ) {
            //noinspection PatternValidation
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        Key.class,
                        ctx
                ));
            }
            inputQueue.remove();

            try {
                //noinspection PatternValidation
                return ArgumentParseResult.success(Key.key(input));
            } catch (InvalidKeyException e) {
                return ArgumentParseResult.failure(new ParseException(input, ctx, e));
            }
        }

        @Override
        public boolean isContextFree() {
            return true;
        }

        @Override
        public List<String> suggestions(CommandContext<C> ctx, String input) {
            return Collections.emptyList();
        }
    }

    /**
     * Exception type.
     */
    public static final class ParseException extends ParserException {
        /**
         * Creates an instance.
         * @param input The input.
         * @param ctx The context.
         * @param e The exception.
         */
        public ParseException(String input, CommandContext<?> ctx, Exception e) {
            super(Key.class, ctx, ARGUMENT_PARSE_FAILURE_KEY,
                CaptionVariable.of("input", input),
                CaptionVariable.of("error", e.getMessage()));
        }
    }
}
