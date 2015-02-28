/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.component;

// JavaFX scene utilities
import javafx.scene.CacheHint;
import javafx.scene.Group;

// JavaFX transformations
import javafx.scene.transform.Affine;

/**
 * Canvas class.
 *
 * @version 1.0.0
 */
public final class Canvas extends Group {
  /**
   * Maximum zoom factor.
   */
  private static final double MAX_ZOOM_FACTOR = 4;

  /**
   * Minimum zoom factor.
   */
  private static final double MIN_ZOOM_FACTOR = 0.5;

  /**
   * The current zoom factor.
   */
  private double currentZoomFactor = 1;

  /**
   * Affine transformation instance.
   */
  private Affine transform = new Affine();

  /**
   * Initialize the canvas.
   */
  public Canvas() {
    this.setCache(true);
    this.setCacheHint(CacheHint.QUALITY);

    this.getTransforms().add(this.transform);
  }

  /**
   * Zoom the canvas.
   *
   * @param factor  The factor with which to zoom.
   * @param x       The x-coordinate of the pivot point.
   * @param y       The y-coordinate of the pivot point.
   */
  public void zoom(final double factor, final double x, final double y) {
    double newZoomFactor = this.currentZoomFactor * factor;

    if (factor > 1 && newZoomFactor >= MAX_ZOOM_FACTOR) {
      return;
    }

    if (factor < 1 && newZoomFactor <= MIN_ZOOM_FACTOR) {
      return;
    }

    this.currentZoomFactor *= factor;

    this.transform.prependScale(factor, factor, x, y);
  }

  /**
   * Zoom the canvas, using the center of the scene as the pivot point.
   *
   * @param factor The factor with which to zoom.
   */
  public void zoom(final double factor) {
    this.zoom(
      factor,
      this.getScene().getWidth() / 2,
      this.getScene().getHeight() / 2
    );
  }

  /**
   * Pan the canvas.
   *
   * @param x The amount to pan on the x-axis.
   * @param y The amount to pan on the y-axis.
   */
  public void pan(final double x, final double y) {
    this.transform.prependTranslation(x, y);
  }

  /**
   * Rotate the canvas.
   *
   * @param angle The angle of the rotation.
   * @param x     The x-coordinate of the pivot point.
   * @param y     The y-coordinate of the pivot point.
   */
  public void rotate(final double angle, final double x, final double y) {
    this.transform.prependRotation(angle, x, y);
  }

  /**
   * Rotate the canvas, using the center of the scene as the pivot point.
   *
   * @param angle The angle of the rotation.
   */
  public void rotate(final double angle) {
    this.rotate(
      angle,
      this.getScene().getWidth() / 2,
      this.getScene().getHeight() / 2
    );
  }
}
