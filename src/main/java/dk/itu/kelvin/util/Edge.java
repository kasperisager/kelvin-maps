/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

import dk.itu.kelvin.model.Node;

public class Edge {
  private Node start;
  private Node end;
  private float weight;
  private int speedLimit;

  public Edge(Node start, Node end, float weight, int speedLimit) {
    this.start = start;
    this.end = end;
    this.weight = weight;
    this.speedLimit = speedLimit;
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
    return speedLimit;
  }

  /**
   * Returns the tail vertex of the directed edge.
   * @return the tail vertex of the directed edge
   */
  public Node from() {
    return start;
  }

  /**
   * Returns the head vertex of the directed edge.
   * @return the head vertex of the directed edge
   */
  public Node to() {
    return end;
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
