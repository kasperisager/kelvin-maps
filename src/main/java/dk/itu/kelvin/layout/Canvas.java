/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// General utilities
import java.util.Collection;

// JavaFX scene utilities
import javafx.scene.Group;
import javafx.scene.Node;

// JavaFX geometry
import javafx.geometry.Bounds;

// JavaFX transformations
import javafx.scene.transform.Affine;

/**
 * Canvas class.
 *
 * @version 1.0.0
 */
public final class Canvas extends Group {
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
   * The tile grid containing all the elements within the canvas.
   */
  private TileGrid tileGrid = new TileGrid(this.transform);

  /**
   * Initialize the canvas.
   */
  public Canvas() {
    this.getTransforms().add(this.transform);
    this.getChildren().add(this.tileGrid);
  }

  /**
   * Add a node to the canvas.
   *
   * @param node The node to add to the canvas.
   */
  public void add(final Node node) {
    Bounds bounds = node.getBoundsInParent();

    int x = (int) (TILE_SIZE * Math.floor(
      Math.round(bounds.getMaxX() / TILE_SIZE)
    ));

    int y = (int) (TILE_SIZE * Math.floor(
      Math.round(bounds.getMaxY() / TILE_SIZE)
    ));

    TileGrid.Anchor anchor = new TileGrid.Anchor(x, y);

    if (this.tileGrid.contains(anchor)) {
      this.tileGrid.get(anchor).add(node);
    }
    else {
      Tile tile = new Tile();
      tile.add(node);
      this.tileGrid.add(anchor, tile);
    }
  }

  /**
   * Add a collection of nodes to the canvas.
   *
   * @param nodes The collection of nodes to add to the canvas.
   */
  public void add(final Collection<Node> nodes) {
    if (nodes == null) {
      return;
    }

    for (Node node: nodes) {
      this.add(node);
    }
  }

  /**
   * Zoom the canvas.
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
