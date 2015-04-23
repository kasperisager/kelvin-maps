/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// Models
import dk.itu.kelvin.model.Node;

public class EdgeWeightedDigraph {
  private final int V;
  private int E;
  private Bag<Edge>[] adj;

  /**
   * Initializes an empty edge-weighted digraph with <tt>V</tt> vertices and 0 edges.
   * param V the number of vertices
   * @throws java.lang.IllegalArgumentException if <tt>V</tt> < 0
   */
  public EdgeWeightedDigraph(int V) {
    if (V < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
    this.V = V;
    this.E = 0;
    adj = (Bag<Edge>[]) new Bag[V];
    for (int v = 0; v < V; v++)
      adj[v] = new Bag<Edge>();
  }

  /**
   * Returns the number of vertices in the edge-weighted digraph.
   * @return the number of vertices in the edge-weighted digraph
   */
  public int V() {
    return V;
  }

  /**
   * Returns the number of edges in the edge-weighted digraph.
   * @return the number of edges in the edge-weighted digraph
   */
  public int E() {
    return E;
  }

  /**
   * Adds the directed edge <tt>e</tt> to the edge-weighted digraph.
   * @param e the edge
   */
  public void addEdge(Edge e) {
    Node v = e.from();
    Node w = e.to();
    adj[v].add(e);
    E++;
  }


  /**
   * Returns the directed edges incident from vertex <tt>v</tt>.
   * @return the directed edges incident from vertex <tt>v</tt> as an Iterable
   * @param v the vertex
   */
  public Iterable<Edge> adj(int v) {
    return adj[v];
  }

  /**
   * Returns the number of directed edges incident from vertex <tt>v</tt>.
   * This is known as the <em>outdegree</em> of vertex <tt>v</tt>.
   * @return the outdegree of vertex <tt>v</tt>
   * @param v the vertex
   */
  public int outdegree(int v) {
    return adj[v].size();
  }

  /**
   * Returns all directed edges in the edge-weighted digraph.
   * @return all edges in the edge-weighted graph as an Iterable.
   */
  public Iterable<Edge> edges() {
    Bag<Edge> list = new Bag<Edge>();
    for (int v = 0; v < V; v++) {
      for (Edge e : adj(v)) {
        list.add(e);
      }
    }
    return list;
  }

}
