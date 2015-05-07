/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// I/O utilities
import java.io.Serializable;

/**
 * Graph interface.
 *
 * @param <N> The type of nodes in the graph.
 * @param <E> The type of edges in the graph.
 */
public interface Graph<N extends Graph.Node, E extends Graph.Edge>
  extends Serializable {

  /**
   * Add edge to graph.
   * @param edge The edge to add.
   */
  void add(final E edge);

  /**
   * Inner class Node.
   */
  public interface Node {
    /**
     * Get x coordinate for Node.
     *
     * @return x coordinate.
     */
    float x();

    /**
     * Get y coordinate for Node.
     *
     * @return y coordinate.
     */
    float y();
  }

  /**
   * Inner class Edge.
   */
  public interface Edge {
    /**
     * Get from node.
     *
     * @return Node that the edge starts in.
     */
    Node from();

    /**
     * Get to node.
     *
     * @return Node that the edge ends in.
     */
    Node to();
  }
}
