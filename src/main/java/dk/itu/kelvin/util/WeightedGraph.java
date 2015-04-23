/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

public class WeightedGraph implements Graph<WeightedGraph.Node, WeightedGraph.Edge>{
  /**
   * The adjacency list for all vertices and edges in the graph.
   */
  private Hashtable<WeightedGraph.Node, HashSet<WeightedGraph.Edge>> adjacencyList = new Hashtable<>();

  /**
   * Total number of vertices in the graph.
   */
  private int V = 0;

  /**
   * Total number of edges in the graph.
   */
  private int E = 0;

  /**
   * Adds an edge to the weighted graph.
   * @param e the edge to add.
   */
  public void add(Edge e){
    if (adjacencyList.get(e.from()) == null) {
      adjacencyList.put(e.from(), new HashSet<>());
      this.V++;
    }

    if (adjacencyList.get(e.to()) == null) {
      adjacencyList.put(e.to(), new HashSet<>());
      this.V++;
    }

    adjacencyList.get(e.from()).add(e);
    this.E++;
    adjacencyList.get(e.to()).add(e);
    this.E++;
  }

  /**
   * Gets all edges incident on a vertex.
   * @param node the vertex.
   * @return set of edges incident on the given vertex.
   */
  public HashSet<Edge> neighbours(final Node node) {
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
    for(HashSet<Edge> set : this.adjacencyList.values()) {
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
    private float x;
    private float y;

    /**
     * Constructor for node.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     */
    public Node(final float x, final float y){
      this.x = x;
      this.y = y;
    }

    /**
     * Returns the x-coordinate.
     * @return x-coordinate of the node.
     */
    public float x() {
      return x;
    }

    /**
     * Returns the y-coordinate.
     * @return y-coordinate of the node.
     */
    public float y(){
      return y;
    }

    /**
     * Compute the hashcode of the WeightedGraph.Node.
     *
     * @return The computed hashcode of the Node.
     */
    @Override
    public int hashCode() {
      long bits = 7L;
      bits = 31L * bits + Double.doubleToLongBits(this.x);
      bits = 31L * bits + Double.doubleToLongBits(this.y);

      return (int) (bits ^ (bits >> 32));
    }

    /**
     * Check if two node objects are equal.
     *
     * @return true or false.
     */
    @Override
    public boolean equals(final Object object) {
      if (object == null || !(object instanceof Node)) {
        return false;
      }

      if (this == object) {
        return true;
      }

      Node n = (Node) object;

      return (n.x() == this.x && n.y() == this.y);
    }

  }

  /**
   * The edge class for the graph.
   */
  public static final class Edge implements Graph.Edge {
    private Node from;
    private Node to;
    private float weight;

    /**
     * Constructor.
     * @param from the node object the edge starts in.
     * @param to the node object the edge ends in.
     */
    public Edge(final Node from, final Node to) {
      this.from = from;
      this.to = to;
      //this.weight = weight;
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
    public final float weight() {
      return weight;
    }

    /**
     * Compute the hashcode of the WeightedGraph.Node.
     *
     * @return The computed hashcode of the Node.
     */
    @Override
    public int hashCode() {
      long bits = 7L;
      bits = 31L * bits + Double.doubleToLongBits(this.weight);
      bits = 31L * bits + this.from.hashCode();
      bits = 31L * bits + this.to.hashCode();

      return (int) (bits ^ (bits >> 32));
    }

    /**
     * Check if two edge objects are equal.
     *
     * @return true or false.
     */
    @Override
    public boolean equals(final Object object) {
      if (object == null || !(object instanceof Node)) {
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
  }

}
