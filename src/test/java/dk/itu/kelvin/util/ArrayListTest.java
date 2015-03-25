/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;


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
  private ArrayList<String> a1;

  /**
   * Instance variable of HashSet.
   */
  private HashSet<String> h1;

  /**
   * Instance variable of String.
   */
  private String s1;

  /**
   * Instance variable of String.
   */
  private String s2;

  /**
   * Instance variable of String.
   */
  private String s3;

  /**
   * Initialize an array list with the default initial capacity and 3 strings.
   */
  @Before
  public void before() {
    this.a1 = new ArrayList<>();
    this.h1 = new HashSet<>();
    this.s1 = new String("ex1");
    this.s2 = new String("ex2");
    this.s3 = new String("ex3");
  }

  /**
   * Simple test of addAll method.
   */
  @Test
  public void testAddAllFromCollection() {
    this.h1.add(this.s1);
    this.h1.add(this.s2);
    this.h1.add(this.s3);

    this.a1.addAll(this.h1);

    assertTrue(this.a1.contains(this.s1));
  }

  /**
   * part two of addAll method.
   */
  @Test
  public void testAddAllIfNullOrEmpty() {
    // if the collection is empty it should return false.
    this.a1.addAll(this.h1);
    assertFalse(this.a1.addAll(this.h1));

    // If it adds null it should be false.
    this.h1.add(null);
    assertFalse(this.a1.addAll(this.h1));
  }

  /**
   * Test boolean add element to array list.
   */
  @Test
  public void testAddToArray() {
    // add element to index 0
    assertTrue(this.a1.add(this.s1));

    // add null to array
    assertFalse(this.a1.add(null));

    // the size of the array equals 1.
    assertEquals(1, this.a1.size());
  }

  /**
   * Test boolean add element to specific index in array list.
   */
  @Test
  public void testAddToIndexArray() {
    // add element equals null to index 1
    assertFalse(this.a1.add(1, null));

    // add 3 strings to force arrayList to grow (starts with 2).
    this.a1.add("bo");
    this.a1.add("c");
    this.a1.add("ko");

    // add element equals s1 to index 3
    assertTrue(this.a1.add(3, this.s1));

  }

  /**
   * Test remove node from index 0
   * and return the removed element
   * and update index position.
   */
  @Test
  public void testRemoveIndexFromArray() {
    // add element to index 0.
    this.a1.add(this.s1);

    // the size of the array equals 1.
    assertEquals(1, this.a1.size());

    // removes element at index 0.
    assertEquals(this.s1, this.a1.remove(0));

    // the size of the array equals 0.
    assertEquals(0, this.a1.size());

    // removes index that does not exist.
    assertEquals(null, this.a1.remove(-1));
    assertEquals(null, this.a1.remove(20));

    // add 3 Strings
    this.a1.add(this.s1);
    this.a1.add(this.s2);
    this.a1.add(this.s3);

    // gets access to element at index 0.
    assertEquals(this.s1, this.a1.get(0));
    // remove index 0.
    this.a1.remove(0);
    // gets to the new element at index 0.
    assertEquals(this.s2, this.a1.get(0));
  }

  /**
   * Test remove a specific node.
   */
  @Test
  public void testRemoveElementFromArray() {
    // add element to index 0.
    this.a1.add(this.s1);

    // remove element n1 from array.
    assertTrue(this.a1.remove(this.s1));

    // remove non-existing element from array.
    assertFalse(this.a1.remove(this.s2));

    // does the array contain a non-existing element.
    assertFalse(this.a1.contains(this.s1));
    assertFalse(this.a1.contains(this.s2));

    // size of array equals 0.
    assertEquals(0, this.a1.size());

    // add element to index 0.
    this.a1.add(this.s2);
    // get access to element using index 0.
    assertEquals(this.s2, this.a1.get(0));

  }

  /**
   * Test get element by index.
   */
  @Test
  public void testGetElementFromIndex() {
    // add element to index 0.
    this.a1.add(this.s1);

    // get access to element at index 0.
    assertEquals(this.s1, this.a1.get(0));

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
    this.a1.add(this.s1);

    // does array contain element n1.
    assertTrue(this.a1.contains(this.s1));

    // does array contain non-existing element.
    assertFalse(this.a1.contains(this.s2));

    // does array contain null.
    assertFalse(this.a1.contains(null));
  }

  /**
   * Tests if the iterator return an iterator over the elements of the list.
   */
  @Test
  public void testIterator() {
    // add element to index 0 and 1.
    this.a1.add(this.s1);
    this.a1.add(this.s2);

    // creates an iterator
    Iterator<String> i = this.a1.iterator();

    int count = 0;
    while (i.hasNext()) {
      String s = i.next();
      // while iteratoring over array it must come by s1 and s2.
      assertTrue(s.equals(this.s1) || s.equals(this.s2));
      // count the number of elements
      count++;
    }
    // does local variable count eqauls the size of the array.
    assertEquals(count, this.a1.size());

  }

}
