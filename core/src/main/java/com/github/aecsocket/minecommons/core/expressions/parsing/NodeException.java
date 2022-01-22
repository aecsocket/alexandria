package com.github.aecsocket.minecommons.core.expressions.parsing;

/**
 * An exception that occurs when creating a node.
 */
public class NodeException extends ParsingException {
    /**
     * Creates an instance.
     * */
    public NodeException() {}

    /**
     * Creates an instance.
     * @param message The message.
     */
    public NodeException(String message) { super(message); }

    /**
     * Creates an instance.
     * @param message The message.
     * @param cause The cause of the exception.
     */
    public NodeException(String message, Throwable cause) { super(message, cause); }

    /**
     * Creates an instance.
     * @param cause The cause of the exception.
     */
    public NodeException(Throwable cause) { super(cause); }

    /**
     * An exception that occurs if an illegal token is found.
     */
    public static class IllegalToken extends NodeException {
        /**
         * Creates an instance.
         * @param found The token found.
         * @param expected The token expected.
         */
        public IllegalToken(Token found, String expected) {
            super("Illegal token: Found `%s`, expected %s".formatted(found, expected));
        }
    }
}
