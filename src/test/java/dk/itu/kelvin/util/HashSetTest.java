/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Iterator;

// JUnit annotations
import org.junit.Before;
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * UnitTest of the HashSet class which is part of the util classes.
 */
public final class HashSetTest {
  /**
   * A HashSet which we use in almost every testcases.
   */
  private HashSet<String> h1;

  /**
   * an ArrayList to use in the addAll method.
   */
  private ArrayList<String> a1;

  /**
   * A HashSet and an ArrayList, that we use more than once.
   */
  @Before
  public void before() {
    this.h1 = new HashSet<>();
    this.a1 = new ArrayList<>();
  }

  /**
   * Test of add method. not done.
   */
  @Test
  public void testAdd() {
    this.h1.add("ex1");
    this.h1.add("ex2");

    assertFalse(this.h1.add("ex1"));
    assertTrue(this.h1.contains("ex1"));
    assertFalse(this.h1.add(null));

    // Add the same element again.
    assertFalse(this.h1.add("ex1"));
  }

  /**
   * Simple test of addAll method.
   */
  @Test
  public void testAddAllFromCollection() {
    this.a1.add("ex1");
    this.a1.add("ex2");
    this.a1.add("ex3");

    this.h1.addAll(this.a1);

    assertTrue(this.h1.contains("ex3"));
  }

  /**
   * part two of addAll method.
   */
  @Test
  public void testAddAllIfNullOrEmpty() {
    // if the collection is empty it should return false.
    this.h1.addAll(this.a1);
    assertFalse(this.h1.addAll(this.a1));

    // If it adds null it should be false.
    this.a1.add(null);
    assertFalse(this.h1.addAll(this.a1));
  }

  /**
   * Here we test the remove method.
   */
  @Test
  public void testRemove() {
    //We add two elements and check if the hashSet contains them.
    this.h1.add("ex1");
    this.h1.add("ex2");
    assertTrue(this.h1.contains("ex1") && this.h1.contains("ex2"));
    assertEquals(2, this.h1.size());

    // We remove one of the elements,
    // and asserts that it doesn't contains it anymore.
    this.h1.remove("ex1");
    assertFalse(this.h1.contains("ex1"));
    assertEquals(1, this.h1.size());

    // we then try to remove the same element again,
    // to see if it returns false, as it should.
    assertFalse(this.h1.remove("ex1"));
    assertEquals(1, this.h1.size());
  }

  /**
   * Test of HashSet constructors when it s given a collection.
   */
  @Test
  public void testHashSetCollection() {
    this.a1.add("ex1");
    this.a1.add("ex2");
    this.a1.add("ex3");

    HashSet<String> h2 = new HashSet<>(this.a1);

    assertTrue(h2.contains("ex1"));
    assertTrue(h2.contains("ex2"));
    assertTrue(h2.contains("ex3"));

    assertEquals(3, h2.size());
  }

  /**
   * Test of the Iterator.
   */
  @Test
  public void testIterator() {
    this.h1.add("ex1");
    this.h1.add("ex2");

    Iterator<String> i = this.h1.iterator();

    int count = 0;
    while (i.hasNext()) {
      String n = i.next();
      assertTrue(n.equals("ex1") || n.equals("ex2"));
      count++;
    }
    assertEquals(count, this.h1.size());
  }
}
