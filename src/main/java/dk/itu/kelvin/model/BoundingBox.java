/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// I/O utilities
import java.io.Serializable;

// JavaFX shapes
import javafx.scene.shape.Rectangle;

/**
 * A bounding box describes the bounds of a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Bounding_Box">
 *      http://wiki.openstreetmap.org/wiki/Bounding_Box</a>
 */
public final class BoundingBox extends Rectangle implements Serializable {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 19;

  /**
   * Initialize a new empty bounding box.
   */
  public BoundingBox() {
    super(0, 0, -1, -1);
  }

  /**
   * Initialize a new bounding box.
   *
   * @param left    The left coordinate of the bounds.
   * @param bottom  The bottom coordinate of the bounds.
   * @param right   The right coordinate of the bounds.
   * @param top     The top coordinate of the bounds.
   */
  public BoundingBox(
    final float left,
    final float bottom,
    final float right,
    final float top
  ) {
    super(left, top, Math.abs(right - left), Math.abs(top - bottom));
  }

  /**
   * Get the smallest x-coodinate of the bounds.
   *
   * @return The smallest x-coordinate of the bounds.
   */
  public double getMinX() {
    return this.getX();
  }

  /**
   * Get the largest x-coordinate of the bounds.
   *
   * @return The largest x-coordinate of the bounds.
   */
  public double getMaxX() {
    return this.getX() + this.getWidth();
  }

  /**
   * Get the smallest y-coordinate of the bounds.
   *
   * @return The smallest y-coordinate of the bounds.
   */
  public double getMinY() {
    return this.getY();
  }

  /**
   * Get the largest y-coordinate of the bounds.
   *
   * @return The largest y-coordinate of the bounds.
   */
  public double getMaxY() {
    return this.getY() + this.getHeight();
  }
}
