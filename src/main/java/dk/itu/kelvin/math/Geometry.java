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
   * Check if the bounds of two shapes intersect.
   *
   * @param a The first shape.
   * @param b The second shape.
   * @return  A boolean indicating whether or not the bounds of the shapes
   *          intersect.
   */
  public static boolean intersects(final Shape a, final Shape b) {
    if (a == null || b == null) {
      return false;
    }

    return Geometry.intersects(a.bounds(), b.bounds());
  }

  /**
   * Get the rectangular union of the specified rectangles.
   *
   * @param a The first rectangle.
   * @param b The second rectangle.
   * @return  The rectangular union of the specified rectangles.
   */
  public static Rectangle union(final Rectangle a, final Rectangle b) {
    Point position = new Point(
      Math.min(a.position.x, b.position.x),
      Math.min(a.position.y, b.position.y)
    );

    Bounds ab = a.bounds();
    Bounds bb = b.bounds();

    return new Rectangle(
      position,
      Math.max(ab.max.x, bb.max.x) - position.x,
      Math.max(ab.max.y, bb.max.y) - position.y
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
    if (!Geometry.intersects(a, b)) {
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
   * The {@link Shape} interface describes a generic geometric shape with a set
   * of rectangular bounds.
   */
  public interface Shape {
    /**
     * Get the bounds of the shape.
     *
     * @return The bounds of the shape.
     */
    Bounds bounds();
  }

  /**
   * The {@link Point} class describes a coordinate in 2-dimensional space.
   */
  public static class Point implements Shape {
    /**
     * The x-coordinate of the point.
     */
    private double x;

    /**
     * The y-coordinate of the point.
     */
    private double y;

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
    public final double x() {
      return this.x;
    }

    /**
     * Get the y-coordinate of the point.
     *
     * @return The y-coordinate of the point.
     */
    public final double y() {
      return this.y;
    }

    /**
     * Get the bounds of the point.
     *
     * @return The bounds of the point.
     */
    public final Bounds bounds() {
      return new Bounds(
        new Point(this.x, this.y),
        new Point(this.x, this.y)
      );
    }

    @Override
    public final String toString() {
      return "Point["
      + "x = " + this.x
      + ", y = " + this.y
      + "]";
    }
  }

  /**
   * The {@link Line} class describes a line between two points in 2-dimensional
   * space.
   */
  public static class Line implements Shape {
    /**
     * The starting point of the line.
     */
    private Point start;

    /**
     * The ending point of the line.
     */
    private Point end;

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
    public final Point start() {
      return this.start;
    }

    /**
     * Get the ending point of the line.
     *
     * @return The ending point of the line.
     */
    public final Point end() {
      return this.end;
    }

    /**
     * Get the length of the line.
     *
     * @return The length of the line.
     */
    public final double length() {
      return Geometry.distance(this.start, this.end);
    }

    /**
     * Check if the line is vertical.
     *
     * @return A boolean indicating whether or not the line is vertical.
     */
    public final boolean isVertical() {
      return Epsilon.equal(this.start.x, this.end.x);
    }

    /**
     * Check if the line is horizontal.
     *
     * @return A boolean indicating whether or not the line is horizontal.
     */
    public final boolean isHorizontal() {
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
    public final boolean contains(final Point point) {
      return Epsilon.equal(
        Geometry.distance(this.start, point)
      + Geometry.distance(point, this.end),
        this.length()
      );
    }

    /**
     * Get the bounds of the line.
     *
     * @return The bounds of the line.
     */
    public final Bounds bounds() {
      return new Bounds(
        new Point(
          Math.min(this.start.x, this.end.x),
          Math.min(this.start.y, this.end.y)
        ),
        new Point(
          Math.max(this.start.x, this.end.x),
          Math.max(this.start.y, this.end.y)
        )
      );
    }

    @Override
    public final String toString() {
      return "Line["
      + "start = " + this.start
      + ", end = " + this.end
      + "]";
    }
  }

  /**
   * The {@link Polyline} class describes a list of connected points that
   * together form a polyline.
   */
  public static class Polyline implements Shape {
    /**
     * The points contained within the polyline.
     */
    private Point[] points;

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

    /**
     * Get the starting point of the polyline.
     *
     * @return The starting point of the polyline.
     */
    public final Point start() {
      return this.points[0];
    }

    /**
     * Get the ending point of the polyline.
     *
     * @return The ending point of the polyline.
     */
    public final Point end() {
      return this.points[this.points.length - 1];
    }

    /**
     * Check if the polyline is closed.
     *
     * @return A boolean indicating whether or not the polyline is closed.
     */
    public final boolean isClosed() {
      if (this.points.length == 2) {
        return false;
      }

      return (
        Epsilon.equal(this.start().x, this.end().x)
        &&
        Epsilon.equal(this.start().y, this.end().y)
      );
    }

    /**
     * Check if the polyline is open.
     *
     * @return A boolean indicating whether or not the polyline is open.
     */
    public final boolean isOpen() {
      return !this.isClosed();
    }

    /**
     * Get the length of the polyline.
     *
     * @return The length of the polyline.
     */
    public final double length() {
      double length = 0.0;

      for (int i = 0; i < this.points.length - 1;) {
        length += Geometry.distance(this.points[i++], this.points[i]);
      }

      return length;
    }

    /**
     * Check if the polyline contains the specified point.
     *
     * @param point The point to check containment of.
     * @return      A boolean indicating whether or not the polyline contains
     *              the specified point.
     */
    public final boolean contains(final Point point) {
      for (int i = 0; i < this.points.length - 1;) {
        Line line = new Line(this.points[i++], this.points[i]);

        if (line.contains(point)) {
          return true;
        }
      }

      return false;
    }

    /**
     * Get the bounds of the polyline.
     *
     * @return The bounds of the polyline.
     */
    public final Bounds bounds() {
      Point first = this.points[0];

      double minX = first.x;
      double minY = first.y;
      double maxX = first.x;
      double maxY = first.y;

      for (int i = 1; i < this.points.length; i++) {
        Point point = this.points[i];

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

      return new Bounds(new Point(minX, minY), new Point(maxX, maxY));
    }

    @Override
    public final String toString() {
      StringBuilder string = new StringBuilder();

      string.append("Polyline[points = [");

      for (int i = 0; i < this.points.length; i++) {
        if (i != 0) {
          string.append(", ");
        }

        string.append(this.points[i]);
      }

      return string.append("]]").toString();
    }
  }

  /**
   * The {@link Circle} class describes a 2-dimensional geometric shape with a
   * center point and a radius.
   */
  public static class Circle implements Shape {
    /**
     * The center of the circle.
     */
    private Point center;

    /**
     * The radius of the circle.
     */
    private double radius;

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
    public final Point center() {
      return this.center;
    }

    /**
     * Get the radius of the circle.
     *
     * @return The radius of the circle.
     */
    public final double radius() {
      return this.radius;
    }

    /**
     * Get the diameter of the circle.
     *
     * @return The diameter of the circle.
     */
    public final double diameter() {
      return this.radius * 2;
    }

    /**
     * Get the circumference of the circle.
     *
     * @return The circumference of the circle.
     */
    public final double circumference() {
      return this.diameter() * Math.PI;
    }

    /**
     * Get the area of the circle.
     *
     * @return The area of the circle.
     */
    public final double area() {
      return Math.pow(this.radius, 2) * Math.PI;
    }

    /**
     * Get the bounds of the circle.
     *
     * @return The bounds of the circle.
     */
    public final Bounds bounds() {
      return new Bounds(
        new Point(
          this.center.x - this.radius,
          this.center.y - this.radius
        ),
        new Point(
          this.center.x + this.radius,
          this.center.y + this.radius
        )
      );
    }

    @Override
    public final String toString() {
      return "Circle["
      + "center = " + this.center
      + ", radius = " + this.radius
      + "]";
    }
  }

  /**
   * The {@link Rectangle} class describes a 2-dimensional geometric shape with
   * a starting position and a width and height.
   */
  public static class Rectangle implements Shape {
    /**
     * The position of the rectangle.
     */
    private Point position;

    /**
     * The width of the rectangle.
     */
    private double width;

    /**
     * The height of the rectangle.
     */
    private double height;

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
    public final Point position() {
      return this.position;
    }

    /**
     * Get the width of the rectangle.
     *
     * @return The width of the rectangle.
     */
    public final double width() {
      return this.width;
    }

    /**
     * Get the height of the rectangle.
     *
     * @return The height of the rectangle.
     */
    public final double height() {
      return this.height;
    }

    /**
     * Get the area of the rectangle.
     *
     * @return The area of the rectangle.
     */
    public final double area() {
      return this.width * this.height;
    }

    /**
     * Get the center point of the rectangle.
     *
     * @return The center point of the rectangle.
     */
    public final Point center() {
      return new Point(
        this.position.x + (this.width / 2),
        this.position.y + (this.height / 2)
      );
    }

    /**
     * Add another rectangle to the current rectangle, letting the current
     * rectangle become the union of the two.
     *
     * @param rectangle The rectangle to add to the current rectangle.
     */
    public final void add(final Rectangle rectangle) {
      if (rectangle == null) {
        return;
      }

      Rectangle union = Geometry.union(this, rectangle);

      this.position = union.position;
      this.width = union.width;
      this.height = union.height;
    }

    /**
     * Compute the area by which the current rectangle would be increased if
     * unioned with the specified rectangle.
     *
     * @param rectangle The rectangle.
     * @return          The area by which the current rectangle would increase
     *                  if unioned with the specified rectangle.
     */
    public final double enlargement(final Rectangle rectangle) {
      return Geometry.union(this, rectangle).area() - this.area();
    }

    /**
     * Get the bounds of the rectangle.
     *
     * @return The bounds of the rectangle.
     */
    public final Bounds bounds() {
      return new Bounds(
        this.position,
        new Point(
          this.position.x + this.width,
          this.position.y + this.height
        )
      );
    }

    @Override
    public final String toString() {
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
  public static class Bounds {
    /**
     * The smallest point of the bounds.
     */
    private Point min;

    /**
     * The largest point of the bounds.
     */
    private Point max;

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

      this.min = new Point(
        Math.min(min.x, max.x),
        Math.min(min.y, max.y)
      );
      this.max = new Point(
        Math.max(min.x, max.x),
        Math.max(min.y, max.y)
      );
    }

    /**
     * Get the smallest point of the bounds.
     *
     * @return The smallest point of the bounds.
     */
    public final Point min() {
      return this.min;
    }

    /**
     * Get the largest point of the bounds.
     *
     * @return The largest point of the bounds.
     */
    public final Point max() {
      return this.max;
    }

    /**
     * Get the center point of the bounds.
     *
     * @return The center point of the bounds.
     */
    public final Point center() {
      return new Point(
        this.min.x + ((this.max.x - this.min.x) / 2),
        this.min.y + ((this.max.y - this.min.y) / 2)
      );
    }

    /**
     * Check if the bounds contain the specified point.
     *
     * @param point The point to check containment of.
     * @return      A boolean indicating whether or not the bounds contain the
     *              specified point.
     */
    public final boolean contains(final Point point) {
      if (point == null) {
        return false;
      }

      return Geometry.intersects(this, point.bounds());
    }

    @Override
    public final String toString() {
      return "Bounds["
      + "min = " + this.min
      + ", max = " + this.max
      + "]";
    }
  }
}
