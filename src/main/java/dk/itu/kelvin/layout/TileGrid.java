/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// General utilities
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX scene utilities
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;

// JavaFX geometry
import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;

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
   * Map of tiles contained within the tile grid.
   */
  private Map<TileGrid.Anchor, Tile> tiles = new HashMap<>();

  /**
   * Initialize a tile grid.
   *
   * @param affine The affine transformation associated with the tile grid.
   */
  public TileGrid(final Affine affine) {
    this.affine = affine;

    // Schedule the initial layout of the tiles.
    Platform.runLater(() -> {
      this.layoutTiles();
    });

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
   * @param anchor  The anchor associated with the tile.
   * @param tile    The tile to add.
   */
  public void add(final Anchor anchor, final Tile tile) {
    if (tile == null || anchor == null) {
      return;
    }

    this.tiles.put(anchor, tile);
  }

  /**
   * Get the tile at the specified anchor if it eixsts.
   *
   * @param anchor  The anchor to get the tile for.
   * @return        The tile at the specified anchor if it exists, otherwise
   *                null.
   */
  public Tile get(final Anchor anchor) {
    if (anchor == null) {
      return null;
    }

    return this.tiles.get(anchor);
  }

  /**
   * Check if the tile grid contains the specified anchor.
   *
   * @param anchor  The anchor to check for.
   * @return        A boolean indicating whether or not the anchor is contained
   *                within the tile grid.
   */
  public boolean contains(final Anchor anchor) {
    if (anchor == null) {
      return false;
    }

    return this.tiles.containsKey(anchor);
  }

  /**
   * Get a list of tiles contained within the tile grid.
   *
   * @return A list of tiles contained within the tile grid.
   */
  public List<Tile> tiles() {
    return new ArrayList<Tile>(this.tiles.values());
  }

  /**
   * Add a tile to the tile grid scene graph.
   *
   * @param tile The tile to add.
   */
  private void render(final Tile tile) {
    tile.setCache(true);
    tile.setCacheHint(CacheHint.SPEED);

    // Schedule an addition of the tile from the scene graph.
    Platform.runLater(() -> {
      if (!this.getChildren().contains(tile)) {
        this.getChildren().add(tile);
      }
    });
  }

  /**
   * Remove a tile from the tile grid scene graph.
   *
   * @param tile The tile to remove.
   */
  private void remove(final Tile tile) {
    tile.setCache(false);

    // Schedule a removal of the tile from the scene graph.
    Platform.runLater(() -> {
      if (this.getChildren().contains(tile)) {
        this.getChildren().remove(tile);
      }
    });
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
    for (Tile tile: this.tiles()) {
      if (this.withinBounds(tile)) {
        this.render(tile);
      }
      else {
        this.remove(tile);
      }
    }
  }

  /**
   * An anchor is a reference point for a tile in a tile grid.
   */
  public static class Anchor extends Point2D {
    /**
     * Initialize a tile achor.
     *
     * @param x The x-coordinate of the anchor.
     * @param y The y-coordiante of the anchor.
     */
    public Anchor(final int x, final int y) {
      super((double) x, (double) y);
    }
  }
}
