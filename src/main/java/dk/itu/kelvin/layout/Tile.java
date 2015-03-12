/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// JavaFX scene utilities
import javafx.scene.CacheHint;
import javafx.scene.Group;

// Utilities
import dk.itu.kelvin.util.ArrayList;
import dk.itu.kelvin.util.List;

// Models
import dk.itu.kelvin.model.Element;

/**
 * Tile class.
 *
 * @version 1.0.0
 */
public final class Tile extends Group {
  /**
   * List of elements contained within the tile.
   */
  private List<Element> elements = new ArrayList<>();

  /**
   * Initialize a tile.
   */
  public Tile() {
    this.setCache(true);
    this.setCacheHint(CacheHint.SPEED);
  }

  /**
   * Add an element to the tile.
   *
   * @param element The element to add to the tile.
   */
  public void add(final Element element) {
    if (element == null) {
      return;
    }

    this.elements.add(element);
  }

  /**
   * Get a list of elements contained within the tile.
   *
   * @return A list of elements contained within the tile.
   */
  public List<Element> elements() {
    return this.elements;
  }

  public void show() {
    for (Element element: this.elements) {
      this.getChildren().add(element.render());
    }
  }

  public void hide() {
    this.getChildren().clear();
  }
}
