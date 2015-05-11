/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineTest {
  // Herro
  /**
   * Test the constructor.
   */
  @Test (expected = RuntimeException.class)
  public void testLine() {
    Geometry.Point p1 = new Geometry.Point(5, 5);
    Geometry.Point p2 = null;

    new Geometry.Line(p1, p2);
  }

  /**
   * Test whether a line is vertical or not.
   */
  @Test
  public void testIsVertical() {
    Geometry.Point p1 = new Geometry.Point(4, 90);
    Geometry.Point p2 = new Geometry.Point(4, 1);
    Geometry.Line l1 = new Geometry.Line(p1, p2);

    assertTrue(l1.isVertical());

    Geometry.Point p3 = new Geometry.Point(4, 90);
    Geometry.Point p4 = new Geometry.Point(5, 1);
    Geometry.Line l2 = new Geometry.Line(p3, p4);

    assertFalse(l2.isVertical());
  }

  /**
   * Test whether a line is horizontal or not.
   */
  @Test
  public void testIsHorizontal() {
    Geometry.Point p1 = new Geometry.Point(99, 6);
    Geometry.Point p2 = new Geometry.Point(300, 6);
    Geometry.Line l1 = new Geometry.Line(p1, p2);

    assertTrue(l1.isHorizontal());

    Geometry.Point p3 = new Geometry.Point(4, 90);
    Geometry.Point p4 = new Geometry.Point(15, 1);
    Geometry.Line l2 = new Geometry.Line(p3, p4);

    assertFalse(l2.isHorizontal());
  }

  /**
   * Test wether a point is contained within a line ojbect.
   */
  @Test
  public void testContains() {
    Geometry.Point p1 = new Geometry.Point(3, 6);
    Geometry.Point p2 = new Geometry.Point(30, 6);
    Geometry.Line l1 = new Geometry.Line(p1, p2);

    Geometry.Point p3 = new Geometry.Point(16, 6);
    Geometry.Point p4 = new Geometry.Point(16, 19);

    assertTrue(l1.contains(p3));
    assertFalse(l1.contains(p4));
  }

}
