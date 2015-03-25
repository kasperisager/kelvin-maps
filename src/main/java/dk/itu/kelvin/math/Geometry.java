/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// General utilities
import java.util.Arrays;

/**
 * Geometry class.
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
   * Check if two bounds intersect.
   *
   * @param a The first bounds.
   * @param b The second bounds.
   * @return  A boolean indicating whether or not the bounds intersect.
   */
  public static boolean intersects(final Bounds a, final Bounds b) {
    if (a == null || b == null) {
      return false;
    }

    return (
      Epsilon.lessOrEqual(a.min.x, b.max.x)
      &&
      Epsilon.greaterOrEqual(a.max.x, b.min.x)
      &&
      Epsilon.lessOrEqual(a.min.y, b.max.y)
      &&
      Epsilon.greaterOrEqual(a.max.y, b.min.y)
    );
  }

  /**
   * Calculate the line-line intersection between the specified line segments.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Line%E2%80%93line_intersection">
   *      http://en.wikipedia.org/wiki/Line%E2%80%93line_intersection</a>
   *
   * @param a The first line segment.
   * @param b The second line segment.
   * @return  Point where the segments intersect, or {@code null} if they don't.
   */
  public static Point intersection(final Line a, final Line b) {
    if (a == null || b == null) {
      return null;
    }

    // Check if the rectangular bounds of the lines intersect before doing any
    // further computations.
    if (!Geometry.intersects(new Geometry.Bounds(a), new Geometry.Bounds(b))) {
      return null;
    }

    // Compute the denominator.
    double d = (
      (a.start.x - a.end.x) * (b.start.y - b.end.y)
    - (a.start.y - a.end.y) * (b.start.x - b.end.x)
    );

    // If the denominator equals 0, the lines are parallel are do therefore not
    // intersect.
    if (Epsilon.equal(d, 0.0f)) {
      return null;
    }

    // Compute the x-coordinate of the intersection.
    double px = (
      (a.start.x * a.end.y - a.start.y * a.end.x) * (b.start.x - b.end.x)
    - (b.start.x * b.end.y - b.start.y * b.end.x) * (a.start.x - a.end.x)
    ) / d;

    // Compute the y-coordinate of the intersection.
    double py = (
      (a.start.x * a.end.y - a.start.y * a.end.x) * (b.start.y - b.end.y)
    - (b.start.x * b.end.y - b.start.y * b.end.x) * (a.start.y - a.end.y)
    ) / d;

    Point p = new Point(px, py);

    // Check if both lines actually contain the intersection point. If this is
    // not the case the lines do not intersect within their individual segments.
    if (!a.contains(p) || !b.contains(p)) {
      return null;
    }

    return p;
  }

  /**
   * Calculate the distance between the specified points.
   *
   * @param a The first point.
   * @param b The second point.
   * @return  The distance between points {@code a} and {@code b} or {@code -1}
   *          if either of them are {@code null}.
   */
  private static double distance(final Point a, final Point b) {
    if (a == null || b == null) {
      return -1;
    }

    return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
  }

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
    private final double x;

    /**
     * The y-coordinate of the point.
     */
    private final double y;

    /**
     * Initialize a point.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public Point(final double x, final double y) {
      this.x = x;
      this.y = y;
    }

    /**
     * Get the x-coordinate of the point.
     *
     * @return The x-coordinate of the point.
     */
    public double x() {
      return this.x;
    }

    /**
     * Get the y-coordinate of the point.
     *
     * @return The y-coordinate of the point.
     */
    public double y() {
      return this.y;
    }

    @Override
    public String toString() {
      return "Point["
      + "x = " + this.x
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
      if (start == null || end == null) {
        throw new RuntimeException(
          "A valid Line must contain a starting and ending point"
        );
      }

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
    public double length() {
      return Geometry.distance(this.start, this.end);
    }

    /**
     * Check if the line is vertical.
     *
     * @return A boolean indicating whether or not the line is vertical.
     */
    public boolean isVertical() {
      return Epsilon.equal(this.start.x, this.end.x);
    }

    /**
     * Check if the line is horizontal.
     *
     * @return A boolean indicating whether or not the line is horizontal.
     */
    public boolean isHorizontal() {
      return Epsilon.equal(this.start.y, this.end.y);
    }

    /**
     * Check if the line contains the specified point.
     *
     * <p>
     * This operation utilizes the fact that a point, C, will lie on the line
     * between two other points, A and B, if, and only if, the distance from A
     * to C plus the distance from C to B equals the distance from A to B.
     *
     * @see <a href="http://stackoverflow.com/a/17693146">
     *      http://stackoverflow.com/a/17693146</a>
     *
     * @param point The point to check containment of.
     * @return      A boolean indicating whether or not the line contains the
     *              specified point.
     */
    public boolean contains(final Point point) {
      return Epsilon.equal(
        Geometry.distance(this.start, point)
      + Geometry.distance(point, this.end),
        this.length()
      );
    }

    @Override
    public String toString() {
      return "Line["
      + "start = " + this.start
      + ", end = " + this.end
      + "]";
    }
  }

  /**
   * The {@link Polyline} class describes a list of connected points that together
   * form a polyline.
   */
  public static final class Polyline {
    /**
     * The points contained within the polyline.
     */
    private final Point[] points;

    /**
     * Initialize a polyline.
     *
     * @param points The  points contained within the polyline.
     */
    public Polyline(final Point[] points) {
      if (points == null || points.length < 2) {
        throw new RuntimeException(
          "A valid Polyline must contain at least two points"
        );
      }

      this.points = Arrays.copyOf(points, points.length);
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
    private final double radius;

    /**
     * Initialize a circle.
     *
     * @param center The center of the circle.
     * @param radius The radius of the circle.
     */
    public Circle(final Point center, final double radius) {
      if (center == null) {
        throw new RuntimeException(
          "A valid Circle must contain a center point"
        );
      }

      if (radius < 0) {
        throw new RuntimeException(
          "A valid Circle cannot have a negative radius"
        );
      }

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
    public double radius() {
      return this.radius;
    }

    /**
     * Get the diameter of the circle.
     *
     * @return The diameter of the circle.
     */
    public double diameter() {
      return this.radius * 2;
    }

    /**
     * Get the circumference of the circle.
     *
     * @return The circumference of the circle.
     */
    public double circumference() {
      return this.diameter() * Math.PI;
    }

    /**
     * Get the area of the circle.
     *
     * @return The area of the circle.
     */
    public double area() {
      return Math.pow(this.radius, 2) * Math.PI;
    }

    @Override
    public String toString() {
      return "Circle["
      + "center = " + this.center
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
    private final double width;

    /**
     * The height of the rectangle.
     */
    private final double height;

    /**
     * Initialize a rectangle.
     *
     * @param position  The position of the rectangle.
     * @param width     The width of the rectangle.
     * @param height    The height of the rectangle.
     */
    public Rectangle(
      final Point position,
      final double width,
      final double height
    ) {
      if (position == null) {
        throw new RuntimeException(
          "A valid Rectangle must contain a position"
        );
      }

      if (width < 0 || height < 0) {
        throw new RuntimeException(
          "A valid Rectangle cannot have a negative width or height"
        );
      }

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
    public double width() {
      return this.width;
    }

    /**
     * Get the height of the rectangle.
     *
     * @return The height of the rectangle.
     */
    public double height() {
      return this.height;
    }

    /**
     * Get the area of the rectangle.
     *
     * @return The area of the rectangle.
     */
    public double area() {
      return this.width * this.height;
    }

    @Override
    public String toString() {
      return "Rectangle["
      + "position = " + this.position
      + ", width = " + this.width
      + ", height = " + this.height
      + "]";
    }
  }

  /**
   * The {@link Bounds} class describes the rectangular, geometric bounds of a
   * 2-dimensional shape.
   *
   * <p>
   * <b>OBS:</b> This class is only meant to be used for intermediate results.
   * It should never be stored anywhere other than a local variable at most.
   */
  public static final class Bounds {
    /**
     * The smallest point of the bounds.
     */
    private final Point min;

    /**
     * The largest point of the bounds.
     */
    private final Point max;

    /**
     * Initialize a set of bounds.
     *
     * @param min The smallest point of the bounds.
     * @param max The largest point of the bounds.
     */
    public Bounds(final Point min, final Point max) {
      if (min == null || max == null) {
        throw new RuntimeException(
          "Valid Bounds must contain a minimum and maximum point"
        );
      }

      if (Epsilon.greater(min.x, max.x) || Epsilon.greater(min.y, max.y)) {
        throw new RuntimeException(
          "Valid Bounds must contain a minimum point that is smaller than the"
        + " maximum point"
        );
      }

      this.min = min;
      this.max = max;
    }

    /**
     * Initialize the bounds of a {@link Line}.
     *
     * @param line The line whose bounds to initialize.
     */
    public Bounds(final Line line) {
      if (line == null) {
        throw new RuntimeException(
          "Cannot initialize Bounds without a valid shape"
        );
      }

      this.min = new Geometry.Point(
        Math.min(line.start.x, line.end.x),
        Math.min(line.start.y, line.end.y)
      );

      this.max = new Geometry.Point(
        Math.max(line.start.x, line.end.x),
        Math.max(line.start.y, line.end.y)
      );
    }

    /**
     * Initialize the bounds of a {@link Polyline}.
     *
     * @param polyline The polyline whose bounds to initialize.
     */
    public Bounds(final Polyline polyline) {
      if (polyline == null) {
        throw new RuntimeException(
          "Cannot initialize Bounds without a valid shape"
        );
      }

      Point first = polyline.points[0];

      double minX = first.x;
      double minY = first.y;
      double maxX = first.x;
      double maxY = first.y;

      for (int i = 1; i < polyline.points.length; i++) {
        Point point = polyline.points[i];

        if (Epsilon.less(point.x, minX)) {
          minX = point.x;
        }

        if (Epsilon.less(point.y, minY)) {
          minY = point.y;
        }

        if (Epsilon.greater(point.x, maxX)) {
          maxX = point.x;
        }

        if (Epsilon.greater(point.y, maxY)) {
          maxY = point.y;
        }
      }

      this.min = new Geometry.Point(minX, minY);
      this.max = new Geometry.Point(maxX, maxY);
    }

    /**
     * Initialize the bounds of a {@link Rectangle}.
     *
     * @param rectangle The rectangle whose bounds to initialize.
     */
    public Bounds(final Rectangle rectangle) {
      if (rectangle == null) {
        throw new RuntimeException(
          "Cannot initialize Bounds without a valid shape"
        );
      }

      this.min = rectangle.position;
      this.max = new Geometry.Point(
        this.min.x + rectangle.width,
        this.min.y + rectangle.height
      );
    }

    /**
     * Get the smallest point of the bounds.
     *
     * @return The smallest point of the bounds.
     */
    public Point min() {
      return this.min;
    }

    /**
     * Get the largest point of the bounds.
     *
     * @return The largest point of the bounds.
     */
    public Point max() {
      return this.max;
    }

    /**
     * Check if the bounds contain the specified point.
     *
     * @param point The point to check containment of.
     * @return      A boolean indicating whether or not the bounds contain the
     *              specified point.
     */
    public boolean contains(final Point point) {
      if (point == null) {
        return false;
      }

      return (
        Epsilon.greaterOrEqual(point.x, this.min.x)
        &&
        Epsilon.lessOrEqual(point.x, this.max.x)
        &&
        Epsilon.greaterOrEqual(point.y, this.min.y)
        &&
        Epsilon.lessOrEqual(point.y, this.max.y)
      );
    }

    @Override
    public String toString() {
      return "Bounds["
      + "min = " + this.min
      + ", max = " + this.max
      + "]";
    }
  }
}
