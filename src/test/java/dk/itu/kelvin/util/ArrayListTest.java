/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import dk.itu.kelvin.model.Node;


import java.util.Iterator;

// JUnit annotations
import org.junit.Before;
import org.junit.Test;


// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;


/**
 * Test of the ArrayList class.
 */
public final class ArrayListTest {

  /**
   * Instance variable of ArrayList.
   */
  private ArrayList a1;

  /**
   * Instance variable of Nodes.
   */
  private Node n1;

  /**
   * Instance variable of Nodes.
   */
  private Node n2;

  /**
   * Instance variable of Nodes.
   */
  private Node n3;

  /**
   * Initialize an array list with the default initial capacity and 3 nodes.
   */
  @Before
  public void before() {
    this.a1 = new ArrayList();
    this.n1 = new Node(10, 10, 10);
    this.n2 = new Node(10, 12, 12);
    this.n3 = new Node(13, 12, 12);
  }

  /**
   * Test boolean add element to array list.
   */
  @Test
  public void testAddToArray() {
    assertTrue(this.a1.add(10));
    assertFalse(this.a1.add(null));
    this.a1.add(null);
    assertEquals(1, this.a1.size());
  }

  /**
   * Test remove node from index 0
   * and return the removed element
   * and update index position.
   */
  @Test
  public void testRemoveIndexFromArray() {
    this.a1.add(this.n1);
    assertEquals(1, this.a1.size());
    assertEquals(this.n1, this.a1.remove(0));
    assertEquals(0, this.a1.size());

    assertEquals(null, this.a1.remove(-1));
    assertEquals(null, this.a1.remove(20));


    this.a1.add(this.n1);
    this.a1.add(this.n2);
    this.a1.add(this.n3);
    assertEquals(this.n1, this.a1.get(0));
    this.a1.remove(0);
    assertEquals(this.n2, this.a1.get(0));



  }

  /**
   * Test remove a specific node.
   */
  @Test
  public void testRemoveElementFromArray() {
    this.a1.add(this.n1);
    assertTrue(this.a1.remove(this.n1));
    assertFalse(this.a1.remove(this.n2));
    assertFalse(this.a1.contains(this.n1));
    assertFalse(this.a1.contains(this.n2));
    assertEquals(0, this.a1.size());
    this.a1.add(this.n2);
    assertEquals(this.n2, this.a1.get(0));

  }

  /**
   * Test get element by index.
   */
  @Test
  public void testGetElementFromIndex() {
    this.a1.add(this.n1);
    assertEquals(this.n1, this.a1.get(0));
    assertEquals(null, this.a1.get(-1));
    assertEquals(null, this.a1.get(100000));
  }

  /**
   * Tests if array list contains a specific node.
   */
  @Test
  public void testArrayContainsElement() {
    this.a1.add(this.n1);
    assertTrue(this.a1.contains(this.n1));
    assertFalse(this.a1.contains(this.n2));
    assertFalse(this.a1.contains(null));
  }

  /**
   * Tests if the iterator return an iterator over the elements of the list.
   */
  @Test
  public void testIterator() {
    this.a1.add(this.n1);
    this.a1.add(this.n2);

    Iterator<Node> i = this.a1.iterator();

    int count = 0;
    while (i.hasNext()) {
      Node n = i.next();
      assertTrue(n.equals(this.n1) || n.equals(this.n2));
      count++;
    }
    assertEquals(count, this.a1.size());

  }

}
