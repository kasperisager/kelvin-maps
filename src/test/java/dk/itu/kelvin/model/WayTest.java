/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// Java utilities
import java.util.List;
import java.util.ArrayList;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Way unit tests.
 */
public final class WayTest {
  public WayTest() {
    Way w2 = new Way(21838449);
    Node n4 = new Node(1112550893, (float)57.1198149, (float)18.1159972);
    Node n5 = new Node(1212550893, (float)58.2298149, (float)16.2259972);
    Node n6 = new Node(1312550893, (float)59.3398149, (float)15.3359972);
    w2.node(n4);
    w2.node(n5);
    w2.node(n6);
  }

  /**
   * Test the tagging of ways.
   */
  @Test
  public void testTag() {
    // Test that a tag is recognized and added to the Way object.
    Way w3 = new Way(25338049);
    w3.tag("building", null);
    assertEquals(1, w3.tags().size());
    assertTrue(w3.tags().containsKey("building"));

    // Test that key and value type tags are recognized and added to the Way object.
    w3.tag("highway", "primary");
    assertEquals(2, w3.tags().size());
    assertTrue(w3.tags().containsKey("highway"));
    assertEquals("primary", w3.tags().get("highway"));
  }

  /**
   * Test if the closed method recognizes closed ways and unclosed ways.
   */
  @Test
  public void testClosed() {
    Way w1 = new Way(15838049);
    Node n1 = new Node(1112550893, (float)55.1198149, (float)12.1159972);
    Node n2 = new Node(1212550893, (float)56.2298149, (float)17.2259972);
    Node n3 = new Node(1312550893, (float)57.3398149, (float)18.3359972);
    Node n4 = new Node(1312550893, (float)55.1198149, (float)12.1159972);
    w1.node(n1);
    w1.node(n2);
    w1.node(n3);
    w1.node(n4);

    assertTrue(w1.closed());

    Way w2 = new Way(15838049);
    Node n5 = new Node(1112550893, (float)55.1198149, (float)12.1159972);
    Node n6 = new Node(1212550893, (float)56.2298149, (float)17.2259972);
    Node n7 = new Node(1312550893, (float)57.3398149, (float)18.3359972);
    Node n8 = new Node(1312550893, (float)55.4498149, (float)12.4459972);
    w2.node(n5);
    w2.node(n6);
    w2.node(n7);
    w2.node(n8);

    assertFalse(w2.closed());
  }

  /**
   * StartsIn should only return true if the current
   * * Way starts in the same coordinates as the node in the parameter ends or starts in.
   */
  @Test
  public void testStartsIn() {
    // Way w1 and Way w2 starts in the same coordinates.
    Way w1 = new Way(15838049);
    Node n1 = new Node(1112550893, (float)55.1198149, (float)12.1159972);
    Node n2 = new Node(1212550893, (float)56.2298149, (float)17.2259972);
    Node n3 = new Node(1312550893, (float)57.3398149, (float)18.3359972);
    Node n4 = new Node(1412550893, (float)55.1198149, (float)12.1159972);
    w1.node(n1);
    w1.node(n2);
    w1.node(n3);
    w1.node(n4);

    Way w2 = new Way(15838049);
    Node n5 = new Node(1512550893, (float)55.1198149, (float)12.1159972);
    Node n6 = new Node(1612550893, (float)56.2298149, (float)17.2259972);
    Node n7 = new Node(1712550893, (float)57.3398149, (float)18.3359972);
    Node n8 = new Node(1812550893, (float)55.4498149, (float)12.4459972);
    w2.node(n5);
    w2.node(n6);
    w2.node(n7);
    w2.node(n8);

    assertTrue(w1.startsIn(w2));
    assertFalse(w1.startsIn(null));

    Way w3 = new Way(15838049);
    Node n9 = new Node(1512550893, (float)59.1198149, (float)18.1159972);
    Node n10 = new Node(1612550893, (float)56.2298149, (float)17.2259972);
    Node n11 = new Node(1712550893, (float)57.3398149, (float)18.3359972);
    Node n12 = new Node(1112550893, (float)55.1198149, (float)12.1159972);
    w3.node(n9);
    w3.node(n10);
    w3.node(n11);
    w3.node(n12);

    // w1 starts in the same coordinates as w3 ends in.
    assertTrue(w1.startsIn(w3));
    // w3 ends in the same coordinates as w1 but does not start in it.
    assertFalse(w3.startsIn(w1));
  }

