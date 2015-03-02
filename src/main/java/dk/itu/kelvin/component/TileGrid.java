/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.component;

// General utilities
import java.util.ArrayList;
import java.util.List;

// JavaFX scene utilities
import javafx.scene.Group;
import javafx.scene.Scene;

// JavaFX geometry
import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;

// JavaFX transformations
import javafx.scene.transform.Affine;

/**
 * Tile grid class.
 *
 * @version 1.0.0
 */
public final class TileGrid extends Group {
  /**
   * The affine transformation associated with the tile grid.
   *
   * Whenever this transformation changes, the tile grid will adjust its tiles
   * accordingly.
   */
  private Affine affine;

  /**
   * List of tiles contained within the tile grid.
   */
  private List<Tile> tiles = new ArrayList<>();

  /**
   * Initialize a tile grid.
   *
   * @param affine The affine transformation associated with the tile grid.
   */
  public TileGrid(final Affine affine) {
    this.affine = affine;

    this.layoutTiles();

    this.affine.txProperty().addListener((ob, ov, nv) -> {
      this.layoutTiles();
    });

    this.affine.tyProperty().addListener((ob, ov, nv) -> {
      this.layoutTiles();
    });
  }

  /**
   * Add a tile to the tile grid.
   *
   * @param tile The tile to add.
   */
  public void tile(final Tile tile) {
    if (tile == null) {
      return;
    }

    this.tiles.add(tile);
  }

  /**
   * Get a list of tiles contained within the tile grid.
   *
   * @return A list of tiles contained within the tile grid.
   */
  public List<Tile> tiles() {
    return this.tiles;
  }

  /**
   * Add a tile to the tile grid scene graph.
   *
   * @param tile The tile to add.
   */
  private void add(final Tile tile) {
    if (!this.getChildren().contains(tile)) {
      this.getChildren().add(tile);
    }
  }

  /**
   * Remove a tile from the tile grid scene graph.
   *
   * @param tile The tile to remove.
   */
  private void remove(final Tile tile) {
    if (this.getChildren().contains(tile)) {
      this.getChildren().remove(tile);
    }
  }

  /**
   * Check if a given tile is within the visible bounds of the tile grid.
   *
   * @param tile  The tile to check the visibility of.
   * @return      A boolean indicating whether or not the specified tile is
   *              within the visible bounds of the tile grid.
   */
  private boolean withinBounds(final Tile tile) {
    Scene scene = this.getScene();

    if (scene == null) {
      return false;
    }

    // Create a bounding box corresponding to the bounds of the scene.
    Bounds sceneBounds = new BoundingBox(
      0, 0,
      scene.getWidth(),
      scene.getHeight()
    );

    // Get the bounds of the tile relatively to the bounds of the scene.
    Bounds tileBounds = this.localToScene(
      tile.getBoundsInParent()
    );

    // If the bounds of the scene and tile intersect, the tile will be within
    // the bounds of the scene.
    return sceneBounds.intersects(tileBounds);
  }

  /**
   * Add the visible tiles to the tile grid.
   */
  private void layoutTiles() {
    for (Tile tile: this.tiles) {
      if (this.withinBounds(tile)) {
        this.add(tile);
      }
      else {
        this.remove(tile);
      }
    }
  }
}
