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
import java.util.Properties;

// Utilities
import dk.itu.kelvin.util.WeightedGraph.Edge;
import dk.itu.kelvin.util.WeightedGraph.Node;

// Math
import dk.itu.kelvin.math.Epsilon;

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
  private final Map<N, Float> distance = new HashMap<>();

  /**
   * Maps vertices with the last edge on the vertex's shortest path.
   */
  private final Map<N, N> edgeTo = new HashMap<>();

  /**
   * Priority queue holding all vertices.
   */
  private final PriorityQueue<N> queue;

  /**
   * Configuration map of custom properties.
   */
  private final Properties properties;

  private final N from;

  private final N to;

  /**
   * Initialize a new shortest path instance given a weighted graph and a
   * source node.
   *
   * @param graph The weighted graph to use for constructing the path tree.
   * @param from  Starting point for the shortest path.
   * @param to    Ending point for the shortest path.
   */
  public ShortestPath(
    final WeightedGraph<N, E> graph,
    final N from,
    final N to
  ) {
    this(graph, from, to, new Properties());
  }

  /**
   * Initialize a new shortest path instance given a weighted graph and a
   * source node along with any custom properties.
   *
   * @param graph       The weighted graph to use for constructing the path tree.
   * @param from        Starting point for the shortest path.
   * @param to          Ending point for the shortest path.
   * @param properties  A configuration map of custom properties.
   */
  public ShortestPath(
    final WeightedGraph<N, E> graph,
    final N from,
    final N to,
    final Properties properties
  ) {
    this.from = from;
    this.to = to;
    this.properties = properties;

    for (E edge: graph.edges()) {
      for (N node: edge.nodes()) {
        this.distance.put(node, Float.POSITIVE_INFINITY);
      }
    }

    this.distance.put(from, 0.0f);

    this.queue = new PriorityQueue<>(this.distance.size(), (a, b) -> {
      return this.distance.get(a).compareTo(this.distance.get(b));
    });

    this.queue.add(from);

    while (!this.queue.isEmpty()) {
      N next = this.queue.poll();

      // Bail out as soon as we've dequeued the node we're looking for.
      if (next.equals(to)) {
        break;
      }

      Map<N, E> neighbours = graph.neighbours(next);

      if (neighbours == null || neighbours.isEmpty()) {
        continue;
      }

      for (Map.Entry<N, E> neighbour: neighbours.entrySet()) {
        N node = neighbour.getKey();
        E edge = neighbour.getValue();

        this.relax(next, node, edge);
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

    double weight = edge.weight(from, to, this.properties);

    double estimateFrom = edge.weight(from, this.to, this.properties);
    double estimateTo = edge.weight(to, this.to, this.properties);

    weight += estimateFrom - estimateTo;

    double distFrom = this.distance.get(from);
    double distTo = this.distance.get(to);

    if (Epsilon.greater(distTo, distFrom + weight)) {
      this.distance.put(to, (float) (distFrom + weight));
      this.edgeTo.put(to, from);

      this.queue.remove(to);
      this.queue.add(to);
    }
  }

  public float distance() {
    return this.distance.get(this.to);
  }

  public boolean hasPath() {
    return this.distance() < Float.POSITIVE_INFINITY;
  }

  public List<N> path() {
    if (!this.hasPath()) {
      return null;
    }

    List<N> path = new ArrayList<>();

    // Add the source node to the path.
    path.add(this.to);

    for (
      N n = this.edgeTo.get(this.to);
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
