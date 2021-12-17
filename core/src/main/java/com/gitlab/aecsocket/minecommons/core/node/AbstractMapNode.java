package com.gitlab.aecsocket.minecommons.core.node;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Partial implementation of a map node.
 * @param <N> The type of all nodes in the structure.
 */
public abstract class AbstractMapNode<N extends AbstractMapNode<N>> implements MapNode.Scoped<N> {
    /**
     * A record storing parent info.
     * @param <N> The type of all nodes in the structure.
     */
    protected record Key<N extends MapNode.Scoped<N>>(N parent, String key) {}

    /** The parent info. */
    protected @Nullable Key<N> key;
    /** The child nodes. */
    protected final Map<String, N> children = new HashMap<>();

    /**
     * Creates a deep copy instance from another node.
     * @param o The other node.
     */
    public AbstractMapNode(AbstractMapNode<N> o) {
        key = o.key;
        for (var entry : o.children.entrySet()) {
            children.put(entry.getKey(), entry.getValue().copy());
        }
    }

    /**
     * Creates an instance.
     * @param key The parent info.
     */
    protected AbstractMapNode(@Nullable Key<N> key) {
        this.key = key;
    }

    /**
     * Creates an instance.
     * @param parent The parent of this node.
     * @param key The key under which this node is stored in the parent.
     */
    public AbstractMapNode(N parent, String key) {
        this.key = new Key<>(parent, key);
    }

    /**
     * Creates an instance with no parent.
     */
    public AbstractMapNode() {}

    @Override
    public @Nullable N parent() {
        return key == null ? null : key.parent;
    }

    @Override
    public @Nullable String key() {
        return key == null ? null : key.key;
    }

    @Override
    public NodePath path() {
        Stack<String> path = new Stack<>();
        for (var cur = this; cur != null && cur.key != null; cur = cur.parent())
            path.push(cur.key.key);
        return NodePath.path(path);
    }

    @Override
    public N root() {
        return key == null ? self() : key.parent.root();
    }

    @Override
    public boolean isRoot() {
        return key == null;
    }

    @Override public Map<String, N> children() {
        return new HashMap<>(children);
    }

    @Override
    public Set<String> childKeys() {
        return children.keySet();
    }

    @Override
    public Collection<N> childValues() {
        return children.values();
    }

    @Override
    public boolean has(String path) {
        return children.containsKey(path);
    }

    @Override
    public Optional<N> get(String... path) {
        N current = self();
        for (var part : path) {
            current = current.children.get(part);
            if (current == null)
                return Optional.empty();
        }
        return Optional.of(current);
    }

    @Override
    public Optional<N> get(NodePath path) {
        N current = self();
        for (var part : path) {
            current = current.children.get(part);
            if (current == null)
                return Optional.empty();
        }
        return Optional.of(current);
    }

    @Override
    public void visit(Consumer<N> visitor) {
        visitor.accept(self());
        for (var child : children.values()) {
            child.visit(visitor);
        }
    }

    @Override
    public void visitBaseNodes(Consumer<MapNode> visitor) {
        visit(visitor::accept);
    }

    @Override
    public N asRoot() {
        N result = copy();
        result.key = null;
        return result;
    }

    @Override
    public Iterator<Map.Entry<String, N>> iterator() {
        return children.entrySet().iterator();
    }
}
