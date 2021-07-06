package com.gitlab.aecsocket.minecommons.core.expressions.parsing;

/**
 * An exception that occurs when parsing - tokenizing or creating a node.
 */
public class ParsingException extends Exception {
    public ParsingException() {}
    public ParsingException(String message) { super(message); }
    public ParsingException(String message, Throwable cause) { super(message, cause); }
    public ParsingException(Throwable cause) { super(cause); }
}
