package com.gitlab.aecsocket.minecommons.core.expressions.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An expression node in a tree.
 * @param <V> The visitor input.
 */
public interface Node<V extends Visitor<?>> {
    /**
     * Gets the input of this node as an arbitrary number.
     * @return The input.
     */
    int type();

    /**
     * Accepts a visitor, passing the visitor along to child nodes.
     * @param visitor The visitor.
     * @return This instance.
     */
    Node<V> accept(V visitor);

    /**
     * An expression node with a list of child terms.
     * @param <N> The node input.
     * @param <V> The visitor input of this node, and its children.
     */
    abstract class Sequence<T extends Sequence.Term<N, V>, N extends Node<V>, V extends Visitor<N>> implements Node<V> {
        /**
         * A term in a sequence.
         * @param <N> The node type.
         * @param <V> The node visitor type.
         */
        public interface Term<N extends Node<V>, V extends Visitor<N>> {
            /**
             * Gets the node.
             * @return The node.
             */
            N node();
        }

        /** The terms. */
        protected final List<T> terms = new ArrayList<>();

        /**
         * Adds a term.
         * @param term The term.
         * @return This instance.
         */
        public Sequence<T, N, V> add(T term) {
            terms.add(term);
            return this;
        }

        /**
         * Gets this as a {@link N}.
         * @return This instance.
         */
        protected abstract N self();

        @Override
        public N accept(V visitor) {
            visitor.visit(self());
            for (T term : terms) {
                term.node().accept(visitor);
            }
            return self();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Sequence<?, ?, ?> sequence = (Sequence<?, ?, ?>) o;
            return terms.equals(sequence.terms);
        }

        @Override
        public int hashCode() {
            return Objects.hash(terms);
        }
    }
}
