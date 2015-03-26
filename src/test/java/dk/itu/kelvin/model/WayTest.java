/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JUnit annotations
import org.junit.Test;
import org.junit.Ignore;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

// Utilities
import dk.itu.kelvin.util.ArrayList;
import dk.itu.kelvin.util.List;

/**
 * Way unit tests class.
 */
public final class WayTest {

  /**
   * Test the tagging of ways.
   */
  @Test
  // Tag method does no longer exist. Must update WayTest.
  public void testTag() {
    // Test that a tag is recognized and added to the Way object.
    Way w3 = new Way();
    w3.tag("building", null);
    assertEquals(0, w3.tags().size());
    assertFalse(w3.tags().containsKey("building"));

    // Test that key and value type tags
    // are recognized and added to the Way object.
    w3.tag("highway", "primary");
    assertEquals(1, w3.tags().size());
    assertTrue(w3.tags().containsKey("highway"));
    assertEquals("primary", w3.tags().get("highway"));
  }

  /**
   * Tests if the element order is higher, lower, or equal.
   */
  @Test
  public void testOrder() {
    Way w1 = new Way();
    assertEquals(Way.Order.DEFAULT, w1.order());
    w1.order(Way.Order.NATURAL_WATER);
    assertEquals(Way.Order.NATURAL_WATER, w1.order());

    Way w2 = new Way();
    w2.order(Way.Order.HIGHWAY);

    Way w3 = new Way();
    w3.order(Way.Order.HIGHWAY);

    Way w4 = new Way();
    w4.order(Way.Order.HIGHWAY_MOTORWAY);

    // < 0 first object's order is smaller than the second obejct's order.
    // 0 if there are equal.
    // and > 0 if first is larger than second.
    assertTrue(Way.compare(w1, w3) < 0);
    assertTrue(Way.compare(w4, w2) > 0);
    assertTrue(Way.compare(w2, w3) == 0);
  }

  /**
   * test of layers and test when there is a layer and a order.
   */
  @Test
  public void testLayer() {
    Way w1 = new Way();
    w1.layer(-2);

    Way w2 = new Way();
    w2.layer(0);

    Way w3 = new Way();
    w3.layer(2);

    Way w4 = new Way();
    w4.layer(2);

    assertTrue(Way.compare(w1, w2) < 0);
    assertTrue(Way.compare(w1, w3) < 0);
    assertTrue(Way.compare(w3, w2) > 0);
    assertTrue(Way.compare(w3, w4) == 0);

    Way n5 = new Way();
    n5.order(Way.Order.HIGHWAY);
    n5.layer(0);

    Way n6 = new Way();
    n6.order(Way.Order.HIGHWAY);
    n6.layer(1);

    Way n7 = new Way();
    n7.order(Way.Order.HIGHWAY_MOTORWAY);
    n7.layer(-2);

    Way n8 = new Way();
    n8.order(Way.Order.HIGHWAY_MOTORWAY);
    n8.layer(-2);

    assertTrue(Way.compare(n5, n6) < 0);
    assertTrue(Way.compare(n7, n8) == 0);
  }

  /**
   * Test if the closed method recognizes closed ways and unclosed ways.
   */
  @Test
  public void testClosed() {
    Way w1 = new Way();
    Node n1 = new Node(55.1198149F, 12.1159972F);
    Node n2 = new Node(56.2298149F, 17.2259972F);
    Node n3 = new Node(57.3398149F, 18.3359972F);
    Node n4 = new Node(55.1198149F, 12.1159972F);
    w1.add(n1);
    w1.add(n2);
    w1.add(n3);
    w1.add(n4);

    assertTrue(w1.closed());

    Way w2 = new Way();
    Node n5 = new Node(55.1198149F, 12.1159972F);
    Node n6 = new Node(56.2298149F, 17.2259972F);
    Node n7 = new Node(57.3398149F, 18.3359972F);
    Node n8 = new Node(55.4498149F, 12.4459972F);
    w2.add(n5);
    w2.add(n6);
    w2.add(n7);
    w2.add(n8);

    assertFalse(w2.closed());
  }

