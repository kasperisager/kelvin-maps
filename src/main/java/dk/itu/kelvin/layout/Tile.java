/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// JavaFX scene utilities
import javafx.scene.Group;

// Models
import dk.itu.kelvin.model.Element;

/**
 * Tile class.
 *
 * @version 1.0.0
 */
public final class Tile extends Group {
  /**
   * Add an element to the tile.
   *
   * @param element The element to add to the tile.
   */
  public void add(final Element element) {
    if (element == null) {
      return;
    }

    this.getChildren().add(element.render());
  }
}
