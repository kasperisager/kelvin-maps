/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Map;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test of the node class.
 */
public final class NodeTest {
  /**
   * Test initialization of a node object with an id and a x y coordinate.
   */
  @Test
  public void testInitialization() {
    Node n1 = new Node(5, 15, 25);
    assertEquals(5, n1.id());
    assertEquals(15, n1.getX(), 0);
    assertEquals(25, n1.getY(), 0);
  }

  /**
   * Tests the tag class with different keys and values.
   */
  @Test
  public void testTag() {
    Node n1 = new Node(1, 1, 1);

    //the map size with no input.
    Map<String, String> tags = n1.tags();
    assertEquals(0, tags.size());

   //Test the size of the Map with one input.
    n1.tag("aaa", "bbb");
    tags = n1.tags();
    assertEquals(1, tags.size());
    assertEquals("bbb", tags.get("aaa"));

    //if one of the strings are empty.
    n1.tag("", "ccc");
    assertEquals("ccc", tags.get(""));

    // if the key is null.
    n1.tag(null, "ddd");
    assertEquals(null, tags.get(null));

    //if the value is null.
    n1.tag("eee", null);
    assertEquals(null, tags.get("eee"));

  }

  /**
   * Tests if the element order is higher, lower, or equal.
   */
  @Test
  public void testOrder() {
    Node n1 = new Node(1, 1, 1);
    assertEquals(Element.Order.DEFAULT, n1.order());
    n1.order(Element.Order.NATURAL_WATER);
    assertEquals(Element.Order.NATURAL_WATER, n1.order());

    Node n2 = new Node(2, 2, 2);
    n2.order(Element.Order.HIGHWAY);

    Node n3 = new Node(3, 3, 3);
    n3.order(Element.Order.HIGHWAY);

    Node n4 = new Node(4, 4, 4);
    n4.order(Element.Order.HIGHWAY_MOTORWAY);

    // < 0 first object's order is smaller than the second obejct's order.
    // 0 if there are equal.
    // and > 0 if first is larger than second.
    assertTrue(Element.Order.compare(n1, n3) < 0);
    assertTrue(Element.Order.compare(n4, n2) > 0);
    assertTrue(Element.Order.compare(n2, n3) == 0);

  }

  /**
   * test of layers and test when there is a layer and a order.
   */
  @Test
  public void testLayer() {
    Node n1 = new Node(1, 1, 1);
    n1.layer(-2);

    Node n2 = new Node(2, 2, 2);
    n2.layer(0);

    Node n3 = new Node(3, 3, 3);
    n3.layer(2);

    Node n4 = new Node(4, 4, 4);
    n4.layer(2);

    assertTrue(Element.Order.compare(n1, n2) < 0);
    assertTrue(Element.Order.compare(n1, n3) < 0);
    assertTrue(Element.Order.compare(n3, n2) > 0);
    assertTrue(Element.Order.compare(n3, n4) == 0);

    Node n5 = new Node(4, 4, 4);
    n5.order(Element.Order.HIGHWAY);
    n5.layer(0);

    Node n6 = new Node(4, 4, 4);
    n6.order(Element.Order.HIGHWAY);
    n6.layer(1);

    Node n7 = new Node(4, 4, 4);
    n7.order(Element.Order.HIGHWAY_MOTORWAY);
    n7.layer(-2);

    Node n8 = new Node(4, 4, 4);
    n8.order(Element.Order.HIGHWAY_MOTORWAY);
    n8.layer(-2);

    assertTrue(Element.Order.compare(n5, n6) < 0);
    assertTrue(Element.Order.compare(n7, n8) == 0);

  }
}
