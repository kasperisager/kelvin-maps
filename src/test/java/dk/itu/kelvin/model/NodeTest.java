/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

// JUnit annotations
// JUnit assertions

/**
 * {@link Node} test suite.
 */
public final class NodeTest {
  /**
   * Test initialization of a node object with an id and an x-/y-coordinate.
   */
  @Test
  public void testInitialization() {
    Node n1 = new Node(15, 25);
    assertEquals(15, n1.x(), 0);
    assertEquals(25, n1.y(), 0);
  }

  /**
   * Tests the tag class with different keys and values.
   */
  @Test
  public void testTag() {
    Node n1 = new Node(1, 1);

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
    assertEquals(null, tags.get(""));

    // if the key is null.
    n1.tag(null, "ddd");
    assertEquals(null, tags.get(null));

    //if the value is null.
    n1.tag("eee", null);
    assertEquals(null, tags.get("eee"));
  }


  @Test
  public void testRender() {
    /*Node n1 = new Node(1, 2);
    n1.tag("amenity", "pub");
    n1.tag("name", "something");

    n1.render();

    assertTrue(testLabel.getText().equals("\uf26a"));
    assertTrue(testLabel.getLayoutX() == 1);
    assertTrue(testLabel.getLayoutY() == 2);
    assertTrue(testLabel.getStyleClass().contains("icon"));*/

  }
}
