/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

// Utilities
import dk.itu.kelvin.util.Map;

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
    assertEquals(15, n1.x(), 0);
    assertEquals(25, n1.y(), 0);
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
    n1.order(null);
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
    assertTrue(Element.compare(n1, n3) < 0);
    assertTrue(Element.compare(n4, n2) > 0);
    assertTrue(Element.compare(n2, n3) == 0);

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

    assertTrue(Element.compare(n1, n2) < 0);
    assertTrue(Element.compare(n1, n3) < 0);
    assertTrue(Element.compare(n3, n2) > 0);
    assertTrue(Element.compare(n3, n4) == 0);

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

    assertTrue(Element.compare(n5, n6) < 0);
    assertTrue(Element.compare(n7, n8) == 0);

  }
  /**
   * Test of equals method.
   * two nodes with the same id, x, y, order and layer should be same node.
   */
  @Test
  public void testEquals() {
    Node n1 = new Node(1, 1, 1);
    n1.order(Element.Order.BUILDING);
    n1.layer(0);

    Node n2 = new Node(1, 1, 1);
    n2.order(Element.Order.BUILDING);
    n2.layer(0);

    Node n3 = new Node(3, 3, 3);
    n3.order(Element.Order.HIGHWAY);
    n3.layer(1);

    assertTrue(n1.equals(n2));
    assertFalse(n1.equals(n3));
    assertEquals(n1.hashCode(), n2.hashCode());
    assertNotEquals(n1.hashCode(), n3.hashCode());

    //test the method's three if-statements.
    Node n6 = new Node(4, 4, 4);
    assertFalse(n6.equals(null));
    assertTrue(n6.equals(n6));
    assertFalse(n6.equals("not a node"));
  }

    /**
     * Second part of equal method.
     * Tests all combinations of id, x, y, order and layer.
     */
    @Test
    public void testEqualCombinations() {
    //all Nodes will be compared with n1a.
    Node n1a = new Node(1, 1, 1);
    n1a.order(Element.Order.BUILDING);
    n1a.layer(0);

    //1.
    Node n1b = new Node(1, 1, 1);
    n1b.order(Element.Order.BUILDING);
    n1b.layer(1);
    assertFalse(n1a.equals(n1b));

    //2.
    Node n2b = new Node(1, 1, 1);
    n2b.order(Element.Order.HIGHWAY);
    n2b.layer(1);
    assertFalse(n1a.equals(n2b));

    //3.
    Node n3b = new Node(1, 1, 2);
    n3b.order(Element.Order.HIGHWAY);
    n3b.layer(1);
    assertFalse(n1a.equals(n3b));

    //4.
    Node n4b = new Node(1, 2, 2);
    n4b.order(Element.Order.HIGHWAY);
    n4b.layer(1);
    assertFalse(n1a.equals(n4b));

    //5.
    Node n5b = new Node(2, 1, 2);
    n5b.order(Element.Order.HIGHWAY);
    n5b.layer(1);
    assertFalse(n1a.equals(n5b));

    //6.
    Node n6b = new Node(2, 2, 1);
    n6b.order(Element.Order.HIGHWAY);
    n6b.layer(1);
    assertFalse(n1a.equals(n6b));

    //7.
    Node n7b = new Node(2, 2, 2);
    n7b.order(Element.Order.BUILDING);
    n7b.layer(1);
    assertFalse(n1a.equals(n7b));

    //8.
    Node n8b = new Node(2, 2, 2);
    n8b.order(Element.Order.HIGHWAY);
    n8b.layer(0);
    assertFalse(n1a.equals(n8b));

    //9.
    Node n9b = new Node(2, 2, 2);
    n9b.order(Element.Order.BUILDING);
    n9b.layer(0);
    assertFalse(n1a.equals(n9b));

    //10.
    Node n10b = new Node(2, 2, 1);
    n10b.order(Element.Order.BUILDING);
    n10b.layer(0);
    assertFalse(n1a.equals(n10b));

    //11.
    Node n11b = new Node(2, 1, 1);
    n11b.order(Element.Order.BUILDING);
    n11b.layer(0);
    assertFalse(n1a.equals(n11b));

    //12.
    Node n12b = new Node(1, 2, 2);
    n12b.order(Element.Order.HIGHWAY);
    n12b.layer(0);
    assertFalse(n1a.equals(n12b));

    //13.
    Node n13b = new Node(1, 2, 1);
    n13b.order(Element.Order.HIGHWAY);
    n13b.layer(0);
    assertFalse(n1a.equals(n13b));

    //14.
    Node n14b = new Node(2, 1, 2);
    n14b.order(Element.Order.BUILDING);
    n14b.layer(1);
    assertFalse(n1a.equals(n14b));

    //15.
    Node n15b = new Node(1, 2, 1);
    n15b.order(Element.Order.BUILDING);
    n15b.layer(0);
    assertFalse(n1a.equals(n15b));

    //16.
    Node n16b = new Node(1, 1, 2);
    n16b.order(Element.Order.BUILDING);
    n16b.layer(0);
    assertFalse(n1a.equals(n16b));

    //17.
    Node n17b = new Node(1, 1, 1);
    n17b.order(Element.Order.HIGHWAY);
    n17b.layer(0);
    assertFalse(n1a.equals(n17b));

    //18.
    Node n18b = new Node(1, 2, 2);
    n18b.order(Element.Order.BUILDING);
    n18b.layer(0);
    assertFalse(n1a.equals(n18b));

    //19.
    Node n19b = new Node(1, 1, 2);
    n19b.order(Element.Order.HIGHWAY);
    n19b.layer(0);
    assertFalse(n1a.equals(n19b));

    //20.
    Node n20b = new Node(2, 1, 1);
    n20b.order(Element.Order.BUILDING);
    n20b.layer(1);
    assertFalse(n1a.equals(n20b));

  }

}
