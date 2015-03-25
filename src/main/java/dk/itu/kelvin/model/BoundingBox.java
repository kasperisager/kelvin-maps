/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JavaFX shapes
import javafx.scene.shape.Rectangle;

// JavaFX paint
import javafx.scene.paint.Color;

// Math
import dk.itu.kelvin.math.Geometry;

/**
 * A bounding box describes the bounds of a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Bounding_Box">
 *      http://wiki.openstreetmap.org/wiki/Bounding_Box</a>
 */
public final class BoundingBox extends Element<Rectangle> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 19;

  /**
   * The top coordinate of the bounds.
   */
  private float top;

  /**
   * The right coordinate of the bounds.
   */
  private float right;

  /**
   * The bottom coordinate of the bounds.
   */
  private float bottom;

  /**
   * The left coordinate of the bounds.
   */
  private float left;

  /**
   * Initialize a new bounding box.
   *
   * @param top     The top coordinate of the bounds.
   * @param right   The right coordinate of the bounds.
   * @param bottom  The bottom coordinate of the bounds.
   * @param left    The left coordinate of the bounds.
   */
  public BoundingBox(
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
   * Get the top coordinate of the bounds.
   *
   * @return The top coordinate of the bounds.
   */
  public float top() {
    return this.top;
  }

  /**
   * Get the right coordinate of the bounds.
   *
   * @return The right coordinate of the bounds.
   */
  public float right() {
    return this.right;
  }

  /**
   * Get the bottom coordinate of the bounds.
   *
   * @return The bottom coordinate of the bounds.
   */
  public float bottom() {
    return this.bottom;
  }

  /**
   * Get the left coordinate of the bounds.
   *
   * @return The left coordinate of the bounds.
   */
  public float left() {
    return this.left;
  }

  /**
   * Check if the bounds contains the specified point.
   *
   * @param x The x-coordinate of the point.
   * @param y The y-coordinate of the point.
   * @return  A boolean indicating whether or not the bounds contain the
   *          specified point.
   */
  public boolean contains(final float x, final float y) {
    Geometry.Bounds bounds = new Geometry.Bounds(
      this.top,
      this.right,
      this.bottom,
      this.left
    );

    return bounds.contains(new Geometry.Point(x, y));
  }

  /**
   * Check if the bounds contains the specified {@link Node}.
   *
   * @param node  The node to check containment of.
   * @return      A boolean indicating whether or not the bounds contain the
   *              specified node.
   */
  public boolean contains(final Node node) {
    if (node == null) {
      return false;
    }

    return this.contains(node.x(), node.y());
  }

  /**
   * Check if the bounds contain the specified {@link Way}.
   *
   * @param way The way to check containment of.
   * @return    A boolean indicating whether or not the bounds contain the
   *            specified way.
   */
  public boolean contains(final Way way) {
    if (way == null) {
      return false;
    }

    return (
      this.contains(way.start().x(), way.start().y())
      &&
      this.contains(way.end().x(), way.end().y())
    );
  }

  /**
   * Get the JavaFX representation of the bounds.
   *
   * @return The JavaFX representation of the bounds.
   */
  public Rectangle render() {
    Rectangle rectangle = new Rectangle(
      this.left,
      this.top,
      Math.abs(this.right - this.left),
      Math.abs(this.top - this.bottom)
    );

    // Ensure that the bounds of the bounding box are calculated correctly. This
    // is only the case if both a stroke and a fill is set, otherwise
    // calculation of bounds will be off.
    rectangle.setStroke(Color.BLACK);
    rectangle.setFill(Color.BLACK);

    return rectangle;
  }
}
