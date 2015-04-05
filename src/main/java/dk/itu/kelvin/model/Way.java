/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JavaFX shapes
import javafx.scene.shape.Polyline;

// Utilities
import dk.itu.kelvin.util.ArrayList;
import dk.itu.kelvin.util.List;
import dk.itu.kelvin.util.Map;
import dk.itu.kelvin.util.RectangleTree;

/**
 * A way is an ordered list of 2 to 2,000 nodes.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Way">
 *      http://wiki.openstreetmap.org/wiki/Way</a>
 */
public final class Way extends Element<Polyline>
  implements RectangleTree.Index {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 67;

  /**
   * List of nodes contained within the way.
   *
   * <p>
   * The list is initialized on-demand when first accessed to avoid allocating
   * memory to empty lists.
   */
  private List<Node> nodes;

  /**
   * The smallest x-coordinate of the way.
   */
  private float minX;

  /**
   * The smallest y-coordinate of the way.
   */
  private float minY;

  /**
   * The largest x-coordinate of the way.
   */
  private float maxX;

  /**
   * The largest y-coordinate of the way.
   */
  private float maxY;

  /**
   * Get the smallest x-coordinate of the way.
   *
   * @return The smallest x-coordinate of the way.
   */
  public float minX() {
    return this.minX;
  }

  /**
   * Get the smallest y-coordinate of the way.
   *
   * @return The smallest y-coordinate of the way.
   */
  public float minY() {
    return this.minY;
  }

  /**
   * Get the largest x-coordinate of the way.
   *
   * @return The largest x-coordinate of the way.
   */
  public float maxX() {
    return this.maxX;
  }

  /**
   * Get the largest y-coordinate of the way.
   *
   * @return The largest y-coordinate of the way.
   */
  public float maxY() {
    return this.maxY;
  }

  /**
   * Get the initial node of the way.
   *
   * @return The initial node of the way.
   */
  public Node start() {
    if (this.nodes == null) {
      return null;
    }

    return this.nodes.get(0);
  }

  /**
   * Get the last node of the way.
   *
   * @return The last node of the way.
   */
  public Node end() {
    if (this.nodes == null) {
      return null;
    }

    return this.nodes.get(this.nodes.size() - 1);
  }

  /**
   * Is the way closed?
   *
   * @return Boolean indicating whether or not the way is closed.
   */
  public boolean isClosed() {
    Node start = this.start();
    Node end = this.end();

    if (start == null || end == null) {
      return false;
    }

    return start.x() == end.x() && start.y() == end.y();
  }

  /**
   * Check if the way is open (unclosed).
   *
   * @return Boolean indicating whether or not the way is open.
   */
  public boolean isOpen() {
    return !this.isClosed();
  }

  /**
   * Check if the current way starts in the same node as another way either ends
   * or starts.
   *
   * @param way The way to check against.
   * @return    Boolean indicating whether or not the current way starts in
   *            either the end or start node of the specified way.
   */
  public boolean startsIn(final Way way) {
    if (way == null) {
      return false;
    }

    Node start = this.start();

    if (start == null || way.start() == null) {
      return false;
    }

    return (
      (start.x() == way.start().x() && start.y() == way.start().y())
      || (start.x() == way.end().x() && start.y() == way.end().y())
    );
  }

  /**
   * Check if the current way end in the same node as another way either ends or
   * starts.
   *
   * @param way The way to check against.
   * @return    Boolean indicating whether or not the current way ends in either
   *            the end or start node of the specified way.
   */
  public boolean endsIn(final Way way) {
    if (way == null) {
      return false;
    }

    Node end = this.end();

    if (end == null || way.end() == null) {
      return false;
    }

    return (
      (end.x() == way.start().x() && end.y() == way.start().y())
      || (end.x() == way.end().x() && end.y() == way.end().y())
    );
  }

  /**
   * Get the nodes contained within the way.
   *
   * @return The nodes contained within the way.
   */
  public List<Node> nodes() {
    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

    return this.nodes;
  }

  /**
   * Add a node to the way.
   *
   * @param node The node to add to the way.
   */
  public void add(final Node node) {
    if (node == null) {
      return;
    }

    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

    boolean empty = this.nodes.isEmpty();

    this.minX = !empty ? Math.min(this.minX, node.x()) : node.x();
    this.minY = !empty ? Math.min(this.minY, node.y()) : node.y();
    this.maxX = !empty ? Math.max(this.maxX, node.x()) : node.x();
    this.maxY = !empty ? Math.max(this.maxY, node.y()) : node.y();

    this.nodes.add(node);
  }

  /**
   * Add a list of nodes to the way.
   *
   * @param nodes The nodes to add to the way.
   */
  public void add(final List<Node> nodes) {
    if (nodes == null || nodes.isEmpty()) {
      return;
    }

    for (Node node: nodes) {
      this.add(node);
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

    this.add(way.nodes());
  }

  /**
   * Get the JavaFX representation of the way.
   *
   * @return The JavaFX representation of the way.
   */
  public Polyline render() {
    Polyline polyline = new Polyline();

    // Don't use antialiasing for performance reasons.
    polyline.setSmooth(false);

    polyline.getStyleClass().add("way");

    for (Map.Entry<String, String> tag: this.tags().entrySet()) {
      polyline.getStyleClass().add(tag.getKey());
      polyline.getStyleClass().add(tag.getValue());
    }

    if (this.nodes != null) {
      for (Node node: this.nodes) {
        polyline.getPoints().add((double) node.x());
        polyline.getPoints().add((double) node.y());
      }
    }

    return polyline;
  }

  /**
   * Check if the specified key/value pair should be included in the tags of
   * the way.
   *
   * @param key   The key to check.
   * @param value The value to check.
   * @return      A bollean indicating whether or not the specified key/value
   *              pair should be included in the tags of the way.
   */
  protected boolean include(final String key, final String value) {
    if (key == null || value == null) {
      return false;
    }

    switch (key) {
      case "building":
      case "area":
      case "land":
      case "highway":
      case "leisure":
      case "landuse":
      case "natural":
      case "waterway":
      case "amenity":
      case "barrier":
        return true;

      default:
        return false;
    }
  }
}
