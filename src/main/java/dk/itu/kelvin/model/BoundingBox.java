/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JavaFX shapes
import javafx.scene.shape.Rectangle;

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
   * The smallest x-coordinate of the bounds.
   */
  private float minX;

  /**
   * The smallest y-coordinate of the bounds.
   */
  private float minY;

  /**
   * The largest x-coordinate of the bounds.
   */
  private float maxX;

  /**
   * The largest y-coordinate of the bounds.
   */
  private float maxY;

  /**
   * Initialize a new bounding box.
   *
   * @param minX The smallest x-coordinate of the bounds.
   * @param minY The smallest y-coordinate of the bounds.
   * @param maxX The largest x-coordinate of the bounds.
   * @param maxY The largest y-coordinate of the bounds.
   */
  public BoundingBox(
    final float minX,
    final float minY,
    final float maxX,
    final float maxY
  ) {
    this.minX = Math.min(minX, maxX);
    this.minY = Math.min(minY, maxY);
    this.maxX = Math.max(minX, maxX);
    this.maxY = Math.max(minY, maxY);
  }

  /**
   * Get the smallest x-coordinate of the bounds.
   *
   * @return The smallest x-coordinate of the bounds.
   */
  public float minX() {
    return this.minX;
  }

  /**
   * Get the smallest y-coordinate of the bounds.
   *
   * @return The smallest y-coordinate of the bounds.
   */
  public float minY() {
    return this.minY;
  }

  /**
   * Get the largest x-coordinate of the bounds.
   *
   * @return The largest x-coordinate of the bounds.
   */
  public float maxX() {
    return this.maxX;
  }

  /**
   * Get the largest y-coordinate of the bounds.
   *
   * @return The largest y-coordinate of the bounds.
   */
  public float maxY() {
    return this.maxY;
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
    return (
      this.minX <= x
      && this.minY <= y
      && this.maxX >= x
      && this.maxY >= y
    );
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
      && this.contains(way.end().x(), way.end().y())
    );
  }

  /**
   * Get the JavaFX representation of the bounds.
   *
   * @return The JavaFX representation of the bounds.
   */
  public Rectangle render() {
    Rectangle rectangle = new Rectangle(
      this.minX,
      this.minY,
      Math.abs(this.maxX - this.minX),
      Math.abs(this.maxY - this.minY)
    );

    rectangle.getStyleClass().add("map");

    return rectangle;
  }

  /**
   * Check if the specified key/value pair should be included in the tags of
   * the bounds.
   *
   * @param key   The key to check.
   * @param value The value to check.
   * @return      A bollean indicating whether or not the specified key/value
   *              pair should be included in the tags of the bounds.
   */
  protected boolean include(final String key, final String value) {
    return false;
  }
}
