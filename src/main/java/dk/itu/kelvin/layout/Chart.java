/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// JavaFX scene utilities
import javafx.scene.Group;

// JavaFX geometry
import javafx.geometry.Bounds;

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
   * The bounds of the chart.
   */
  private BoundingBox bounds;

  /**
   * Land polygons.
   */
  private Group land = new Group();

  /**
   * The tile grid containing all the elements within the chart.
   */
  private TileGrid elements = new TileGrid();

  /**
   * Meta layer.
   */
  private TileGrid meta = new TileGrid();

  /**
   * Initialize the chart.
   */
  public Chart() {
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
    this.land.getChildren().add(land.render());
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
   * Pan the chart.
   *
   * @param x The amount to pan on the x-axis.
   * @param y The amount to pan on the y-axis.
   */
  public void pan(final double x, final double y) {
    this.setTranslateX(this.getTranslateX() + x);
    this.setTranslateY(this.getTranslateY() + y);
  }

  /**
   * Zoom the chart.
   *
   * @param factor  The factor with which to zoom.
   * @param x       The x-coordinate of the pivot point.
   * @param y       The y-coordinate of the pivot point.
   */
  public void zoom(final double factor, final double x, final double y) {
    double oldScale = this.getScaleX();
    double newScale = oldScale * factor;

    if (factor > 1 && newScale >= MAX_ZOOM_FACTOR) {
      return;
    }

    if (factor < 1 && newScale <= MIN_ZOOM_FACTOR) {
      return;
    }

    this.setScaleX(newScale);
    this.setScaleY(newScale);

    // Calculate the difference between the new and the old scale.
    double f = (newScale / oldScale) - 1;

    // Get the layout bounds of the chart in local coordinates.
    Bounds bounds = this.localToScene(this.getLayoutBounds());

    double dx = x - (bounds.getMinX() + bounds.getWidth() / 2);
    double dy = y - (bounds.getMinY() + bounds.getHeight() / 2);

    this.setTranslateX(this.getTranslateX() - f * dx);
    this.setTranslateY(this.getTranslateY() - f * dy);
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
   * Rotate the chart.
   *
   * @param angle The angle of the rotation.
   * @param x     The x-coordinate of the pivot point.
   * @param y     The y-coordinate of the pivot point.
   */
  public void rotate(final double angle, final double x, final double y) {
    double oldAngle = this.getRotate();
    double newAngle = oldAngle + angle;

    this.setRotate(newAngle);

    Bounds bounds = this.localToScene(this.getLayoutBounds());

    double dx = x - (bounds.getMinX() + bounds.getWidth() / 2);
    double dy = y - (bounds.getMinY() + bounds.getHeight() / 2);
    double dt = Math.toRadians(newAngle - oldAngle);

    this.setTranslateX(
      this.getTranslateX()
    + (dx - dx * Math.cos(dt) + dy * Math.sin(dt))
    );

    this.setTranslateY(
      this.getTranslateY()
    + (dy - dx * Math.sin(dt) - dy * Math.cos(dt))
    );
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
