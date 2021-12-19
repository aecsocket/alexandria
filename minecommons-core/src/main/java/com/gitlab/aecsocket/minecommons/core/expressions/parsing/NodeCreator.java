package com.gitlab.aecsocket.minecommons.core.expressions.parsing;

import com.gitlab.aecsocket.minecommons.core.expressions.node.Node;

import java.util.Deque;

/**
 * Parses a queue of {@link Token}s into a {@link N}.
 * @param <N> The created node type.
 */
public interface NodeCreator<N extends Node<?>> {
    /**
     * Creates a {@link N} from a queue of {@link Token}s.
     * @param tokens The tokens.
     * @return The node.
     * @throws NodeException If the node could not be created.
     */
    N node(Deque<Token> tokens) throws NodeException;
}
