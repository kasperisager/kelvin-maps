/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.HashMap;
import java.util.Map;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ternarySearchTreeTest {
  /**
   * Test if a tst is empty or not.
   */
  @Test
  public void testIsEmpty() {
    Map<String, Integer> nullElements = new HashMap<>();
    Map<String, Integer> elements = new HashMap<>();
    elements.put("Dabra", 4);
    elements.put("Cabra", 3);
    elements.put("Aabra", 1);
    elements.put("Babra", 2);

    TernarySearchTree tst = new TernarySearchTree(elements);
    TernarySearchTree tstNull = new TernarySearchTree(nullElements);

    assertTrue(tstNull.isEmpty());
    assertFalse(tst.isEmpty());
  }


  /**
   * Test getting the mapped element out of a ternary search tree.
   * Indirectly tests: Initialize(), partition() and get().
   */
  @Test
  public void testGet() {
    Map<String, Integer> nullElements = new HashMap<>();
    Map<String, Integer> elements = new HashMap<>();
    elements.put("Dabra", 4);
    elements.put("Cabra", 3);
    elements.put("Aabra", 1);
    elements.put("Babra", 2);

    TernarySearchTree tst = new TernarySearchTree(elements);
    TernarySearchTree tstNull = new TernarySearchTree(nullElements);
    assertEquals(tstNull.get("Aabra"), null);
    assertEquals(tst.get(""), null);
    assertEquals(tst.get(null), null);
    assertEquals(tst.get("Aabra"), 1);
  }

  /**
   *
   */
  @Test
  public void testContains() {
    Map<String, Integer> elements = new HashMap<>();
    elements.put("Dabra", 4);
    elements.put("Cabra", 3);
    elements.put("Aabra", 1);
    elements.put("Babra", 2);

    String nullKey = null;
    String key = "bra";
    String key2 = "Cabra";

    TernarySearchTree tst = new TernarySearchTree(elements);

    assertFalse(tst.contains(nullKey));
    assertFalse(tst.contains(""));
    assertFalse(tst.contains(key));
    assertTrue(tst.contains(key2));
  }

  /**
   *
   */
  @Test
  public void testPut() {
    Map<String, Integer> elements = new HashMap<>();
    elements.put("Amagerbrogade 4", 1);
    elements.put("Amagerbrogade 12", 2);
    elements.put("Amager Torv", 3);
    elements.put("Gothersgade", 4);
    elements.put("Vesterbrogade", 5);

    TernarySearchTree tst = new TernarySearchTree(elements);

    tst.put("Vejle", 99);
    assertTrue(tst.contains("Vejle"));
    assertTrue(tst.size() == 6);

    tst.put("Vejle", 99);
    assertTrue(tst.size() == 6);

    tst.put(null, 11);
    assertTrue(tst.size() == 6);

    tst.put("Yoma", null);
    assertTrue(tst.size() == 6);

    tst.put("", 999);
    assertTrue(tst.size() == 6);
  }

  /**
   *
   */
  @Test
  public void testRemove() {
  }

  /**
   *
   */
  @Test
  public void testClear() {
  }

  /**
   *
   */
  @Test
  public void testSearch() {
    Map<String, Integer> elements = new HashMap<>();
    elements.put("Amagerbrogade 4", 1);
    elements.put("Amagerbrogade 12", 2);
    elements.put("Amager Torv", 3);
    elements.put("Gothersgade", 4);
    elements.put("Vesterbrogade", 5);

    Map<String, Integer> nullElements = new HashMap<>();

    TernarySearchTree tst = new TernarySearchTree(elements);

    assertTrue(tst.search("Amager").size() == 3);
    assertTrue(tst.search("Amager").containsKey("Amagerbrogade 4"));
    assertTrue(tst.search("Amager").containsValue(1));
    assertTrue(tst.search("Amager").containsKey("Amagerbrogade 12"));
    assertTrue(tst.search("Amager").containsValue(2));
    assertTrue(tst.search("Amager").containsKey("Amager Torv"));
    assertTrue(tst.search("Amager").containsValue(3));
    assertFalse(tst.search("Amager").containsKey("Gothersgade"));
    assertFalse(tst.search("Amager").containsValue(4));
    assertFalse(tst.search("Amager").containsKey("Vesterbrogade"));
    assertFalse(tst.search("Amager").containsValue(5));

    assertTrue(tst.search(null).size() == 0);

    TernarySearchTree nullTst = new TernarySearchTree(nullElements);
    assertTrue(nullTst.search("Amager").size() == 0);
  }
}
