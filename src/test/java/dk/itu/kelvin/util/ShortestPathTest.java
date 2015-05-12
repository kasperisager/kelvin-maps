/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// Models
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Way;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Testing the shortestpath class.
 */
public class ShortestPathTest {
  /**
   * Test the path methods returns the shortest path.
   */
  @Test
  public void testPath() {
    Node n1 = new Node(1, 1);
    Node n2 = new Node(6, 7);
    Node n3 = new Node(19, 19);

    Way w1 = new Way();
    w1.add(n1);
    w1.add(new Node(3, 1));
    w1.add(new Node(3, 2));
    w1.add(new Node(6, 2));
    w1.add(new Node(6, 1));
    w1.add(new Node(8, 1));
    w1.add(new Node(8, 7));
    w1.add(new Node(6, 7));

    Way w2 = new Way();
    w2.add(new Node(1, 1));
    w2.add(new Node(4, 1));
    w2.add(new Node(4, 3));
    w2.add(new Node(6, 7));

    Way w3 = new Way();
    w3.add(new Node(16, 16));
    w3.add(new Node(19, 19));

    WeightedGraph<Node, Way> wg = new WeightedGraph<>();
    wg.add(w1);
    wg.add(w2);
    wg.add(w3);

    ShortestPath<Node, Way> sp = new ShortestPath<>(wg, n1, n2);
    ShortestPath<Node, Way> sp2 = new ShortestPath<>(wg, n1, n3);
    assertTrue(sp.path().size() == 4);

    assertNull(sp2.path());
  }
}
