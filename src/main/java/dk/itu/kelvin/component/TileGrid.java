/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.component;

// General utilities
import java.util.ArrayList;
import java.util.List;

// JavaFX scene utilities
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;

// JavaFX geometry
import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;

/**
 * Tile grid class.
 *
 * @version 1.0.0
 */
public class TileGrid extends Group {
  /**
   * Add a tile to the tile grid.
   *
   * @param tile The tile to add.
   */
  public void tile(final Tile tile) {
    if (tile == null) {
      return;
    }

    this.getChildren().add(tile);
  }

  /**
   * Get a list of tiles contained within the tile grid.
   *
   * @return A list of tiles contained within the tile grid.
   */
  public List<Tile> tiles() {
    List<Tile> tiles = new ArrayList<>();

    for (Node node: this.getChildren()) {
      if (Tile.class.isAssignableFrom(node.getClass())) {
        tiles.add((Tile) node);
      }
    }

    return tiles;
  }

  /**
   * Check if a given tile is within the visible bounds of the tile grid.
   *
   * @param tile  The tile to check the visibility of.
   * @return      A boolean indicating whether or not the specified tile is
   *              within the visible bounds of the tile grid.
   */
  public boolean withinBounds(final Tile tile) {
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
}
