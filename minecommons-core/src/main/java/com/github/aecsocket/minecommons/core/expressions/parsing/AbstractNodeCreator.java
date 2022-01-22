package com.github.aecsocket.minecommons.core.expressions.parsing;

import java.util.Deque;

import com.github.aecsocket.minecommons.core.expressions.node.Node;

/**
 * An abstract implementation of a node creator, handling empty tokens and unexpected tokens.
 * @param <N> The created node type.
 */
public abstract class AbstractNodeCreator<N extends Node<?>> implements NodeCreator<N> {
    /** The tokens ahead of the lookahead. */
    protected Deque<Token> tokens;
    /** The token in front. */
    protected Token lookahead;

    @Override
    public N node(Deque<Token> tokens) throws NodeException {
        this.tokens = tokens;
        if (tokens.isEmpty())
            throw new NodeException("No tokens provided");
        lookahead = tokens.getFirst();

        N node = expression();

        if (!Token.EPSILON.equals(lookahead))
            throw new NodeException("Unexpected token %s".formatted(lookahead));

        return node;
    }

    /**
     * Gets the next token.
     */
    protected void next() {
        tokens.pop();
        lookahead = tokens.isEmpty() ? Token.EPSILON : tokens.getFirst();
    }

    /**
     * Evaluates an expression using the lookahead.
     * @return The created node.
     * @throws NodeException If the node could not be created.
     */
    protected abstract N expression() throws NodeException;
}
