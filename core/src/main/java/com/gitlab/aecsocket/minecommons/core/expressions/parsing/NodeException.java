package com.gitlab.aecsocket.minecommons.core.expressions.parsing;

/**
 * An exception that occurs when creating a node.
 */
public class NodeException extends ParsingException {
    public NodeException() {}
    public NodeException(String message) { super(message); }
    public NodeException(String message, Throwable cause) { super(message, cause); }
    public NodeException(Throwable cause) { super(cause); }

    /**
     * An exception that occurs if an illegal token is found.
     */
    public static class IllegalToken extends NodeException {
        public IllegalToken(Token found, String expected) {
            super("Illegal token: Found `%s`, expected %s".formatted(found, expected));
        }
    }
}
