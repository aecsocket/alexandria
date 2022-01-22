package com.github.aecsocket.minecommons.core.node;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * A generic node in a tree, with a backing {@code Map<String, MapNode>} providing child values.
 * <p>
 * Provides capability for navigation around the tree, but is immutable by default.
 */
public interface MapNode {
    /**
     * Gets the parent node of this node. May be null.
     * @return The parent node.
     */
    @Nullable MapNode parent();

    /**
     * Gets the key that this node is stored under in the parent node. May be null.
     * @return The key of this node.
     */
    @Nullable String key();

    /**
     * Gets the full path from the root to this node.
     * @return The path.
     */
    NodePath path();

    /**
     * Gets the root node of this structure - the node which no longer has any parents.
     * @return The root.
     */
    MapNode root();

    /**
     * Gets if this node is the root node of this structure.
     * @return The flag.
     */
    boolean isRoot();

    /**
     * Gets all child nodes of this node, mapped to their string keys.
     * @return The child nodes map.
     */
    Map<String, ? extends MapNode> children();

    /**
     * Gets all keys of child nodes.
     * @return The keys of the child nodes.
     */
    Set<String> childKeys();

    /**
     * Gets all child nodes under this node.
     * @return The child nodes.
     */
    Collection<? extends MapNode> childValues();

    /**
     * Gets if this node has a node at the specified path as a child.
     * @param path The path.
     * @return The result.
     */
    boolean has(String path);

    /**
     * Recursively gets a node under this node.
     * @param path The path to the node.
     * @return The node at the path.
     */
    Optional<? extends MapNode> get(NodePath path);

    /**
     * Recursively gets a node under this node.
     * @param path The path to the node.
     * @return The node at the path.
     */
    Optional<? extends MapNode> get(String... path);

    /**
     * Visits and applies a function to each node under this node, including itself.
     * @param visitor The visitor.
     */
    void visitBaseNodes(Consumer<MapNode> visitor);

    /**
     * Creates a deep copy of this node.
     * @return The copy.
     */
    MapNode copy();

    /**
     * Creates a deep copy of this node, as its own root and detached from its parent.
     * @return The detached copy.
     */
    MapNode asRoot();

    /**
     * A scoped version of the node.
     * @param <N> The type of all nodes in the structure.
     */
    interface Scoped<N extends Scoped<N>> extends MapNode, Iterable<Map.Entry<String, N>> {
        /**
         * Gets this instance expressed as an {@link N}.
         * @return This instance.
         */
        N self();

        @Override N parent();

        @Override N root();

        @Override Map<String, N> children();
        @Override Collection<N> childValues();

        @Override Optional<N> get(String... path);
        @Override Optional<N> get(NodePath path);

        /**
         * Visits and applies a function to each node under this node, including itself.
         * @param visitor The visitor.
         */
        void visit(Consumer<N> visitor);

        @Override N copy();
        @Override N asRoot();
    }

    /**
     * A mutable version of a node, allowing modification of child nodes and attach/detaching from trees.
     * @param <N> The type of all nodes in the structure.
     */
    interface Mutable<N extends Mutable<N>> extends Scoped<N> {
        /**
         * Attaches this node to a parent.
         * @param parent The parent node.
         * @param key The key under which this node is located in the parent.
         */
        void attach(N parent, String key);

        /**
         * Detaches this node from a parent.
         */
        void detach();

        /**
         * Removes a child node, and {@link #detach() detaches} it.
         * @param key The key of the node to remove.
         * @return The removed node.
         */
        N removeChild(String key);

        /**
         * Sets a node, {@link #attach(Mutable, String) attaching} it to this node and
         * {@link #detach() detaching} the previous node.
         * @param key The key under which the value will be located.
         * @param val The node to set to.
         * @return The value.
         */
        N set(String key, N val);
    }
}
