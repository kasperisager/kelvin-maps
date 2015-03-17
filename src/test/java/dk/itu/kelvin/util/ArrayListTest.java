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
 * Test of ArrayList class.
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
    // add element to index 0
    assertTrue(this.a1.add(10));

    // add null to array
    assertFalse(this.a1.add(null));

    // the size of the array equals 1.
    assertEquals(1, this.a1.size());
  }

  /**
   * Test remove node from index 0
   * and return the removed element
   * and update index position.
   */
  @Test
  public void testRemoveIndexFromArray() {
    // add element to index 0.
    this.a1.add(this.n1);

    // the size of the array equals 1.
    assertEquals(1, this.a1.size());

    // removes element at index 0.
    assertEquals(this.n1, this.a1.remove(0));

    // the size of the array equals 0.
    assertEquals(0, this.a1.size());

    // removes index that does not exist.
    assertEquals(null, this.a1.remove(-1));
    assertEquals(null, this.a1.remove(20));

    // add 3 values
    this.a1.add(this.n1);
    this.a1.add(this.n2);
    this.a1.add(this.n3);

    // gets access to element at index 0.
    assertEquals(this.n1, this.a1.get(0));
    // remove index 0.
    this.a1.remove(0);
    // gets to the new element at index 0.
    assertEquals(this.n2, this.a1.get(0));
  }

  /**
   * Test remove a specific node.
   */
  @Test
  public void testRemoveElementFromArray() {
    // add element to index 0.
    this.a1.add(this.n1);

    // remove element n1 from array.
    assertTrue(this.a1.remove(this.n1));

    // remove non-existing element from array.
    assertFalse(this.a1.remove(this.n2));

    // does the array contain a non-existing element.
    assertFalse(this.a1.contains(this.n1));
    assertFalse(this.a1.contains(this.n2));

    // size of array equals 0.
    assertEquals(0, this.a1.size());

    // add element to index 0.
    this.a1.add(this.n2);
    // get access to element using index 0.
    assertEquals(this.n2, this.a1.get(0));

  }

  /**
   * Test get element by index.
   */
  @Test
  public void testGetElementFromIndex() {
    // add element to index 0.
    this.a1.add(this.n1);

    // get access to element at index 0.
    assertEquals(this.n1, this.a1.get(0));

    // get access to element by a non-existing index.
    assertEquals(null, this.a1.get(-1));
    assertEquals(null, this.a1.get(100000));
  }

  /**
   * Tests if array list contains a specific node.
   */
  @Test
  public void testArrayContainsElement() {
    // add element to index 0.
    this.a1.add(this.n1);

    // does array contain element n1.
    assertTrue(this.a1.contains(this.n1));

    // does array contain non-existing element.
    assertFalse(this.a1.contains(this.n2));

    // does array contain null.
    assertFalse(this.a1.contains(null));
  }

  /**
   * Tests if the iterator return an iterator over the elements of the list.
   */
  @Test
  public void testIterator() {
    // add element to index 0 and 1.
    this.a1.add(this.n1);
    this.a1.add(this.n2);

    // creates an iterator
    Iterator<Node> i = this.a1.iterator();

    int count = 0;
    while (i.hasNext()) {
      Node n = i.next();
      // while iteratoring over array it must come by n1 and n2.
      assertTrue(n.equals(this.n1) || n.equals(this.n2));
      // count the number of elements
      count++;
    }
    // does local variable count eqauls the size of the array.
    assertEquals(count, this.a1.size());

  }

}
