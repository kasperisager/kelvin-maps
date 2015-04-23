/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

public class Edge {
  private Node start;
  private Node end;
  private float weight;
  private int speeedLimit;

  public Edge(Node start, Node end, float weight, int speedLimit) {
    this.start = start;
    this.end = end;
    this.weight = weight;
    this.speeedLimit = speedLimit;
  }

  /**
   * Returns the weight of the edge.
   * @return the weight of the edge
   */
  public double weight() {
    return weight;
  }

  /**
   * Returns the speed limit of the edge.
   * @return the speed limit of the edge
   */
  public double speedLimit() {
    return speeedLimit;
  }

  /**
   * Returns either endpoint of the edge.
   * @return either endpoint of the edge
   */
  public Node either() {
    return start;
  }

  /**
   * Returns the endpoint of the edge that is different from the given vertex
   * (unless the edge represents a self-loop in which case it returns the same vertex).
   * @param vertex one endpoint of the edge
   * @return the endpoint of the edge that is different from the given vertex
   *   (unless the edge represents a self-loop in which case it returns the same vertex)
   * @throws java.lang.IllegalArgumentException if the vertex is not one of the endpoints
   *   of the edge
   */
  public Node other(Node vertex) {
    if      (vertex.equals(this.start)) return this.end;
    else if (vertex.equals(this.end)) return this.start;
    else throw new IllegalArgumentException("Illegal endpoint");
  }

  /**
   * Compares two edges by weight.
   * @param that the other edge
   * @return a negative integer, zero, or positive integer depending on whether
   *    this edge is less than, equal to, or greater than that edge
   */
  public int compareTo(Edge that) {
    if      (this.weight() < that.weight()) return -1;
    else if (this.weight() > that.weight()) return +1;
    else                                    return  0;
  }
}
