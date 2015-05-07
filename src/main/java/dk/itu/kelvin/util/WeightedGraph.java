/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * WeightedGraph class.
 */
public final class WeightedGraph
  implements Graph<WeightedGraph.Node, WeightedGraph.Edge> {
  /**
   * The adjacency list for all vertices and edges in the graph.
   */
  private HashMap<WeightedGraph.Node, HashSet<WeightedGraph.Edge>> adjacencyList
    = new HashMap<>();

  /**
   * Total number of vertices in the graph.
   */
  private int v = 0;

  /**
   * Total number of edges in the graph.
   */
  private int e = 0;

  /**
   * Adds an edge to the weighted graph.
   * @param e the edge to add.
   */
  public void add(final Edge e) {
    if (this.adjacencyList.get(e.from()) == null) {
      this.adjacencyList.put(e.from(), new HashSet<>());
      this.v++;
    }

    if (this.adjacencyList.get(e.to()) == null) {
      this.adjacencyList.put(e.to(), new HashSet<>());
      this.v++;
    }

    this.adjacencyList.get(e.from()).add(e);
    this.e++;
    this.adjacencyList.get(e.to()).add(e);
    this.e++;
  }

  /**
   * Return number of vertices in the graph.
   * @return number of vertices.
   */
  public int v() {
    return this.v;
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
  public HashSet<WeightedGraph.Edge> neighbours(final Node node) {
    return this.adjacencyList.get(node);
  }

  /**
   * Returns the number of edges incident from vertex.
   * @param node the vertex.
   * @return the outdegree.
   */
  public int outdegree(final Node node) {
    return this.adjacencyList.get(node).size();
  }

  /**
   * Returns all edges in the graph.
   * @return all edges.
   */
  public List<Edge> edges() {
    List<Edge> list = new ArrayList<>();
    for (HashSet<Edge> set : this.adjacencyList.values()) {
      for (Edge edge : set) {
        list.add(edge);
      }
    }
    return list;
  }

  /**
   * The node class for the graph.
   */
  public static final class Node implements Graph.Node {
    /**
     * The x-coordinate of the node.
     */
    private float x;

    /**
     * The y-coordinate of the node.
     */
    private float y;

    /**
     * Constructor for node.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     */
    public Node(final float x, final float y) {
      this.x = x;
      this.y = y;
    }

    /**
     * Returns the x-coordinate.
     * @return x-coordinate of the node.
     */
    public float x() {
      return this.x;
    }

    /**
     * Returns the y-coordinate.
     * @return y-coordinate of the node.
     */
    public float y() {
      return this.y;
    }

    /**
     * Compute the hashcode of the WeightedGraph.Node.
     *
     * @return The computed hashcode of the Node.
     */
    @Override
    public int hashCode() {
      long bits = 7L;
      bits = 31L * bits + Float.hashCode(this.x);
      bits = 31L * bits + Float.hashCode(this.y);

      return (int) (bits ^ (bits >> 32));
    }

    /**
     * Check if the current node equals the specified object.
     *
     * @param object  The object to compare the current node to.
     * @return        A boolean indicating whether or not the current node
     *                equals the specified object.
     */
    @Override
    public boolean equals(final Object object) {
      if (object == null || !(object instanceof Node)) {
        return false;
      }

      if (this == object) {
        return true;
      }

      Node node = (Node) object;

      return (node.x() == this.x && node.y() == this.y);
    }

    @Override
    public String toString() {
      return "X: " + this.x + " Y: " + this.y;
    }
  }

  /**
   * The edge class for the graph.
   */
  public static final class Edge implements Graph.Edge {
    /**
     * The node that the edge starts in.
     */
    private Node from;

    /**
     * The node that the edge ends in.
     */
    private Node to;

    /**
     * The weight of the edge.
     */
    private float weight;

    /**
     * Initialize a new edge.
     *
     * @param from    The node object the edge starts in.
     * @param to      The node object the edge ends in.
     * @param weight  The weight of the edge.
     */
    public Edge(final Node from, final Node to, final float weight) {
      this.from = from;
      this.to = to;
      this.weight = weight;
    }

    /**
     * Returns the from object.
     * @return the from node.
     */
    public Node from() {
      return this.from;
    }

    /**
     * Returns the to object.
     * @return the to node.
     */
    public Node to() {
      return this.to;
    }

    /**
     * Returns the weight of the edge object.
     * @return weight.
     */
    public float weight() {
      return this.weight;
    }

    /**
     * Compute the hashcode of the WeightedGraph.Node.
     * @return The computed hashcode of the Node.
     */
    @Override
    public int hashCode() {
      long bits = 7L;
      bits = 31L * bits + Float.hashCode(this.weight);
      bits = 31L * bits + this.from.hashCode();
      bits = 31L * bits + this.to.hashCode();

      return (int) (bits ^ (bits >> 32));
    }

    /**
     * Check if the current edge equals the specified object.
     *
     * @param object  The object to compare the current edge to.
     * @return        A boolean indicating whether or not the current edge
     *                equals the specified object.
     */
    @Override
    public boolean equals(final Object object) {
      if (object == null || !(object instanceof Edge)) {
        return false;
      }

      if (this == object) {
        return true;
      }
      Edge e = (Edge) object;

      return (e.from().equals(this.from)
      && e.to().equals(this.to)
      && e.weight() == this.weight);
    }

    @Override
    public String toString() {
      String s = this.from.toString() + " -> " + this.to.toString();
      return s;
    }

  }

}
