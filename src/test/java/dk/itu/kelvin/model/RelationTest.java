/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JUnit annotations
import org.junit.Test;

import static org.junit.Assert.assertTrue;

// JUnit assertions

/**
 * {@link Relation} test suite.
 */
public final class RelationTest {

  /**
   * Test max. and min. coordinates of the relation.
   */
  @Test
  public void testCoordinates() {
    // Test creation of relation with a number of members.
    Relation r1 = new Relation();
    Node n1 = new Node(3, 3);
    Node n2 = new Node(4, 4);
    Node n3 = new Node(5, 5);

    r1.add(n1);
    r1.add(n2);
    r1.add(n3);

    // Test largest coordinates of the relation.
    assertTrue(3 == r1.minX());
    assertTrue(3 == r1.minY());

    // Test smallest coordinates of the relation.
    assertTrue(5 == r1.maxX());
    assertTrue(5 == r1.maxY());
  }

  /**
   * A relation should be able to consist of one or more member elements.
   */
  @Test
  public void addElement() {
    Relation r1 = new Relation();

    // test add element = null
    assertTrue(0 == r1.members().size());
    Node n1 = null;
    r1.add(n1);
    assertTrue(0 == r1.members().size());

    // test add !empty way
    Way w1 = new Way();
    Node n2 = new Node(3, 3);
    w1.add(n2);
    r1.add(w1);

    // test add empty way
    Way w2 = new Way();
    r1.add(w2);
  }
}
