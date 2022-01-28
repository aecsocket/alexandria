package com.github.aecsocket.minecommons.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.graph.Graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilities for using Google Guava graphs.
 */
@SuppressWarnings("UnstableApiUsage")
public final class Graphs {
    private Graphs() {}

    /**
     * Creates an immutable set of nodes of a graph, which are topologically sorted.
     * @param graph The graph.
     * @param <N> The node type.
     * @return The set.
     */
    public static <N> Set<N> topologicallySorted(Graph<N> graph) {
        return new TopologicalSortSet<>(graph);
    }

    private static class TopologicalSortSet<N> extends AbstractSet<N> {
        private final Graph<N> graph;

        private TopologicalSortSet(Graph<N> graph) {
            this.graph = Preconditions.checkNotNull(graph, "graph");
        }

        @Override public UnmodifiableIterator<N> iterator() {
            return new TopologicalOrderIterator<>(graph);
        }
        @Override public int size() { return graph.nodes().size(); }
        @Override public boolean remove(Object o) { throw new UnsupportedOperationException(); }
    }

    private static class TopologicalOrderIterator<N> extends AbstractIterator<N> {
        private final Graph<N> graph;
        private final Queue<N> roots;
        private final Map<N, Integer> nonRootsToInDegree;

        private TopologicalOrderIterator(Graph<N> graph) {
            this.graph = Preconditions.checkNotNull(graph, "graph");
            this.roots = graph
                            .nodes()
                            .stream()
                            .filter(node -> graph.inDegree(node) == 0)
                            .collect(Collectors.toCollection(ArrayDeque::new));
            this.nonRootsToInDegree = graph
                            .nodes()
                            .stream()
                            .filter(node -> graph.inDegree(node) > 0)
                            .collect(Collectors.toMap(node -> node, graph::inDegree, (a, b) -> a, HashMap::new));
        }

        @Override
        protected N computeNext() {
            // Kahn's algorithm
            if (!roots.isEmpty()) {
                N next = roots.remove();
                for (N successor : graph.successors(next)) {
                    int newInDegree = nonRootsToInDegree.get(successor) - 1;
                    nonRootsToInDegree.put(successor, newInDegree);
                    if (newInDegree == 0) {
                        nonRootsToInDegree.remove(successor);
                        roots.add(successor);
                    }
                }
                return next;
            }
            Preconditions.checkState(nonRootsToInDegree.isEmpty(), "graph has at least one cycle");
            return endOfData();
        }
    }
}
