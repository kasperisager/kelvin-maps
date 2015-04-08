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
 * {@link Relation} test suite.
 */
public final class RelationTest {
  /**
   * A relation should be able to consist of one or more member elements.
   */
  @Test
  public void testRelationListOfMembers() {
    // Test creation of relation with a number of members.
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

    // Test creation of relation without members.
    Relation r2 = new Relation();
    assertEquals(0, r2.members().size());
  }
}
