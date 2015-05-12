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

/**
 * {@link WeightedGraph} test suite.
 */
public class WeightedGraphTest {
  /**
   * Testing the add method.
   */
  @Test
  public void testAdd() {
    Node n1 = new Node(2, 2);
    Node n2 = new Node(3, 3);
    Node n3 = new Node(4, 4);
    Way w1 = new Way();
    w1.add(n1);
    w1.add(n2);
    w1.add(n3);

    Node n4 = new Node(5, 5);
    Node n5 = new Node(6, 6);
    Node n6 = new Node(7, 7);
    Way w2 = new Way();
    w2.add(n4);
    w2.add(n5);
    w2.add(n6);
    w2.tag("oneway", "yes");

    WeightedGraph<Node, Way> wg = new WeightedGraph<>();

    wg.add(w1);
    assertTrue(wg.edges().contains(w1));

    wg.add(w2);
    assertTrue(wg.edges().contains(w1));
  }

  /**
   * Test if the right neighbours are returned.
   */
  @Test
  public void testNeighbours() {
    Node n4 = new Node(5, 5);
    Node n5 = new Node(6, 6);
    Node n6 = new Node(7, 7);
    Way w2 = new Way();
    w2.add(n4);
    w2.add(n5);
    w2.add(n6);

    Node n7 = new Node(8, 3);
    Node n8 = new Node(3, 3);
    Node n9 = new Node(4, 4);
    Way w3 = new Way();
    w3.add(n7);
    w3.add(n8);
    w3.add(n9);

    Node n10 = new Node(1, 1);
    Node n11 = new Node(2, 1);
    Node n12 = new Node(2, 4);
    Way w4 = new Way();
    w4.add(n10);
    w4.add(n11);
    w4.add(n12);

    WeightedGraph<Node, Way> wg = new WeightedGraph<>();

    wg.add(w2);
    wg.add(w3);
    wg.add(w4);
    assertTrue(wg.neighbours(n5).containsKey(n4));
    assertTrue(wg.neighbours(n5).containsKey(n6));
    assertTrue(wg.neighbours(n5).size() == 2);
  }

}
