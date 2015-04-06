/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JUnit annotations
import org.junit.Test;

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

    assertTrue(w1.isClosed());

    Way w2 = new Way();
    Node n5 = new Node(55.1198149F, 12.1159972F);
    Node n6 = new Node(56.2298149F, 17.2259972F);
    Node n7 = new Node(57.3398149F, 18.3359972F);
    Node n8 = new Node(55.4498149F, 12.4459972F);
    w2.add(n5);
    w2.add(n6);
    w2.add(n7);
    w2.add(n8);

    assertFalse(w2.isClosed());
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
}
