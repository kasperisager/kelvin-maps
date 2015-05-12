/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * {@link Geometry} test suite.
 */
public class GeometryTest {
  /**
   * Test the calculation of distance between two points.
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
   * Test if two bound objects intersect.
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
   * Test if two rectangle objects intersect.
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
   * Test if two line objects intersect.
   */
  @Test
  public void testIntersection() {
    Geometry.Point p1 = new Geometry.Point(2, 2);
    Geometry.Point p2 = new Geometry.Point(8, 8);
    Geometry.Point p3 = new Geometry.Point(2, 8);
    Geometry.Point p4 = new Geometry.Point(8, 2);

    Geometry.Point p5 = new Geometry.Point(5.0, 5.0);

    Geometry.Line l1 = new Geometry.Line(p1, p2);
    Geometry.Line l2 = new Geometry.Line(p3, p4);
    Geometry.Line l3 = null;

    assertTrue(Geometry.intersection(l1, l2).x() == p5.x());
    assertTrue(Geometry.intersection(l1, l2).y() == p5.y());
    assertEquals(null, Geometry.intersection(l1, l3));
  }

  /**
   * Test the angles between the points.
   * We accept a deviation of 2%.
   */
  @Test
  public void testAngleBetweenPoints() {
    Geometry.Point p1 = new Geometry.Point(2, 2);
    Geometry.Point p2 = new Geometry.Point(2, 8);
    Geometry.Point p3 = new Geometry.Point(8, 2);
    Geometry.Point p4 = null;

    assertTrue(Geometry.angle(p1, p2, p4) == -1);

    double expectedAngle = 90 * 1.01;
    double expectedAngle2 = 90 * 0.99;
    double angle = Geometry.angle(p1, p2 , p3);

    assertTrue(angle < expectedAngle);
    assertTrue(angle > expectedAngle2);
  }

  /**
   * Test the angle between two lines.
   */
  @Test
  public void testAngleBetweenLines() {
    Geometry.Point p1 = new Geometry.Point(1, 2);
    Geometry.Point p2 = new Geometry.Point(8, 2);
    Geometry.Point p3 = new Geometry.Point(2, 1);
    Geometry.Point p4 = new Geometry.Point(2, 8);

    Geometry.Line l1 = new Geometry.Line(p1, p2);
    Geometry.Line l2 = new Geometry.Line(p3, p4);
    Geometry.Line l3 = null;

    double expectedAngle = 90 * 1.01;
    double expectedAngle2 = 90 * 0.99;
    double angle = Geometry.angle(l1, l2);

    assertTrue(angle < expectedAngle);
    assertTrue(angle > expectedAngle2);

    assertTrue(Geometry.angle(l1, l3) == -1);
  }

  /**
   *
   */
  @Test
  public void testUnion() {
    Geometry.Point p1 = new Geometry.Point(2, 6);
    Geometry.Point p2 = new Geometry.Point(3, 5);

    Geometry.Rectangle r1 = new Geometry.Rectangle(p1, 6, 4);
    Geometry.Rectangle r2 = new Geometry.Rectangle(p2, 4, 5);

    Geometry.Rectangle r3 = Geometry.union(r1, r2);
    Geometry.Point p3 = new Geometry.Point(2, 5);

    assertTrue(r3.position().x() == p3.x());
    assertTrue(r3.position().y() == p3.y());
    assertTrue(r3.width() == 6);
    assertTrue(r3.height() == 5);
  }

  /**
   * {@link LineTest} test suite.
   */
  public static final class LineTest {
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
     * Test whether a point is contained within a line ojbect.
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

    /**
     *  Test the bounding of line objects.
     */
    @Test
    public void testBounds() {
      Geometry.Point p1 = new Geometry.Point(11, 34);
      Geometry.Point p2 = new Geometry.Point(30, 16);
      Geometry.Line l1 = new Geometry.Line(p1, p2);

      Geometry.Point p3 = new Geometry.Point(11, 16);
      Geometry.Point p4 = new Geometry.Point(30, 34);
      Geometry.Bounds b1 = new Geometry.Bounds(p3, p4);

      assertTrue(b1.min().x() == l1.bounds().min().x());
      assertTrue(b1.min().y() == l1.bounds().min().y());
      assertTrue(b1.max().x() == l1.bounds().max().x());
      assertTrue(b1.max().y() == l1.bounds().max().y());
    }
  }

