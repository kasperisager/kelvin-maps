/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.List;

// JUnit annotations
import org.junit.Before;
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;

public class ShortestPathTest {

  WeightedGraph.Node n1;
  WeightedGraph.Node n2;
  WeightedGraph.Node n3;
  WeightedGraph.Node n4;

  WeightedGraph.Edge e1;
  WeightedGraph.Edge e2;
  WeightedGraph.Edge e3;
  WeightedGraph.Edge e4;
  WeightedGraph.Edge e5;

  WeightedGraph wg;

  @Before
  public void setUp(){
    n1 = new WeightedGraph.Node(1,1);
    n2 = new WeightedGraph.Node(3,1);
    n3 = new WeightedGraph.Node(3,3);
    n4 = new WeightedGraph.Node(7,2);

    e1 = new WeightedGraph.Edge(n1, n2, 2);
    e2 = new WeightedGraph.Edge(n1, n3, 1);
    e3 = new WeightedGraph.Edge(n2, n3, 8);
    e4 = new WeightedGraph.Edge(n2, n4, 4);
    e5 = new WeightedGraph.Edge(n3, n4, 1);

    System.out.println("Edge1: " + e1);
    System.out.println("Edge2: " + e2);
    System.out.println("Edge3: " + e3);
    System.out.println("Edge4: " + e4);
    System.out.println("Edge5: " + e5);
    System.out.println();

    wg = new WeightedGraph();
    wg.add(e1);
    wg.add(e2);
    wg.add(e3);
    wg.add(e4);
    wg.add(e5);


  }

  @Test
  public void testDistTo() {
    ShortestPath sp = new ShortestPath(wg, n1);
    assertTrue(2 == sp.distTo(n4));

    List<WeightedGraph.Edge> list = sp.path(n4);

    /*assertTrue(sp.map().keySet().contains(n1));

    List<WeightedGraph.Edge> list = sp.path(n4);

    System.out.println(list);

    assertTrue(2 == list.size());

    assertTrue(list.contains(e5) && list.contains(e2));*/


  }
}
