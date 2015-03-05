/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// JavaFX shapes
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.Path;

// JavaFX paint
import javafx.scene.paint.Color;

/**
 * A way is an ordered list of 2 to 2,000 nodes.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Way">
 *      http://wiki.openstreetmap.org/wiki/Way</a>
 */
public final class Way extends Path implements Element {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 74;

  /**
   * The ID of the way.
   */
  private long id;

  /**
   * A map of tags associated with the way.
   */
  private Map<String, String> tags = new HashMap<>();

  /**
   * Drawing order of the way.
   */
  private Element.Order order = Element.Order.DEFAULT;

  /**
   * Drawing layer of the way.
   */
  private int layer;

  /**
   * Nodes contained within the way.
   */
  private List<Node> nodes = new ArrayList<>();

  /**
   * Initialize a way.
   *
   * @param id The ID of the way.
   */
  public Way(final long id) {
    this.id = id;

    // Don't use antialiasing for performance reasons.
    this.setSmooth(false);

    // Ensure that the bounds of the way are calculated correctly. This is only
    // the case if both a stroke and a fill is set, otherwise calculation of
    // bounds will be off.
    this.setStroke(Color.TRANSPARENT);
    this.setFill(Color.TRANSPARENT);

    this.getStyleClass().add("way");
  }

  /**
   * Get the ID of the way.
   *
   * @return The ID of the way.
   */
  public long id() {
    return this.id;
  }

  /**
   * Add a tag to the way.
   *
   * @param key   The key of the tag.
   * @param value The value of the tag.
   * @return      The previous value of the key, if any.
   */
  public String tag(final String key, final String value) {
    switch (key) {
      case "building":
      case "area":
        this.getStyleClass().add(key);
        break;
      case "highway":
      case "leisure":
      case "landuse":
      case "natural":
      case "waterway":
        this.getStyleClass().add(key);
        this.getStyleClass().add(value);
        break;
      default:
        // Do nothing.
    }

    return this.tags.put(key, value);
  }

  /**
   * Get a map of tags for the way.
   *
   * @return A map of tags for the way.
   */
  public Map<String, String> tags() {
    return this.tags;
  }

  /**
   * Get the drawing order of the way.
   *
   * @return The drawing order of the way.
   */
  public Element.Order order() {
    return this.order;
  }

  /**
   * Set the drawing order of the way.
   *
   * @param order The drawing order of the way.
   */
  public void order(final Element.Order order) {
    if (order == null) {
      return;
    }

    this.order = order;
  }

  /**
   * Get the drawing layer of the way.
   *
   * @return The drawing layer of the way.
   */
  public int layer() {
    return this.layer;
  }

  /**
   * Set the drawing layer of the way.
   *
   * @param layer The drawing layer of the way.
   */
  public void layer(final int layer) {
    this.layer = layer;
  }

  /**
   * Get the initial node of the way.
   *
   * @return The initial node of the way.
   */
  protected Node start() {
    return this.nodes.get(0);
  }

  /**
   * Get the last node of the way.
   *
   * @return The last node of the way.
   */
  protected Node end() {
    return this.nodes.get(this.nodes.size() - 1);
  }

  /**
   * Is the way closed?
   *
   * @return Boolean indicating whether or not the way is closed.
   */
  public boolean closed() {
    return this.start().equals(this.end());
  }

  /**
   * Check if the way is open (unclosed).
   *
   * @return Boolean indicating whether or not the way is open.
   */
  public boolean open() {
    return !this.closed();
  }

  /**
   * Check if the current way starts in the same node as another way either
   * ends or starts.
   *
   * @param way The way to check against.
   * @return    Boolean indicating whether or not the current way starts in
   *            either the end or start node of the specified way.
   */
  public boolean startsIn(final Way way) {
    if (way == null) {
      return false;
    }

    return (
      this.start().equals(way.start())
      || this.start().equals(way.end())
    );
  }

  /**
   * Check if the current way end in the same node as another way either
   * ends or starts.
   *
   * @param way The way to check against.
   * @return    Boolean indicating whether or not the current way ends in
   *            either the end or start node of the specified way.
   */
  public boolean endsIn(final Way way) {
    if (way == null) {
      return false;
    }

    return (
      this.end().equals(way.start())
      || this.end().equals(way.end())
    );
  }

  /**
   * Get the nodes contained within the way.
   *
   * @return The nodes contained within the way.
   */
  public List<Node> nodes() {
    return this.nodes;
  }

  /**
   * Add a node to the way.
   *
   * @param node The node to add to the way.
   */
  public void node(final Node node) {
    if (node == null) {
      return;
    }

    if (this.getElements().isEmpty()) {
      this.getElements().add(new MoveTo(node.getX(), node.getY()));
    }
    else {
      this.getElements().add(new LineTo(node.getX(), node.getY()));
    }

    this.nodes.add(node);
  }

  /**
   * Add a list of nodes to the way.
   *
   * @param nodes The nodes to add to the way.
   */
  public void nodes(final List<Node> nodes) {
    if (nodes == null) {
      return;
    }

    for (Node node: nodes) {
      this.node(node);
    }
  }

  /**
   * Append the nodes of another way to the current way.
   *
   * @param way The way whose nodes to append to the current way.
   */
  public void append(final Way way) {
    if (way == null) {
      return;
    }

    this.nodes(way.nodes());
  }

  /**
   * Does the current way intersect the specified way?.
   *
   * @param way The way to check intersection of.
   * @return    Boolean indicating whether or not the current way intersects the
   *            specified way.
   */
  public boolean intersects(final Way way) {
    if (way == null) {
      return false;
    }

    return this.intersects(
      this.parentToLocal(way.getBoundsInParent())
    );
  }

  /**
   * Does the current way contain the specified way?.
   *
   * @param way The way to check containment of.
   * @return    Boolean indicating whether or not the current way contains the
   *            specified way.
   */
  public boolean contains(final Way way) {
    if (way == null) {
      return false;
    }

    return (
      this.contains(this.parentToLocal(way.start()))
      && this.contains(this.parentToLocal(way.end()))
    );
  }
}
