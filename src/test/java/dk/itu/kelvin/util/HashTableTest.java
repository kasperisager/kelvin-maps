/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import dk.itu.kelvin.model.Node;


// JUnit annotations
import org.junit.Before;
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;


/**
 * Test of AbstractCollection class.
 */
public final class HashTableTest {

  /**
   * Instance variable of HashTable.
   */
  private HashTable h1;

  /**
   * Instance variable of Node.
   */
  private Node n1;

  /**
   * Instance variable of Node.
   */
  private Node n2;

  /**
   * Initialize a HashTable with the default initial capacity and 2 nodes.
   */
  @Before
  public void before() {
    this.h1 = new HashTable();
    this.n1 = new Node(10, 10, 10);
    this.n2 = new Node(12, 12, 12);
  }

  /**
   * Test put key/value using return values.
   */
  @Test
  public void testPutKeyAndValue() {
    //if value equals null the method return null.
    assertEquals(null, this.h1.put(1, null));

    // if key equals null the method return null.
    assertEquals(null, this.h1.put(null, this.n1));

    // if key && value equals null the method return null.
    assertEquals(null, this.h1.put(null, null));

    //if key and value are put into the table the method return null.
    assertEquals(null, this.h1.put(2, this.n1));

    // if a new value are referred to an existing key - return old value.
    assertEquals(this.n1, this.h1.put(2, this.n2));
  }

  /**
   * Test remove a value by its key.
   */
  @Test
  public void testRemoveValue() {
    // put 2 sets into the table
    this.h1.put(1, this.n1);
    this.h1.put(2, this.n2);

    // removes a key which does not exist.
    assertEquals(null, this.h1.remove(3));

    // removes a key which exist and return the removed value.
    assertEquals(this.n1, this.h1.remove(1));
  }

  /**
   * Test boolean if the table contains a value.
   */
  @Test
  public void testContainsValue() {
    // put 1 set into the table
    this.h1.put(1, this.n1);

    // does the table contain n1.
    assertTrue(this.h1.containsValue(this.n1));

    // does the table contain a non-existing value.
    assertFalse(this.h1.containsValue(this.n2));

    // does the table contain null.
    assertFalse(this.h1.containsValue(null));

  }

  /**
   * Test get a value from the table by its key.
   */
  @Test
  public void testGetValue() {
    // put 1 set into the table
    this.h1.put(1, this.n1);

    // tries to get value equals null.
    assertEquals(null, this.h1.get(null));

    // get value from index 1.
    assertEquals(this.n1 , this.h1.get(1));

    // get value from a non-existing index.
    assertEquals(null, this.h1.get(12));
  }

  /**
   * Test boolean if table contains a key.
   */
  @Test
  public void testContainsKey() {
    // put 2 sets into the table
    this.h1.put(1, this.n1);
    this.h1.put(2, this.n2);

    // does the table contain key equals 1.
    assertTrue(this.h1.containsKey(1));

    // does the table contain a non-existing key.
    assertFalse(this.h1.containsKey(3));

    // does the table contain null.
    assertFalse(this.h1.containsKey(null));
  }

  /**
   * Test return a set of keys contained within the table.
   */
  @Test
  public void testSetOfKeys() {
    // put 2 sets into the table
    this.h1.put(1, this.n1);
    this.h1.put(2, this.n2);

    // the set is not empty.
    assertFalse(this.h1.keySet().isEmpty());

    // does the size of the set equals 2.
    assertEquals(2, this.h1.keySet().size());

    // does the keySet contain key equals 1.
    assertTrue(this.h1.keySet().contains(1));

    // doest the keySet contain a non-existing key.
    assertFalse(this.h1.keySet().contains(3));

    // doest the keySet contain null.
    assertFalse(this.h1.values().contains(null));
  }

  /**
   * Test return a collection of the values contained within the table.
   */
  @Test
  public void testCollectionOfValues() {
    // put 1 set into the table
    this.h1.put(1, this.n1);

    // does the size of the set equals 1.
    assertEquals(1 , this.h1.values().size());

    // does the collection contain value equals n1.
    assertTrue(this.h1.values().contains(this.n1));

    // does the collection contain a non-existing value.
    assertFalse(this.h1.values().contains(this.n2));

    // does the collection contain null.
    assertFalse(this.h1.values().contains(null));
  }

  /**
   * Test return a set of entries contained within the map.
   */
  @Test
  public void testEntrySetOfKeysAndValues() {
    // put 2 sets into the table
    this.h1.put(1, this.n1);
    this.h1.put(2, this.n2);

    // the set is not empty.
    assertFalse(this.h1.entrySet().isEmpty());

    // does the size of the set equals 2.
    assertEquals(2, this.h1.entrySet().size());

    // does the set contain null.
    assertFalse(this.h1.entrySet().contains(null));
  }
}


