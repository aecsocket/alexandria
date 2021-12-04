package com.gitlab.aecsocket.minecommons.core.node;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A mutable version of the partial abstract node implementation.
 * @param <N> The type of all nodes in the structure.
 */
public abstract class MutableAbstractMapNode<N extends MutableAbstractMapNode<N>>
        extends AbstractMapNode<N> implements MapNode.Mutable<N> {
    /**
     * Creates a deep copy instance from another node.
     * @param o The other node.
     */
    public MutableAbstractMapNode(AbstractMapNode<N> o) {
        super(o);
    }

    /**
     * Creates an instance.
     * @param key The parent info.
     */
    public MutableAbstractMapNode(@Nullable Key<N> key) {
        super(key);
    }

    /**
     * Creates an instance.
     * @param parent The parent of this node.
     * @param key The key under which this node is stored in the parent.
     */
    public MutableAbstractMapNode(N parent, String key) {
        super(parent, key);
    }

    /**
     * Creates an instance with no parent.
     */
    public MutableAbstractMapNode() {}

    @Override
    public void attach(N parent, String key) {
        this.key = new Key<>(parent, key);
    }

    @Override
    public void detach() {
        key = null;
    }

    @Override
    public N removeNode(String key) {
        return children.remove(key);
    }

    @Override
    public N node(String key, N val) {
        N old = children.get(key);
        if (old != null)
            old.detach();
        children.put(key, val);
        val.attach(self(), key);
        return val;
    }
}
