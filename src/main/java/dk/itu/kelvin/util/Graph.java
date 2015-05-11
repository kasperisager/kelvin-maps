/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.List;

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
   * Add edge to graph.
   * @param edge The edge to add.
   */
  void add(final E edge);

  List<E> edges();

  /**
   * Inner class Node.
   */
  public interface Node {
    // No common methods. Yet.
  }

  /**
   * Inner class Edge.
   */
  public interface Edge<N extends Node> {
    void add(final N node);

    void add(final List<N> nodes);

    List<N> nodes();

    Direction direction();
  }

  public enum Direction {
    UNI,

    BI;
  }
}
