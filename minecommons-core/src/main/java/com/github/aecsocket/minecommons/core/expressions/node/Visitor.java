package com.github.aecsocket.minecommons.core.expressions.node;

/**
 * A visitor, applied to nodes of a specific type.
 * @param <N> The node type.
 */
public interface Visitor<N extends Node<?>> {
    /**
     * Performs functions on a node.
     * @param node The node.
     */
    void visit(N node);
}