  /**
   * Testing of the inner class Polyline.
   *
   * {@link Geometry.Polyline} test suite.
   */
  public static final class PolylineTest {
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

    /**
     * Testing if a polyline contains a point.
     */
    @Test
    public void testContains() {
      Geometry.Point p1 = new Geometry.Point(1, 90);
      Geometry.Point p2 = new Geometry.Point(11, 90);
      Geometry.Point p3 = new Geometry.Point(21, 90);
      Geometry.Point p4 = new Geometry.Point(31, 90);
      Geometry.Point p5 = new Geometry.Point(26, 90);
      Geometry.Point p6 = new Geometry.Point(70, 34);

      Geometry.Polyline pl1 = new Geometry.Polyline(p1, p2 , p3, p4);
      assertTrue(pl1.contains(p5));
      assertFalse(pl1.contains(p6));
    }

    /**
     * Testing finding bounds of a poly line object.
     */
    @Test
    public void testBounds() {
      Geometry.Point p1 = new Geometry.Point(1, 1);
      Geometry.Point p2 = new Geometry.Point(11, 32);
      Geometry.Point p3 = new Geometry.Point(21, 25);
      Geometry.Point p4 = new Geometry.Point(16, 90);

      Geometry.Polyline pl1 = new Geometry.Polyline(p1, p2, p3 , p4);

      Geometry.Point p5 = new Geometry.Point(1, 1);
      Geometry.Point p6 = new Geometry.Point(21, 90);
      Geometry.Bounds b1 = new Geometry.Bounds(p5, p6);

      assertTrue(pl1.bounds().min().x() == b1.min().x());
      assertTrue(pl1.bounds().min().y() == b1.min().y());
      assertTrue(pl1.bounds().max().x() == b1.max().x());
      assertTrue(pl1.bounds().max().y() == b1.max().y());
    }

    /**
     * Testing the toString method.
     */
    @Test
    public void testToString() {
      Geometry.Point p1 = new Geometry.Point(1, 1);
      Geometry.Point p2 = new Geometry.Point(11, 32);

      Geometry.Polyline pl1 = new Geometry.Polyline(p1, p2);

      String polyString =
              "Polyline[points = [Point[x = 1.0, y = 1.0], "
                      + "Point[x = 11.0, y = 32.0]]]";

      assertEquals(polyString, pl1.toString());

    }
  }

  /**
   * Testing of the inner class Circle.
   *
   * {@link Geometry.Circle} test suite.
   */
  public static final class CircleTest {
    /**
     * Test constructor with null point.
     */
    @Test (expected = RuntimeException.class)
    public void testCircleInitializeWithNullCenter() {
      Geometry.Point p1 = null;

      new Geometry.Circle(p1, 8);
    }

    /**
     * Test constructor with a negative radius.
     */
    @Test (expected = RuntimeException.class)
    public void testCircleInitializeWithNegativeRadius() {
      Geometry.Point p1 = new Geometry.Point(5, 5);

      new Geometry.Circle(p1, -8);
    }

    /**
     * Testing the toString method of the circle inner class.
     */
    @Test
    public void testToString() {
      Geometry.Point p1 = new Geometry.Point(3, 4);
      Geometry.Circle c1 = new Geometry.Circle(p1, 15);

      String circleString
              = "Circle[center = Point[x = 3.0, y = 4.0], radius = 15.0]";

      assertEquals(circleString, c1.toString());
    }
  }

  /**
   * Test of the inner class Rectangle.
   *
   * {@link Geometry.Rectangle} test suite.
   */
  public static final class RectangleTest {
    /**
     * Test initializing a rectangle with a null point.
     */
    @Test (expected = RuntimeException.class)
    public void testRectangleInitializeWithNullPoint() {
      Geometry.Point p1 = null;

      new Geometry.Rectangle(p1, 3, 4);
    }