  @Test
  public void testEndsIn() {
    // Way w1 and Way w2 starts in the same coordinates.
    Way w1 = new Way(15838049);
    Node n1 = new Node(1112550893, (float)55.1198149, (float)12.1159972);
    Node n2 = new Node(1212550893, (float)56.2298149, (float)17.2259972);
    Node n3 = new Node(1312550893, (float)57.3398149, (float)18.3359972);
    w1.node(n1);
    w1.node(n2);
    w1.node(n3);

    Way w2 = new Way(15838049);
    Node n4 = new Node(1112550893, (float)57.3398149, (float)18.3359972);
    Node n5 = new Node(1612550893, (float)56.2298149, (float)17.2259972);
    Node n6 = new Node(1712550893, (float)50.3398149, (float)10.3359972);
    w2.node(n4);
    w2.node(n5);
    w2.node(n6);

    // w1 ends in the same coordinates as w2 starts in.
    assertTrue(w1.endsIn(w2));
    // w2 does not end in the same coordinates as w1 starts or ends in.
    assertFalse(w2.endsIn(w1));
    assertFalse(w1.endsIn(null));

    Way w3 = new Way(15838049);
    Node n9 = new Node(1512550893, (float)59.1198149, (float)18.1159972);
    Node n10 = new Node(1612550893, (float)56.2298149, (float)17.2259972);
    Node n11 = new Node(1712550893, (float)50.3398149, (float)10.3359972);
    Node n12 = new Node(1112550893, (float)57.3398149, (float)18.3359972);
    w3.node(n9);
    w3.node(n10);
    w3.node(n11);
    w3.node(n12);

    // w1 ends in the same coordinates as w3 ends in.
    assertTrue(w1.endsIn(w3));
  }

  @Test
  public void testNodes() {
    List<Node> nodes = new ArrayList<Node>();
    Node n1 = new Node(1512550893, (float)59.1198149, (float)18.1159972);
    Node n2 = new Node(1612550893, (float)56.2298149, (float)17.2259972);
    Node n3 = new Node(1712550893, (float)50.3398149, (float)10.3359972);
    Node n4 = new Node(1112550893, (float)57.3398149, (float)18.3359972);

    nodes.add(n1);
    nodes.add(n2);
    nodes.add(n3);
    nodes.add(n4);

    Way w1 = new Way(1112550893);
    w1.nodes(null);
    assertEquals(0, w1.nodes().size());

    w1.nodes(nodes);
    assertEquals(4, w1.nodes().size());
    assertTrue(w1.nodes().contains(n1));
    assertTrue(w1.nodes().contains(n2));
    assertTrue(w1.nodes().contains(n3));
    assertTrue(w1.nodes().contains(n4));
  }

  @Test
  public void testAppend() {
    Way w1 = new Way(1112550893);
    Way w2 = new Way(1112550893);

    Node n1 = new Node(1512550893, (float)59.1198149, (float)18.1159972);
    Node n2 = new Node(1612550893, (float)56.2298149, (float)17.2259972);
    Node n3 = new Node(1712550893, (float)50.3398149, (float)10.3359972);
    Node n4 = new Node(1112550893, (float)57.3398149, (float)18.3359972);

    w1.node(n1);
    w1.node(n2);
    w2.node(n3);
    w2.node(n4);

    assertEquals(2, w1.nodes().size());

    w1.append(w2);
    assertEquals(4, w1.nodes().size());
    assertTrue(w1.nodes().contains(n1));
    assertTrue(w1.nodes().contains(n2));
    assertTrue(w1.nodes().contains(n3));
    assertTrue(w1.nodes().contains(n4));
  }

}
