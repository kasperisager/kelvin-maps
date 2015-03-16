/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Comparator;

// I/O utilities
import java.io.Serializable;

// JavaFX scene utilities
import javafx.scene.Node;

// Utilities
import dk.itu.kelvin.util.HashTable;
import dk.itu.kelvin.util.Map;

/**
 * Interface that all elements of a chart must follow.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Element">
 * http://wiki.openstreetmap.org/wiki/Element</a>
 *
 * @param <T> The type of JavaFX Node that the element represents.
 */
public abstract class Element<T extends Node> implements Serializable {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 42;

  /**
   * Comparator for comparing the drawing layer and order of two elements.
   */
  public static final Comparator<Element> COMPARATOR =
    new Comparator<Element>() {
    /**
     * Compare two elements taking into account their drawing layer and
     * drawing order.
     *
     * @param a The first element.
     * @param b The second element.
     * @return  A negative integer, zero, or a positive integer as the first
     *          element is less than, equal to, or greater than the second
     *          element.
     */
    @Override
    public int compare(final Element a, final Element b) {
      return Element.compare(a, b);
    }
  };

  /**
   * The ID of the node.
   */
  private long id;

  /**
   * A map of tags associated with the element.
   *
   * The map is initialized on-demand when first accessed to avoid allocating
   * memory to empty maps.
   */
  private Map<String, String> tags;

  /**
   * Drawing order of the element.
   *
   * The order is initialized on-demand when first accessed to avoid allocating
   * memory to never-used orders.
   */
  private Element.Order order;

  /**
   * Drawing layer of the element.
   */
  private int layer;

  /**
   * Initialize an element.
   *
   * @param id The ID of the element.
   */
  public Element(final long id) {
    this.id = id;
  }

  /**
   * Get the ID of the element.
   *
   * @return The ID of the element.
   */
  public final long id() {
    return this.id;
  }

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
   * Get the order of the element.
   *
   * @return The order of the element.
   */
  public final Order order() {
    if (this.order == null) {
      return Element.Order.DEFAULT;
    }

    return this.order;
  }

  /**
   * Set the order of the element.
   *
   * @param order The order of the element.
   */
  public final void order(final Order order) {
    if (order == null) {
      return;
    }

    this.order = order;
  }

  /**
   * Get the layer of the element.
   *
   * @return The layer of the element.
   */
  public final int layer() {
    return this.layer;
  }

  /**
   * Set the layer of the element.
   *
   * @param layer The layer of the element.
   */
  public final void layer(final int layer) {
    this.layer = layer;
  }

  /**
   * Get a JavaFX representation of the element.
   *
   * This method can be called from the JavaFX thread whenever it wants to
   * draw the element.
   *
   * @return A JavaFX representation of the element.
   */
  public abstract T render();

  /**
   * Compare two elements taking into account their drawing layer and drawing
   * order.
   *
   * @param a The first element.
   * @param b The second element.
   * @return  A negative integer, zero, or a positive integer as the first
   *          element is less than, equal to, or greater than the second
   *          element.
   */
  public static final int compare(final Element a, final Element b) {
    if (a == null && b == null) {
      return 0;
    }
    else if (a == null) {
      return -1;
    }
    else if (b == null) {
      return 1;
    }

    return Integer.compare(
      a.order().ordinal() + (Order.SIZE * a.layer()),
      b.order().ordinal() + (Order.SIZE * b.layer())
    );
  }

  /**
   * Enumerator describing the drawing order of different elements.
   *
   * The declaration order of the enumerator elements is what determines the
   * drawing order. Elements declared first are thus drawn first and appear
   * below elements declared after.
   */
  public static enum Order {
    /** Water draw order. */
    NATURAL_WATER,

    /** Default draw order. */
    DEFAULT,

    /** Default natural draw order. */
    NATURAL,

    /** Default landuse draw order. */
    LANDUSE,

    /** Default waterway draw order. */
    WATERWAY,

    /** Islands. */
    PLACE_ISLAND,

    /** Default leisure draw order. */
    LEISURE,

    /** Default highway draw order. */
    HIGHWAY,

    /** Building draw order. */
    BUILDING,

    /** Service highway draw order. */
    HIGHWAY_SERVICE,

    /** Residential highway draw order. */
    HIGHWAY_RESIDENTIAL,

    /** Tertiary highway draw order. */
    HIGHWAY_TERTIARY,

    /** Secondary highway draw order. */
    HIGHWAY_SECONDARY,

    /** Primary highway draw order. */
    HIGHWAY_PRIMARY,

    /** Trunk highway draw order. */
    HIGHWAY_TRUNK,

    /** Motorway highway draw order. */
    HIGHWAY_MOTORWAY;

    /**
     * Number of enumerator elements.
     */
    public static final int SIZE = Order.values().length;

    /**
     * Convert a key and value to an enumerator element.
     *
     * @param key   The "key" of the enumerator element.
     * @param value The "value" of the enumerator element.
     * @return      The enumerator element if found, otherwise null;
     */
    public static final Order fromString(final String key, final String value) {
      if (key == null || value == null) {
        return null;
      }

      try {
        return Order.valueOf(String.format(
          "%s_%s",
          key.toUpperCase(),
          value.toUpperCase()
        ));
      }
      catch (Exception ex1) {
        try {
          return Order.valueOf(key.toUpperCase());
        }
        catch (Exception ex2) {
          return null;
        }
      }
    }
  }
}