  /**
   * StartsIn should only return true if the current
   * * Way starts in the same coordinates as the
   * * node in the parameter ends or starts in.
   */
  @Test
  public void testStartsIn() {
    // Way w1 and Way w2 starts in the same coordinates.
    Way w1 = new Way();
    Node n1 = new Node(55.1198149F, 12.1159972F);
    Node n2 = new Node(56.2298149F, 17.2259972F);
    Node n3 = new Node(57.3398149F, 18.3359972F);
    Node n4 = new Node(55.1198149F, 12.1159972F);
    w1.add(n1);
    w1.add(n2);
    w1.add(n3);
    w1.add(n4);

    Way w2 = new Way();
    Node n5 = new Node(55.1198149F, 12.1159972F);
    Node n6 = new Node(56.2298149F, 17.2259972F);
    Node n7 = new Node(57.3398149F, 18.3359972F);
    Node n8 = new Node(55.4498149F, 12.4459972F);
    w2.add(n5);
    w2.add(n6);
    w2.add(n7);
    w2.add(n8);

    assertTrue(w1.startsIn(w2));
    assertFalse(w1.startsIn(null));

    Way w3 = new Way();
    Node n9 = new Node(59.1198149F, 18.1159972F);
    Node n10 = new Node(56.2298149F, 17.2259972F);
    Node n11 = new Node(57.3398149F, 18.3359972F);
    Node n12 = new Node(55.1198149F, 12.1159972F);
    w3.add(n9);
    w3.add(n10);
    w3.add(n11);
    w3.add(n12);

    // w1 starts in the same coordinates as w3 ends in.
    assertTrue(w1.startsIn(w3));
    // w3 ends in the same coordinates as w1 but does not start in it.
    assertFalse(w3.startsIn(w1));
  }

  /**
   * Tests if one way ends in the same node as the start or end of another way.
   */
  @Test
  public void testEndsIn() {
    // Way w1 and Way w2 starts in the same coordinates.
    Way w1 = new Way();
    Node n1 = new Node(55.1198149F, 12.1159972F);
    Node n2 = new Node(56.2298149F, 17.2259972F);
    Node n3 = new Node(57.3398149F, 18.3359972F);
    w1.add(n1);
    w1.add(n2);
    w1.add(n3);

    Way w2 = new Way();
    Node n4 = new Node(57.3398149F, 18.3359972F);
    Node n5 = new Node(56.2298149F, 17.2259972F);
    Node n6 = new Node(50.3398149F, 10.3359972F);
    w2.add(n4);
    w2.add(n5);
    w2.add(n6);

    // w1 ends in the same coordinates as w2 starts in.
    assertTrue(w1.endsIn(w2));
    // w2 does not end in the same coordinates as w1 starts or ends in.
    assertFalse(w2.endsIn(w1));
    assertFalse(w1.endsIn(null));

    Way w3 = new Way();
    Node n9 = new Node(59.1198149F, 18.1159972F);
    Node n10 = new Node(56.2298149F, 17.2259972F);
    Node n11 = new Node(50.3398149F, 10.3359972F);
    Node n12 = new Node(57.3398149F, 18.3359972F);
    w3.add(n9);
    w3.add(n10);
    w3.add(n11);
    w3.add(n12);

    // w1 ends in the same coordinates as w3 ends in.
    assertTrue(w1.endsIn(w3));
  }

  /**
   * Tests if the method returns all elements added to the list.
   */
  @Test
  public void testNodes() {
    List<Node> nodes = new ArrayList<Node>();
    Node n1 = new Node(59.1198149F, 18.1159972F);
    Node n2 = new Node(56.2298149F, 17.2259972F);
    Node n3 = new Node(50.3398149F, 10.3359972F);
    Node n4 = new Node(57.3398149F, 18.3359972F);

    nodes.add(n1);
    nodes.add(n2);
    nodes.add(n3);
    nodes.add(n4);

    Way w1 = new Way();
    assertEquals(0, w1.nodes().size());

    w1.add(nodes);
    assertEquals(4, w1.nodes().size());
    assertTrue(w1.nodes().contains(n1));
    assertTrue(w1.nodes().contains(n2));
    assertTrue(w1.nodes().contains(n3));
    assertTrue(w1.nodes().contains(n4));
  }

