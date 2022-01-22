package com.github.aecsocket.minecommons.core.expressions.parsing;

/**
 * An exception that occurs when lexing input.
 */
public class TokenzingException extends ParsingException {
    /**
     * Creates an instance.
     * */
    public TokenzingException() {}

    /**
     * Creates an instance.
     * @param message The message.
     */
    public TokenzingException(String message) { super(message); }

    /**
     * Creates an instance.
     * @param message The message.
     * @param cause The cause of the exception.
     */
    public TokenzingException(String message, Throwable cause) { super(message, cause); }

    /**
     * Creates an instance.
     * @param cause The cause of the exception.
     */
    public TokenzingException(Throwable cause) { super(cause); }
}
