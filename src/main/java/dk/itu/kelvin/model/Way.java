/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// JavaFX shapes
import javafx.scene.shape.Polyline;

// JavaFX paint
import javafx.scene.paint.Color;

/**
 * A way is an ordered list of 2 to 2,000 nodes.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Way">
 * http://wiki.openstreetmap.org/wiki/Way</a>
 */
public final class Way extends Element<Polyline> {
  /**
   * The JavaFX representation of the way.
   */
  private Polyline fx;

  /**
   * List of nodes contained within the way.
   *
   * The list is initialized on-demand when first accessed to avoid allocating
   * memory to empty lists.
   */
  private List<Node> nodes;

  /**
   * Initialize a way.
   *
   * @param id The ID of the way.
   */
  public Way(final long id) {
    super(id);
  }

  /**
   * Get the initial node of the way.
   *
   * @return The initial node of the way.
   */
  protected Node start() {
    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

    return this.nodes.get(0);
  }

  /**
   * Get the last node of the way.
   *
   * @return The last node of the way.
   */
  protected Node end() {
    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

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
  public void node(final Node node) {
    if (node == null) {
      return;
    }

    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

    this.nodes.add(node);
  }

  /**
   * Add a list of nodes to the way.
   *
   * @param nodes The nodes to add to the way.
   */
  public void nodes(final List<Node> nodes) {
    if (nodes == null || nodes.isEmpty()) {
      return;
    }

    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

    this.nodes.addAll(nodes);
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
   * Get the JavaFX representation of the way.
   *
   * @return The JavaFX representation of the way.
   */
  public Polyline fx() {
    if (this.fx != null) {
      return this.fx;
    }

    Polyline polyline = new Polyline();

    // Don't use antialiasing for performance reasons.
    polyline.setSmooth(false);

    // Ensure that the bounds of the way are calculated correctly. This is only
    // the case if both a stroke and a fill is set, otherwise calculation of
    // bounds will be off.
    polyline.setStroke(Color.TRANSPARENT);
    polyline.setFill(Color.TRANSPARENT);

    // this.getStyleClass().add("way");
    for (Map.Entry<String, String> tag: this.tags().entrySet()) {
      String key = tag.getKey();
      String value = tag.getValue();

      switch (key) {
        case "building":
        case "area":
          polyline.getStyleClass().add(key);
          break;
        case "highway":
        case "leisure":
        case "landuse":
        case "natural":
        case "waterway":
          polyline.getStyleClass().add(key);
          polyline.getStyleClass().add(value);
          break;
        default:
          // Do nothing.
      }
    }

    for (Node node: this.nodes()) {
      polyline.getPoints().add((double) node.x());
      polyline.getPoints().add((double) node.y());
    }

    this.fx = polyline;

    return this.fx;
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

    return this.fx().intersects(
      this.fx().parentToLocal(way.fx().getBoundsInParent())
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
      this.fx().contains(this.fx().parentToLocal(
        way.start().x(), way.start().y())
      )
      && this.fx().contains(this.fx().parentToLocal(
        way.end().x(), way.end().y())
      )
    );
  }
}
