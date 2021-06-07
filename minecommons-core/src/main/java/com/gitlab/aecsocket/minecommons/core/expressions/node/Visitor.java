package com.gitlab.aecsocket.minecommons.core.expressions.node;

/**
 * A visitor, applied to nodes of a specific type.
 * @param <N> The node type.
 */
public interface Visitor<N extends Node<?>> {
    void visit(N node);
}
