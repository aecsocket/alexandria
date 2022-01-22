package com.github.aecsocket.minecommons.core.expressions.parsing;

/**
 * An exception that occurs when parsing - tokenizing or creating a node.
 */
public class ParsingException extends Exception {
    /**
     * Creates an instance.
     * */
    public ParsingException() {}

    /**
     * Creates an instance.
     * @param message The message.
     */
    public ParsingException(String message) { super(message); }

    /**
     * Creates an instance.
     * @param message The message.
     * @param cause The cause of the exception.
     */
    public ParsingException(String message, Throwable cause) { super(message, cause); }

    /**
     * Creates an instance.
     * @param cause The cause of the exception.
     */
    public ParsingException(Throwable cause) { super(cause); }
}
