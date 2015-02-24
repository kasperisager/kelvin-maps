/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JavaFX shapes
import javafx.scene.shape.Rectangle;

// JavaFX paint
import javafx.scene.paint.Color;

/**
 * A bounding box (bounds for short) describes the bounds of a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Bounding_Box">
 *      http://wiki.openstreetmap.org/wiki/Bounding_Box</a>
 */
public final class Bounds extends Rectangle {
  /**
   * Initialize new bounds.
   *
   * @param left    The left coordinate of the bounds.
   * @param bottom  The bottom coordinate of the bounds.
   * @param right   The right coordinate of the bounds.
   * @param top     The top coordinate of the bounds.
   */
  public Bounds(
    final float left,
    final float bottom,
    final float right,
    final float top
  ) {
    super(left, top, Math.abs(right - left), Math.abs(top - bottom));

    this.setFill(null);
    this.setStroke(Color.TRANSPARENT);
  }
}
