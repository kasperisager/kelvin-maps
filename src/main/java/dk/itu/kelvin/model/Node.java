/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.HashMap;
import java.util.Map;

// JavaFX geometry
import javafx.geometry.Point2D;

/**
 * A node describes a 2-dimensional coordinate within a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Node">
 *      http://wiki.openstreetmap.org/wiki/Node</a>
 */
public final class Node extends Point2D implements Element {
  /**
   * The ID of the node.
   */
  private long id;

  /**
   * A map of tags associated with the node.
   */
  private Map<String, String> tags = new HashMap<>();

  /**
   * Drawing order of the node.
   */
  private Element.Order order = Element.Order.DEFAULT;

  /**
   * Drawing layer of the node.
   */
  private int layer;

  /**
   * Initialize a node.
   *
   * @param id  The ID of the node.
   * @param x   The x-coordinate of the node.
   * @param y   The y-coordinate of the node.
   */
  public Node(final long id, final float x, final float y) {
    super(x, y);
    this.id = id;
  }

  /**
   * Get the ID of the node.
   *
   * @return The ID of the node.
   */
  public long id() {
    return this.id;
  }

  /**
   * Add a tag to the node.
   *
   * @param key   The key of the tag.
   * @param value The value of the tag.
   * @return      The previous value of the key, if any.
   */
  public String tag(final String key, final String value) {
    return this.tags.put(key, value);
  }

  /**
   * Get a map of tags for the node.
   *
   * @return A map of tags for the node.
   */
  public Map<String, String> tags() {
    return this.tags;
  }

  /**
   * Get the drawing order of the node.
   *
   * @return The drawing order of the node.
   */
  public Element.Order order() {
    return this.order;
  }

  /**
   * Set the drawing order of the node.
   *
   * @param order The drawing order of the node.
   */
  public void order(final Element.Order order) {
    if (order == null) {
      return;
    }

    this.order = order;
  }

  /**
   * Compare the drawing order of this node with the drawing order of another
   * element.
   *
   * @param element The element to compare the current node to.
   * @return        A negative integer, zero, or a positive integer as this node
   *                is less than, equal to, or greater than the specified
   *                element.
   */
  public int compareTo(final Element element) {
    return Element.Order.compare(this, element);
  }

  /**
   * Get the drawing layer of the node.
   *
   * @return The drawing layer of the node.
   */
  public int layer() {
    return this.layer;
  }

  /**
   * Set the drawing layer of the node.
   *
   * @param layer The drawing layer of the node.
   */
  public void layer(final int layer) {
    this.layer = layer;
  }
}
