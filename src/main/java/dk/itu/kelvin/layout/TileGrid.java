/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX scene utilities
import javafx.scene.Group;
import javafx.scene.Scene;

// JavaFX geometry
import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;

// Utilities
import dk.itu.kelvin.util.ArrayList;
import dk.itu.kelvin.util.HashTable;
import dk.itu.kelvin.util.List;
import dk.itu.kelvin.util.Map;

// Models
import dk.itu.kelvin.model.Element;

/**
 * Tile grid class.
 *
 * @version 1.0.0
 */
public final class TileGrid extends Group {
  /**
   * The size of each tile within the tile grid.
   */
  private static final int TILE_SIZE = 256;

  /**
   * Map of tiles contained within the tile grid.
   */
  private Map<Anchor, Tile> tiles = new HashTable<>();

  /**
   * Add an element to the tile grid.
   *
   * @param <E>     The type of the element.
   * @param element The element to add to the tile grid.
   */
  public <E extends Element> void add(final E element) {
    Bounds bounds = element.render().getBoundsInParent();

    int x = (int) (TILE_SIZE * Math.floor(
      Math.round(bounds.getMaxX() / TILE_SIZE)
    ));

    int y = (int) (TILE_SIZE * Math.floor(
      Math.round(bounds.getMaxY() / TILE_SIZE)
    ));

    Anchor anchor = new Anchor(x, y);

    if (this.tiles.containsKey(anchor)) {
      this.tiles.get(anchor).add(element);
    }
    else {
      Tile tile = new Tile();
      tile.add(element);
      this.tiles.put(anchor, tile);
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
  public synchronized void layoutTiles() {
    for (Tile tile: this.tiles.values()) {
      if (this.withinBounds(tile)) {
        // Schedule an addition of the tile from the scene graph.
        Platform.runLater(() -> {
          if (!this.getChildren().contains(tile)) {
            this.getChildren().add(tile);
          }
        });
      }
      else {
        // Schedule a removal of the tile from the scene graph.
        Platform.runLater(() -> {
          if (this.getChildren().contains(tile)) {
            this.getChildren().remove(tile);
          }
        });
      }
    }
  }

  /**
   * An anchor is a reference point for a tile in a tile grid.
   */
  private static final class Anchor {
    /**
     * The x-coordinate of the anchor.
     */
    private int x;

    /**
     * The y-coordinate of the anchor.
     */
    private int y;

    /**
     * Initialize a tile achor.
     *
     * @param x The x-coordinate of the anchor.
     * @param y The y-coordiante of the anchor.
     */
    public Anchor(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    /**
     * Check if the current anchor equals the specified object.
     *
     * @param object  The reference object with which to compare.
     * @return        Boolean indicating whether or not the anchor is equal to
     *                the specified object.
     */
    @Override
    public boolean equals(final Object object) {
      if (object == null || !(object instanceof Anchor)) {
        return false;
      }

      if (this == object) {
        return true;
      }

      Anchor anchor = (Anchor) object;

      return this.x == anchor.x && this.y == anchor.y;
    }

    /**
     * Compute the hashcode of the anchor.
     *
     * @return The computed hashcode of the anchor.
     */
    @Override
    public int hashCode() {
      long bits = 7L;
      bits = 31L * bits + this.x;
      bits = 31L * bits + this.y;

      return (int) (bits ^ (bits >> 32));
    }
  }

  /**
   * Tile class.
   *
   * @version 1.0.0
   */
  private static final class Tile extends Group {
    /**
     * List of elements contained within the tile.
     */
    private List<Element> elements;

    /**
     * Add an element to the tile.
     *
     * @param element The element to add to the tile.
     */
    public void add(final Element element) {
      if (element == null) {
        return;
      }

      if (this.elements == null) {
        this.elements = new ArrayList<>();
      }

      this.elements.add(element);
    }
  }
}
