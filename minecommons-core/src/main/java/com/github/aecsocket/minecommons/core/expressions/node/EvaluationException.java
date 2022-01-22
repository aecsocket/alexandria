package com.github.aecsocket.minecommons.core.expressions.node;

/**
 * An exception that occurs when evaluating the value of a node.
 */
public class EvaluationException extends Exception {
    /**
     * Creates an instance.
     * */
    public EvaluationException() {}

    /**
     * Creates an instance.
     * @param message The message.
     */
    public EvaluationException(String message) { super(message); }

    /**
     * Creates an instance.
     * @param message The message.
     * @param cause The cause of the exception.
     */
    public EvaluationException(String message, Throwable cause) { super(message, cause); }

    /**
     * Creates an instance.
     * @param cause The cause of the exception.
     */
    public EvaluationException(Throwable cause) { super(cause); }
}
