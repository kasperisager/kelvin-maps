/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Map;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import javax.print.DocFlavor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * UnitTest of the Relation class.
 */
public class RelationTest {

  /**
   * Tests addition of a new member without a role.
   */
  @Test
  public void testAddMemberWithoutRole() {
    Relation r1 = new Relation(1);
    Node n1 = new Node(2, 2 ,2);
    r1.member(n1);

    assertEquals(Relation.Role.NONE, r1.role(n1));
    assertEquals(null, r1.role(null));
  }

  /**
   * //TODO
   */
  @Test
  public void testIfElementOrRoleIsNull() {
    Relation r1 = new Relation(10);
    Way w1 = new Way(1);
    r1.member(w1, Relation.Role.INNER);
    assertEquals(Relation.Role.INNER, r1.role(w1));
    assertNotEquals(Relation.Role.NONE, r1.role(null));

  }

  /**
   * Tests the method members().
   * We've made two relations, one to check the lists size
   * and if it contains the given members,
   * and another to check if it makes a new ArrayList if the relation is empty.
   */
  @Test
  public void testRelationListOfMembers() {
    Relation r1 = new Relation(1);
    Node n1 = new Node(3, 3, 3);
    Node n2 = new Node(4, 4, 4);
    Node n3 = new Node(5, 5, 5);

    r1.member(n1);
    r1.member(n2);
    r1.member(n3);

    assertEquals(3, r1.members().size());
    assertTrue(r1.members().contains(n1));
    assertTrue(r1.members().contains(n2));
    assertTrue(r1.members().contains(n3));

    Relation r2 = new Relation(2);
    assertEquals(0, r2.members().size());

  }

  /**
   * Tests the type method.
   */
  @Test
  public void testType() {
    Relation r1 = new Relation(1);
    r1.type(Relation.Type.ROUTE);
    assertEquals(Relation.Type.ROUTE, r1.type());

    Relation r2 = new Relation(2);
    r2.type(null);
    assertEquals(Relation.Type.NONE, r2.type());
  }
}
