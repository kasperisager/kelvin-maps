/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// I/O utilities
import java.io.Serializable;

// JavaFX scene utilities
import javafx.scene.Node;

// Utilities
import dk.itu.kelvin.util.HashTable;
import dk.itu.kelvin.util.Map;

/**
 * Abstract base class that all OSM Elements must extend.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Element">
 *      http://wiki.openstreetmap.org/wiki/Element</a>
 *
 * @param <T> The type of JavaFX Node that the element represents.
 */
public abstract class Element<T extends Node> implements Serializable {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 42;

  /**
   * A map of tags associated with the element.
   *
   * <p>
   * The map is initialized on-demand when first accessed to avoid allocating
   * memory to empty maps.
   */
  private Map<String, String> tags;

  /**
   * Add a tag to the element.
   *
   * @param key   The key of the tag.
   * @param value The value of the tag.
   * @return      The previous value of the key, if any.
   */
  public final String tag(final String key, final String value) {
    if (key == null || value == null) {
      return null;
    }

    if (this.tags == null) {
      this.tags = new HashTable<>();
    }

    return this.tags.put(key, value);
  }

  /**
   * Get a map of tags for the element.
   *
   * @return A map of tags for the element.
   */
  public final Map<String, String> tags() {
    if (this.tags == null) {
      this.tags = new HashTable<>();
    }

    return this.tags;
  }

  /**
   * Get a JavaFX representation of the element.
   *
   * <p>
   * This method can be called from the JavaFX thread whenever it wants to
   * draw the element.
   *
   * @return A JavaFX representation of the element.
   */
  public abstract T render();
}
