/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * UnitTest of the Relation class.
 */
public final class RelationTest {
  /**
   * Tests the method members().
   * We've made two relations, one to check the lists size
   * and if it contains the given members,
   * and another to check if it makes a new ArrayList if the relation is empty.
   */
  @Test
  public void testRelationListOfMembers() {
    Relation r1 = new Relation();
    Node n1 = new Node(3, 3);
    Node n2 = new Node(4, 4);
    Node n3 = new Node(5, 5);

    r1.add(n1);
    r1.add(n2);
    r1.add(n3);

    assertEquals(3, r1.members().size());
    assertTrue(r1.members().contains(n1));
    assertTrue(r1.members().contains(n2));
    assertTrue(r1.members().contains(n3));

    Relation r2 = new Relation();
    assertEquals(0, r2.members().size());
  }
}
