/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * WeightedGraph class.
 */
public final class WeightedGraph<
  N extends WeightedGraph.Node, E extends WeightedGraph.Edge<N>
> implements Graph<N, E> {
  /**
   * The adjacency list for all vertices and edges in the graph.
   */
  private Map<N, Map<N, E>> neighbours = new HashMap<>();

  /**
   * Total number of nodes in the graph.
   */
  private int n;

  /**
   * Total number of edges in the graph.
   */
  private int e;

  /**
   * Adds an edge to the weighted graph.
   * @param e the edge to add.
   */
  public void add(final E edge) {
    List<N> nodes = edge.nodes();
    Direction direction = edge.direction();

    for (int i = 0; i < nodes.size() - 1; i++) {
      N a = nodes.get(i);
      N b = nodes.get(i + 1);

      if (!this.neighbours.containsKey(a)) {
        this.neighbours.put(a, new HashMap<>());
      }

      if (!this.neighbours.containsKey(b)) {
        this.neighbours.put(b, new HashMap<>());
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

    this.n += nodes.size();
    this.e++;
  }

  /**
   * Return number of nodes in the graph.
   * @return number of nodes.
   */
  public int n() {
    return this.n;
  }

  /**
   * Return number of edges in the graph.
   * @return number of edges.
   */
  public int e() {
    return this.e;
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

  public interface Node extends Graph.Node {
    // No common methods. Yet.
  }

  public interface Edge<N extends WeightedGraph.Node> extends Graph.Edge<N> {
    double weight(final N from, final N to);
  }
}
