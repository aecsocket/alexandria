package com.github.aecsocket.minecommons.core.node;

import com.github.aecsocket.minecommons.core.Colls;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * An immutable representation of the individual components required to reach a specific node in a tree structure.
 */
public interface NodePath extends Iterable<String> {
    /**
     * Gets a node path with zero components.
     * @return The path.
     */
    static NodePath empty() {
        return EmptyNodePath.INSTANCE;
    }

    /**
     * Gets a node path from a list of components.
     * @param path The components.
     * @return The path.
     */
    static NodePath path(List<String> path) {
        return new ListNodePath(path);
    }

    /**
     * Gets a node path from an array of components.
     * @param path The components.
     * @return The path.
     */
    static NodePath path(String... path) {
        return new ArrayNodePath(path);
    }

    /**
     * Gets how many components this path has.
     * @return The number of components.
     */
    int size();

    /**
     * Gets a component of this path.
     * @param idx The index to get from.
     * @return The component.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    String get(int idx) throws IndexOutOfBoundsException;

    /**
     * Gets the last component in this path. May return null.
     * @return The last component.
     */
    @Nullable String last();

    /**
     * Gets this path as a list.
     * @return The list form.
     */
    List<String> list();

    /**
     * Gets this path as an array.
     * @return The array form.
     */
    String[] array();

    /**
     * Creates a new path with components appended to it.
     * @param nodes The appended components.
     * @return The new path.
     */
    default NodePath append(String... nodes) {
        return path(Colls.joinList(list(), Arrays.asList(nodes)));
    }
}
