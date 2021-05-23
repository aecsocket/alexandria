package com.gitlab.aecsocket.minecommons.core.expressions.node;

/**
 * An exception that occurs when evaluating the value of a node.
 */
public class EvaluationException extends Exception {
    public EvaluationException() {}
    public EvaluationException(String message) { super(message); }
    public EvaluationException(String message, Throwable cause) { super(message, cause); }
    public EvaluationException(Throwable cause) { super(cause); }
}
