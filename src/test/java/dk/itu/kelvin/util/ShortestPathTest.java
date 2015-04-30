/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

import java.util.ArrayList;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class ShortestPathTest {

  @Test
  public void test() {
    WeightedGraph.Node n1 = new WeightedGraph.Node(1,1);
    WeightedGraph.Node n2 = new WeightedGraph.Node(3,1);
    WeightedGraph.Node n3 = new WeightedGraph.Node(3,3);
    WeightedGraph.Node n4 = new WeightedGraph.Node(7,2);

    WeightedGraph.Edge e1 = new WeightedGraph.Edge(n1, n2, 2);
    WeightedGraph.Edge e2 = new WeightedGraph.Edge(n1, n3, 1);
    WeightedGraph.Edge e3 = new WeightedGraph.Edge(n2, n3, 8);
    WeightedGraph.Edge e4 = new WeightedGraph.Edge(n2, n4, 4);
    WeightedGraph.Edge e5 = new WeightedGraph.Edge(n3, n4, 1);

    WeightedGraph wg = new WeightedGraph();
    wg.add(e1);
    wg.add(e2);
    wg.add(e3);
    wg.add(e4);
    wg.add(e5);

    ShortestPath sp = new ShortestPath(wg, n1);
    assertTrue(2 == sp.distTo(n4));

    assertTrue(2 == sp.path(n4).size());

    ArrayList<WeightedGraph.Edge> list = sp.path(n4);

    assertTrue(list.contains(e5));
  }
}
