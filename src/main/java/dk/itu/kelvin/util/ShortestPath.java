/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.HashMap;

public class shortestPath {
  /**
   * Maps the distance from the source to all different vertices in the graph.
   */
  private HashMap<WeightedGraph.Node, Float> distance = new HashMap<>();

  //private WeightedGraph.Edge[] edgeTo; // edgeTo[v] = last edge on shortest s->v path

  /**
   * Maps vertices with the last edge on the vertex's shortest path.
   */
  private HashMap<WeightedGraph.Node, WeightedGraph.Edge> edgeTo = new HashMap<>();

  /**
   * Priority queue holding all vertices.
   */
  private Queue<Double> pq;


  /**
   * Constructor setting all distances to infinity.
   * @param graph weighted graph.
   * @param source starting point for the shortest path.
   */
  public shortestPath(WeightedGraph graph, WeightedGraph.Node source) {
    for (WeightedGraph.Edge e : graph.edges()) {
      if (e.weight() < 0)
        throw new IllegalArgumentException("edge " + e + " has negative weight");
    }

    for (WeightedGraph.Node n : this.distance.keySet()) {
      this.distance.put(n, Float.POSITIVE_INFINITY);
      this.distance.put(source, 0.0f);


      //distTo[n] = Double.POSITIVE_INFINITY;
      //distTo[source] = 0.0;

      // relax vertices in order of distance from s
      pq = new Queue<Double>(graph.V());
      pq.insert(source, this.distance.get(source));
      while (!pq.isEmpty()) {
        WeightedGraph.No2n();
        for (WeightedGraph.Edge e : graph.neighbours(v))
          relax(e);
      }
    }
  }

  // relax edge e and update pq if changed
  private void relax(WeightedGraph.Edge e) {
    WeightedGraph.Node v = e.from(), w = e.to();
    if (this.distance.get(w) > this.distance.get(v) + e.weight()) {
      this.distance.put(w, this.distance.get(v) + e.weight());
      this.edgeTo.put(w, e);

      if (pq.contains(w)) pq.decreaseKey(w, this.distance.get(w));
      else                pq.insert(w, this.distance.get(w));
    }
  }

  /**
   * Returns the length of a shortest path from the source.
   * @param v the destination vertex.
   * @return the length of a shortest path.
   * Double.POSITIVE_INFINITY if no such path
   */
  public double distTo(WeightedGraph.Node v) {
    return this.distance.get(v);
  }

  /**
   * Is there a path from the source vertex to the param vertex.
   * @param v the destination vertex.
   * @return true if there is a path, false otherwise.
   */
  public boolean hasPathTo(WeightedGraph.Node v) {
    return this.distance.get(v) < Double.POSITIVE_INFINITY;
  }

  /**
   * Returns a shortest path.
   * @param n the destination vertex.
   * @return shortest path from the source vertex to the param vertex
   * as an iterable of edges, and null if no such path.
   */
  public Iterable<WeightedGraph.Edge> path(WeightedGraph.Node n) {
    if (!hasPathTo(n)) return null;
    Stack<WeightedGraph.Edge> path = new Stack<WeightedGraph.Edge>();
    for (WeightedGraph.Edge e = edgeTo[n]; e != null; e = edgeTo[e.from()]) {
      path.push(e);
    }
    return path;
  }

}
