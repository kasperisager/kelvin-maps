/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// I/O utilities
import java.io.Serializable;

/**
 * Graph interface.
 *
 * @param <N> The type of nodes in the graph.
 * @param <E> The type of edges in the graph.
 */
public interface Graph<N extends Graph.Node, E extends Graph.Edge<N>>
  extends Serializable {
  /**
   * Add an edge to the graph.
   *
   * @param edge  The edge to add to the graph.
   * @return      A boolean indicating whether or not the edge was added to the
   *              graph.
   */
  boolean add(final E edge);

  /**
   * Get the edges of the graph.
   *
   * @return The edges of the graph.
   */
  Collection<E> edges();

  /**
   * Get all edges incident on the specified node.
   *
   * @param node  The node whose incident edges to get.
   * @return      A map of nodes and their associated edges incident on the
   *              specified node.
   */
  Map<N, E> neighbours(final N node);

  /**
   * The {@link Node} interface describes a node within a graph.
   */
  public interface Node {
    // No common methods. Yet.
  }

  /**
   * The {@link Edge} interface describes an edge within a graph.
   *
   * @param <N> The type of nodes to use within the edge.
   */
  public interface Edge<N extends Node> {
    /**
     * Add a node to the edge.
     *
     * @param node The node to add to the edge.
     */
    void add(final N node);

    /**
     * Add a list of nodes to the edge.
     *
     * @param nodes The list of nodes to add to the edge.
     */
    void add(final List<N> nodes);

    /**
     * Get a list of nodes contained within the edge.
     *
     * @return A list of nodes contained within the edge.
     */
    List<N> nodes();

    /**
     * Get the direction of the edge.
     *
     * @param properties  A configuration map of custom properties.
     * @return            The direction of the edge.
     */
    Direction direction(final Properties properties);
  }

  /**
   * The {@link Direction} enumerator describes the direction of an edge.
   */
  public enum Direction {
    /**
     * A uni-directional one-way edge.
     */
    UNI,

    /**
     * A bi-directional two-way edge.
     */
    BI;
  }
}
