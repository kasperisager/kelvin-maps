/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

/**
 * Geometry class.
 *
 * @version 1.0.0
 */
public final class Geometry {
  /**
   * Don't allow instantiation of the class.
   *
   * <p>
   * Since the class only contains static fields and methods, we never want to
   * instantiate the class. We therefore define a private constructor so that
   * noone can create instances of the class other than the class itself.
   *
   * <p>
   * NB: This does not make the class a singleton. In fact, there never exists
   * an instance of the class since not even the class instantiates itself.
   */
  private Geometry() {
    super();
  }

  /**
   * Calculate the line-line intersection between the specified line segments.
   *
   * <p>
   * This algorithm is based on an implementation of the line-line intersection
   * algorithm by Alexander Hristov.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Line%E2%80%93line_intersection">
   * http://en.wikipedia.org/wiki/Line%E2%80%93line_intersection</a>
   * @see <a href="http://www.ahristov.com/tutorial/geometry-games/intersection-
   * segments.html">http://www.ahristov.com/tutorial/geometry-games/intersection
   * -segments.html</a>
   *
   * @param p1  The starting point of the first line.
   * @param p2  The ending point of the first line.
   * @param p3  The starting point of the second line.
   * @param p4  The ending point of the second line.
   * @return    Point where the segments intersect, or {@code null} if they
   *            don't.
   */
  public static Point intersection(
    final Point p1,
    final Point p2,
    final Point p3,
    final Point p4
  ) {
    float x1 = p1.x();
    float y1 = p1.y();
    float x2 = p2.x();
    float y2 = p2.y();
    float x3 = p3.x();
    float y3 = p3.y();
    float x4 = p4.x();
    float y4 = p4.y();

    // Calculate the denominator of the line segments.
    float d = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));

    // If the denominator is zero the lines do not intersect.
    if (Epsilon.equal(d, 0.0f)) {
      return null;
    }

    // Calculate the x-coordinate of the intersection.
    float xi = (
      (x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)
    ) / d;

    // Calculate the y-coordinate of the intersection.
    float yi = (
      (y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)
    ) / d;

    if (xi < Math.min(x1, x2) || xi > Math.max(x1, x2)) {
      return null;
    }

    if (xi < Math.min(x3, x4) || xi > Math.max(x3, x4)) {
      return null;
    }

    return new Point(xi, yi);
  }

  /**
   * Calculate the line-line intersection between the specified line segments.
   *
   * @param a The first line segment.
   * @param b The second line segment.
   * @return  Point where the segments intersect, or {@code null} if they don't.
   */
  public static Point intersection(final Line a, final Line b) {
    return Geometry.intersection(a.start(), a.end(), b.start(), b.end());
  }

  /**
   * Calculate the distance between the specified points.
   *
   * @param a The first point.
   * @param b The second point.
   * @return  The distance between points {@code a} and {@code b}.
   */
  // private static float distance(final Point a, final Point b) {
  // }

  /**
   * The {@link Point} class describes a coordinate in 2-dimensional space.
   *
   * <p>
   * <b>OBS:</b> This class is only meant to be used for intermediate results.
   * It should never be stored anywhere other than a local variable at most.
   */
  public static final class Point {
    /**
     * The x-coordinate of the point.
     */
    private final float x;

    /**
     * The y-coordinate of the point.
     */
    private final float y;

    /**
     * Initialize a point.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public Point(final float x, final float y) {
      this.x = x;
      this.y = y;
    }

    /**
     * Get the x-coordinate of the point.
     *
     * @return The x-coordinate of the point.
     */
    public float x() {
      return this.x;
    }

    /**
     * Get the y-coordinate of the point.
     *
     * @return The y-coordinate of the point.
     */
    public float y() {
      return this.y;
    }

    @Override
    public String toString() {
      return "[point: "
      + " x = " + this.x
      + ", y = " + this.y
      + "]";
    }
  }

  /**
   * The {@link Line} class describes a line between two points in 2-dimensional
   * space.
   *
   * <p>
   * <b>OBS:</b> This class is only meant to be used for intermediate results.
   * It should never be stored anywhere other than a local variable at most.
   */
  public static final class Line {
    /**
     * The starting point of the line.
     */
    private final Point start;

    /**
     * The ending point of the line.
     */
    private final Point end;

    /**
     * Initialize a line.
     *
     * @param start The starting point of the line.
     * @param end   The ending point of the line.
     */
    public Line(final Point start, final Point end) {
      this.start = start;
      this.end = end;
    }

    /**
     * Get the starting point of the line.
     *
     * @return The starting point of the line.
     */
    public Point start() {
      return this.start;
    }

    /**
     * Get the ending point of the line.
     *
     * @return The ending point of the line.
     */
    public Point end() {
      return this.end;
    }

    /**
     * Get the length of the line.
     *
     * @return The length of the line.
     */
    // public float length() {
    //   return Geometry.distance(this.start, this.end);
    // }

    @Override
    public String toString() {
      return "[line: "
      + " start = " + this.start
      + ", end = " + this.end
      + "]";
    }
  }

  /**
   * The {@link Circle} class describes a 2-dimensional geometric shape with a
   * center point and a radius.
   *
   * <p>
   * <b>OBS:</b> This class is only meant to be used for intermediate results.
   * It should never be stored anywhere other than a local variable at most.
   */
  public static final class Circle {
    /**
     * The center of the circle.
     */
    private final Point center;

    /**
     * The radius of the circle.
     */
    private final float radius;

    /**
     * Initialize a circle.
     *
     * @param center The center of the circle.
     * @param radius The radius of the circle.
     */
    public Circle(final Point center, final float radius) {
      this.center = center;
      this.radius = radius;
    }

    /**
     * Get the center of the circle.
     *
     * @return The center of the circle.
     */
    public Point center() {
      return this.center;
    }

    /**
     * Get the radius of the circle.
     *
     * @return The radius of the circle.
     */
    public float radius() {
      return this.radius;
    }

    /**
     * Get the diameter of the circle.
     *
     * @return The diameter of the circle.
     */
    public float diameter() {
      return this.radius * 2;
    }

    /**
     * Get the circumference of the circle.
     *
     * @return The circumference of the circle.
     */
    public float circumference() {
      return this.diameter() * (float) Math.PI;
    }

    /**
     * Get the area of the circle.
     *
     * @return The area of the circle.
     */
    public float area() {
      return (float) (Math.pow(this.radius, 2) * Math.PI);
    }

    @Override
    public String toString() {
      return "[circle:"
      + " center = " + this.center
      + ", radius = " + this.radius
      + "]";
    }
  }

  /**
   * The {@link Rectangle} class describes a 2-dimensional geometric shape with
   * a starting position and a width and height.
   *
   * <p>
   * <b>OBS:</b> This class is only meant to be used for intermediate results.
   * It should never be stored anywhere other than a local variable at most.
   */
  public static final class Rectangle {
    /**
     * The position of the rectangle.
     */
    private final Point position;

    /**
     * The width of the rectangle.
     */
    private final float width;

    /**
     * The height of the rectangle.
     */
    private final float height;

    /**
     * Initialize a rectangle.
     *
     * @param position  The position of the rectangle.
     * @param width     The width of the rectangle.
     * @param height    The height of the rectangle.
     */
    public Rectangle(
      final Point position,
      final float width,
      final float height
    ) {
      this.position = position;
      this.width = width;
      this.height = height;
    }

    /**
     * Get the position of the rectangle.
     *
     * @return The position of the rectangle.
     */
    public Point position() {
      return this.position;
    }

    /**
     * Get the width of the rectangle.
     *
     * @return The width of the rectangle.
     */
    public float width() {
      return this.width;
    }

    /**
     * Get the height of the rectangle.
     *
     * @return The height of the rectangle.
     */
    public float height() {
      return this.height;
    }

    /**
     * Get the area of the rectangle.
     *
     * @return The area of the rectangle.
     */
    public float area() {
      return this.width * this.height;
    }

    @Override
    public String toString() {
      return "[rectangle: "
      + " position = " + this.position
      + ", width = " + this.width
      + ", height = " + this.height
      + "]";
    }
  }
}
