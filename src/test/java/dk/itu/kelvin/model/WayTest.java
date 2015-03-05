/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Way unit tests.
 */
public final class WayTest {
  public WayTest() {
    Way w1 = new Way(25838049);
    Node n1 = new Node(1712550893, (float)55.1198149, (float)12.1159972);
    Node n2 = new Node(1712550893, (float)56.2298149, (float)16.2259972);
    Node n3 = new Node(1712550893, (float)55.3398149, (float)12.3359972);
    w1.node(n1);
    w1.node(n2);
    w1.node(n3);

    Way w2 = new Way(31838449);
    Node n4 = new Node(1712550893, (float)55.1198149, (float)12.1159972);
    Node n5 = new Node(1712550893, (float)55.2298149, (float)12.2259972);
    Node n6 = new Node(1712550893, (float)55.3398149, (float)12.3359972);
    w2.node(n4);
    w2.node(n5);
    w2.node(n6);
  }

  /**
   * Test the tagging of ways.
   */
  @Test
  public void testTag() {
    // Test that unspecified tags are not recognized.
    Way w3 = new Way(25338049);
    w3.tag("SuperBrugsen", null);
    assertEquals(0, w3.tags().size());

    // Test that a tag is recognized and added to the Way object.
    w3.tag("building", null);
    assertEquals(1, w3.tags().size());
    assertTrue(w3.tags().containsKey("building"));

    // Test that key and value type tags are recognized and added to the Way object.
    w3.tag("highway", "primary");
    assertEquals(2, w3.tags().size());
    assertTrue(w3.tags().containsKey("highway"));
    assertEquals("primary", w3.tags().get("highway"));
  }

}
