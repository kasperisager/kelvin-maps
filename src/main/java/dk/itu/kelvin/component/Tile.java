/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.component;

// General utilities
import java.util.ArrayList;
import java.util.List;

// JavaFX scene utilities
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * Tile class.
 *
 * @version 1.0.0
 */
public final class Tile extends Group {
  /**
   * List of nodes contained within the tile.
   */
  private List<Node> nodes = new ArrayList<>();

  /**
   * Initialize a tile.
   */
  public Tile() {
    this.setCache(true);
    this.setCacheHint(CacheHint.SPEED);
  }

  /**
   * Add a node to the tile.
   *
   * @param node The node to add to the tile.
   */
  public void add(final Node node) {
    if (node == null) {
      return;
    }

    this.nodes.add(node);

    this.getChildren().add(node);
  }

  /**
   * Get a list of nodes contained within the tile.
   *
   * @return A list of nodes contained within the tile.
   */
  public List<Node> nodes() {
    return this.nodes;
  }
}
