/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

/**
 * A bounding box describes the bounds of a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Bounding_Box">
 *      http://wiki.openstreetmap.org/wiki/Bounding_Box</a>
 */
public final class BoundingBox extends javafx.geometry.BoundingBox {
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
}
