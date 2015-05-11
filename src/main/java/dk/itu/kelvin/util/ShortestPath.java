/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

// Utilities
import dk.itu.kelvin.util.WeightedGraph.Edge;
import dk.itu.kelvin.util.WeightedGraph.Node;

/**
 * ShortestPath class.
 *
 * @param <N> The type of nodes to find paths for.
 * @param <E> The type of edges to find paths for.
 */
public final class ShortestPath<N extends Node, E extends Edge<N>> {
  /**
   * Maps the distance from the source to all different vertices in the graph.
   */
  private Map<N, Float> distance = new HashMap<>();

  /**
   * Maps vertices with the last edge on the vertex's shortest path.
   */
  private Map<N, N> edgeTo = new HashMap<>();

  /**
   * Priority queue holding all vertices.
   */
  private PriorityQueue<N> queue;

  /**
   * Constructor setting all distances to infinity.
   * @param graph weighted graph.
   * @param source starting point for the shortest path.
   */
  public ShortestPath(final WeightedGraph<N, E> graph, final N source) {
    List<E> edges = graph.edges();

    for (E edge: edges) {
      List<N> nodes = edge.nodes();

      for (int i = 0; i < nodes.size() - 1; i++) {
        N a = nodes.get(i);
        N b = nodes.get(i + 1);

        if (edge.weight(a, b) < 0) {
          throw new IllegalArgumentException(
            "Weights of edges cannot be negative"
          );
        }

        this.distance.put(a, Float.POSITIVE_INFINITY);
        this.distance.put(b, Float.POSITIVE_INFINITY);
      }
    }

    this.distance.put(source, 0.0f);

    // relax vertices in order of distance from s
    this.queue = new PriorityQueue<>(11, (a, b) -> {
      // Comparing b to a instead of a to b to make
      // a minimum priority and not maximum priority.
      return this.distance.get(b).compareTo(this.distance.get(a));
    });

    this.queue.add(source);

    while (!this.queue.isEmpty()) {
      N next = this.queue.poll();

      Map<N, E> neighbours = graph.neighbours(next);

      if (neighbours == null || neighbours.isEmpty()) {
        continue;
      }

      for (Map.Entry<N, E> neighbour: neighbours.entrySet()) {
        N to = neighbour.getKey();
        E edge = neighbour.getValue();

        this.relax(next, to, edge);
      }
    }
  }

  /**
   * Relax edge and update queue if changed.
   *
   * @param from  The first node.
   * @param to    The second node.
   * @param edge  The edge between the nodes.
   */
  private void relax(final N from, final N to, final E edge) {
    if (from == null || to == null || edge == null) {
      return;
    }

    float weight = (float) edge.weight(from, to);

    float distFrom = this.distance.get(from);
    float distTo = this.distance.get(to);

    if (distTo > distFrom + weight) {
      this.distance.put(to, distFrom + weight);
      this.edgeTo.put(to, from);

      if (this.queue.contains(to)) {
        this.queue.remove(to);
      }

      this.queue.add(to);
    }
  }

  /**
   * Returns the length of a shortest path from the source.
   * @param node the destination vertex.
   * @return the length of a shortest path.
   * Float.POSITIVE_INFINITY if no such path
   */
  public float distanceTo(final N node) {
    if (node == null) {
      return Float.POSITIVE_INFINITY;
    }

    return this.distance.get(node);
  }

  /**
   * Is there a path from the source vertex to the param vertex.
   * @param node the destination vertex.
   * @return true if there is a path, false otherwise.
   */
  public boolean hasPathTo(final N node) {
    if (!this.distance.containsKey(node)) {
      return false;
    }

    return this.distanceTo(node) < Float.POSITIVE_INFINITY;
  }

  /**
   * Returns a shortest path.
   * @param node the destination vertex.
   * @return shortest path from the source vertex to the param vertex
   * as an iterable of edges, and null if no such path.
   */
  public List<N> path(final N node) {
    if (!this.hasPathTo(node)) {
      return null;
    }

    List<N> path = new ArrayList<>();

    // Add the source node to the path.
    path.add(node);

    for (
      N n = this.edgeTo.get(node);
      n != null;
      n = this.edgeTo.get(n)
    ) {
      path.add(n);
    }

    // The path is located in reverse order and is therefore backwards. Reverse
    // it to correct this.
    Collections.reverse(path);

    return path;
  }
}
