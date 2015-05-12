/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// Utilities
import dk.itu.kelvin.util.Graph;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;


/**
 * {@link Way} test suite.
 */
public final class WayTest {
  /**
   * Test initialization of a way object with an id and an x-/y-coordinate.
   */
  @Test
  public void testInitialization() {
    Way w1 = new Way();
    Node n1 = new Node(2, 2);
    Node n2 = new Node(3, 4);
    Node n3 = new Node(2, 3);
    Node n4 = new Node(5, 6);

    w1.add(n1);
    w1.add(n2);
    w1.add(n3);
    w1.add(n4);

    // check all min and max are true.
    assertTrue(2 == w1.minX());
    assertTrue(2 == w1.minY());
    assertTrue(5 == w1.maxX());
    assertTrue(6 == w1.maxY());
  }

    /**
     * Test add a list of empty nodes or list equal null.
     */
  @Test
  public void addNodes() {
    Way way = new Way();
    List<Node> list = new ArrayList<>();

    // add empty list
    way.add(list);
    assertTrue(0 == way.nodes().size());

    // add list = null
    list = null;
    way.add(list);
    assertTrue(0 == way.nodes().size());
  }

  /**
   * Test add node which equal null.
   */
  @Test
  public void addNode() {
    Way way = new Way();
    Node n = null;
    way.add(n);
    assertTrue(0 == way.nodes().size());
  }

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

    // test if start == null or end == null.
    Way w3 = new Way();
    //assertFalse(w3.isClosed());

  }

  /**
   * Test if the open method recognizes open ways.
   */
  @Test
  public void testOpen() {
    Way w1 = new Way();
    Node n5 = new Node(55.1198149F, 12.1159972F);
    Node n6 = new Node(56.2298149F, 17.2259972F);
    Node n7 = new Node(57.3398149F, 18.3359972F);
    Node n8 = new Node(55.4498149F, 12.4459972F);
    w1.add(n5);
    w1.add(n6);
    w1.add(n7);
    w1.add(n8);

    assertTrue(w1.isOpen());
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

    // test if start.end == null or way.start() == null.
    Way w4 = new Way();
    assertFalse(w4.startsIn(w3));
    assertFalse(w3.startsIn(w4));

  }

  /**
   * Test if one way ends in the same node as the start or end of another way.
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
    Node n9 = new Node(57.3398149F, 18.3359972F);
    Node n10 = new Node(56.2298149F, 17.2259972F);
    Node n11 = new Node(50.3398149F, 10.3359972F);
    Node n12 = new Node(59.1198149F, 18.1159972F);
    w3.add(n9);
    w3.add(n10);
    w3.add(n11);
    w3.add(n12);

    // w1 ends in the same coordinates as w3 ends in.
    assertTrue(w1.endsIn(w3));
    assertTrue(w1.end().x() == w3.start().x());

    // test if w4.end == null
    Way w4 = new Way();
    assertFalse(w4.endsIn(w3));
  }

  /**
   * Test if the method returns all elements added to the list.
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
   * Test if one way has been appended to another way.
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
   * Test the correct weight between nodes.
   */
  @Test
  public void testWeight() {
    Way w1 = new Way();
    w1.tag("maxspeed", "30");

    // test weight if n1 and n2 are null
    Node n1 = null;
    Node n2 = null;

    w1.add(n1);
    w1.add(n2);

    Properties prop = new Properties();
    prop.put("bicycle", "no");
    assertTrue(Double.POSITIVE_INFINITY == w1.weight(n1, n2, prop));

    // weight are != null
    Node n3 = new Node(1, 1);
    Node n4 = new Node(4, 1);

    w1.add(n3);
    w1.add(n4);

    assertTrue(0.1 == w1.weight(n3, n4, prop));
  }

  /**
   * Test ways directions - oneway or both ways.
   */
  @Test
  public void testDirectionOfWay() {
    // way is oneway
    Way w1 = new Way();
    w1.tag("oneway", "yes");

    Properties prop = new Properties();
    prop.put("bicycle", "no");
    assertTrue(Graph.Direction.UNI == w1.direction(prop));

    // way is not oneway
    Way w2 = new Way();
    Properties prop2 = new Properties();
    prop2.put("bicycle", "yes");
    assertTrue(Graph.Direction.BI == w2.direction(prop2));
  }

}
