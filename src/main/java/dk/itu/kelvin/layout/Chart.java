/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// JavaFX scene utilities
import javafx.scene.Group;

// JavaFX transformations
import javafx.scene.transform.Affine;

// Utilities
import dk.itu.kelvin.util.Collection;

// Models
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.model.Element;
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Land;

/**
 * Chart class.
 *
 * @version 1.0.0
 */
public final class Chart extends Group {
  /**
   * Maximum zoom factor.
   */
  private static final double MAX_ZOOM_FACTOR = 4;

  /**
   * Minimum zoom factor.
   */
  private static final double MIN_ZOOM_FACTOR = 0.5;

  /**
   * Affine transformation instance.
   */
  private Affine transform = new Affine();

  /**
   * The bounds of the chart.
   */
  private BoundingBox bounds;

  /**
   * Land polygons.
   */
  private TileGrid land = new TileGrid(this.transform);

  /**
   * The tile grid containing all the elements within the chart.
   */
  private TileGrid elements = new TileGrid(this.transform);

  /**
   * Meta layer.
   */
  private TileGrid meta = new TileGrid(this.transform);

  /**
   * Initialize the chart.
   */
  public Chart() {
    this.getTransforms().add(this.transform);

    this.getChildren().add(this.land);
    this.getChildren().add(this.elements);
    this.getChildren().add(this.meta);
  }

  /**
   * Add bounds to the chart.
   *
   * @param bounds The bounds to add to the chart.
   */
  public void add(final BoundingBox bounds) {
    if (bounds == null) {
      return;
    }

    this.bounds = bounds;

    this.pan(-this.bounds.left(), -this.bounds.top());
    this.setClip(this.bounds.render());
  }

  /**
   * Add a land polygon to the chart.
   *
   * @param land The land polygon to add to the chart.
   */
  public void add(final Land land) {
    this.land.add(land);
  }

  /**
   * Add a node element to the chart.
   *
   * @param node The node element to add to the chart.
   */
  public void add(final Node node) {
    this.meta.add(node);
  }

  /**
   * Add an element to the chart.
   *
   * @param element The element to add to the chart.
   */
  public void add(final Element element) {
    this.elements.add(element);
  }

  /**
   * Add a collection of elements to the chart.
   *
   * @param <E>      The type of element the collection contains.
   * @param elements The collection of elements to add to the chart.
   */
  public <E extends Element> void add(final Collection<E> elements) {
    if (elements == null) {
      return;
    }

    for (Element element: elements) {
      this.add(element);
    }
  }

  /**
   * Zoom the chart.
   *
   * @param factor  The factor with which to zoom.
   * @param x       The x-coordinate of the pivot point.
   * @param y       The y-coordinate of the pivot point.
   */
  public void zoom(final double factor, final double x, final double y) {
    double newZoomFactor = this.transform.getMxx() * factor;

    if (factor > 1 && newZoomFactor >= MAX_ZOOM_FACTOR) {
      return;
    }

    if (factor < 1 && newZoomFactor <= MIN_ZOOM_FACTOR) {
      return;
    }

    this.transform.prependScale(factor, factor, x, y);
  }

  /**
   * Zoom the chart, using the center of the scene as the pivot point.
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
   * Pan the chart.
   *
   * @param x The amount to pan on the x-axis.
   * @param y The amount to pan on the y-axis.
   */
  public void pan(final double x, final double y) {
    this.transform.prependTranslation(x, y);
  }

  /**
   * Rotate the chart.
   *
   * @param angle The angle of the rotation.
   * @param x     The x-coordinate of the pivot point.
   * @param y     The y-coordinate of the pivot point.
   */
  public void rotate(final double angle, final double x, final double y) {
    this.transform.prependRotation(angle, x, y);
  }

  /**
   * Rotate the chart, using the center of the scene as the pivot point.
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
