/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Way;

import java.util.Iterator;

// JUnit annotations
import org.junit.Before;
import org.junit.Test;


// JUnit assertions
import static org.junit.Assert.*;


public final class ArrayListTest {

  private ArrayList a1;
  private Node n1;
  private Node n2;
  private Node n3;

  /**
   * Initialize an array list with the default initial capacity + 2 nodes
   */
  @Before
  public void before() {
    a1 = new ArrayList();
    n1 = new Node(10,10,10);
    n2 = new Node(10,12,12);
    n3 = new Node(13,12,12);
  }

  /**
   * Test boolean add element to array list
   */
  @Test
  public void testAddToArray() {
    assertTrue(a1.add(10));
    assertFalse(a1.add(null));
    a1.add(null);
    assertEquals(1, a1.size());
  }

  /**
   * Test remove node from index 0 and return the removed element.
   */
  @Test
  public void testRemoveIndexFromArray() {
    a1.add(n1);
    assertEquals(1, a1.size());
    assertEquals(n1, a1.remove(0));
    assertEquals(0, a1.size());

    assertEquals(null, a1.remove(-1));
    assertEquals(null,a1.remove(20));

    /*a1.add(n1);
    a1.add(n2);
    a1.add(n3);
    assertEquals(n1, a1.get(0));
    a1.remove(0);
    assertEquals(n2, a1.get(0));*/



  }

  /**
   * Test remove a specific node
   */
  @Test
  public void testRemoveElementFromArray() {
    a1.add(n1);
    assertTrue(a1.remove(n1));
    assertFalse(a1.remove(n2));
    assertFalse(a1.contains(n1));
    assertFalse(a1.contains(n2));
    assertEquals(0, a1.size());
    a1.add(n2);
    assertEquals(n2,a1.get(0));

  }

  /**
   * Test get element using index
   */
  @Test
  public void testGetElementFromIndex() {
    a1.add(n1);
    assertEquals(n1, a1.get(0));
    assertEquals(null, a1.get(-1));
    assertEquals(null, a1.get(100000));
  }

  /**
   * Test c
   */
  @Test
  public void testArrayContainsElement() {
    a1.add(n1);
    assertTrue(a1.contains(n1));
    assertFalse(a1.contains(n2));
    assertFalse(a1.contains(null));
  }

  /**
   *
   */
  @Test
  public void testIterator() {
    a1.add(n1);
    a1.add(n2);

    Iterator<Node> i = a1.iterator();

    int count = 0;
    while (i.hasNext()) {
      Node n = i.next();
      assertTrue(n.equals(n1) || n.equals(n2));
      count++;
    }
    assertEquals(count, a1.size());

  }

}
