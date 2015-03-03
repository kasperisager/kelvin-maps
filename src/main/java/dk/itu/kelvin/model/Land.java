/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// JavaFX scene utilities
import javafx.scene.Group;

/**
 * Land class.
 *
 * @version 1.0.0
 */
public final class Land extends Group implements Element {
  /**
   * ID of the land element.
   */
  private long id;

  /**
   * Tags of the land element.
   */
  private Map<String, String> tags = new HashMap<>();

  /**
   * Drawing order of the land element.
   */
  private Element.Order order = Element.Order.DEFAULT;

  /**
   * Drawing layer of the land element.
   */
  private int layer = -999;

  /**
   * Bounds of the land element.
   */
  private Bounds bounds;

  /**
   * Coastlines contained within the land element.
   */
  private List<Way> coastlines = new ArrayList<>();

  /**
   * Intersections between the coastlines and the bounds of the land.
   */
  // private Map<Node, Intersection> intersections = new HashMap<>();

  /**
   * Initialize a land instance.
   */
  public Land() {
    this.getStyleClass().add("land");
  }

  /**
   * Initialize a land instance with initial bounds.
   *
   * @param bounds The bounds of the land.
   */
  public Land(final Bounds bounds) {
    this();
    this.bounds = bounds;
  }

  /**
   * Get the ID of the land element.
   *
   * @return The ID of the land element.
   */
  public long id() {
    return this.id;
  }

  /**
   * Get a map of tags associated with the element.
   *
   * @return A map of tags associated with the element.
   */
  public Map<String, String> tags() {
    return this.tags;
  }

  /**
   * Add a tag to the element.
   *
   * @param key   The key of the tag.
   * @param value The value of the tag.
   * @return      The previous value of the tag, if any.
   */
  public String tag(final String key, final String value) {
    if (key == null || value == null) {
      return null;
    }

    return this.tags.put(key, value);
  }

  /**
   * Get the drawing order of the element.
   *
   * @return The drawing order of the element.
   */
  public Element.Order order() {
    return this.order;
  }

  /**
   * Set the drawing order of the element.
   *
   * @param order The drawing order of the element.
   */
  public void order(final Element.Order order) {
    if (order == null) {
      return;
    }

    this.order = order;
  }

  /**
   * Get the drawing layer of the element.
   *
   * @return The drawng layer of the element.
   */
  public int layer() {
    return this.layer;
  }

  /**
   * Set the drawing layer of the element.
   *
   * @param layer The drawing layer of the element.
   */
  public void layer(final int layer) {
    this.layer = layer;
  }

  /**
   * Get the bounds of the element.
   *
   * @return The bounds of the element.
   */
  public Bounds bounds() {
    return this.bounds;
  }

  /**
   * Add a coastline to the land.
   *
   * @param way The coastline way to add to the land.
   */
  public void coastline(final Way way) {
    if (way == null) {
      return;
    }

    Way coastline = way;

    Iterator<Way> i = this.coastlines.iterator();

    while (i.hasNext()) {
      Way next = i.next();

      if (coastline.endsIn(next)) {
        coastline.append(next);
        i.remove();
      }
      else if (coastline.startsIn(next)) {
        next.append(coastline);
        coastline = next;
        i.remove();
      }
    }

    this.coastlines.add(coastline);
  }

  /**
   * Get the coastlines contained within the land.
   *
   * @return The coastlines contained within the land.
   */
  public List<Way> coastlines() {
    return this.coastlines;
  }

  /**
   * Enumerator describing the intersections between coastlines and the land
   * bounds.
   */
  private enum Intersection {
    /** Intersection of bounds and start node. */
    START,

    /** Intersection of bounds and end node. */
    END;
  }
}
