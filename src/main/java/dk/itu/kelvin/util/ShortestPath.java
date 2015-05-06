/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Collections;

// Utilities
import dk.itu.kelvin.util.WeightedGraph.Edge;
import dk.itu.kelvin.util.WeightedGraph.Node;

public class ShortestPath {
  /**
   * Maps the distance from the source to all different vertices in the graph.
   */
  private HashMap<Node, Float> distance = new HashMap<>();

  /**
   * Maps vertices with the last edge on the vertex's shortest path.
   */
  private HashMap<Node, Edge> edgeTo = new HashMap<>();

  /**
   * Priority queue holding all vertices.
   */
  private PriorityQueue<Node> queue;

  /**
   * Constructor setting all distances to infinity.
   * @param graph weighted graph.
   * @param source starting point for the shortest path.
   */
  public ShortestPath(final WeightedGraph graph, final Node source) {
    for (Edge e : graph.edges()) {
      if (e.weight() < 0) {
        throw new IllegalArgumentException("edge " + e + " has negative weight");
      }
    }

    for (Edge e: graph.edges()) {
      this.distance.put(e.from(), Float.POSITIVE_INFINITY);
      this.distance.put(e.to(), Float.POSITIVE_INFINITY);
    }

    this.distance.put(source, 0.0f);

    // relax vertices in order of distance from s
    this.queue = new PriorityQueue<>(graph.V(), (a, b) -> {
      // Comparing b to a instead of a to b to make
      // a minimum priority and not maximum priority.
      return this.distance.get(b).compareTo(this.distance.get(a));
    });

    this.queue.add(source);

    while (!queue.isEmpty()) {
      Node v = this.queue.poll();

      Set<Edge> neighbours = graph.neighbours(v);

      for (Edge e: neighbours) {
        relax(e);
      }
    }

    for (Node node: this.distance.keySet()) {
      if (this.distance.get(node) < Float.POSITIVE_INFINITY) {
      }
    }
  }

  // relax edge e and update queue if changed
  private void relax(Edge edge) {
    if (edge == null) {
      return;
    }

    Node from = edge.from();
    Node to = edge.to();

    float distFrom = this.distance.get(from);
    float distTo = this.distance.get(to);

    if (distTo > distFrom + edge.weight()) {
      this.distance.put(to, distFrom + edge.weight());
      this.edgeTo.put(to, edge);

      if (queue.contains(to)) {
        this.queue.remove(to);
      }

      this.queue.add(to);
    }
  }

  public HashMap map(){
    return this.distance;
  }

  /**
   * Returns the length of a shortest path from the source.
   * @param node the destination vertex.
   * @return the length of a shortest path.
   * Float.POSITIVE_INFINITY if no such path
   */
  public float distTo(final Node node) {
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
  public boolean hasPathTo(final Node node) {
    if (!this.distance.containsKey(node)) {
      return false;
    }

    return this.distance.get(node) < Float.POSITIVE_INFINITY;
  }

  /**
   * Returns a shortest path.
   * @param n the destination vertex.
   * @return shortest path from the source vertex to the param vertex
   * as an iterable of edges, and null if no such path.
   */
  public List<Edge> path(final Node n) {
    if (!this.hasPathTo(n)) {
      return null;
    }

    List<Edge> path = new ArrayList<>();

    for (Edge e = this.edgeTo.get(n); e != null; e = this.edgeTo.get(e.from())) {
      path.add(e);
    }

    // The path is located in reverse order and is therefore backwards. Reverse
    // it to correct this.
    Collections.reverse(path);

    return path;
  }
}
