/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PolyLineTest {
  /**
   * Test initializing a polyline with too few points.
   */
  @Test (expected = RuntimeException.class)
  public void testPolyLineInitializationWithSinglePoint() {
    Geometry.Point p1 = new Geometry.Point(4 , 5);

    new Geometry.Polyline(p1);
  }

  /**
   * Test initializing a polyline with a point, which is null.
   */
  @Test (expected = NullPointerException.class)
  public void testPolyLineInitalizationWithNullPoint() {
    Geometry.Point p1 = new Geometry.Point(4 , 5);
    Geometry.Point p2 = null;
    Geometry.Point p3 = new Geometry.Point(4 , 5);

    new Geometry.Polyline(p1, p2, p3);
  }

  /**
   * Test whether a poly line is closed.
   * This means if the poly line starts and ends in the same point.
   */
  @Test
  public void testIsClosed() {
    Geometry.Point p1 = new Geometry.Point(4, 90);
    Geometry.Point p2 = new Geometry.Point(100, 13);
    Geometry.Point p3 = new Geometry.Point(60, 4);
    Geometry.Point p4 = new Geometry.Point(4, 90);

    Geometry.Polyline pl1 = new Geometry.Polyline(p1, p2, p3, p4);
    assertTrue(pl1.isClosed());

    Geometry.Polyline pl2 = new Geometry.Polyline(p1, p2, p4, p3);
    assertFalse(pl2.isClosed());
  }

  /**
   * Test the calculation of a length of a poly line.
   */
  @Test
  public void testLength() {
    Geometry.Point p1 = new Geometry.Point(1, 90);
    Geometry.Point p2 = new Geometry.Point(11, 90);
    Geometry.Point p3 = new Geometry.Point(21, 90);
    Geometry.Point p4 = new Geometry.Point(31, 90);

    Geometry.Polyline pl1 = new Geometry.Polyline(p1, p2, p3, p4);
    assertTrue(pl1.length() == 30);
  }



}
