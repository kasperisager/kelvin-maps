/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// JUnit annotations
import org.junit.Before;
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Test of HashTable class.
 */
public final class HashTableTest {
  /**
   * Instance variable of HashTable.
   */
  private HashTable<String, Integer> h1;

  /**
   * Instance variable of String.
   */
  private String s1;

  /**
   * Instance variable of String.
   */
  private String s2;

  /**
   * Instance variable of integer.
   */
  private Integer i1;

  /**
   * Instance variable of integer.
   */
  private Integer i2;

  /**
   * Initialize a HashTable with the default initial capacity and 2 nodes.
   */
  @Before
  public void before() {
    this.h1 = new HashTable<>();
    this.s1 = "ex1";
    this.s2 = "ex2";
    this.i1 = 10;
    this.i2 = 12;
  }

  /**
   * Test put key/value using return values.
   */
  @Test
  public void testPutKeyAndValue() {
    //if value equals null the method return null.
    assertEquals(null, this.h1.put(this.s1, null));

    // if key equals null the method return null.
    assertEquals(null, this.h1.put(null, this.i1));

    // if key && value equals null the method return null.
    assertEquals(null, this.h1.put(null, null));

    //if key and value are put into the table the method return null.
    assertEquals(null, this.h1.put(this.s1, this.i2));

    // if a new value is referred to an existing key - return old value.
    assertEquals(this.i2, this.h1.put(this.s1, this.i1));
  }

  /**
   * Test remove a value by its key.
   */
  @Test
  public void testRemoveValue() {
    // put 2 sets into the table
    this.h1.put(this.s1, this.i1);
    //this.h1.put(this.s2, i2);

    // remove a value which does not exist.
    assertEquals(null, this.h1.remove(this.i2));

    // remove a value which exist and return the removed key.
    assertEquals(this.i1, this.h1.remove(this.s1));
  }

  /**
   * Test boolean if the table contains a value.
   */
  @Test
  public void testContainsValue() {
    // put 1 set into the table
    this.h1.put(this.s1, this.i1);

    // does the table contain n1.
    assertTrue(this.h1.containsValue(this.i1));

    // does the table contain a non-existing value.
    assertFalse(this.h1.containsValue(this.i2));

    // does the table contain null.
    assertFalse(this.h1.containsValue(null));
  }

  /**
   * Test get a value by key from the table.
   */
  @Test
  public void testGetValueByKey() {
    // put 1 set into the table
    this.h1.put(this.s1, this.i1);

    // tries to get key equals null.
    assertEquals(null, this.h1.get(null));

    // get value from key equals s1.
    assertEquals(this.i1 , this.h1.get(this.s1));

    // get value from a non-existing index.
    assertEquals(null, this.h1.get(this.s2));
  }

  /**
   * Test boolean if table contains a key.
   */
  @Test
  public void testContainsKey() {
    // put 2 sets into the table
    this.h1.put(this.s1, this.i1);

    // does the table contain key equals s1.
    assertTrue(this.h1.containsKey(this.s1));

    // does the table contain a non-existing key.
    assertFalse(this.h1.containsKey(this.s2));

    // does the table contain null.
    assertFalse(this.h1.containsKey(null));
  }

  /**
   * Test return a set of keys contained within the table.
   */
  @Test
  public void testSetOfKeys() {
    // put 2 sets into the table
    this.h1.put(this.s1, this.i1);
    this.h1.put(this.s2, this.i2);

    // the set is not empty.
    assertFalse(this.h1.keySet().isEmpty());

    // does the size of the set equals 2.
    assertEquals(2, this.h1.keySet().size());

    // does the keySet contain key equals s1.
    assertTrue(this.h1.keySet().contains(this.s1));

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
    this.h1.put(this.s1, this.i1);

    // does the size of the set equals 1.
    assertEquals(1 , this.h1.values().size());

    // does the collection contain value equals i1.
    assertTrue(this.h1.values().contains(this.i1));

    // does the collection contain a non-existing value.
    assertFalse(this.h1.values().contains(this.i2));

    // does the collection contain null.
    assertFalse(this.h1.values().contains(null));
  }

  /**
   * Test return a set of entries contained within the map.
   */
  @Test
  public void testEntrySetOfKeysAndValues() {
    // put 2 sets into the table
    this.h1.put(this.s1, this.i1);
    this.h1.put(this.s2, this.i2);

    // the set is not empty.
    assertFalse(this.h1.entrySet().isEmpty());

    // does the size of the set equals 2.
    assertEquals(2, this.h1.entrySet().size());

    // does the set contain null.
    assertFalse(this.h1.entrySet().contains(null));
  }
}
