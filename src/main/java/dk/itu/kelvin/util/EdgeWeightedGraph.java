/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;



public class EdgeWeightedGraph {
  private final int V;
  private int E;
  private Bag<Edge>[] adj;

  /**
   * Initializes an empty edge-weighted graph with <tt>V</tt> vertices and 0 edges.
   * param V the number of vertices
   * @throws java.lang.IllegalArgumentException if <tt>V</tt> < 0
   */
  public EdgeWeightedGraph(int V) {
    if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
    this.V = V;
    this.E = 0;
    adj = (Bag<Edge>[]) new Bag[V];
    for (int v = 0; v < V; v++) {
      adj[v] = new Bag<Edge>();
    }
  }


  /**
   * Returns the number of vertices in the edge-weighted graph.
   * @return the number of vertices in the edge-weighted graph
   */
  public int V() {
    return V;
  }

  /**
   * Returns the number of edges in the edge-weighted graph.
   * @return the number of edges in the edge-weighted graph
   */
  public int E() {
    return E;
  }

  // throw an IndexOutOfBoundsException unless 0 <= v < V
  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
  }

  /**
   * Adds the undirected edge <tt>e</tt> to the edge-weighted graph.
   * @param e the edge
   * @throws java.lang.IndexOutOfBoundsException unless both endpoints are between 0 and V-1
   */
  public void addEdge(Edge e) {
    int v = e.either();
    int w = e.other(v);
    validateVertex(v);
    validateVertex(w);
    adj[v].add(e);
    adj[w].add(e);
    E++;
  }

  /**
   * Returns the edges incident on vertex <tt>v</tt>.
   * @return the edges incident on vertex <tt>v</tt> as an Iterable
   * @param v the vertex
   * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
   */
  public Iterable<Edge> adj(int v) {
    validateVertex(v);
    return adj[v];
  }

  /**
   * Returns the degree of vertex <tt>v</tt>.
   * @return the degree of vertex <tt>v</tt>
   * @param v the vertex
   * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
   */
  public int degree(int v) {
    validateVertex(v);
    return adj[v].size();
  }

  /**
   * Returns all edges in the edge-weighted graph.
   * To iterate over the edges in the edge-weighted graph, use foreach notation:
   * <tt>for (Edge e : G.edges())</tt>.
   * @return all edges in the edge-weighted graph as an Iterable.
   */
  public Iterable<Edge> edges() {
    Bag<Edge> list = new Bag<Edge>();
    for (int v = 0; v < V; v++) {
      int selfLoops = 0;
      for (Edge e : adj(v)) {
        if (e.other(v) > v) {
          list.add(e);
        }
        // only add one copy of each self loop (self loops will be consecutive)
        else if (e.other(v) == v) {
          if (selfLoops % 2 == 0) list.add(e);
          selfLoops++;
        }
      }
    }
    return list;
  }

  /**
   * Returns a string representation of the edge-weighted graph.
   * This method takes time proportional to <em>E</em> + <em>V</em>.
   * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
   *   followed by the <em>V</em> adjacency lists of edges
   */
  public String toString() {
    String NEWLINE = System.getProperty("line.separator");
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (Edge e : adj[v]) {
        s.append(e + "  ");
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }

}
