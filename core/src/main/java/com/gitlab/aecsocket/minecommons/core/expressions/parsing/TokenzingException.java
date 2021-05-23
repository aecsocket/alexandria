package com.gitlab.aecsocket.minecommons.core.expressions.parsing;

/**
 * An exception that occurs when lexing input.
 */
public class TokenzingException extends ParsingException {
    public TokenzingException() {}
    public TokenzingException(String message) { super(message); }
    public TokenzingException(String message, Throwable cause) { super(message, cause); }
    public TokenzingException(Throwable cause) { super(cause); }
}
