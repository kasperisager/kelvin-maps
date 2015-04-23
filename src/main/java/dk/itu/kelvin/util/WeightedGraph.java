/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

import java.util.HashSet;
import java.util.Hashtable;

public class WeightedGraph implements Graph<WeightedGraph.Node, WeightedGraph.Edge>{
  private Hashtable<WeightedGraph.Node, HashSet<WeightedGraph.Edge>> adjacencyList = new Hashtable<>();

  public void add(Edge e){
    if (adjacencyList.get(e.from()) == null) {
      adjacencyList.put(e.from(), new HashSet<>());

    }
    if (adjacencyList.get(e.to()) == null) {
      adjacencyList.put(e.to(), new HashSet<>());
    }

    adjacencyList.get(e.from()).add(e);
    adjacencyList.get(e.to()).add(e);
  }

  public static final class Node implements Graph.Node {
    private float x;
    private float y;

    public Node(final float x, final float y){
      this.x = x;
      this.y = y;
    }

    public float x() {
      return x;
    }

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

  public static final class Edge implements Graph.Edge {
    private Node from;
    private Node to;
    private float weight;

    public Edge(final Node from, final Node to) {
      this.from = from;
      this.to = to;
      //this.weight = weight;
    }

    public Node from() {
      return this.from;
    }

    public Node to() {
      return this.to;
    }

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

      return (e.from().equals(this.from) && e.to().equals(this.to) && e.weight() == this.weight);
    }
  }

}
