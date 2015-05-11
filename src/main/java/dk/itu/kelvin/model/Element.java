/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Comparator;
import java.util.Map;

// I/O utilities
import java.io.Serializable;

// JavaFX scene utilities
import javafx.scene.Node;

// Utilities
import dk.itu.kelvin.util.StringPool;

// Fast utils
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
   * Internal string pool for elements.
   */
  private static final StringPool STRING_POOL = new StringPool();

  /**
   * Comparator for comparing the drawing order and layer of two elements.
   */
  public static final Comparator<Element> COMPARATOR = (a, b) -> {
    return Element.compare(a, b);
  };

  /**
   * The initial capacity of the tables containing the element tags.
   *
   * <p>
   * The default initial capacity of hash tables is 16 slots. We rarely need
   * that many slots for tags so we lower the initial capacity substantially.
   */
  private static final int INITIAL_TAG_CAPACITY = 2;

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

    String k = key.trim();
    String v = value.trim();

    if (k.isEmpty() || v.isEmpty()) {
      return null;
    }

    if (this.tags == null) {
      this.tags = new Object2ObjectOpenHashMap<>(INITIAL_TAG_CAPACITY);
      //this.tags = HashObjObjMaps.newMutableMap(INITIAL_TAG_CAPACITY);
    }

    return this.tags.put(STRING_POOL.get(k), STRING_POOL.get(v));
  }

  /**
   * Get the value of the specified tag.
   *
   * @param key The key of the tag to get.
   * @return    The value of the specified tag.
   */
  public final String tag(final String key) {
    if (key == null || this.tags == null) {
      return null;
    }

    String k = key.trim();

    if (k.isEmpty()) {
      return null;
    }

    return this.tags.get(key);
  }

  /**
   * Get a map of tags for the element.
   *
   * @return A map of tags for the element.
   */
  public final Map<String, String> tags() {
    if (this.tags == null) {
      this.tags = new Object2ObjectOpenHashMap<>(INITIAL_TAG_CAPACITY);
      //this.tags = HashObjObjMaps.newMutableMap(INITIAL_TAG_CAPACITY);
    }

    return this.tags;
  }

  /**
   * Get the drawing order of the element.
   *
   * @return The drawing order of the element.
   */
  public final int order() {
    String v;

    if ((v = this.tag("land")) != null) {
      switch (v) {
        default: return -1;
      }
    }

    if ((v = this.tag("natural")) != null) {
      switch (v) {
        default: return 1;
      }
    }

    if ((v = this.tag("landuse")) != null) {
      switch (v) {
        case "military":  return 8;
        default:          return 2;
      }
    }

    if ((v = this.tag("waterway")) != null) {
      switch (v) {
        default: return 3;
      }
    }

    if ((v = this.tag("place")) != null) {
      switch (v) {
        case "island":  return 4;
        default:        return 5;
      }
    }

    if ((v = this.tag("leisure")) != null) {
      switch (v) {
        default: return 6;
      }
    }

    if ((v = this.tag("building")) != null) {
      switch (v) {
        default: return 7;
      }
    }

    if ((v = this.tag("highway")) != null) {
      switch (v) {
        case "path":
        case "bridleway":
        case "footway":
        case "cycleway":
        case "steps":
        case "track":         return 9;

        case "unclassified":  return 11;

        case "living_street":
        case "road":
        case "pedestrian":    return 12;

        case "service":       return 13;
        case "residential":   return 14;
        case "tertiary":      return 15;
        case "secondary":     return 16;
        case "primary":       return 17;
        case "trunk":         return 18;
        case "motorway":      return 19;

        default: return 10;
      }
    }

    return 0;
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

  /**
   * Compare two elements taking into account their drawing order and layer.
   *
   * @param a The first element.
   * @param b The second element.
   * @return  A negative integer, zero, or a positive integer as the first
   *          element is less than, equal to, or greater than the second
   *          element.
   */
  public static final int compare(final Element a, final Element b) {
    if (a == b) {
      return 0;
    }

    if (a == null) {
      return -1;
    }

    if (b == null) {
      return 1;
    }

    String als = a.tag("layer");
    String bls = b.tag("layer");

    int al = als != null ? Integer.parseInt(als) : 0;
    int bl = bls != null ? Integer.parseInt(bls) : 0;

    if (al == bl) {
      return Integer.compare(a.order(), b.order());
    }
    else {
      return Integer.compare(al, bl);
    }
  }
}
