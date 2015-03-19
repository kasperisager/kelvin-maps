/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// JavaFX scene utilities
import javafx.scene.Group;

// JavaFX geometry
import javafx.geometry.Bounds;

// JavaFX transformations
import javafx.scene.transform.Affine;

// Utilities
import dk.itu.kelvin.util.Collection;

// Models
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.model.Element;

/**
 * Chart class.
 *
 * @version 1.0.0
 */
public final class Chart extends Group {
  /**
   * The size of each tile within the tile grid.
   */
  private static final int TILE_SIZE = 256;

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
   * The tile grid containing all the elements within the chart.
   */
  private TileGrid tileGrid = new TileGrid(this.transform);

  /**
   * Initialize the chart.
   */
  public Chart() {
    this.getTransforms().add(this.transform);
    this.getChildren().add(this.tileGrid);
  }

  /**
   * Get the bounds of the chart.
   *
   * @return The bounds of the chart.
   */
  public BoundingBox bounds() {
    return this.bounds;
  }

  /**
   * Set the bounds of the chart.
   *
   * @param bounds The bounds of the chart.
   */
  public void bounds(final BoundingBox bounds) {
    if (bounds == null) {
      return;
    }

    this.bounds = bounds;

    this.pan(-bounds.left(), -bounds.top());
  }

  /**
   * Add an element to the chart.
   *
   * @param element The element to add to the chart.
   */
  public void add(final Element element) {
    Bounds bounds = element.render().getBoundsInParent();

    int x = (int) (TILE_SIZE * Math.floor(
      Math.round(bounds.getMaxX() / TILE_SIZE)
    ));

    int y = (int) (TILE_SIZE * Math.floor(
      Math.round(bounds.getMaxY() / TILE_SIZE)
    ));

    TileGrid.Anchor anchor = new TileGrid.Anchor(x, y);

    if (this.tileGrid.contains(anchor)) {
      this.tileGrid.get(anchor).add(element);
    }
    else {
      Tile tile = new Tile();
      tile.add(element);
      this.tileGrid.add(anchor, tile);
    }
  }

  /**
   * Add a collection of elements to the chart.
   *
   * @param elements The collection of elements to add to the chart.
   */
  public void add(final Collection<Element> elements) {
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