    /**
     * Test initializing a rectangle with a negative width.
     */
    @Test (expected = RuntimeException.class)
    public void testRectangleInitializeWithNegativeWidth() {
      Geometry.Point p1 = new Geometry.Point(3, 90);

      new Geometry.Rectangle(p1, -3, 4);
    }

    /**
     * Test of adding two rectangles together.
     */
    @Test
    public void testAdd() {
      Geometry.Point p1 = new Geometry.Point(3, 3);
      Geometry.Rectangle r1 = new Geometry.Rectangle(p1, 5, 4);

      Geometry.Point p2 = new Geometry.Point(6, 5);
      Geometry.Rectangle r2 = new Geometry.Rectangle(p2, 3, 4);

      r2.add(r1);

      assertTrue(r2.position().x() == p1.x());
      assertTrue(r2.position().y() == p1.y());
      assertTrue(r2.width() == 6);
      assertTrue(r2.height() == 6);
    }

    /**
     * Test calculation of added area through add method.
     */
    @Test
    public void testEnlargement() {
      Geometry.Point p1 = new Geometry.Point(2, 2);
      Geometry.Point p2 = new Geometry.Point(4, 4);
      Geometry.Rectangle r1 = new Geometry.Rectangle(p1, 5, 5);
      Geometry.Rectangle r2 = new Geometry.Rectangle(p2, 4, 4);

      assertTrue(r1.enlargement(r2) == 11);
    }

    /**
     * Test the string representation of rectangle.
     */
    @Test
    public void testToString() {
      Geometry.Point p1 = new Geometry.Point(2, 2);
      Geometry.Rectangle r1 = new Geometry.Rectangle(p1, 5, 5);

      String rectangleString =
              "Rectangle[position = Point[x = 2.0, y = 2.0], "
                      + "width = 5.0, height = 5.0]";
      assertEquals(r1.toString(), rectangleString);
    }
  }

  /**
   * Test of the inner class Bounds.
   *
   * {@link Geometry.Bounds} test suite.
   */
  public static final class BoundsTest {
    /**
     * Test initialization with null point.
     */
    @Test (expected = RuntimeException.class)
    public void testBounds() {
      Geometry.Point p1 = new Geometry.Point(3, 3);
      Geometry.Point p2 = null;

      new Geometry.Bounds(p1, p2);
    }

    /**
     * Testing center method.
     */
    @Test
    public void testCenter() {
      Geometry.Point p1 = new Geometry.Point(1, 1);
      Geometry.Point p2 = new Geometry.Point(11, 11);

      Geometry.Bounds b1 = new Geometry.Bounds(p1, p2);
      Geometry.Point p3 = new Geometry.Point(6, 6);

      assertTrue(b1.center().x() == p3.x());
      assertTrue(b1.center().y() == p3.y());

      Geometry.Point p4 = new Geometry.Point(10, -200);
      Geometry.Point p5 = new Geometry.Point(40, -20);

      Geometry.Bounds b2 = new Geometry.Bounds(p4, p5);
      Geometry.Point p6 = new Geometry.Point(25, -110);

      assertTrue(b2.center().x() == p6.x());
      assertTrue(b2.center().y() == p6.y());
    }

    /**
     * Test if a point is contained within bounds.
     */
    @Test
    public void testContains() {
      Geometry.Point p1 = new Geometry.Point(2, 2);
      Geometry.Point p2 = new Geometry.Point(5, 5);
      Geometry.Point p3 = null;
      Geometry.Point p4 = new Geometry.Point(3, 3);

      Geometry.Bounds b1 = new Geometry.Bounds(p1, p2);

      assertFalse(b1.contains(p3));
      assertTrue(b1.contains(p4));
    }

    /**
     *  Test the string representation of bounds.
     */
    @Test
    public void testToString() {
      Geometry.Point p1 = new Geometry.Point(2, 2);
      Geometry.Point p2 = new Geometry.Point(5, 5);

      Geometry.Bounds b1 = new Geometry.Bounds(p1, p2);

      String boundsString =
              "Bounds[min = Point[x = 2.0, y = 2.0], "
                      + "max = Point[x = 5.0, y = 5.0]]";
      assertEquals(b1.toString(), boundsString);
    }

  }

}
