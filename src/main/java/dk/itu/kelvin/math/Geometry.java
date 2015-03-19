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
   * Check if two bounds intersects.
   *
   * @param a The first bounds.
   * @param b The second bounds.
   * @return  A boolean indicating whether or not the bounds intersect.
   */
  public static boolean intersects(final Bounds a, final Bounds b) {
    return (
      a.left <= b.right
      &&
      a.right >= b.left
      &&
      a.top <= b.bottom
      &&
      a.bottom >= b.top
    );
  }

  /**
   * Calculate the line-line intersection between the specified line segments.
   *
   * @see <a href="http://goo.gl/f7uPdH">Line-line intersection</a>
   *
   * @param a The first line segment.
   * @param b The second line segment.
   * @return  Point where the segments intersect, or {@code null} if they don't.
   */
  public static Point intersection(final Line a, final Line b) {
    float d = (
      (a.start.x - a.end.x) * (b.start.y - b.end.y)
    - (a.start.y - a.end.y) * (b.start.x - b.end.x)
    );

    if (Epsilon.equals(d, 0.0f)) {
      return null;
    }

    double px = (
      (b.end.x - b.start.x) * (a.start.y - b.start.y)
    - (b.end.y - b.start.y) * (a.start.x - b.start.x)
    ) / d;

    double py = (
      (a.end.x - a.start.x) * (a.start.y - b.start.y)
    - (a.end.y - a.start.y) * (a.start.x - b.start.x)
    ) / d;

    if (
      (Epsilon.less(px, 0.0f) || Epsilon.greater(px, 1.0f))
      &&
      (Epsilon.less(py, 0.0f) || Epsilon.greater(py, 1.0f))
    ) {
      return null;
    }

    Point p = new Point(
      (float) (a.start.x + px * (a.end.x - a.start.x)),
      (float) (a.start.y + py * (a.end.y - a.start.y))
    );

    if (
      Epsilon.less(p.x, Math.min(a.start.x, a.end.x))
      ||
      Epsilon.greater(p.x, Math.max(a.start.x, a.end.x))
      ||
      Epsilon.less(p.x, Math.min(b.start.x, b.end.x))
      ||
      Epsilon.greater(p.x, Math.max(b.start.x, b.end.x))
      ||
      Epsilon.less(p.y, Math.min(a.start.y, a.end.y))
      ||
      Epsilon.greater(p.y, Math.max(a.start.y, a.end.y))
      ||
      Epsilon.less(p.y, Math.min(b.start.y, b.end.y))
      ||
      Epsilon.greater(p.y, Math.max(b.start.y, b.end.y))
    ) {
      return null;
    }

    return p;
  }

  /**
   * Calculate the distance between the specified points.
   *
   * @param a The first point.
   * @param b The second point.
   * @return  The distance between points {@code a} and {@code b}.
   */
  private static float distance(final Point a, final Point b) {
    return (float) Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
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
    public float length() {
      return Geometry.distance(this.start, this.end);
    }

    /**
     * Check if the line is vertical.
     *
     * @return A boolean indicating whether or not the line is vertical.
     */
    public boolean isVertical() {
      return Epsilon.equals(this.start.x, this.end.x);
    }

    /**
     * Check if the line is horizontal.
     *
     * @return A boolean indicating whether or not the line is horizontal.
     */
    public boolean isHorizontal() {
      return Epsilon.equals(this.start.y, this.end.y);
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
     * The top-most position of the bounds.
     */
    private final float top;

    /**
     * The right-most position of the bounds.
     */
    private final float right;

    /**
     * The bottom-most position of the bounds.
     */
    private final float bottom;

    /**
     * The left-most position of the bounds.
     */
    private final float left;

    /**
     * Initialize a set of bounds.
     *
     * @param top     The top-most position of the bounds.
     * @param right   The right-most position of the bounds.
     * @param bottom  The bottom-most position of the bounds.
     * @param left    The left-most position of the bounds.
     */
    private Bounds(
      final float top,
      final float right,
      final float bottom,
      final float left
    ) {
      this.top = top;
      this.right = right;
      this.bottom = bottom;
      this.left = left;
    }

    /**
     * Initialize the bounds of a {@link Line}.
     *
     * @param line The line whose bounds to initialize.
     */
    public Bounds(final Line line) {
      if (line.start.x > line.end.x) {
        this.right = line.end.x;
        this.left = line.start.x;
      }
      else {
        this.right = line.start.x;
        this.left = line.end.x;
      }

      if (line.start.y > line.end.y) {
        this.top = line.start.y;
        this.bottom = line.end.y;
      }
      else {
        this.top = line.end.y;
        this.bottom = line.start.y;
      }
    }

    /**
     * Initialize the bounds of a {@link Rectangle}.
     *
     * @param rectangle The rectangle whose bounds to initialize.
     */
    public Bounds(final Rectangle rectangle) {
      this.left = rectangle.position.x;
      this.right = this.left + rectangle.width;

      this.top = rectangle.position.y;
      this.bottom = this.top + rectangle.height;
    }

    /**
     * Get the top-most position of the bounds.
     *
     * @return The top-most position of the bounds.
     */
    public float top() {
      return this.top;
    }

    /**
     * Get the right-most position of the bounds.
     *
     * @return The right-most position of the bounds.
     */
    public float right() {
      return this.right;
    }

    /**
     * Get the bottom-most position of the bounds.
     *
     * @return The bottom-most position of the bounds.
     */
    public float bottom() {
      return this.bottom;
    }

    /**
     * Get the left-most position of the bounds.
     *
     * @return The left-most position of the bounds.
     */
    public float left() {
      return this.left;
    }
  }
}
