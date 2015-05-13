/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;

// Fast utils
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

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
  private final Map<N, Float> distance = new Object2FloatOpenHashMap<>();

  /**
   * Maps vertices with the last edge on the vertex's shortest path.
   */
  private final Map<N, N> edgeTo = new Object2ObjectOpenHashMap<>();

  /**
   * Priority queue holding all vertices.
   */
  private final PriorityQueue<N> queue;

  /**
   * Configuration map of custom properties.
   */
  private final Properties properties;

  /**
   * The starting node.
   */
  private final N from;

  /**
   * The ending node.
   */
  private final N to;

  /**
   * Initialize a new shortest path instance given a graph and a source node.
   *
   * @param graph The graph to use for constructing the path tree.
   * @param from  Starting point for the shortest path.
   * @param to    Ending point for the shortest path.
   */
  public ShortestPath(final Graph<N, E> graph, final N from, final N to) {
    this(graph, from, to, new Properties());
  }

  /**
   * Initialize a new shortest path instance given a graph and a source node
   * along with any custom properties.
   *
   * @param graph       The graph to use for constructing the path tree.
   * @param from        Starting point for the shortest path.
   * @param to          Ending point for the shortest path.
   * @param properties  A configuration map of custom properties.
   */
  public ShortestPath(
    final Graph<N, E> graph,
    final N from,
    final N to,
    final Properties properties
  ) {
    this.from = from;
    this.to = to;
    this.properties = properties;

    this.queue = new PriorityQueue<>(11, (a, b) -> {
      return Float.compare(this.distance(a), this.distance(b));
    });

    this.distance.put(this.from, 0.0f);
    this.queue.add(this.from);

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

    // Clear remaining elements in the queue and let GC do its work.
    this.queue.clear();
  }

  /**
   * Get the distance of the shortest path.
   *
   * @return The distance of the shortest path.
   */
  public float distance() {
    return this.distance(this.to);
  }

  /**
   * Check if a path exists.
   *
   * @return A boolean indicating whether or not a path exists.
   */
  public boolean hasPath() {
    return this.distance() < Float.POSITIVE_INFINITY;
  }

  /**
   * Get the nodes in the shortest path.
   *
   * @return A list of nodes in the shortest path.
   */
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

  /**
   * Relax edge and update queue if changed.
   *
   * @param from  The first node.
   * @param to    The second node.
   * @param edge  The edge connecting the nodes.
   */
  private void relax(final N from, final N to, final E edge) {
    if (from == null || to == null || edge == null) {
      return;
    }

    double weight = this.weight(from, to, edge);
    double distFrom = this.distance(from);
    double distTo = this.distance(to);

    if (Epsilon.lessOrEqual(distTo, distFrom + weight)) {
      return;
    }

    this.distance.put(to, (float) (distFrom + weight));
    this.edgeTo.put(to, from);
    this.queue.add(to);
  }

  /**
   * Compute the weight between the specified nodes.
   *
   * @param from  The first node.
   * @param to    The second node.
   * @param edge  The edge connecting the nodes.
   * @return      The weight between the specified nodes.
   */
  private double weight(final N from, final N to, final E edge) {
    if (from == null || to == null || edge == null) {
      return Double.POSITIVE_INFINITY;
    }

    // Compute the actual weight between the nodes.
    double weight = edge.weight(from, to, this.properties);

    // Approximate the weight from both nodes to the target node.
    double estimateFrom = edge.weight(from, this.to, this.properties);
    double estimateTo = edge.weight(to, this.to, this.properties);

    // Decrease the actual weight by the difference between the approximated
    // distances. This ensures that nodes closer to the target node will be moved
    // further up the queue.
    weight -= estimateFrom - estimateTo;

    return weight;
  }

  /**
   * Get the distance from the starting node to the specified node.
   *
   * @param node  The node whose distance from the starting node to get.
   * @return      The distance from the starting node to the specified node.
   */
  private float distance(final N node) {
    Float distance = this.distance.get(node);

    if (node == null || distance == null) {
      return Float.POSITIVE_INFINITY;
    }

    return distance;
  }
}
