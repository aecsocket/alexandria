package com.github.aecsocket.minecommons.core.node;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A mutable version of the partial abstract node implementation.
 * @param <N> The type of all nodes in the structure.
 */
public abstract class MutableAbstractMapNode<N extends MutableAbstractMapNode<N>>
        extends AbstractMapNode<N> implements MapNode.Mutable<N> {
    /**
     * Creates a deep copy instance from another node implementing the same class.
     * @param o The other node.
     */
    protected MutableAbstractMapNode(AbstractMapNode<N> o) {
        super(o);
    }

    /**
     * Creates a deep copy instance from another generic node.
     * @param o The other node.
     */
    protected MutableAbstractMapNode(MapNode.Scoped<N> o) {
        super(o);
    }

    /**
     * Creates an instance.
     * @param key The parent info.
     */
    protected MutableAbstractMapNode(@Nullable Key<N> key) {
        super(key);
    }

    /**
     * Creates an instance.
     * @param parent The parent of this node.
     * @param key The key under which this node is stored in the parent.
     */
    protected MutableAbstractMapNode(N parent, String key) {
        super(parent, key);
    }

    /**
     * Creates an instance with no parent.
     */
    protected MutableAbstractMapNode() {}

    @Override
    public void attach(N parent, String key) {
        this.key = new Key<>(parent, key);
    }

    @Override
    public void detach() {
        key = null;
    }

    @Override
    public N removeChild(String key) {
        return children.remove(key);
    }

    @Override
    public N clearChildren() {
        children.clear();
        return self();
    }

    @Override
    public N set(String key, N val) {
        N old = children.get(key);
        if (old != null)
            old.detach();
        children.put(key, val);
        val.attach(self(), key);
        return val;
    }

    /**
     * Sets a node without doing any attaching or detaching.
     * @param key The key under which the value will be located.
     * @param val The node to set to. If null, the node at the key will be removed.
     */
    public void setUnsafe(String key, @Nullable N val) {
        if (val == null)
            children.remove(key);
        else
            children.put(key, val);
    }
}
