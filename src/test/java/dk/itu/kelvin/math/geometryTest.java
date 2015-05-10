/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class geometryTest {

  /**
   *
   */
  @Test
  public void testDistance() {
    Geometry.Point p1 = new Geometry.Point(2, 2);
    Geometry.Point p2 = new Geometry.Point(6, 6);

    assertTrue(Geometry.distance(p1, p2) == 5.656854249492381);

    Geometry.Point p3 = null;

    assertTrue(Geometry.distance(p1, p3) == -1);
  }

  /**
   *
   */
  @Test
  public void testIntersects() {
    Geometry.Point p1 = new Geometry.Point(1, 1);
    Geometry.Point p2 = new Geometry.Point(6, 9);
    Geometry.Point p3 = new Geometry.Point(3, 2);
    Geometry.Point p4 = new Geometry.Point(16, 19);

    Geometry.Bounds b1 = new Geometry.Bounds(p1, p2);
    Geometry.Bounds b2 = new Geometry.Bounds(p3, p4);
    Geometry.Bounds b3 = null;

    assertTrue(Geometry.intersects(b1, b2));
    assertFalse(Geometry.intersects(b1, b3));
  }

  /**
   *
   */
  @Test
  public void testIntersects2() {
    Geometry.Point p1 = new Geometry.Point(13, 13);
    Geometry.Point p2 = new Geometry.Point(10, 10);
    Geometry.Circle c1 = new Geometry.Circle(p1, 8);

    Geometry.Rectangle r1 = new Geometry.Rectangle(p2, 20, 20);
    Geometry.Rectangle r2 = null;

    assertTrue(Geometry.intersects(c1, r1));
    assertFalse(Geometry.intersects(c1, r2));
  }

  /**
   *
   */
  @Test
  public void testIntersection3() {
    Geometry.Point p1 = new Geometry.Point(2, 2);
    Geometry.Point p2 = new Geometry.Point(8, 8);
    Geometry.Point p3 = new Geometry.Point(2, 8);
    Geometry.Point p4 = new Geometry.Point(8, 2);

    Geometry.Point p5 = new Geometry.Point(5.0, 5.0);

    Geometry.Line l1 = new Geometry.Line(p1, p2);
    Geometry.Line l2 = new Geometry.Line(p3, p4);
    Geometry.Line l3 = null;

    assertEquals(p5, Geometry.intersection(l1, l2));
    assertEquals(null, Geometry.intersection(l1, l3));
  }

  /**
   *
   */
  @Test (expected = RuntimeException.class)
  public void testListClass() {
    Geometry.Point p1 = new Geometry.Point(5, 5);
    Geometry.Point p2 = null;

    new Geometry.Line(p1, p2);
  }

}
