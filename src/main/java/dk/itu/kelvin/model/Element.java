/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Map;

/**
 * Interface that all elements of a chart must follow.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Element">
 * http://wiki.openstreetmap.org/wiki/Element</a>
 */
public interface Element extends Comparable<Element> {
  /**
   * Return the ID of this element.
   *
   * @return The ID of this element.
   */
  public long id();

  /**
   * Add a tag to the element.
   *
   * @param key   The key of the tag.
   * @param value The value of the tag.
   * @return      The previous value of the key, if any.
   */
  public String tag(final String key, final String value);

  /**
   * Get a map of tags for the element.
   *
   * @return A map of tags for the element.
   */
  public Map<String, String> tags();

  /**
   * Get the order of the element.
   *
   * @return The order of the element.
   */
  public Order order();

  /**
   * Set the order of the element.
   *
   * @param order The order of the element.
   */
  public void order(final Order order);

  /**
   * Get the layer of the element.
   *
   * @return The layer of the element.
   */
  public int layer();

  /**
   * Set the layer of the element.
   *
   * @param layer The layer of the element.
   */
  public void layer(final int layer);

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
  public static int compare(final Element a, final Element b) {
    if (a == null) {
      return -1;
    }

    if (b == null) {
      return 1;
    }

    return Integer.compare(
      a.order().ordinal() + (Order.size() * a.layer()),
      b.order().ordinal() + (Order.size() * b.layer())
    );
  }

  /**
   * Enumerator describing the drawing order of different elements.
   *
   * The declaration order of the enumerator elements is what determines the
   * drawing order. Elements declared first are thus drawn first and appear
   * below elements declared after.
   */
  public enum Order {
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
    private static final int SIZE = Order.values().length;

    /**
     * Return the number of enumerator elements.
     *
     * @return The number of enumrator elements.
     */
    public static int size() {
      return Order.SIZE;
    }

    /**
     * Convert a key and value to an enumerator element.
     *
     * @param key   The "key" of the enumerator element.
     * @param value The "value" of the enumerator element.
     * @return      The enumerator element if found, otherwise null;
     */
    public static Order fromString(final String key, final String value) {
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
