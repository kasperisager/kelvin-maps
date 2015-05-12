/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// Fast utils
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

/**
 * WeightedGraph class.
 *
 * @param <N> The type of nodes to use within the weighted graph.
 * @param <E> The type of edges to use within the weighted graph.
 */
public final class WeightedGraph<
  N extends WeightedGraph.Node, E extends WeightedGraph.Edge<N>
> implements Graph<N, E> {
  /**
   * The adjacency list for all vertices and edges in the graph.
   */
  private final Map<N, Map<N, E>> neighbours = new Object2ObjectOpenHashMap<>();

  /**
   * Configuration map of custom properties.
   */
  private final Properties properties;

  /**
   * Initialize a new weighted graph.
   */
  public WeightedGraph() {
    this.properties = new Properties();
  }

  /**
   * Initialize a new weighted graph with the specified properties.
   *
   * @param properties A configuration map of custom properties.
   */
  public WeightedGraph(final Properties properties) {
    this.properties = properties;
  }

  /**
   * Add an edge to the weighted graph.
   *
   * @param edge The edge to add to the weighted graph.
   */
  public void add(final E edge) {
    List<N> nodes = edge.nodes();
    Direction direction = edge.direction(this.properties);

    for (int i = 0; i < nodes.size() - 1; i++) {
      N a = nodes.get(i);
      N b = nodes.get(i + 1);

      if (!this.neighbours.containsKey(a)) {
        this.neighbours.put(a, new Object2ObjectOpenHashMap<>(2));
      }

      if (!this.neighbours.containsKey(b)) {
        this.neighbours.put(b, new Object2ObjectOpenHashMap<>(2));
      }

      switch (direction) {
        case UNI:
          this.neighbours.get(a).put(b, edge);
          break;

        case BI:
        default:
          this.neighbours.get(a).put(b, edge);
          this.neighbours.get(b).put(a, edge);
      }
    }
  }

  /**
   * Gets all edges incident on a vertex.
   * @param node the vertex.
   * @return set of edges incident on the given vertex.
   */
  public Map<N, E> neighbours(final N node) {
    return this.neighbours.get(node);
  }

  /**
   * Returns all edges in the graph.
   * @return all edges.
   */
  public List<E> edges() {
    List<E> edges = new ArrayList<>();

    for (Map<N, E> neighbour: this.neighbours.values()) {
      for (E edge: neighbour.values()) {
        edges.add(edge);
      }
    }

    return edges;
  }

  /**
   * The {@link Node} interface describes a node within a weighted graph.
   */
  public interface Node extends Graph.Node {
    // No common methods. Yet.
  }

  /**
   * The {@link Edge} interface describes an edge within a weighted graph.
   *
   * @param <N> The type of nodes to use within the edge.
   */
  public interface Edge<N extends WeightedGraph.Node> extends Graph.Edge<N> {
    /**
     * Get the weight between the specified nodes.
     *
     * @param a           The first node.
     * @param b           The second node.
     * @param properties  A configuration map of custom properties.
     * @return            The weight between the specified nodes.
     */
    double weight(final N a, final N b, final Properties properties);
  }
}