  /**
   * Tests if one way has been appended to another way.
   */
  @Test
  public void testAppend() {
    Way w1 = new Way();
    Way w2 = new Way();

    Node n1 = new Node(59.1198149F, 18.1159972F);
    Node n2 = new Node(56.2298149F, 17.2259972F);
    Node n3 = new Node(50.3398149F, 10.3359972F);
    Node n4 = new Node(57.3398149F, 18.3359972F);

    w1.add(n1);
    w1.add(n2);
    w2.add(n3);
    w2.add(n4);

    assertEquals(2, w1.nodes().size());
    w1.append(null);
    assertEquals(2, w1.nodes().size());

    w1.append(w2);
    assertEquals(4, w1.nodes().size());
    assertTrue(w1.nodes().contains(n1));
    assertTrue(w1.nodes().contains(n2));
    assertTrue(w1.nodes().contains(n3));
    assertTrue(w1.nodes().contains(n4));
  }

  /**
   * Test wether two roads intersect.
   */
  @Ignore("Intersection detection has been removed")
  @Test
  public void testIntersect() {
    Way w1 = new Way();
    Node n1 = new Node(55.6758261F, 12.5831593F);
    Node n2 = new Node(55.6753869F, 12.5850681F);
    Node n3 = new Node(55.6752059F, 12.5849356F);
    Node n4 = new Node(55.6756510F, 12.5830358F);
    w1.add(n1);
    w1.add(n2);
    w1.add(n3);
    w1.add(n4);

    Way w2 = new Way();
    Node n5 = new Node(55.6758241F, 12.5831493F);
    Node n6 = new Node(55.6753869F, 12.5850681F);
    Node n7 = new Node(55.6752059F, 12.5849356F);
    Node n8 = new Node(55.6756510F, 12.5830358F);
    w2.add(n5);
    w2.add(n6);
    w2.add(n7);
    w2.add(n8);

    Way w3 = new Way();
    Node n9 = new Node(50.6758241F, 10.5831493F);
    Node n10 = new Node(50.6753869F, 10.5850681F);
    Node n11 = new Node(50.6752059F, 10.5849356F);
    Node n12 = new Node(50.6756510F, 10.5830358F);
    w3.add(n9);
    w3.add(n10);
    w3.add(n11);
    w3.add(n12);

    // assertFalse(w1.intersects(w3));
    // assertFalse(w2.intersects(w3));
  }

  /**
   * Tests whether a way is sorrounded by another way.
   */
  @Test
  public void testContains() {
    Way w1 = new Way();
    Node n1 = new Node(56.6758261F, 12.5831593F);
    Node n2 = new Node(52.6753869F, 12.5831593F);
    Node n3 = new Node(52.6753869F, 18.5849356F);
    Node n4 = new Node(56.6758261F, 18.5830358F);
    w1.add(n1);
    w1.add(n2);
    w1.add(n3);
    w1.add(n4);

    Way w2 = new Way();
    Node n5 = new Node(55.6758241F, 12.1831493F);
    Node n6 = new Node(53.6753869F, 12.1831493F);
    Node n7 = new Node(53.6753869F, 15.5849356F);
    Node n8 = new Node(55.6758241F, 15.5849356F);
    w2.add(n5);
    w2.add(n6);
    w2.add(n7);
    w2.add(n8);

    Way w3 = new Way();
    Node n9 = new Node(55.6758241F, 11.5831493F);
    Node n10 = new Node(53.6753869F, 11.5850681F);
    Node n11 = new Node(53.6753869F, 11.5849356F);
    Node n12 = new Node(55.6758241F, 11.5830358F);
    w3.add(n9);
    w3.add(n10);
    w3.add(n11);
    w3.add(n12);

    assertTrue(w1.contains(w2));
    assertFalse(w1.contains(w3));
  }
}
